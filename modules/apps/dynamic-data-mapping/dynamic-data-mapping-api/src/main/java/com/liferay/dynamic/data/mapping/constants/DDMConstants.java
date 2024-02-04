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

package com.liferay.dynamic.data.mapping.constants;

import com.liferay.dynamic.data.mapping.model.DDMFormFieldType;

/**
 * @author Lino Alves
 */
public class DDMConstants {

	public static final String ADVANCED_FORM_BUILDER = "advanced.form.builder";

	public static final String ADVANCED_FORM_BUILDER_LIST_VALIDATION = "advanced.form.builder.validation.list";

	public static final String ADVANCED_FORM_BUILDER_PRICE_FIELD = "advanced.form.builder.pricefield";

	public static final String ADVANCED_FORM_BUILDER_UNIQUE_FIELD = "advanced.form.builder.uniquefield";

	public static final String ADVANCED_FORM_BUILDER_CONFIRM_ON_SUBMIT = "advanced.form.builder.confirmonsubmit";

	public static final String ADVANCED_FORM_BUILDER_EMAIL_NOTIFICATION = "advanced.form.builder.email.notification";

	public static final String ADVANCED_FORM_BUILDER_REGEX_VALIDATION = "advanced.form.builder.validation.regex";

	public static final String ADVANCED_FORM_BUILDER_DATA_PROVIDER = "advanced.form.builder.validation.dataprovider";

	public static final String AVAILABLE_FIELDS =
		"Liferay.FormBuilder.AVAILABLE_FIELDS.DDM_STRUCTURE";

	public static final String EXPRESSION_FUNCTION_FACTORY_NAME =
		"com.liferay.dynamic.data.mapping.expression." +
			"DDMExpressionFunctionFactory";

	public static final String RESOURCE_NAME =
		"com.liferay.dynamic.data.mapping";

	public static final String SERVICE_NAME =
		"com.liferay.dynamic.data.mapping";

	public static final String[] SUPPORTED_DDM_FORM_FIELD_TYPES = {
		DDMFormFieldType.CHECKBOX, DDMFormFieldType.CHECKBOX_MULTIPLE,
		DDMFormFieldType.COLOR, DDMFormFieldType.DATE, DDMFormFieldType.DECIMAL,
		DDMFormFieldType.DOCUMENT_LIBRARY, DDMFormFieldType.FIELDSET,
		DDMFormFieldType.GEOLOCATION, DDMFormFieldType.GRID,
		DDMFormFieldType.IMAGE, DDMFormFieldType.INTEGER,
		DDMFormFieldType.JOURNAL_ARTICLE, DDMFormFieldType.LINK_TO_PAGE,
		DDMFormFieldType.LOCALIZABLE_TEXT, DDMFormFieldType.NUMBER,
		DDMFormFieldType.NUMERIC, DDMFormFieldType.PASSWORD,
		DDMFormFieldType.RADIO, DDMFormFieldType.SELECT,
		DDMFormFieldType.SEPARATOR, DDMFormFieldType.TEXT,
		DDMFormFieldType.TEXT_AREA, DDMFormFieldType.TEXT_HTML
	};

}