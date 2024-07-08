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

package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener;

import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Role;

import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.user.management.util.AuditMessageRoleAssociationUtil;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(immediate = true, service = ModelListener.class)
public class RoleModelListener extends BaseModelListener<Role> {

	public void onAfterAddAssociation(
		Object classPK, String associationClassName,
		Object associationClassP) {
		try {
			String additName =
				_auditMessageRoleAssociationUtil.getName(associationClassName);
			String additValue =
				_auditMessageRoleAssociationUtil.getValue(associationClassName,
					associationClassP);

			AuditMessage auditMessage =
				AuditMessageBuilder.buildAuditMessage(
					EventTypes.UPDATE, Role.class.getName(),
					(long) associationClassP, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			additionalInfoJSONObject.put(
				"associationType", EventTypes.ADD
			).put(
				"associationName", additName
			).put(
				"associationValue", additValue
			).put(
				"associationClassName", associationClassName
			).put(
				"groupId", serviceContext.getScopeGroupId()
			);

			_auditRouter.route(auditMessage);
		}
		catch (Exception e) {
			System.out.println("e = " + e);
		}
	}

	public void onAfterRemoveAssociation(
		Object classPK, String associationClassName,
		Object associationClassP) {
		try {
			String additName =
				_auditMessageRoleAssociationUtil.getName(associationClassName);
			String additValue =
				_auditMessageRoleAssociationUtil.getValue(associationClassName,
					associationClassP);

			AuditMessage auditMessage =
				AuditMessageBuilder.buildAuditMessage(
					EventTypes.UPDATE, Role.class.getName(),
					(long) associationClassP, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			additionalInfoJSONObject.put(
				"associationType", EventTypes.DELETE
			).put(
				"associationName", additName
			).put(
				"associationValue", additValue
			).put(
				"associationClassName", associationClassName
			).put(
				"groupId", serviceContext.getScopeGroupId()
			);

			_auditRouter.route(auditMessage);
		}
		catch (Exception e) {
			System.out.println("e = " + e);
		}
	}

	@Override
	public void onAfterCreate(Role role) throws ModelListenerException {
		audit(EventTypes.ADD, role);
	}

	@Override
	public void onAfterRemove(Role role) throws ModelListenerException {
		audit(EventTypes.DELETE, role);
	}

	@Override
	public void onAfterUpdate(Role role) throws ModelListenerException {
		audit(EventTypes.UPDATE, role);
	}

	protected void audit(String eventType, Role role)
		throws ModelListenerException {

		try {
			long roleId = role.getRoleId();
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, Role.class.getName(), roleId, null);

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();
			additionalInfoJSONObject.put(
				"groupId", serviceContext.getScopeGroupId()
			).put(
				"roleId", roleId
			).put(
				"roleName", role.getRoleId()
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
	private AuditMessageRoleAssociationUtil _auditMessageRoleAssociationUtil;
}