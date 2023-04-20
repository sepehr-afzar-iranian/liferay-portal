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

package com.liferay.dynamic.data.mapping.form.evaluator.internal.function;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunction;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author Yousef Ghadiri
 */
public class IsHomePhoneNumberFunction
	implements DDMExpressionFunction.Function1<String, Boolean> {

	public static final String NAME = "isHomePhoneNumber";

	@Override
	public Boolean apply(String parameter) {
		return Stream.of(
			StringUtil.split(parameter, CharPool.COMMA)
		).map(
			String::trim
		).allMatch(
			this::_isHomePhoneNumber
		);
	}

	private boolean _isHomePhoneNumber(String homePhoneNumber) {
		if (Validator.isNull(homePhoneNumber))

			return false;

		Matcher matcher = _homePhoneNumberPattern.matcher(homePhoneNumber);

		return matcher.matches();
	}

	private static final Pattern _homePhoneNumberPattern = Pattern.compile(
		"^0\\d{2,}\\d{8,}$");

	@Override
	public String getName() {
		return NAME;
	}

}