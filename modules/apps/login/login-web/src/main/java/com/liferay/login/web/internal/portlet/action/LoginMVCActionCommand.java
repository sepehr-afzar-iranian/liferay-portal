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

package com.liferay.login.web.internal.portlet.action;

import com.liferay.captcha.util.CaptchaUtil;
import com.liferay.login.web.constants.LoginPortletKeys;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.captcha.CaptchaConfigurationException;
import com.liferay.portal.kernel.captcha.CaptchaException;
import com.liferay.portal.kernel.exception.CompanyMaxUsersException;
import com.liferay.portal.kernel.exception.CookieNotSupportedException;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PasswordExpiredException;
import com.liferay.portal.kernel.exception.UserActiveException;
import com.liferay.portal.kernel.exception.UserEmailAddressException;
import com.liferay.portal.kernel.exception.UserIdException;
import com.liferay.portal.kernel.exception.UserIpException;
import com.liferay.portal.kernel.exception.UserLockoutException;
import com.liferay.portal.kernel.exception.UserPasswordException;
import com.liferay.portal.kernel.exception.UserScreenNameException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.auth.AuthException;
import com.liferay.portal.kernel.security.auth.session.AuthenticatedSessionManager;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PrefsPropsUtil;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Peter Fellwock
 */
@Component(
	property = {
		"javax.portlet.name=" + LoginPortletKeys.FAST_LOGIN,
		"javax.portlet.name=" + LoginPortletKeys.LOGIN,
		"mvc.command.name=/login/login"
	},
	service = MVCActionCommand.class
)
public class LoginMVCActionCommand extends BaseMVCActionCommand {

	public String getClientIpAddress(HttpServletRequest httpServletRequest) {
		String ip = httpServletRequest.getHeader("X-Forwarded-For");

		if ((ip == null) || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = httpServletRequest.getHeader("Proxy-Client-IP");
		}

		if ((ip == null) || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = httpServletRequest.getHeader("WL-Proxy-Client-IP");
		}

		if ((ip == null) || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = httpServletRequest.getHeader("HTTP_X_FORWARDED_FOR");
		}

		if ((ip == null) || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = httpServletRequest.getHeader("HTTP_X_FORWARDED");
		}

		if ((ip == null) || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = httpServletRequest.getHeader("HTTP_CLIENT_IP");
		}

		if ((ip == null) || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
			ip = httpServletRequest.getRemoteAddr();
		}

		if ((ip != null) && ip.contains(",")) {
			ip = ip.split(",")[0].trim();
		}

		return ip;
	}

	protected void checkCaptcha(ActionRequest actionRequest)
		throws CaptchaConfigurationException, CaptchaException {

		CaptchaUtil.check(actionRequest);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (PropsValues.AUTH_LOGIN_DISABLED) {
			actionResponse.sendRedirect(
				themeDisplay.getPathMain() +
					PropsValues.AUTH_LOGIN_DISABLED_PATH);

			return;
		}

		/*if (actionRequest.getRemoteUser() != null) {
			actionResponse.sendRedirect(themeDisplay.getPathMain());

			return;
		}*/

		try {
			checkCaptcha(actionRequest);

			login(themeDisplay, actionRequest, actionResponse);

			boolean doActionAfterLogin = ParamUtil.getBoolean(
				actionRequest, "doActionAfterLogin");

			if (doActionAfterLogin) {
				LiferayPortletResponse liferayPortletResponse =
					_portal.getLiferayPortletResponse(actionResponse);

				PortletURL renderURL = liferayPortletResponse.createRenderURL();

				renderURL.setParameter(
					"mvcRenderCommandName", "/login/login_redirect");

				actionRequest.setAttribute(
					WebKeys.REDIRECT, renderURL.toString());
			}
		}
		catch (Exception exception) {
			if (exception instanceof AuthException) {
				Throwable throwable = exception.getCause();

				if (throwable instanceof PasswordExpiredException ||
					throwable instanceof UserLockoutException) {

					SessionErrors.add(
						actionRequest, throwable.getClass(), throwable);
				}
				else {
					if (_log.isInfoEnabled()) {
						_log.info("Authentication failed");
					}

					SessionErrors.add(actionRequest, exception.getClass());
				}
			}
			else if (exception instanceof CompanyMaxUsersException ||
					 exception instanceof CookieNotSupportedException ||
					 exception instanceof NoSuchUserException ||
					 exception instanceof PasswordExpiredException ||
					 exception instanceof UserActiveException ||
					 exception instanceof UserEmailAddressException ||
					 exception instanceof UserIdException ||
					 exception instanceof UserIpException ||
					 exception instanceof UserLockoutException ||
					 exception instanceof UserPasswordException ||
					 exception instanceof UserScreenNameException) {

				SessionErrors.add(
					actionRequest, exception.getClass(), exception);
			}
			else if (exception instanceof CaptchaException) {
				SessionErrors.add(actionRequest, exception.getClass());
			}
			else {
				_log.error(exception, exception);

				_portal.sendError(exception, actionRequest, actionResponse);

				return;
			}

			postProcessAuthFailure(actionRequest, actionResponse);

			hideDefaultErrorMessage(actionRequest);
		}
	}

	protected String getCompleteRedirectURL(
		HttpServletRequest httpServletRequest, String redirect) {

		HttpSession session = httpServletRequest.getSession();

		Boolean httpsInitial = (Boolean)session.getAttribute(
			WebKeys.HTTPS_INITIAL);

		String portalURL = null;

		if (PropsValues.COMPANY_SECURITY_AUTH_REQUIRES_HTTPS &&
			!PropsValues.SESSION_ENABLE_PHISHING_PROTECTION &&
			(httpsInitial != null) && !httpsInitial.booleanValue()) {

			portalURL = _portal.getPortalURL(httpServletRequest, false);
		}
		else {
			portalURL = _portal.getPortalURL(httpServletRequest);
		}

		return portalURL.concat(redirect);
	}

