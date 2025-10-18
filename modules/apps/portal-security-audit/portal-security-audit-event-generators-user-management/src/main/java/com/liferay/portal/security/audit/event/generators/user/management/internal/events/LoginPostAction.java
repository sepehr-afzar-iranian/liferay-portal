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

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserTracker;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.servlet.PortalSessionContext;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.liveusers.LiveUsers;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.event.generators.constants.AuditConstants;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.user.management.util.AuditMessageHelperUtil;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;
import com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(
	configurationPid = "com.liferay.portal.security.audit.configuration.AuditConfiguration",
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
			if (!_auditConfiguration.enabled()) {
				return;
			}

			User user = _portal.getUser(httpServletRequest);

			long userId = user.getUserId();

			String userFullName = user.getFullName();

			long ldapServerId = user.getLdapServerId();

			AuditMessage auditMessage = new AuditMessage(
				EventTypes.LOGIN, user.getCompanyId(), userId, userFullName,
				User.class.getName(), String.valueOf(userId),
				AuditMessageHelperUtil.getMessage(
					EventTypes.LOGIN, null, userFullName, ldapServerId));

			boolean ldap = false;

			if (ldapServerId > 0) {
				ldap = true;
			}

			JSONObject additionalInfoJSONObject = JSONUtil.put("ldap", ldap);

			if (ldap) {
				additionalInfoJSONObject.put("ldapServerId", ldapServerId);

				LDAPServerConfiguration ldapServerConfiguration =
					_ldapServerConfigurationProvider.getConfiguration(
						user.getCompanyId(), ldapServerId);

				if (ldapServerConfiguration != null) {
					additionalInfoJSONObject.put(
						"ldapServerName", ldapServerConfiguration.serverName());
				}
			}

			auditMessage.setAdditionalInfo(additionalInfoJSONObject);

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}

		boolean expirePreviousSessionsOnLogin = Boolean.parseBoolean(
			PropsUtil.get(AuditConstants.EXPIRE_PREVIOUS_SESSIONS_ON_LOGIN));

		if (expirePreviousSessionsOnLogin) {
			try {
				long userId = Long.parseLong(
					httpServletRequest.getRemoteUser());

				long companyId = _companyLocalService.getCompanyIdByUserId(
					userId);

				Map<String, UserTracker> sessionUsers =
					LiveUsers.getSessionUsers(companyId);

				List<UserTracker> userTrackers = new ArrayList<>(
					sessionUsers.values());

				for (UserTracker userTracker : userTrackers) {
					if (userId != userTracker.getUserId()) {
						continue;
					}

					HttpSession userSession = PortalSessionContext.get(
						userTracker.getSessionId());

					if (Objects.equals(
							userSession, httpServletRequest.getSession())) {

						continue;
					}

					if (!Objects.equals(userSession, null)) {
						userSession.setAttribute(
							WebKeys.SESSION_TERMINATED_REASON,
							WebKeys.SIMULTANEOUS_LOGINS);
					}
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to set expire attribute for user sessions",
						exception);
				}
			}
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_auditConfiguration = ConfigurableUtil.createConfigurable(
			AuditConfiguration.class, properties);
	}

	@Reference(
		target = "(factoryPid=com.liferay.portal.security.ldap.configuration.LDAPServerConfiguration)",
		unbind = "-"
	)
	protected void setLDAPServerConfigurationProvider(
		ConfigurationProvider<LDAPServerConfiguration>
			ldapServerConfigurationProvider) {

		_ldapServerConfigurationProvider = ldapServerConfigurationProvider;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LoginPostAction.class);

	private volatile AuditConfiguration _auditConfiguration;

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private CompanyLocalService _companyLocalService;

	private ConfigurationProvider<LDAPServerConfiguration>
		_ldapServerConfigurationProvider;

	@Reference
	private Portal _portal;

}