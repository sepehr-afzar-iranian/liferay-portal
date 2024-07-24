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

package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener;

import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(immediate = true, service = ModelListener.class)
public class UserGroupRoleModelListener
	extends BaseModelListener<UserGroupRole> {

	public void onBeforeCreate(UserGroupRole userGroupRole)
		throws ModelListenerException {

		auditOnCreateOrRemove(EventTypes.ADD, userGroupRole);
	}

	public void onBeforeRemove(UserGroupRole userGroupRole)
		throws ModelListenerException {

		auditOnCreateOrRemove(EventTypes.DELETE, userGroupRole);
	}

	protected void auditOnCreateOrRemove(
			String eventType, UserGroupRole userGroupRole)
		throws ModelListenerException {

		try {
			long userGroupRoleId = userGroupRole.getUserGroupRoleId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, UserGroupRole.class.getName(), userGroupRoleId,
				null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"roleId", userGroupRole.getRoleId()
			).put(
				"userGroupRoleId", userGroupRoleId
			).put(
				"userID", userGroupRole.getUserId()
			);

			Role role = userGroupRole.getRole();

			if (!Objects.equals(role, null)) {
				additionalInfoJSONObject.put("roleName", role.getName());
			}

			User user = userGroupRole.getUser();

			if (!Objects.equals(user, null)) {
				additionalInfoJSONObject.put("userName", user.getFullName());
			}

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private AuditRouter _auditRouter;

}