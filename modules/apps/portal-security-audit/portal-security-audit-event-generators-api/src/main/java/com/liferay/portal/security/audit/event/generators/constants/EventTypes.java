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

package com.liferay.portal.security.audit.event.generators.constants;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 */
@ProviderType
public interface EventTypes {

	public static final String ADD = "ADD";

	public static final String ALL = "";

	public static final String ASSIGN = "ASSIGN";

	public static final String CONFIGURATION_SAVE = "SAVE CONFIGURATION";

	public static final String DELETE = "DELETE";

	public static final String IMPERSONATE = "IMPERSONATE";

	public static final String LOGIN = "LOGIN";

	public static final String LOGIN_FAILURE = "LOGIN_FAILURE";

	public static final String LOGOUT = "LOGOUT";

	public static final String CHECK_AUDIT = "CHECK AUDIT";

	public static final String PERMISSION_UPDATE = "UPDATE PERMISSION";

	public static final String UNASSIGN = "UNASSIGN";

	public static final String UPDATE = "UPDATE";

}