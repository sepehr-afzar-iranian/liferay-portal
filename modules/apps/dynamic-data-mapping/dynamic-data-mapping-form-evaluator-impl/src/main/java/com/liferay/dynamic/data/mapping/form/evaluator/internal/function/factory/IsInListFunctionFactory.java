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

package com.liferay.dynamic.data.mapping.form.evaluator.internal.function.factory;

import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunction;
import com.liferay.dynamic.data.mapping.expression.DDMExpressionFunctionFactory;
import com.liferay.dynamic.data.mapping.form.evaluator.internal.function.IsInListFunction;

import org.osgi.service.component.annotations.Component;

/**
 * @author Yousef Ghadiri
 */
@Component(
	property = "name=" + IsInListFunction.NAME,
	service = DDMExpressionFunctionFactory.class
)
public class IsInListFunctionFactory implements DDMExpressionFunctionFactory {

	@Override
	public DDMExpressionFunction create() {
		return _IS_IN_LIST_FUNCTION;
	}

	private static final IsInListFunction _IS_IN_LIST_FUNCTION =
		new IsInListFunction();

}