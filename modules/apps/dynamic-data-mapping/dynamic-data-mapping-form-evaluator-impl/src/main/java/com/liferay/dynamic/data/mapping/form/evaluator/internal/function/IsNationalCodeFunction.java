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
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author Yousef Ghadiri
 */
public class IsNationalCodeFunction
	implements DDMExpressionFunction.Function1<String, Boolean> {

	public static final String NAME = "isNationalCode";

	@Override
	public Boolean apply(String parameter) {
		return Stream.of(
			StringUtil.split(parameter, CharPool.COMMA)
		).map(
			String::trim
		).allMatch(
			this::_isNationalCode
		);
	}

	private boolean _isNationalCode(String nationalCode) {
		if (Validator.isNull(nationalCode)) {
			return false;
		}

		String addSpacePadding = String.format("%1$10s", nationalCode);

		String addZeroPadding = addSpacePadding.replace(' ', '0');

		Matcher matcher = _nationalCodePattern.matcher(nationalCode);

		if (matcher.matches() && _validateNationalCode(addZeroPadding)) {
			return true;
		}

		return false;
	}

	private boolean _validateNationalCode(String nationalCode) {
		int sum = 0;
		int controlDigit = GetterUtil.getInteger(
			String.valueOf(nationalCode.charAt(9)));

		for (int i = 8; i >= 0; i--) {
			sum +=
				GetterUtil.getInteger(String.valueOf(nationalCode.charAt(i))) *
					(10 - i);
		}

		int remnant = sum % 11;

		if (remnant < 2) {
			if (controlDigit == remnant) {
				return true;
			}

			return false;
		}

		if (controlDigit == (11 - remnant)) {
			return true;
		}

		return false;
	}

	private static final Pattern _nationalCodePattern = Pattern.compile(
		"^\\d{8,10}$");

	@Override
	public String getName() {
		return NAME;
	}

}