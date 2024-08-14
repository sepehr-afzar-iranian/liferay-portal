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

package com.liferay.portal.security.audit.web.internal;

import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;
import com.liferay.portal.security.audit.web.internal.portlet.AuditField;

import java.util.LinkedHashMap;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Greenwald
 * @author Prathima Shreenath
 */
@Component(immediate = true, service = {})
public class AuditEventManagerUtil {

	public static List<AuditEvent> getAuditEvents(
		long companyId, String keywords, String userId, String userName,
		String eventType, String className, String classPK, String clientHost,
		String clientIP, String serverName, String serverPort, int cur,
		int delta) {

		LinkedHashMap<String, Object> params = new LinkedHashMap<>();

		params.put(AuditField.USER_ID, userId);

		params.put(AuditField.USER_NAME, userName);

		params.put(AuditField.CLASS_NAME, className);

		params.put(AuditField.CLASS_PK, classPK);

		params.put(AuditField.CLIENT_HOST, clientHost);

		params.put(AuditField.CLIENT_IP, clientIP);

		params.put(AuditField.EVENT_TYPE, eventType);

		params.put(AuditField.SERVER_NAME, serverName);

		params.put(AuditField.SERVER_PORT, serverPort);

		BaseModelSearchResult<AuditEvent> result =
			_auditEventLocalService.search(
				companyId, keywords, params, cur, delta, "createDate", true);

		return result.getBaseModels();
	}

	public static int getAuditEventsCount(
		long companyId, String keywords, String userId, String userName,
		String eventType, String className, String classPK, String clientHost,
		String clientIP, String serverName, String serverPort) {

		List<AuditEvent> auditEvents = getAuditEvents(
			companyId, keywords, userId, userName, eventType, className,
			classPK, clientHost, clientIP, serverName, serverPort, -1, -1);

		return auditEvents.size();
	}

	public static String[] getEventTypes() {
		return _EVENT_TYPES;
	}

	@Reference(unbind = "-")
	protected void setAuditEventLocalService(
		AuditEventLocalService auditEventLocalService) {

		_auditEventLocalService = auditEventLocalService;
	}

	private static final String[] _EVENT_TYPES = {
		EventTypes.ALL, EventTypes.ADD, EventTypes.ASSIGN, EventTypes.DELETE,
		EventTypes.IMPERSONATE, EventTypes.LOGIN, EventTypes.LOGIN_FAILURE,
		EventTypes.LOGOUT, EventTypes.UNASSIGN, EventTypes.UPDATE
	};

	private static AuditEventLocalService _auditEventLocalService;

}