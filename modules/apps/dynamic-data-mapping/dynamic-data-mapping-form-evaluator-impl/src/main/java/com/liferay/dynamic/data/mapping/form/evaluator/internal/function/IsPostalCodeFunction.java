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
public class IsPostalCodeFunction
	implements DDMExpressionFunction.Function1<String, Boolean> {

	public static final String NAME = "isPostalCode";

	@Override
	public Boolean apply(String parameter) {
		return Stream.of(
			StringUtil.split(parameter, CharPool.COMMA)
		).map(
			String::trim
		).allMatch(
			this::_isPostalCode
		);
	}

	private boolean _isPostalCode(String isPostalCode) {
		if (Validator.isNull(isPostalCode)) {
			return false;
		}

		Matcher matcher = _isPostalCodePattern.matcher(isPostalCode);

		return matcher.matches();
	}

	private static final Pattern _isPostalCodePattern = Pattern.compile(
		"\\b(?!(\\d)\\1{3})[13-9]{4}[1346-9][013-9]{5}\\b");

	@Override
	public String getName() {
		return NAME;
	}

}