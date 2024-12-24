/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.sync.internal.model.listener;

import com.liferay.petra.concurrent.NoticeableExecutorService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;
import com.liferay.sync.model.SyncDLObject;

import java.util.Date;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shinn Lok
 */
@Component(immediate = true, service = ModelListener.class)
public class ResourcePermissionModelListener
	extends SyncBaseModelListener<ResourcePermission> {

	@Override
	public void onAfterUpdate(ResourcePermission resourcePermission)
		throws ModelListenerException {

		try {
			long resourcePermissionId =
				resourcePermission.getResourcePermissionId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				EventTypes.PERMISSION_UPDATE,
				ResourcePermission.class.getName(), resourcePermissionId, null);

			String resourcePermissionName = resourcePermission.getName();

			JSONObject permissionsJSONObject =
				JSONFactoryUtil.createJSONObject();

			for (ResourceAction resourceAction :
					_resourceActionLocalService.getResourceActions(
						resourcePermissionName)) {

				permissionsJSONObject.put(
					resourceAction.getActionId(),
					resourcePermission.hasActionId(
						resourceAction.getActionId()));
			}

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"permissions", permissionsJSONObject
			).put(
				"resourcePermissionId", resourcePermissionId
			).put(
				"resourcePermissionName", resourcePermissionName
			).put(
				"resourcePermissionPrimKey", resourcePermission.getPrimKey()
			);

			long roleId = resourcePermission.getRoleId();

			Role role = _roleLocalService.fetchRole(roleId);

			if (!Objects.equals(role, null)) {
				additionalInfoJSONObject.put(
					"roleId", roleId
				).put(
					"roleName", role.getName()
				);
			}

			auditMessage.setMessage(
				StringBundler.concat(
					"ResourcePermission with the name ", _getShortClassName(resourcePermissionName),
					"was updated."));

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

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

	private static String _getShortClassName(String className) {
		if (Validator.isNotNull(className)) {
			try {
				return className.substring(className.lastIndexOf('.') + 1);
			}
			catch (Exception exception) {
			}
		}

		return className;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ResourcePermissionModelListener.class);

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}