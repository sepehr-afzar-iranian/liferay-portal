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

import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.security.audit.event.generators.constants.AuditConstants;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(immediate = true, service = ModelListener.class)
public class AuditEventModelListener extends BaseModelListener<AuditEvent> {

	@Override
	public void onAfterCreate(AuditEvent auditEvent)
		throws ModelListenerException {

		try {
			_auditCounter++;

			if ((_auditCounter == _AUDIT_THRESHOLD) ||
				((_auditCounter > _AUDIT_THRESHOLD) &&
				 ((_auditCounter % _AUDIT_THRESHOLD) == 0))) {

				_sendNotificationToInstanceAdministrators(
					auditEvent.getCompanyId());
			}
		}
		catch (Exception exception) {
			System.out.println("e = " + exception);
		}
	}

	@Activate
	protected void activate() {
		try {
			_auditCounter = _auditEventLocalService.getAuditEventsCount();
		}
		catch (Exception exception) {
			System.out.println(
				"Error initializing counters: " + exception.getMessage());
		}
	}

	private void _sendNotificationToInstanceAdministrators(long companyId)
		throws PortalException {

		Role role = _roleLocalService.getRole(
			companyId, RoleConstants.ADMINISTRATOR);

		JSONObject payloadjsonObject = JSONUtil.put("counter", _auditCounter);

		for (User user : _userLocalService.getRoleUsers(role.getRoleId())) {
			_userNotificationEventLocalService.sendUserNotificationEvents(
				user.getUserId(), AuditConstants.AUDIT_THRESHOLD_NOTIFICATION,
				UserNotificationDeliveryConstants.TYPE_WEBSITE,
				payloadjsonObject);
		}
	}

	private static final int _AUDIT_THRESHOLD = 1000;

	private long _auditCounter;

	@Reference
	private AuditEventLocalService _auditEventLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}