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

import java.util.Arrays;
import java.util.List;

/**
 * @author Yousef Ghadiri
 */
public class IsInListFunction
	implements DDMExpressionFunction.Function2<String, String, Boolean> {

	public static final String NAME = "isInList";

	@Override
	public Boolean apply(String value, String list) {
		String[] listOfValues = list.split("\n");

		for (int i = 0; i < listOfValues.length; i++) {
			listOfValues[i] = listOfValues[i].trim();
		}

		List<String> seperatedValues = Arrays.asList(listOfValues);

		if (seperatedValues.contains(value.trim())) {
			return true;
		}

		return false;
	}

	@Override
	public String getName() {
		return NAME;
	}

}