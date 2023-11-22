/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceVersion;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.comparator.FormInstanceVersionVersionComparator;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.zip.ZipWriter;
import com.liferay.portal.kernel.zip.ZipWriterFactoryUtil;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import com.liferay.portal.kernel.util.Validator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Pedro Queiroz
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
		"mvc.command.name=/dynamic_data_mapping_form/export_form_instance_files"
	},
	service = MVCResourceCommand.class
)
public class ExportFormInstanceFilesMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
		ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		long formInstanceId = ParamUtil.getLong(
			resourceRequest, "formInstanceId");

		Map<String, DDMFormField> ddmFormFields = getDistinctFields(
			formInstanceId);

		Locale locale = resourceRequest.getLocale();

		DDMFormInstance formInstance = _ddmFormInstanceService.getFormInstance(
			formInstanceId);

		List<DDMFormInstanceRecord> ddmFormInstanceRecords =
			_ddmFormInstanceRecordService.getFormInstanceRecords(
				formInstanceId);

		ZipWriter zipWriter = ZipWriterFactoryUtil.getZipWriter();

		for (DDMFormInstanceRecord ddmFormInstanceRecord :
			ddmFormInstanceRecords) {

			DDMFormValues ddmFormValues =
				ddmFormInstanceRecord.getDDMFormValues();

			String trackingCode = ddmFormInstanceRecord.getTrackingCode();

			Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
				ddmFormValues.getDDMFormFieldValuesReferencesMap(true);

			for (Map.Entry<String, DDMFormField> entry : ddmFormFields.entrySet()) {
				String fieldReference = entry.getValue().getFieldReference();
				if (ddmFormFieldValuesMap.containsKey(fieldReference)) {
					List<DDMFormFieldValue> ddmFormFieldValues =
						ddmFormFieldValuesMap.get(fieldReference);
					for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
						FileEntry fileEntry =
							_getRender(ddmFormFieldValue, locale);
						if (Validator.isNotNull(fileEntry)) {
							zipWriter.addEntry(
								trackingCode + "/" + fileEntry.getFileName(),
								fileEntry.getContentStream());
						}
					}
				}
			}
		}

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse,
			formInstance.getName(locale) + ".zip",
			Files.newInputStream(zipWriter.getFile().toPath()),
			ContentTypes.APPLICATION_ZIP);

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
				ddmFormFields::putIfAbsent)
		);

		return ddmFormFields;
	}

	protected Map<String, DDMFormField>
	getNontransientDDMFormFieldsReferencesMap(
		DDMStructureVersion ddmStructureVersion) {

		DDMForm ddmForm = ddmStructureVersion.getDDMForm();

		return ddmForm.getNontransientDDMFormFieldsReferencesMap(true);
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

	private FileEntry _getRender(
		DDMFormFieldValue ddmFormFieldValue, Locale locale) {

		String type = ddmFormFieldValue.getType();

		if (!type.equals("document_library") && !type.equals("image")) {
			return null;
		}

		JSONObject jsonObject = _getValueJSONObject(ddmFormFieldValue, locale);

		String uuid = jsonObject.getString("uuid");
		long groupId = jsonObject.getLong("groupId");

		if (Validator.isNull(uuid) || (groupId == 0)) {
			return null;
		}

		try {
			return dlAppService.getFileEntryByUuidAndGroupId(
				uuid, groupId);
		}
		catch (Exception exception) {
			return null;
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
				_log.debug(jsonException, jsonException);
			}
			return jsonFactory.createJSONObject();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ExportFormInstanceFilesMVCResourceCommand.class);

	@Reference
	protected DDMFormInstanceVersionLocalService
		ddmFormInstanceVersionLocalService;

	@Reference
	protected DLAppService dlAppService;

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	private DDMFormInstanceRecordService _ddmFormInstanceRecordService;

	@Reference
	private DDMFormInstanceService _ddmFormInstanceService;

}