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

package com.liferay.portal.servlet.filters.healthcheckdatasource;

import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.util.PrefsPropsUtil;
import com.liferay.portal.util.PropsValues;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Yousef Ghadiri
 */
public class DatabaseHealthCheckDataSourceFilter extends BasePortalFilter {

	@Override
	public void destroy() {
	}

	@Override
	public boolean isFilterEnabled() {
		return true;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		if (!PrefsPropsUtil.getBoolean(
				PropsKeys.DATABASE_HEALTH_CHECK_FILTER_ENABLED, PropsValues.DATABASE_HEALTH_CHECK_FILTER_ENABLED)) {
			filterChain.doFilter(httpServletRequest, httpServletResponse);
			return;
		}

		DataSource dataSource = InfrastructureUtil.getDataSource();

		try (Connection connection = dataSource.getConnection()) {
			if (connection.isValid(0)) {
				filterChain.doFilter(httpServletRequest, httpServletResponse);
			}
			else {
				_writeMessage(httpServletResponse, httpServletRequest);
			}
		}
		catch (SQLException sqlException) {
			_writeMessage(httpServletResponse, httpServletRequest);
		}
	}

	private void _writeMessage(
			HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest)
		throws Exception {

		Theme theme = (Theme)httpServletRequest.getAttribute(
				WebKeys.SERVLET_CONTEXT_INCLUDE_FILTER_THEME);

		httpServletRequest.setAttribute(WebKeys.THEME, theme);

		FilterConfig filterConfig = getFilterConfig();

		ServletContext servletContext = filterConfig.getServletContext();

		RequestDispatcher requestDispatcher =
				servletContext.getRequestDispatcher(
						"/WEB-INF/jsp/_database_health_check_error.jsp");

		requestDispatcher.forward(httpServletRequest, httpServletResponse);
	}

}