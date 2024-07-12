/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.sync.internal.model.listener;

import com.liferay.petra.concurrent.NoticeableExecutorService;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.RoleServiceUtil;

import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.sync.model.SyncDLObject;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.Date;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shinn Lok
 */
@Component(immediate = true, service = ModelListener.class)
public class ResourcePermissionModelListener
	extends SyncBaseModelListener<ResourcePermission> {

	@Override
	public void onBeforeCreate(ResourcePermission resourcePermission)
		throws ModelListenerException {

		SyncDLObject syncDLObject = getSyncDLObject(resourcePermission);

		if (syncDLObject == null) {
			return;
		}

		if (resourcePermission.hasActionId(ActionKeys.VIEW)) {
			updateSyncDLObject(syncDLObject);
		}
	}

	@Override
	public void onBeforeRemove(ResourcePermission resourcePermission)
		throws ModelListenerException {

		SyncDLObject syncDLObject = getSyncDLObject(resourcePermission);

		if (syncDLObject == null) {
			return;
		}

		if (resourcePermission.hasActionId(ActionKeys.VIEW)) {
			Date date = new Date();

			syncDLObject.setModifiedTime(date.getTime());
			syncDLObject.setLastPermissionChangeDate(date);

			syncDLObjectLocalService.updateSyncDLObject(syncDLObject);
		}
	}

	@Override
	public void onBeforeUpdate(ResourcePermission resourcePermission)
		throws ModelListenerException {

		SyncDLObject syncDLObject = getSyncDLObject(resourcePermission);

		if (syncDLObject == null) {
			return;
		}

		ResourcePermission originalResourcePermission =
			resourcePermissionLocalService.fetchResourcePermission(
				resourcePermission.getResourcePermissionId());

		if (originalResourcePermission.hasActionId(ActionKeys.VIEW) &&
			!resourcePermission.hasActionId(ActionKeys.VIEW)) {

			Date date = new Date();

			syncDLObject.setModifiedTime(date.getTime());
			syncDLObject.setLastPermissionChangeDate(date);

			syncDLObjectLocalService.updateSyncDLObject(syncDLObject);
		}
		else if (!originalResourcePermission.hasActionId(ActionKeys.VIEW) &&
				 resourcePermission.hasActionId(ActionKeys.VIEW)) {

			TransactionCommitCallbackUtil.registerCallback(
				() -> {
					NoticeableExecutorService noticeableExecutorService =
						portalExecutorManager.getPortalExecutor(
							ResourcePermissionModelListener.class.getName());

					noticeableExecutorService.submit(
						() -> {
							try {
								updateSyncDLObject(syncDLObject);
							}
							catch (Exception exception) {
								throw new ModelListenerException(exception);
							}

							return null;
						});

					return null;
				});
		}
	}

	@Override
	public void onAfterUpdate(ResourcePermission resourcePermission)
		throws ModelListenerException {
		try {
			long resourcePermissionId =
				resourcePermission.getResourcePermissionId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				EventTypes.PERMISSION_UPDATE,
				ResourcePermission.class.getName(),
				resourcePermissionId, null);

			long roleId = resourcePermission.getRoleId();
			String resourcePermissionName = resourcePermission.getName();

			JSONObject permissions = JSONFactoryUtil.createJSONObject();

			for (ResourceAction resourceAction : _resourceActionLocalService.getResourceActions(
				resourcePermissionName)) {
				permissions.put(
					resourceAction.getActionId(),
					resourcePermission.hasActionId(
						resourceAction.getActionId()));
			}

			Role role =
				RoleServiceUtil.fetchRole(roleId);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"resourcePermissionId", resourcePermissionId
			).put(
				"resourcePermissionName", resourcePermissionName
			).put(
				"resourcePermissionPrimKey", resourcePermission.getPrimKey()
			).put(
				"roleId", roleId
			).put(
				"roleName", role.getName()
			).put(
				"permissions", permissions
			);

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

}