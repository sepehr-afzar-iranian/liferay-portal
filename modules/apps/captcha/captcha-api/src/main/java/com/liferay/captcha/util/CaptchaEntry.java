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

package com.liferay.captcha.util;

import java.io.Serializable;

/**
 * @author Brian Wing Shun Chan
 * @author Pei-Jung Lan
 */
public class CaptchaEntry implements Serializable {

	public CaptchaEntry(String text) {
		_text = text;

		_createdAt = System.currentTimeMillis();
	}

	public String getText() {
		return _text;
	}

	public boolean isExpired(long ttlMillis) {
		if ((System.currentTimeMillis() - _createdAt) > ttlMillis) {
			return true;
		}

		return false;
	}

	private static final long serialVersionUID = 1L;

	private final long _createdAt;
	private final String _text;

}