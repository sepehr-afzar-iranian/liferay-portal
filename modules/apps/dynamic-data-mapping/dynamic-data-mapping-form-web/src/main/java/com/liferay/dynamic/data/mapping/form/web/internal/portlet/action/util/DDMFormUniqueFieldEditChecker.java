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

import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.ParamUtil;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author S.Abolfazl Eftekhari
 */
@Component(immediate = true, service = DDMFormUniqueFieldEditChecker.class)
public class DDMFormUniqueFieldEditChecker {

	public void check(
			ActionRequest actionRequest, DDMFormFieldValue currentValue)
		throws PortalException {

		long formInstanceRecordId = ParamUtil.getLong(
			actionRequest, "formInstanceRecordId");

		String currentReference = currentValue.getFieldReference();

		DDMFormInstanceRecord ddmFormInstanceRecord =
			_ddmFormInstanceRecordLocalService.getDDMFormInstanceRecord(
				formInstanceRecordId);

		DDMFormValues ddmFormValues = ddmFormInstanceRecord.getDDMFormValues();

		List<DDMFormFieldValue> ddmFormInstanceRecords =
			ddmFormValues.getDDMFormFieldValues();

		Value getCurrentValue = currentValue.getValue();

		Map<Locale, String> getCurrentValues = getCurrentValue.getValues();

		Collection<String> getCurrentValuesCollection =
			getCurrentValues.values();

		String currentValue_ = String.valueOf(
			getCurrentValuesCollection.toArray()[0]);

		String fieldReference = "";
		boolean stillUnique = true;

		for (DDMFormFieldValue fieldValue : ddmFormInstanceRecords) {
			Value getPrevFieldValue = fieldValue.getValue();

			Map<Locale, String> getPrevFieldValues =
				getPrevFieldValue.getValues();

			Collection<String> getPrevFieldValuesCollection =
				getPrevFieldValues.values();

			String prevValue = String.valueOf(
				getPrevFieldValuesCollection.toArray()[0]);

			fieldReference = fieldValue.getFieldReference();

			if (!currentValue_.equals(prevValue) &&
				fieldReference.equals(currentReference)) {

				stillUnique = false;

				break;
			}
		}

		if (!stillUnique &&
			_ddmFormUniqueFieldChecker.check(currentValue_, fieldReference)) {

			throw new PortalException("This-Value-is-already-used");
		}
	}

	@Reference
	private DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;

	@Reference
	private DDMFormUniqueFieldChecker _ddmFormUniqueFieldChecker;

}