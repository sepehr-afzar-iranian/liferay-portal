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
import com.liferay.dynamic.data.mapping.model.DDMForm;
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
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.IOException;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author S.Abolfazl Eftekhari
 */
@Component(immediate = true, service = DDMFormUniqueFieldChecker.class)
public class DDMFormUniqueFieldChecker {

	public void checkForAdd(
			ActionRequest actionRequest, DDMFormFieldValue fieldValue)
		throws PortalException {

		DDMFormField getDDMFormField = fieldValue.getDDMFormField();

		DDMForm ddmForm = getDDMFormField.getDDMForm();

		String languageId = LanguageUtil.getLanguageId(actionRequest);

		Locale getDDMFormLangLocale = LocaleUtil.fromLanguageId(languageId);

		String fieldLang = String.valueOf(getDDMFormLangLocale);

		Value getFieldValue = fieldValue.getValue();

		Map<Locale, String> getFieldValues = getFieldValue.getValues();

		String currentValue = String.valueOf(
			getFieldValues.get(getDDMFormLangLocale));

		String fieldReference = fieldValue.getFieldReference();

		LocalizedValue getLabel = getDDMFormField.getLabel();

		Map<Locale, String> getLabelValues = getLabel.getValues();

		Collection<String> getLableValuesCollection = getLabelValues.values();

		String fieldLabel = String.valueOf(
			getLableValuesCollection.toArray()[0]);

		for (Locale locale : ddmForm.getAvailableLocales()) {
			getFieldValue.addString(locale, currentValue);
		}

		if (_isUnique(currentValue, fieldReference, fieldLang)) {
			throw new DDMFormValuesValidationException.UniqueValue(fieldLabel);
		}
	}

	public void checkForEdit(
			ActionRequest actionRequest, ActionResponse actionResponse,
			DDMFormFieldValue fieldValue, DDMFormInstance ddmFormInstance,
			long formInstanceRecordId)
		throws IOException, PortalException {

		DDMFormInstanceRecord ddmFormInstanceRecord =
			_ddmFormInstanceRecordLocalService.getDDMFormInstanceRecord(
				formInstanceRecordId);

		DDMFormValues ddmFormValues = ddmFormInstanceRecord.getDDMFormValues();

		List<DDMFormFieldValue> ddmFormInstanceRecords =
			ddmFormValues.getDDMFormFieldValues();

		String currentFieldReference = fieldValue.getFieldReference();

		DDMFormField currentDDMFormField = fieldValue.getDDMFormField();

		DDMForm ddmForm = currentDDMFormField.getDDMForm();

		String languageId = LanguageUtil.getLanguageId(actionRequest);

		Locale currentLocale = LocaleUtil.fromLanguageId(languageId);

		Value currentValue = fieldValue.getValue();

		Map<Locale, String> currentValues = currentValue.getValues();

		String other = currentValues.get(currentLocale);

		Map<String, Integer> valueCountMap = new HashMap<>();

		Collection<String> values = currentValues.values();

		values.forEach(
			element -> valueCountMap.put(
				element, valueCountMap.getOrDefault(element, 0) + 1));

		Set<Map.Entry<String, Integer>> entries = valueCountMap.entrySet();

		Stream<Map.Entry<String, Integer>> stream = entries.stream();

		String uniqueValue = stream.filter(
			entry -> entry.getValue() == 1
		).findFirst(
		).map(
			Map.Entry::getKey
		).orElse(
			other
		);

		LocalizedValue fieldLabel = currentDDMFormField.getLabel();

		String fieldLabelValue = fieldLabel.getString(currentLocale);

		String fieldReference = "";
		boolean unique = true;

		for (DDMFormFieldValue prevFieldValue : ddmFormInstanceRecords) {
			Value prevValue = prevFieldValue.getValue();

			Map<Locale, String> prevValues = prevValue.getValues();

			String prevValueString = prevValues.get(currentLocale);

			fieldReference = prevFieldValue.getFieldReference();

			if (!uniqueValue.equals(prevValueString) &&
				fieldReference.equals(currentFieldReference)) {

				unique = false;

				break;
			}
		}

		if (!unique && _isUnique(uniqueValue, fieldReference, languageId)) {
			DDMFormInstanceSettings formInstanceSettings =
				ddmFormInstance.getSettingsModel();

			String redirectURL = ParamUtil.getString(
				actionRequest, "back", formInstanceSettings.redirectURL());

			if (Validator.isNotNull(redirectURL)) {
				actionResponse.sendRedirect(redirectURL);
			}

			throw new DDMFormValuesValidationException.UniqueValue(
				fieldLabelValue);
		}

		for (Locale locale : ddmForm.getAvailableLocales()) {
			currentValue.addString(locale, uniqueValue);
		}
	}

	private boolean _isUnique(
			String fieldValue, String fieldReference, String fieldLang)
		throws PortalException {

		DynamicQuery dynamicQuery = _ddmContentLocalService.dynamicQuery();

		String value = String.format("%%%s%%", fieldValue);
		String reference = String.format("%%%s%%", fieldReference);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("data"));
		dynamicQuery.add(
			RestrictionsFactoryUtil.and(
				RestrictionsFactoryUtil.like("data", value),
				RestrictionsFactoryUtil.like("data", reference)));

		List<DDMContent> oldValues = _ddmContentLocalService.dynamicQuery(
			dynamicQuery);

		for (Object oldValue : oldValues) {
			String jsonString = String.valueOf(oldValue);

			JSONObject jsonObject = _jsonFactory.createJSONObject(jsonString);

			JSONArray jsonArray = jsonObject.getJSONArray("fieldValues");

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject fieldValuesJSONObject = jsonArray.getJSONObject(i);

				String jsonReference = fieldValuesJSONObject.getString(
					"fieldReference");

				JSONObject valueJSONObject =
					fieldValuesJSONObject.getJSONObject("value");

				String value_ = valueJSONObject.getString(fieldLang);

				if (fieldValue.equals(value_) &&
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