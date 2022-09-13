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

package com.liferay.oauth2.provider.internal.test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author Arthur Chan
 */
public class TestPasswordAuthorizationGrant implements TestAuthorizationGrant {

	public TestPasswordAuthorizationGrant(String userName, String password) {
		_authorizationGrantData.add("grant_type", "password");
		_authorizationGrantData.add("password", password);
		_authorizationGrantData.add("username", userName);
	}

	@Override
	public MultivaluedMap<String, String> getAuthorizationGrantParameters() {
		return _authorizationGrantData;
	}

	private final MultivaluedMap<String, String> _authorizationGrantData =
		new MultivaluedHashMap<>();

}