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

import com.liferay.dynamic.data.mapping.annotations.DDMForm;
import com.liferay.dynamic.data.mapping.annotations.DDMFormField;
import com.liferay.dynamic.data.mapping.annotations.DDMFormLayout;
import com.liferay.dynamic.data.mapping.annotations.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.annotations.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.annotations.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.annotations.DDMFormRule;
import com.liferay.dynamic.data.mapping.form.field.type.DefaultDDMFormFieldTypeSettings;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldValidation;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;

/**
 * @author Yousef Ghadiri
 */
@DDMForm(
	rules = {
		@DDMFormRule(
			actions = {
				"setValidationDataType('validation', 'string')",
				"setValidationFieldName('validation', getValue('name'))"
			}
		)
	}
)
@DDMFormLayout(
	paginationMode = com.liferay.dynamic.data.mapping.model.DDMFormLayout.TABBED_MODE,
	value = {
		@DDMFormLayoutPage(
			title = "%basic",
			value = {
				@DDMFormLayoutRow(
					{
						@DDMFormLayoutColumn(
							{
								"label", "placeholder", "tip", "required",
								"validateIranianFormat"
							}
						)
					}
				)
			}
		),
		@DDMFormLayoutPage(
			title = "%advanced",
			value = {
				@DDMFormLayoutRow(
					{
						@DDMFormLayoutColumn(
							{
								"name", "fieldReference", "predefinedValue",
								"visibilityExpression", "fieldNamespace",
								"indexType", "localizable", "readOnly",
								"dataType", "type", "showLabel", "repeatable",
								"validation"
							}
						)
					}
				)
			}
		)
	}
)
public interface LicensePlateDDMFormFieldTypeSettings
	extends DefaultDDMFormFieldTypeSettings {

	@DDMFormField(
		label = "%searchable", optionLabels = {"%disable", "%keyword", "%text"},
		optionValues = {"none", "keyword", "text"}, predefinedValue = "keyword",
		type = "radio"
	)
	@Override
	public String indexType();

	@DDMFormField(
		dataType = "string", label = "%placeholder-text",
		properties = "tooltip=%enter-placeholder-for-license-plate-field",
		type = "text"
	)
	public LocalizedValue placeholder();

	@DDMFormField(
		label = "%validate-iranian-format", properties = "showAsSwitcher=true"
	)
	public boolean validateIranianFormat();

	@DDMFormField(
		dataType = "string", label = "%validation", type = "validation"
	)
	@Override
	public DDMFormFieldValidation validation();

}