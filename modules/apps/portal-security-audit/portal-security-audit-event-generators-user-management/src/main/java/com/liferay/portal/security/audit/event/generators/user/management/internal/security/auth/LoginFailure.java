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

package com.liferay.portal.security.audit.event.generators.user.management.internal.security.auth;

import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.AuthFailure;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.user.management.util.AuditMessageHelperUtil;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(
	immediate = true, property = "key=auth.failure", service = AuthFailure.class
)
public class LoginFailure implements AuthFailure {

	@Override
	public void onFailureByEmailAddress(
		long companyId, String emailAddress, Map<String, String[]> headerMap,
		Map<String, String[]> parameterMap) {

		User user = _userLocalService.fetchUserByEmailAddress(
			companyId, emailAddress);

		audit(
			user, headerMap, "Failed to authenticate by email address",
			"emailAddress", emailAddress);
	}

	@Override
	public void onFailureByScreenName(
		long companyId, String screenName, Map<String, String[]> headerMap,
		Map<String, String[]> parameterMap) {

		User user = _userLocalService.fetchUserByScreenName(
			companyId, screenName);

		audit(
			user, headerMap, "Failed to authenticate by screen name",
			"screenName", screenName);
	}

	@Override
	public void onFailureByUserId(
		long companyId, long userId, Map<String, String[]> headerMap,
		Map<String, String[]> parameterMap) {

		User user = null;

		try {
			user = _userLocalService.getUserById(companyId, userId);
		}
		catch (Exception exception) {
		}

		audit(
			user, headerMap, "Failed to authenticate by user ID", "userId",
			String.valueOf(userId));
	}

	protected void audit(
		User user, Map<String, String[]> headerMap, String reason, String type,
		String typeValue) {

		try {
			JSONObject additionalInfoJSONObject =
				_jsonFactory.createJSONObject();

			additionalInfoJSONObject.put(
				type, typeValue
			).put(
				"headers", _jsonFactory.serialize(headerMap)
			).put(
				"reason", reason
			).put(
				"type", type
			);

			String userFullName = user.getFullName();

			AuditMessage auditMessage = new AuditMessage(
				EventTypes.LOGIN_FAILURE, user.getCompanyId(), user.getUserId(),
				userFullName, User.class.getName(),
				String.valueOf(user.getPrimaryKey()),
				AuditMessageHelperUtil.getMessage(
					EventTypes.LOGIN_FAILURE, null, userFullName, 0),
				additionalInfoJSONObject);

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(LoginFailure.class);

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private UserLocalService _userLocalService;

}