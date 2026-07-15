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

package com.liferay.portal.security.content.security.policy.internal.servlet.filter;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.content.security.policy.internal.configuration.ContentSecurityPolicyConfiguration;
import com.liferay.portal.servlet.filters.BasePortalFilter;

import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Olivér Kecskeméty
 */
@Component(
	configurationPid = "com.liferay.portal.security.auth.verifier.internal.configuration.ContentSecurityPolicyConfiguration",
	property = {
		"after-filter=Portal CORS Servlet Filter", "dispatcher=FORWARD",
		"dispatcher=REQUEST", "servlet-context-name=",
		"servlet-filter-name=Content Security Policy Filter", "url-pattern=/*"
	},
	service = Filter.class
)
public class ContentSecurityPolicyFilter extends BasePortalFilter {

	@Override
	public boolean isFilterEnabled(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		if (CompanyThreadLocal.getCompanyId() == 0) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Content security policy will not be applied to this " +
						"request for company ID 0");
			}

			return false;
		}

		System.out.println(!_contentSecurityPolicyConfiguration.enabled());

		if (!_contentSecurityPolicyConfiguration.enabled() ||
			Validator.isNull(_contentSecurityPolicyConfiguration.policy()) ||
			_isExcludedURIPath(
				_contentSecurityPolicyConfiguration, httpServletRequest)) {

			return false;
		}

		return true;
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_contentSecurityPolicyConfiguration =
			ConfigurableUtil.createConfigurable(
				ContentSecurityPolicyConfiguration.class, properties);
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		try {
			httpServletResponse.setContentType("text/html; charset=UTF-8");

			String policy = _contentSecurityPolicyConfiguration.policy();

			if (_contentSecurityPolicyConfiguration.reportOnly()) {
				httpServletResponse.setHeader(
					"Content-Security-Policy-Report-Only", policy);
			}
			else {
				httpServletResponse.setHeader(
					"Content-Security-Policy", policy);
			}

			if (httpServletRequest.isSecure()) {
				httpServletResponse.setHeader(
					"Strict-Transport-Security",
					"max-age=31536000; includeSubDomains");
			}

			httpServletRequest.setAttribute(
				"Referrer-Policy", "strict-origin-when-cross-origin");

			filterChain.doFilter(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception.getMessage(), exception);
			}
		}
	}

	private boolean _isExcludedURIPath(
		ContentSecurityPolicyConfiguration contentSecurityPolicyConfiguration,
		HttpServletRequest httpServletRequest) {

		String requestURI = httpServletRequest.getRequestURI();

		if (Validator.isNotNull(requestURI)) {
			requestURI = StringUtil.toLowerCase(requestURI);

			for (String internallyExcludedPath : _INTERNALLY_EXCLUDED_PATHS) {
				if (Validator.isNotNull(internallyExcludedPath) &&
					requestURI.startsWith(
						StringUtil.toLowerCase(internallyExcludedPath))) {

					return true;
				}
			}
		}

		requestURI = GetterUtil.getString(
			httpServletRequest.getAttribute(
				JavaConstants.JAVAX_SERVLET_FORWARD_REQUEST_URI),
			requestURI);

		if (Validator.isNull(requestURI)) {
			return false;
		}

		requestURI = StringUtil.toLowerCase(requestURI);

		for (String excludedPath :
				contentSecurityPolicyConfiguration.excludedPaths()) {

			if (Validator.isNotNull(excludedPath) &&
				requestURI.startsWith(StringUtil.toLowerCase(excludedPath))) {

				return true;
			}
		}

		return false;
	}

	private static final String[] _INTERNALLY_EXCLUDED_PATHS = {
		"/group/", "/user/", "/web/"
	};

	private static final Log _log = LogFactoryUtil.getLog(
		ContentSecurityPolicyFilter.class);

	private volatile ContentSecurityPolicyConfiguration
		_contentSecurityPolicyConfiguration;

}