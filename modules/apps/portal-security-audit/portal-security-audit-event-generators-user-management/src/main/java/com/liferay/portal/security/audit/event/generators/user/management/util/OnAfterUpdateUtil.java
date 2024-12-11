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

package com.liferay.portal.security.audit.event.generators.user.management.util;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.OrderFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalServiceUtil;

import java.util.List;

/**
 * @author Yousef Ghadiri
 */
public class OnAfterUpdateUtil {

	public static void update(String className, long id) {
		update(className, id, EventTypes.UPDATE);
	}

	public static void update(String className, long id, String eventType) {
		try {
			Property classNameProperty = PropertyFactoryUtil.forName(
				"className");
			Property classPKProperty = PropertyFactoryUtil.forName("classPK");
			Property eventTypeProperty = PropertyFactoryUtil.forName(
				"eventType");

			DynamicQuery dynamicQuery =
				AuditEventLocalServiceUtil.dynamicQuery();

			dynamicQuery.add(classNameProperty.eq(className));
			dynamicQuery.add(classPKProperty.eq(String.valueOf(id)));
			dynamicQuery.add(eventTypeProperty.eq(eventType));
			dynamicQuery.addOrder(OrderFactoryUtil.desc("createDate"));

			List<AuditEvent> auditEvents =
				AuditEventLocalServiceUtil.dynamicQuery(dynamicQuery, 0, 1);

			if (!auditEvents.isEmpty()) {
				AuditEvent auditEvent = auditEvents.get(0);

				auditEvent.setEventType(eventType + "_AFTER");

				AuditEventLocalServiceUtil.updateAuditEvent(auditEvent);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to update audit message", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OnAfterUpdateUtil.class);

}