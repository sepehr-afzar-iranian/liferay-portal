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

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueValidationException;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueValidator;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.Value;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Yousef Ghadiri
 */
@Component(
	immediate = true, property = "ddm.form.field.type.name=license_plate",
	service = DDMFormFieldValueValidator.class
)
public class LicensePlateDDMFormFieldValueValidator
	implements DDMFormFieldValueValidator {

	@Override
	public void validate(DDMFormField ddmFormField, Value value)
		throws DDMFormFieldValueValidationException {

		Map<Locale, String> valuesMap = value.getValues();

		Collection<String> values = valuesMap.values();

		boolean doAdvanceValidate = Boolean.parseBoolean(
			String.valueOf(ddmFormField.getProperty("validateIranianFormat")));

		for (String str : values) {
			if (str.isEmpty()) {
				throw new DDMFormFieldValueValidationException(
					"license plate value is empty");
			}

			if (!LicensePlateDDMFormFieldUtil.simplePlateValidator(str)) {
				throw new DDMFormFieldValueValidationException(
					"invalid license plate value is not valid");
			}

			if (doAdvanceValidate &&
				!LicensePlateDDMFormFieldUtil.advancePlateValidator(str)) {

				throw new DDMFormFieldValueValidationException(
					"invalid license plate value is not completly valid");
			}
		}
	}

}