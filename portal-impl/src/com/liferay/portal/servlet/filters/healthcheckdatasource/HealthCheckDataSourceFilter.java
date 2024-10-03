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

import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.servlet.filters.BasePortalFilter;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.sql.DataSource;

/**
 * @author Shuyang Zhou
 */
public class HealthCheckDataSourceFilter extends BasePortalFilter {

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

		// curl -i http://localhost:8080/health_check/data_source

		DataSource dataSource = InfrastructureUtil.getDataSource();

		try (Connection connection = dataSource.getConnection()) {
			if (connection.isValid(0)) {
				_writeMessage(
					httpServletResponse, HttpServletResponse.SC_OK,
					"Portal is healthy.");
			}
			else {
				_writeMessage(
					httpServletResponse,
					HttpServletResponse.SC_SERVICE_UNAVAILABLE,
					"Portal is not healthy.");
			}
		}
		catch (SQLException sqlException) {
			_writeMessage(
				httpServletResponse,
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				sqlException.getMessage());
		}
	}

	private void _writeMessage(
			HttpServletResponse httpServletResponse, int status, String message)
		throws IOException {

		httpServletResponse.setStatus(status);

		try (PrintWriter printWriter = httpServletResponse.getWriter()) {
			printWriter.println(message);
		}
	}

}