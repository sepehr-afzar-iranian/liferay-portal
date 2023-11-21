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

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.util;

import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.validator.DDMFormValuesValidationException;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author S.Abolfazl Eftekhari
 */
@Component(immediate = true, service = DDMFormUniqueFieldSubmitChecker.class)
public class DDMFormUniqueFieldSubmitChecker {

	public void check(DDMFormFieldValue fieldValue) throws PortalException {
		Value getFieldValue = fieldValue.getValue();

		Map<Locale, String> getFieldValues = getFieldValue.getValues();

		Collection<String> getFieldValueCollection = getFieldValues.values();

		String currentValue = String.valueOf(
			getFieldValueCollection.toArray()[0]);

		String fieldReference = fieldValue.getFieldReference();

		DDMFormField getDDMFormField = fieldValue.getDDMFormField();

		LocalizedValue getLabel = getDDMFormField.getLabel();

		Map<Locale, String> getLabelValues = getLabel.getValues();

		Collection<String> getLableValuesCollection = getLabelValues.values();

		String fieldName = String.valueOf(
			getLableValuesCollection.toArray()[0]);

		boolean unique = _ddmFormUniqueFieldChecker.check(
			currentValue, fieldReference);

		if (unique) {
			throw new DDMFormValuesValidationException.UniqueValue(
				fieldName, currentValue);
		}
	}

	@Reference
	private DDMFormUniqueFieldChecker _ddmFormUniqueFieldChecker;

}