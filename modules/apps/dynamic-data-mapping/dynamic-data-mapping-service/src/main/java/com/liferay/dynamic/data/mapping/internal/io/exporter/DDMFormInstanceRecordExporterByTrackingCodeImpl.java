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

package com.liferay.dynamic.data.mapping.internal.io.exporter;

import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.constants.DDMConstants;
import com.liferay.dynamic.data.mapping.exception.FormInstanceRecordExporterException;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTypeServicesTracker;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordExporterByTrackingCode;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordExporterRequest;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordExporterResponse;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordWriter;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordWriterRequest;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordWriterResponse;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordWriterTracker;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.comparator.FormInstanceVersionVersionComparator;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.text.Format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(
	immediate = true,
	service = DDMFormInstanceRecordExporterByTrackingCode.class
)
public class DDMFormInstanceRecordExporterByTrackingCodeImpl
	implements DDMFormInstanceRecordExporterByTrackingCode {

	@Override
	public DDMFormInstanceRecordExporterResponse export(
			DDMFormInstanceRecordExporterRequest
				ddmFormInstanceRecordExporterRequest)
		throws FormInstanceRecordExporterException {

		long ddmFormInstanceId =
			ddmFormInstanceRecordExporterRequest.getDDMFormInstanceId();
		int status = ddmFormInstanceRecordExporterRequest.getStatus();
		int start = ddmFormInstanceRecordExporterRequest.getStart();
		int end = ddmFormInstanceRecordExporterRequest.getEnd();
		OrderByComparator<DDMFormInstanceRecord> orderByComparator =
			ddmFormInstanceRecordExporterRequest.getOrderByComparator();
		Locale locale = ddmFormInstanceRecordExporterRequest.getLocale();
		String type = ddmFormInstanceRecordExporterRequest.getType();

		DDMFormInstanceRecordExporterResponse.Builder builder =
			DDMFormInstanceRecordExporterResponse.Builder.newBuilder();

		try {
			List<DDMFormInstanceRecord> ddmFormInstanceRecords =
				ddmFormInstanceRecordLocalService.getFormInstanceRecords(
					ddmFormInstanceId, status, start, end, orderByComparator);

			Map<String, DDMFormField> ddmFormFields = getDistinctFields(
				ddmFormInstanceId);

			Map<String, Integer> ddmFormFieldValuesSize =
				getDDMFormFieldValuesSize(
					ddmFormFields, ddmFormInstanceRecords, locale);

			byte[] content = write(
				type,
				getDDMFormFieldsLabel(
					ddmFormFields, ddmFormFieldValuesSize, locale),
				getDDMFormFieldValues(
					ddmFormFields, ddmFormInstanceRecords,
					ddmFormFieldValuesSize, locale));

			builder = builder.withContent(content);
		}
		catch (Exception exception) {
			throw new FormInstanceRecordExporterException(exception);
		}

		return builder.build();
	}

	protected Map<String, String> getDDMFormFieldsLabel(
		Map<String, DDMFormField> ddmFormFieldMap,
		Map<String, Integer> ddmFormFieldValuesSize, Locale locale) {

		Map<String, String> ddmFormFieldsLabel = new LinkedHashMap<>();

		Collection<DDMFormField> ddmFormFields = ddmFormFieldMap.values();

		Collection<Integer> ddmFormFieldValuesSizeValues =
			ddmFormFieldValuesSize.values();

		Stream<Integer> ddmFormFieldValuesSizeStream =
			ddmFormFieldValuesSizeValues.stream();

		int maxSize = ddmFormFieldValuesSizeStream.mapToInt(
			Integer::intValue
		).max(
		).orElse(
			0
		);

		for (int i = 1; i <= maxSize; i++) {
			for (DDMFormField ddmFormField : ddmFormFields) {
				int size = ddmFormFieldValuesSize.getOrDefault(
					ddmFormField.getFieldReference(), 0);

				if (i <= size) {
					LocalizedValue localizedValue = ddmFormField.getLabel();

					String baseLabel = localizedValue.getString(locale);

					if (size > 1) {
						ddmFormFieldsLabel.put(
							ddmFormField.getFieldReference() + "-" + i,
							baseLabel);
					}
					else {
						if (i == 1) {
							ddmFormFieldsLabel.put(
								ddmFormField.getFieldReference(), baseLabel);
						}
					}
				}
			}
		}

		if (_HAS_ADVANCED_FORM_BUILDER_TRACKING_CODE) {
			ddmFormFieldsLabel.put(
				_TRACKING_CODE,
				LanguageUtil.get(
					ResourceBundleUtil.getBundle(
						locale,
						DDMFormInstanceRecordExporterByTrackingCode.class),
					"tracking-code"));
		}

		ddmFormFieldsLabel.put(_STATUS, LanguageUtil.get(locale, _STATUS));
		ddmFormFieldsLabel.put(
			_MODIFIED_DATE, LanguageUtil.get(locale, "modified-date"));
		ddmFormFieldsLabel.put(_AUTHOR, LanguageUtil.get(locale, _AUTHOR));

		return ddmFormFieldsLabel;
	}

	protected String getDDMFormFieldValue(
		DDMFormField ddmFormField,
		Map<String, List<DDMFormFieldValue>> ddmFormFieldValueMap,
		Locale locale) {

		List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValueMap.get(
			ddmFormField.getFieldReference());

		DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			ddmFormFieldTypeServicesTracker.getDDMFormFieldValueRenderer(
				ddmFormField.getType());

		Stream<DDMFormFieldValue> stream = ddmFormFieldValues.stream();

		return HtmlUtil.extractText(
			StringUtil.merge(
				stream.map(
					ddmForFieldValue -> _getRender(
						ddmForFieldValue, ddmFormFieldValueRenderer, locale)
				).filter(
					Validator::isNotNull
				).collect(
					Collectors.toList()
				),
				StringPool.COMMA_AND_SPACE));
	}

	protected List<String> getDDMFormFieldValues(
		DDMFormField ddmFormField,
		Map<String, List<DDMFormFieldValue>> ddmFormFieldValueMap,
		Locale locale) {

		List<DDMFormFieldValue> ddmFormFieldValues = ddmFormFieldValueMap.get(
			ddmFormField.getFieldReference());

		if (ddmFormFieldValues == null) {
			return new ArrayList<>();
		}

		List<DDMFormFieldValue> newDDMFormFieldValues = new ArrayList<>();

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			DDMFormValues ddmFormValues = ddmFormFieldValue.getDDMFormValues();

			if (ddmFormValues == null) {
				continue;
			}

			List<DDMFormFieldValue> ddmValues =
				ddmFormValues.getDDMFormFieldValues();

			if (ddmValues == null) {
				continue;
			}

			for (DDMFormFieldValue ddmValue : ddmValues) {
				List<DDMFormFieldValue> nestedDDMFormFieldValues =
					ddmValue.getNestedDDMFormFieldValues();

				if (nestedDDMFormFieldValues != null) {
					Stream<DDMFormFieldValue> nestedDDMFormFieldValuesStream =
						nestedDDMFormFieldValues.stream();

					newDDMFormFieldValues.addAll(
						nestedDDMFormFieldValuesStream.filter(
							ddmForFieldValue ->
								ddmForFieldValue.getFieldReference(
								).equals(
									ddmFormField.getFieldReference()
								)
						).collect(
							Collectors.toList()
						));
				}
			}
		}

		DDMFormFieldValueRenderer ddmFormFieldValueRenderer =
			ddmFormFieldTypeServicesTracker.getDDMFormFieldValueRenderer(
				ddmFormField.getType());

		if (newDDMFormFieldValues.isEmpty()) {
			return ListUtil.fromArray(
				getDDMFormFieldValue(
					ddmFormField, ddmFormFieldValueMap, locale));
		}

		Stream<DDMFormFieldValue> stream = newDDMFormFieldValues.stream();

		return stream.map(
			ddmForFieldValue -> HtmlUtil.extractText(
				_getRender(ddmForFieldValue, ddmFormFieldValueRenderer, locale))
		).filter(
			Validator::isNotNull
		).collect(
			Collectors.toList()
		);
	}

	protected List<Map<String, String>> getDDMFormFieldValues(
			Map<String, DDMFormField> ddmFormFields,
			List<DDMFormInstanceRecord> ddmFormInstanceRecords,
			Map<String, Integer> ddmFormFieldValuesSize, Locale locale)
		throws Exception {

		List<Map<String, String>> ddmFormFieldValues = new ArrayList<>();

		for (DDMFormInstanceRecord ddmFormInstanceRecord :
				ddmFormInstanceRecords) {

			DDMFormValues ddmFormValues =
				ddmFormInstanceRecord.getDDMFormValues();

			Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
				ddmFormValues.getDDMFormFieldValuesReferencesMap(true);

			Map<String, String> ddmFormFieldsValue = new LinkedHashMap<>();

			Collection<Integer> ddmFormFieldValuesSizeValues =
				ddmFormFieldValuesSize.values();

			Stream<Integer> ddmFormFieldValuesSizeStream =
				ddmFormFieldValuesSizeValues.stream();

			int maxSize = ddmFormFieldValuesSizeStream.mapToInt(
				Integer::intValue
			).max(
			).orElse(
				0
			);

			for (int i = 1; i <= maxSize; i++) {
				for (Map.Entry<String, DDMFormField> entry :
						ddmFormFields.entrySet()) {

					String key = entry.getKey();
					DDMFormField ddmFormField = entry.getValue();
					int filledSize = ddmFormFieldValuesSize.get(key);

					if (i <= filledSize) {
						if (!ddmFormFieldValuesMap.containsKey(key)) {
							if (filledSize > 1) {
								ddmFormFieldsValue.put(
									key + "-" + i, StringPool.BLANK);
							}
							else {
								if (i == 1) {
									ddmFormFieldsValue.put(
										key, StringPool.BLANK);
								}
							}
						}
						else {
							List<String> values = getDDMFormFieldValues(
								ddmFormField, ddmFormFieldValuesMap, locale);

							if (filledSize > 1) {
								ddmFormFieldsValue.put(
									key + "-" + i,
									(values.size() > (i - 1)) ?
										values.get(i - 1) : StringPool.BLANK);
							}
							else {
								if (i == 1) {
									String value =
										!values.isEmpty() ? values.get(0) :
											StringPool.BLANK;

									ddmFormFieldsValue.put(key, value);
								}
							}
						}
					}
				}
			}

			DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
				ddmFormInstanceRecord.getFormInstanceRecordVersion();

			if (_HAS_ADVANCED_FORM_BUILDER_TRACKING_CODE) {
				ddmFormFieldsValue.put(
					_TRACKING_CODE, ddmFormInstanceRecord.getTrackingCode());
			}

			ddmFormFieldsValue.put(
				_STATUS,
				getStatusMessage(
					ddmFormInstanceRecordVersion.getStatus(), locale));

			ddmFormFieldsValue.put(
				_MODIFIED_DATE,
				getModifiedDate(
					ddmFormInstanceRecordVersion.getStatusDate(), locale));

			ddmFormFieldsValue.put(
				_AUTHOR, ddmFormInstanceRecordVersion.getUserName());

			ddmFormFieldValues.add(ddmFormFieldsValue);
		}

		return ddmFormFieldValues;
	}

	protected Map<String, Integer> getDDMFormFieldValuesSize(
			Map<String, DDMFormField> ddmFormFields,
			List<DDMFormInstanceRecord> ddmFormInstanceRecords, Locale locale)
		throws Exception {

		Map<String, Integer> ddmFormFieldValuesSize = new LinkedHashMap<>();

		for (DDMFormInstanceRecord ddmFormInstanceRecord :
				ddmFormInstanceRecords) {

			DDMFormValues ddmFormValues =
				ddmFormInstanceRecord.getDDMFormValues();

			Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
				ddmFormValues.getDDMFormFieldValuesReferencesMap(true);

			for (Map.Entry<String, DDMFormField> entry :
					ddmFormFields.entrySet()) {

				String key = entry.getKey();
				List<String> ddmFormFieldValues = getDDMFormFieldValues(
					entry.getValue(), ddmFormFieldValuesMap, locale);

				if (!ddmFormFieldValuesSize.containsKey(key) ||
					(ddmFormFieldValuesSize.containsKey(key) &&
					 (ddmFormFieldValuesSize.get(key) <
						 ddmFormFieldValues.size()))) {

					ddmFormFieldValuesSize.put(key, ddmFormFieldValues.size());
				}
			}
		}

		return ddmFormFieldValuesSize;
	}

	protected Map<String, DDMFormField> getDistinctFields(
			long ddmFormInstanceId)
		throws Exception {

		List<DDMStructureVersion> ddmStructureVersions = getStructureVersions(
			ddmFormInstanceId);

		Map<String, DDMFormField> ddmFormFields = new LinkedHashMap<>();

		Stream<DDMStructureVersion> stream = ddmStructureVersions.stream();

		stream.map(
			this::getNontransientDDMFormFieldsReferencesMap
		).forEach(
			map -> map.forEach(
				(key, ddmFormField) -> ddmFormFields.putIfAbsent(
					key, ddmFormField))
		);

		return ddmFormFields;
	}

	protected String getModifiedDate(Date date, Locale locale) {
		String languageId = LanguageUtil.getLanguageId(locale);

		if (languageId.equals("fa_IR")) {
			SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss", LocaleUtil.getDefault());

			NumberFormat numberFormat = NumberFormat.getNumberInstance(
				LocaleUtil.ENGLISH);

			formatter.setNumberFormat(numberFormat);

			ULocale uLocale = ULocale.forLocale(
				LocaleUtil.fromLanguageId("fa_IR"));

			Calendar calendar = Calendar.getInstance(uLocale);

			calendar.setTime(date);

			return formatter.format(calendar);
		}

		Format dateTimeFormat = FastDateFormatFactoryUtil.getDateTime(locale);

		return dateTimeFormat.format(date);
	}

	protected Map<String, DDMFormField>
		getNontransientDDMFormFieldsReferencesMap(
			DDMStructureVersion ddmStructureVersion) {

		DDMForm ddmForm = ddmStructureVersion.getDDMForm();

		return ddmForm.getNontransientDDMFormFieldsReferencesMap(true);
	}

	protected String getStatusMessage(int status, Locale locale) {
		return LanguageUtil.get(
			locale, WorkflowConstants.getStatusLabel(status));
	}

	protected List<DDMStructureVersion> getStructureVersions(
			long ddmFormInstanceId)
		throws Exception {

		List<DDMFormInstanceVersion> ddmFormInstanceVersions =
			ddmFormInstanceVersionLocalService.getFormInstanceVersions(
				ddmFormInstanceId, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		ddmFormInstanceVersions = ListUtil.sort(
			ddmFormInstanceVersions,
			new FormInstanceVersionVersionComparator());

		List<DDMStructureVersion> ddmStructureVersions = new ArrayList<>();

		for (DDMFormInstanceVersion ddmFormInstanceVersion :
				ddmFormInstanceVersions) {

			ddmStructureVersions.add(
				ddmFormInstanceVersion.getStructureVersion());
		}

		return ddmStructureVersions;
	}

	protected byte[] write(
			String type, Map<String, String> ddmFormFieldsLabel,
			List<Map<String, String>> ddmFormFieldValues)
		throws Exception {

		DDMFormInstanceRecordWriter ddmFormInstanceRecordWriter =
			ddmFormInstanceRecordWriterTracker.getDDMFormInstanceRecordWriter(
				type);

		DDMFormInstanceRecordWriterRequest.Builder builder =
			DDMFormInstanceRecordWriterRequest.Builder.newBuilder(
				ddmFormFieldsLabel, ddmFormFieldValues);

		DDMFormInstanceRecordWriterRequest ddmFormInstanceRecordWriterRequest =
			builder.build();

		DDMFormInstanceRecordWriterResponse
			ddmFormInstanceRecordWriterResponse =
				ddmFormInstanceRecordWriter.write(
					ddmFormInstanceRecordWriterRequest);

		return ddmFormInstanceRecordWriterResponse.getContent();
	}

	@Reference
	protected DDMFormFieldTypeServicesTracker ddmFormFieldTypeServicesTracker;

	@Reference
	protected DDMFormInstanceRecordLocalService
		ddmFormInstanceRecordLocalService;

	@Reference
	protected DDMFormInstanceRecordWriterTracker
		ddmFormInstanceRecordWriterTracker;

	@Reference
	protected DDMFormInstanceVersionLocalService
		ddmFormInstanceVersionLocalService;

	@Reference
	protected DLAppService dlAppService;

	@Reference
	protected DLURLHelper dlurlHelper;

	@Reference
	protected JSONFactory jsonFactory;

	private String _getRender(
		DDMFormFieldValue ddmFormFieldValue,
		DDMFormFieldValueRenderer ddmFormFieldValueRenderer, Locale locale) {

		String type = ddmFormFieldValue.getType();

		if (!type.equals("document_library") && !type.equals("image")) {
			return ddmFormFieldValueRenderer.render(ddmFormFieldValue, locale);
		}

		JSONObject jsonObject = _getValueJSONObject(ddmFormFieldValue, locale);

		String uuid = jsonObject.getString("uuid");
		long groupId = jsonObject.getLong("groupId");

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (Validator.isNull(uuid) || (groupId == 0)) {
			return StringPool.BLANK;
		}

		try {
			FileEntry fileEntry = dlAppService.getFileEntryByUuidAndGroupId(
				uuid, groupId);

			return dlurlHelper.getDownloadURL(
				fileEntry, fileEntry.getFileVersion(),
				serviceContext.getThemeDisplay(), null);
		}
		catch (Exception exception) {
			return LanguageUtil.format(
				locale, "is-temporarily-unavailable", "content");
		}
	}

	private JSONObject _getValueJSONObject(
		DDMFormFieldValue ddmFormFieldValue, Locale locale) {

		Value value = ddmFormFieldValue.getValue();

		if (value == null) {
			return jsonFactory.createJSONObject();
		}

		try {
			return jsonFactory.createJSONObject(value.getString(locale));
		}
		catch (JSONException jsonException) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to parse JSON object", jsonException);
			}

			return jsonFactory.createJSONObject();
		}
	}

	private static final String _AUTHOR = "author";

	private static final Boolean _HAS_ADVANCED_FORM_BUILDER_TRACKING_CODE =
		Boolean.parseBoolean(
			PropsUtil.get(DDMConstants.ADVANCED_FORM_BUILDER_TRACKING_CODE));

	private static final String _MODIFIED_DATE = "modifiedDate";

	private static final String _STATUS = "status";

	private static final String _TRACKING_CODE = "trackingCode";

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormInstanceRecordExporterByTrackingCodeImpl.class);

}