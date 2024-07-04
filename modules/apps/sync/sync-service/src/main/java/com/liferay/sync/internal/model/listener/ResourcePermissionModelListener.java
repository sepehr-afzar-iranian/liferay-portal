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
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.RoleServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
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
	public void onAfterCreate(ResourcePermission resourcePermission)
		throws ModelListenerException {
		audit(EventTypes.PERMISSION_ADD, resourcePermission);
	}

	@Override
	public void onAfterRemove(ResourcePermission resourcePermission)
		throws ModelListenerException {
		audit(EventTypes.PERMISSION_DELETE, resourcePermission);
	}

	@Override
	public void onAfterUpdate(ResourcePermission resourcePermission)
		throws ModelListenerException {
		audit(EventTypes.PERMISSION_UPDATE, resourcePermission);
	}

	protected void audit(
		String eventType, ResourcePermission resourcePermission)
		throws ModelListenerException {
		try {
			long resourcePermissionId =
				resourcePermission.getResourcePermissionId();
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, ResourcePermission.class.getName(),
				resourcePermissionId, null);
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();
			long roleId = resourcePermission.getRoleId();

			Role role =
				RoleServiceUtil.fetchRole(roleId);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"groupId", serviceContext.getScopeGroupId()
			).put(
				"resourcePermissionId", resourcePermissionId
			).put(
				"resourcePermissionName", resourcePermission.getName()
			).put(
				"resourcePermissionPrimKey", resourcePermission.getPrimKey()
			).put(
				"roleId", roleId
			).put(
				"roleName", role.getName()
			);

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private AuditRouter _auditRouter;

}