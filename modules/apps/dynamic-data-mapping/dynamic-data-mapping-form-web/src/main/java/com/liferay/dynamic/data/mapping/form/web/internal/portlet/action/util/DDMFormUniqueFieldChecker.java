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

import com.liferay.dynamic.data.mapping.model.DDMContent;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMContentLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.validator.DDMFormValuesValidationException;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.ParamUtil;

import java.io.IOException;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author S.Abolfazl Eftekhari
 */
@Component(immediate = true, service = DDMFormUniqueFieldChecker.class)
public class DDMFormUniqueFieldChecker {

	public void checkForAdd(DDMFormFieldValue fieldValue)
		throws PortalException {

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

		String fieldLabel = String.valueOf(
			getLableValuesCollection.toArray()[0]);

		if (checkForUnique(currentValue, fieldReference)) {
			throw new DDMFormValuesValidationException.UniqueValue(
				fieldLabel, currentValue);
		}
	}

	public void checkForEdit(
			ActionRequest actionRequest, ActionResponse actionResponse,
			DDMFormFieldValue currentValue, DDMFormInstance ddmFormInstance)
		throws IOException, PortalException {

		long formInstanceRecordId = ParamUtil.getLong(
			actionRequest, "formInstanceRecordId");

		String currentFieldReference = currentValue.getFieldReference();

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

		DDMFormField getDDMFormField = currentValue.getDDMFormField();

		LocalizedValue getLabel = getDDMFormField.getLabel();

		Map<Locale, String> getLabelValues = getLabel.getValues();

		Collection<String> getLableValuesCollection = getLabelValues.values();

		String fieldLabel = String.valueOf(
			getLableValuesCollection.toArray()[0]);

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
				fieldReference.equals(currentFieldReference)) {

				stillUnique = false;

				break;
			}
		}

		if (!stillUnique && checkForUnique(currentValue_, fieldReference)) {
			DDMFormInstanceSettings formInstanceSettings =
				ddmFormInstance.getSettingsModel();

			String redirectURL = ParamUtil.getString(
				actionRequest, "back", formInstanceSettings.redirectURL());

			actionResponse.sendRedirect(redirectURL);

			throw new DDMFormValuesValidationException.UniqueValue(
				fieldLabel, currentValue_);
		}
	}

	public boolean checkForUnique(String fieldValue, String fieldReference)
		throws PortalException {

		DynamicQuery dynamicQuery = _ddmContentLocalService.dynamicQuery();

		String value = String.format("%%%s%%", fieldValue);
		String reference = String.format("%%%s%%", fieldReference);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("data"));
		dynamicQuery.add(RestrictionsFactoryUtil.like("data", value));
		dynamicQuery.add(RestrictionsFactoryUtil.like("data", reference));

		List<DDMContent> oldValues = _ddmContentLocalService.dynamicQuery(
			dynamicQuery);

		for (Object oldValue : oldValues) {
			String jsonString = String.valueOf(oldValue);

			JSONObject createJSONObject = _jsonFactory.createJSONObject(
				jsonString);

			JSONArray jsonArray = createJSONObject.getJSONArray("fieldValues");

			for (int i = 0; i < jsonArray.length(); i++) {
				String jsonReference = jsonArray.getJSONObject(
					i
				).getString(
					"fieldReference"
				);

				JSONObject jsonObject = jsonArray.getJSONObject(
					i
				).getJSONObject(
					"value"
				);

				String enUsValue = jsonObject.getString("en_US");

				if (fieldValue.equals(enUsValue) &&
					fieldReference.equals(jsonReference)) {

					return true;
				}
			}
		}

		return false;
	}

	@Reference
	private DDMContentLocalService _ddmContentLocalService;

	@Reference
	private DDMFormInstanceRecordLocalService
		_ddmFormInstanceRecordLocalService;

	@Reference
	private JSONFactory _jsonFactory;

}