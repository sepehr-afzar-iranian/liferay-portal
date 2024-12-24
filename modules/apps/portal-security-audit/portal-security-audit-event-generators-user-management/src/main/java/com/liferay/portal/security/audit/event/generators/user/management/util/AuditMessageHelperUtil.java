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

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;

import java.util.Objects;

/**
 * @author Yousef Ghadiri
 */
public class AuditMessageHelperUtil {

	public static String getMessage(
		String eventType, String className, String name, long id) {

		if (eventType.equals(EventTypes.AUADIT_AUTO_DELETE))

			return "Old audits were automatically deleted.";

		if (eventType.equals(EventTypes.LOGIN) ||
			eventType.equals(EventTypes.LOGOUT) ||
			eventType.equals(EventTypes.LOGIN_FAILURE) ||
			eventType.equals(EventTypes.IMPERSONATE))

			return _userMessage(eventType, name);

		return _regularMessage(eventType, className, name, id);
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

	private static String _regularMessage(
		String eventType, String className, String name, long id) {

		StringBuilder sb = new StringBuilder();

		sb.append(_getShortClassName(className));
		sb.append(StringPool.SPACE);
		sb.append("with");
		sb.append(StringPool.SPACE);
		sb.append("the");
		sb.append(StringPool.SPACE);

		if (!Objects.equals(name, null)) {
			sb.append("name");
			sb.append(StringPool.SPACE);
			sb.append(name);
		}
		else {
			sb.append("id");
			sb.append(StringPool.SPACE);
			sb.append(id);
		}

		sb.append(StringPool.SPACE);
		sb.append("was");
		sb.append(StringPool.SPACE);

		if (eventType.equals(EventTypes.ADD)) {
			sb.append("Added");
		}
		else if (eventType.equals(EventTypes.DELETE)) {
			sb.append("Deleted");
		}
		else if (eventType.equals(EventTypes.UPDATE)) {
			sb.append("Updated");
		}
		else if (eventType.equals(EventTypes.CONFIGURATION_SAVE)) {
			sb.append("Saved");
		}
		else {
			sb.append("Changed");
		}

		sb.append(StringPool.PERIOD);

		return sb.toString();
	}

	private static String _userMessage(String eventType, String userName) {
		StringBuilder sb = new StringBuilder();

		sb.append("User with the name");
		sb.append(StringPool.SPACE);
		sb.append(userName);
		sb.append(StringPool.SPACE);

		if (eventType.equals(EventTypes.LOGIN)) {
			sb.append("just logged in");
		}
		else if (eventType.equals(EventTypes.LOGOUT)) {
			sb.append("just logged out");
		}
		else if (eventType.equals(EventTypes.LOGIN_FAILURE)) {
			sb.append("failed to logged in");
		}
		else if (eventType.equals(EventTypes.IMPERSONATE)) {
			sb.append("just impersonated someone");
		}

		sb.append(StringPool.PERIOD);

		return sb.toString();
	}

}