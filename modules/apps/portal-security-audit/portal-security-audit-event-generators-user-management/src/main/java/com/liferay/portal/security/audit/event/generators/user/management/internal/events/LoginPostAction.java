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

import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.servlet.PortalSessionContext;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.user.management.util.AuditMessageHelperUtil;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(
	immediate = true, property = "key=login.events.post",
	service = LifecycleAction.class
)
public class LoginPostAction extends Action {

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		try {
			User user = _portal.getUser(httpServletRequest);

			long userId = user.getUserId();

			String userFullName = user.getFullName();

			AuditMessage auditMessage = new AuditMessage(
				EventTypes.LOGIN, user.getCompanyId(), userId, userFullName,
				User.class.getName(), String.valueOf(userId),
				AuditMessageHelperUtil.getMessage(
					EventTypes.LOGIN, null, userFullName, 0));

			_auditRouter.route(auditMessage);

			Collection<HttpSession> sessions = PortalSessionContext.values();

			Stream<HttpSession> stream = sessions.stream();

			stream.filter(
				session ->
					Objects.equals(userId, session.getAttribute("USER_ID")) &&
					!Objects.equals(session, httpServletRequest.getSession())
			).forEach(
				session -> {
					try {
						session.invalidate();
					}
					catch (Exception exception) {
						if (_log.isWarnEnabled()) {
							_log.warn(
								"Unable to invalidate session", exception);
						}
					}
				}
			);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LoginPostAction.class);

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private Portal _portal;

}