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

package com.liferay.portal.security.audit.storage.internal.search;

import com.liferay.portal.kernel.search.Field;

/** @author Fatemeh Akbari */
public class AuditField extends Field {

	public static final String EVENT_TYPE = "eventType";

	public static final String MESSAGE = "message";

	public static final String CLIENT_HOST = "clientHost";

	public static final String CLIENT_IP = "clientIP";

	public static final String SERVER_NAME = "serverName";

	public static final String SERVER_PORT = "serverPort";

	public static final String SESSION_ID = "sessionID";

	public AuditField(String name) {
		super(name);
	}

}