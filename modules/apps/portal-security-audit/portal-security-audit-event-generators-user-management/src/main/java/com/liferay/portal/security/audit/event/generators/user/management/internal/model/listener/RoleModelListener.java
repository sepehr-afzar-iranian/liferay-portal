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
		associationAudit(associationClassName, associationClassP, EventTypes.ADD);
	}

	public void onAfterRemoveAssociation(
		Object classPK, String associationClassName,
		Object associationClassP) {
		associationAudit(associationClassName, associationClassP, EventTypes.DELETE);
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

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();
			additionalInfoJSONObject.put(
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

	protected void associationAudit(
		String associationClassName,
		Object associationClassP, String eventType) {
		try {
			AuditMessage auditMessage =
				AuditMessageBuilder.buildAuditMessage(
					EventTypes.UPDATE, Role.class.getName(),
					(long) associationClassP, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"associationType", eventType
			).put(
				"associationName", _auditMessageRoleAssociationUtil.getName(associationClassName)
			).put(
				"associationValue", _auditMessageRoleAssociationUtil.getValue(associationClassName, associationClassP)
			).put(
				"associationClassName", associationClassName
			);

			_auditRouter.route(auditMessage);
		}
		catch (Exception e) {
			System.out.println("e = " + e);
		}
	}

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private AuditMessageRoleAssociationUtil _auditMessageRoleAssociationUtil;
}