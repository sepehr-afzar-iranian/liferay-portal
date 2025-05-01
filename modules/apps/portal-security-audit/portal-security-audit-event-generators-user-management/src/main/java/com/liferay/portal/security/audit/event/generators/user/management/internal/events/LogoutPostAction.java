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

package com.liferay.portal.security.audit.event.generators.user.management.internal.events;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(
	immediate = true, property = "key=logout.events.post",
	service = LifecycleAction.class
)
public class LogoutPostAction extends Action {

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		try {
			User user = _portal.getUser(httpServletRequest);

			if (Objects.equals(user, null)) {
				return;
			}

			String userFullName = user.getFullName();

			AuditMessage auditMessage = new AuditMessage(
				EventTypes.LOGOUT, user.getCompanyId(), user.getUserId(),
				userFullName, User.class.getName(),
				String.valueOf(user.getUserId()),
				_getMessage(userFullName, httpServletRequest));

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private String _getMessage(String userName, HttpServletRequest httpServletRequest) {
		boolean passwordModified = GetterUtil.getBoolean(
				httpServletRequest.getAttribute(WebKeys.PASSWORD_MODIFIED));
		boolean screenNameModified = GetterUtil.getBoolean(
				httpServletRequest.getAttribute(WebKeys.SCREEN_NAME_MODIFIED));

		StringBuilder sb = new StringBuilder();

		sb.append("User with the name");
		sb.append(StringPool.SPACE);
		sb.append(userName);
		sb.append(StringPool.SPACE);

		if (passwordModified) {
			sb.append("was logged out because password was modified");
		} else if (screenNameModified) {
			sb.append("was logged out because screen name was modified");
		} else {
			sb.append("just logged out");
		}

		sb.append(StringPool.PERIOD);

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LogoutPostAction.class);

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private Portal _portal;

}