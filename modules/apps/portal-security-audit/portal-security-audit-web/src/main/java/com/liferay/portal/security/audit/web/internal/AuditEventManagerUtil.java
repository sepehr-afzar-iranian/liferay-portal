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
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;
import com.liferay.portal.security.audit.web.internal.portlet.AuditField;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
		String clientIP, String serverName, String serverPort, Date startDate,
		Date endDate, int cur, int delta) {

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

		if ((cur == -1) && (delta == -1)) {
			BaseModelSearchResult<AuditEvent> result =
				_auditEventLocalService.search(
					companyId, keywords, params, -1, -1, "createDate", true);

			List<AuditEvent> auditEvents = result.getBaseModels();

			Stream<AuditEvent> stream = auditEvents.stream();

			_allAuditEventsFilterByDate = stream.filter(
				auditEvent -> {
					if ((startDate != null) && (endDate != null)) {
						return !startDate.after(auditEvent.getCreateDate()) &&
							   !endDate.before(auditEvent.getCreateDate());
					}

					return true;
				}
			).collect(
				Collectors.toList()
			);

			return _allAuditEventsFilterByDate;
		}

		Stream<AuditEvent> stream = _allAuditEventsFilterByDate.stream();

		return stream.skip(
			cur
		).limit(
			delta
		).collect(
			Collectors.toList()
		);
	}

	public static int getAuditEventsCount(
		long companyId, String keywords, String userId, String userName,
		String eventType, String className, String classPK, String clientHost,
		String clientIP, String serverName, String serverPort, Date startDate,
		Date endDate) {

		List<AuditEvent> auditEvents = getAuditEvents(
			companyId, keywords, userId, userName, eventType, className,
			classPK, clientHost, clientIP, serverName, serverPort, startDate,
			endDate, -1, -1);

		return auditEvents.size();
	}

	public static String[] getEventTypes() {
		Stream<AuditEvent> stream = _allAuditEvents.stream();

		String[] eventTypes = stream.map(
			AuditEvent::getEventType
		).distinct(
		).toArray(
			String[]::new
		);

		return _addEmptyString(eventTypes);
	}

	public static String[] getResourceNames() {
		Stream<AuditEvent> stream = _allAuditEvents.stream();

		String[] resourceNames = stream.map(
			AuditEvent::getClassName
		).map(
			resourceName -> Validator.isNotNull(resourceName) ?
				resourceName.substring(resourceName.lastIndexOf('.') + 1) : ""
		).distinct(
		).toArray(
			String[]::new
		);

		return _addEmptyString(resourceNames);
	}

	public static void setAllAuditEvents(long companyId) {
		BaseModelSearchResult<AuditEvent> result =
			_auditEventLocalService.search(
				companyId, "", new LinkedHashMap<>(), -1, -1, "createDate",
				true);

		_allAuditEvents = result.getBaseModels();
	}

	@Reference(unbind = "-")
	protected void setAuditEventLocalService(
		AuditEventLocalService auditEventLocalService) {

		_auditEventLocalService = auditEventLocalService;
	}

	private static String[] _addEmptyString(String[] auditEventFields) {
		String[] finalAuditEventTypes = new String[auditEventFields.length + 1];

		finalAuditEventTypes[0] = "";

		System.arraycopy(
			auditEventFields, 0, finalAuditEventTypes, 1,
			auditEventFields.length);

		return finalAuditEventTypes;
	}

	private static List<AuditEvent> _allAuditEvents;
	private static List<AuditEvent> _allAuditEventsFilterByDate;
	private static AuditEventLocalService _auditEventLocalService;

}