	protected void login(
			ThemeDisplay themeDisplay, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		HttpServletRequest httpServletRequest =
			_portal.getOriginalServletRequest(
				_portal.getHttpServletRequest(actionRequest));

		long companyId = _portal.getCompanyId(actionRequest);

		String userIp = getClientIpAddress(httpServletRequest);

		String[] disallowIps = PrefsPropsUtil.getStringArray(
			companyId, PropsKeys.AUTH_LOGIN_DISALLOW_IPS, StringPool.NEW_LINE);

		Set<String> hostsAllowed = new HashSet<>(Arrays.asList(disallowIps));

		if (!hostsAllowed.isEmpty() &&
			AccessControlUtil.isAccessAllowed(userIp, hostsAllowed)) {
			_audit(actionRequest, themeDisplay, userIp, companyId);

			throw new UserIpException();
		}

		if (!themeDisplay.isSignedIn()) {
			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(actionResponse);

			String login = ParamUtil.getString(actionRequest, "login");
			String password = actionRequest.getParameter("password");
			boolean rememberMe = ParamUtil.getBoolean(
				actionRequest, "rememberMe");

			PortletPreferences portletPreferences =
				PortletPreferencesFactoryUtil.getStrictPortletSetup(
					themeDisplay.getLayout(),
					_portal.getPortletId(actionRequest));

			String authType = portletPreferences.getValue("authType", null);

			_authenticatedSessionManager.login(
				httpServletRequest, httpServletResponse, login, password,
				rememberMe, authType);
		}

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		String mainPath = themeDisplay.getPathMain();

		if (PropsValues.PORTAL_JAAS_ENABLE) {
			if (Validator.isNotNull(redirect)) {
				redirect = StringBundler.concat(
					mainPath, "/portal/protected?redirect=",
					URLCodec.encodeURL(redirect));
			}
			else {
				redirect = mainPath.concat("/portal/protected");
			}

			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(actionResponse);

			httpServletResponse.sendRedirect(redirect);

			return;
		}

		if (Validator.isNotNull(redirect)) {
			if (!themeDisplay.isSignedIn()) {
				actionRequest.setAttribute(
					WebKeys.REDIRECT,
					_http.addParameter(
						_portal.getPathMain() + "/portal/login", "redirect",
						redirect));

				return;
			}

			redirect = _portal.escapeRedirect(redirect);

			if (Validator.isNotNull(redirect) &&
				!redirect.startsWith(Http.HTTP)) {

				redirect = getCompleteRedirectURL(httpServletRequest, redirect);
			}
		}

		if (Validator.isNotNull(redirect)) {
			actionResponse.sendRedirect(redirect);
		}
		else {
			boolean doActionAfterLogin = ParamUtil.getBoolean(
				actionRequest, "doActionAfterLogin");

			if (doActionAfterLogin) {
				return;
			}

			actionResponse.sendRedirect(mainPath);
		}
	}

	protected void postProcessAuthFailure(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		LiferayPortletRequest liferayPortletRequest =
			_portal.getLiferayPortletRequest(actionRequest);

		String portletName = liferayPortletRequest.getPortletName();

		Layout layout = (Layout)actionRequest.getAttribute(WebKeys.LAYOUT);

		PortletURL portletURL = PortletURLFactoryUtil.create(
			actionRequest, liferayPortletRequest.getPortlet(), layout,
			PortletRequest.RENDER_PHASE);

		portletURL.setParameter("saveLastPath", Boolean.FALSE.toString());

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		String login = ParamUtil.getString(actionRequest, "login");

		if (Validator.isNotNull(login)) {
			SessionErrors.add(actionRequest, "login", login);
		}

		if (portletName.equals(LoginPortletKeys.LOGIN)) {
			portletURL.setWindowState(WindowState.MAXIMIZED);
		}
		else {
			portletURL.setWindowState(actionRequest.getWindowState());
		}

		actionResponse.sendRedirect(portletURL.toString());
	}

	private void _audit(ActionRequest actionRequest, ThemeDisplay themeDisplay, String userIp, long companyId) {
		try {
			String login = ParamUtil.getString(actionRequest, "login");

			PortletPreferences portletPreferences =
					PortletPreferencesFactoryUtil.getStrictPortletSetup(
							themeDisplay.getLayout(),
							_portal.getPortletId(actionRequest));

			String authType = portletPreferences.getValue("authType", null);

			if (Validator.isNull(authType)) {
				Company company = _portal.getCompany(actionRequest);
				authType = company.getAuthType();
			}

			StringBuilder sb = new StringBuilder();

			sb.append("Someone with");
			sb.append(StringPool.SPACE);
			sb.append(authType);
			sb.append(StringPool.SPACE);
			sb.append(login);
			sb.append(StringPool.SPACE);
			sb.append("tried to login with banned ip");
			sb.append(StringPool.PERIOD);

			JSONObject additionalInfoJSONObject = JSONUtil.put(
					"login", login
			).put(
					"ip", userIp
			).put(
					"authType", authType
			);

			AuditMessage auditMessage = new AuditMessage(
					EventTypes.LOGIN_FAILURE, companyId, 0,
					"", HttpServletRequest.class.getName(),
					"0", sb.toString(),
					additionalInfoJSONObject);

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LoginMVCActionCommand.class);

	@Reference
	private AuthenticatedSessionManager _authenticatedSessionManager;

	@Reference
	private Http _http;

	@Reference
	private Portal _portal;

	@Reference
	private AuditRouter _auditRouter;

}