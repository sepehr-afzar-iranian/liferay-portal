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

package com.liferay.dynamic.data.mapping.form.field.type.internal.license.plate;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Yousef Ghadiri
 */
@Component(
	immediate = true, property = "ddm.form.field.type.name=license_plate",
	service = {
		DDMFormFieldTemplateContextContributor.class,
		LicensePlateDDMFormFieldTemplateContextContributor.class
	}
)
public class LicensePlateDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public Map<String, Object> getParameters(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		Map<String, Object> parameters = new HashMap<>();

		if (ddmFormFieldRenderingContext.isReturnFullContext()) {
			parameters.put(
				"placeholder",
				getPlaceholder(ddmFormField, ddmFormFieldRenderingContext));
			parameters.put(
				"validateIranianFormat", isValidateIranianFormat(ddmFormField));
		}

		String predefinedValue = getPredefinedValue(
			ddmFormField, ddmFormFieldRenderingContext);

		if (predefinedValue != null) {
			parameters.put("predefinedValue", predefinedValue);
		}

		String value = getValue(ddmFormFieldRenderingContext);

		if (Validator.isNotNull(value)) {
			parameters.put("value", value);

			if (isValidateIranianFormat(ddmFormField)) {
				parameters.put(
					"isValidFormat",
					LicensePlateDDMFormFieldUtil.advancePlateValidator(value));
				parameters.put("validateIranianFormat", true);
			}
			else {
				parameters.put(
					"isValidFormat",
					LicensePlateDDMFormFieldUtil.simplePlateValidator(value));
				parameters.put("validateIranianFormat", false);
			}
		}

		return parameters;
	}

	protected String getPlaceholder(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		LocalizedValue placeholder = (LocalizedValue)ddmFormField.getProperty(
			"placeholder");

		return getValueString(
			placeholder, ddmFormFieldRenderingContext.getLocale(),
			ddmFormFieldRenderingContext);
	}

	protected String getPredefinedValue(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		LocalizedValue predefinedValue = ddmFormField.getPredefinedValue();

		if (predefinedValue == null) {
			return null;
		}

		return predefinedValue.getString(
			ddmFormFieldRenderingContext.getLocale());
	}

	protected String getValue(
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		String value = String.valueOf(
			ddmFormFieldRenderingContext.getProperty("value"));

		if (ddmFormFieldRenderingContext.isViewMode()) {
			value = HtmlUtil.extractText(value);
		}

		return value;
	}

	protected String getValueString(
		Value value, Locale locale,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		if (value == null) {
			return StringPool.BLANK;
		}

		return value.getString(locale);
	}

	protected boolean isValidateIranianFormat(DDMFormField ddmFormField) {
		return GetterUtil.getBoolean(
			ddmFormField.getProperty("validateIranianFormat"), true);
	}

}