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

package com.liferay.portal.settings.web.internal.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouterUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropertiesParamUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;

import javax.portlet.ActionRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Yousef Ghadiri
 */
public class AuditPortalSettingsConfigurationScreenUtil {

	public static void audit(
		ActionRequest actionRequest, String eventType,
		String disallowIpsChanged) {

		try {
			HttpServletRequest httpServletRequest =
				PortalUtil.getHttpServletRequest(actionRequest);
			long companyId = PortalUtil.getCompanyId(actionRequest);
			long userId = 0;
			String userFullName = "";
			User user;

			try {
				user = PortalUtil.getUser(httpServletRequest);
			}
			catch (Exception exception) {
				user = UserLocalServiceUtil.fetchDefaultUser(companyId);
			}

			if (user != null) {
				userId = user.getUserId();
				userFullName = user.getFullName();
			}

			String redirectURL = httpServletRequest.getParameter("redirect");

			int lastIndex = redirectURL.lastIndexOf('=');

			String configurationName = redirectURL.substring(lastIndex + 1);

			AuditMessage auditMessage = new AuditMessage(
				eventType, companyId, userId, userFullName, configurationName,
				"0", _getMessage(eventType, configurationName, userFullName));

			if (!eventType.equals(EventTypes.DELETE)) {
				UnicodeProperties unicodeProperties =
					PropertiesParamUtil.getProperties(
						actionRequest, "settings--");

				JSONObject additionalInfoJSONObject = JSONUtil.put(
					"newProperties", unicodeProperties);

				if (disallowIpsChanged != null) {
					additionalInfoJSONObject.put(
						"disallowIpsChanges", disallowIpsChanged);
				}

				auditMessage.setAdditionalInfo(additionalInfoJSONObject);
			}

			AuditRouterUtil.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private static String _getMessage(
		String eventType, String configurationName, String userFullName) {

		StringBuilder sb = new StringBuilder();

		sb.append("Settings Configuration");
		sb.append(StringPool.SPACE);
		sb.append(configurationName);
		sb.append(StringPool.SPACE);
		sb.append("was");
		sb.append(StringPool.SPACE);

		if (eventType.equals(EventTypes.UPDATE)) {
			sb.append("updated");
		}
		else if (eventType.equals(EventTypes.DELETE)) {
			sb.append("deleted");
		}
		else {
			sb.append("added");
		}

		sb.append(StringPool.SPACE);

		if (userFullName != null) {
			sb.append("by");
			sb.append(StringPool.SPACE);
			sb.append(userFullName);
		}

		sb.append(StringPool.PERIOD);

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AuditPortalSettingsConfigurationScreenUtil.class);

}