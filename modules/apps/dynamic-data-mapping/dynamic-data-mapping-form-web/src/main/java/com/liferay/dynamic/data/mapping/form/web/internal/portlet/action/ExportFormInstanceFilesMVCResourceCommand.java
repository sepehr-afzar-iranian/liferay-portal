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

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceVersionLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
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
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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

		Locale locale = resourceRequest.getLocale();

		DDMFormInstance formInstance = _ddmFormInstanceService.getFormInstance(
			formInstanceId);

		DDMForm ddmForm = formInstance.getDDMForm();

		ArrayList<String> fileFieldReferences = new ArrayList<>();

		for (DDMFormField ddmFormField : ddmForm.getDDMFormFields()) {
			String type = ddmFormField.getType();

			if (type.equals("document_library") || type.equals("image")) {
				fileFieldReferences.add(ddmFormField.getFieldReference());
			}
		}

		List<DDMFormInstanceRecord> ddmFormInstanceRecords =
			_ddmFormInstanceRecordService.getFormInstanceRecords(
				formInstanceId);

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		ZipOutputStream zipOutputStream = new ZipOutputStream(
			byteArrayOutputStream);

		for (DDMFormInstanceRecord ddmFormInstanceRecord :
				ddmFormInstanceRecords) {

			DDMFormValues ddmFormValues =
				ddmFormInstanceRecord.getDDMFormValues();

			String trackingCode = ddmFormInstanceRecord.getTrackingCode();

			Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap =
				ddmFormValues.getDDMFormFieldValuesReferencesMap(true);

			for (String fieldReference : fileFieldReferences) {
				List<DDMFormFieldValue> ddmFormFieldValues =
					ddmFormFieldValuesMap.get(fieldReference);

				DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

				FileEntry fileEntry = _getFileEntry(
					_getValueJSONObject(ddmFormFieldValue, locale));

				if (!Objects.equals(fileEntry, null)) {
					String stringBuilder = _getFolderName(
						trackingCode, ddmFormFieldValuesMap,
						fileEntry.getFileName(), ddmForm.getDefaultLocale());

					ZipEntry zipEntry = new ZipEntry(stringBuilder);

					zipOutputStream.putNextEntry(zipEntry);

					InputStream inputStream = fileEntry.getContentStream();
					ByteArrayOutputStream tempByteArrayOutputStream =
						new ByteArrayOutputStream();
					byte[] buffer = new byte[0xFFFF];

					for (int len = inputStream.read(buffer); len != -1;
						 len = inputStream.read(buffer)) {

						tempByteArrayOutputStream.write(buffer, 0, len);
					}

					zipOutputStream.write(
						tempByteArrayOutputStream.toByteArray());
					zipOutputStream.closeEntry();
				}
			}
		}

		zipOutputStream.close();

		byte[] zipContent = byteArrayOutputStream.toByteArray();

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse,
			formInstance.getName(locale) + ".zip",
			new ByteArrayInputStream(zipContent), ContentTypes.APPLICATION_ZIP);
	}

	@Reference
	protected DDMFormInstanceVersionLocalService
		ddmFormInstanceVersionLocalService;

	@Reference
	protected DLAppService dlAppService;

	@Reference
	protected JSONFactory jsonFactory;

	private FileEntry _getFileEntry(JSONObject jsonObject) {
		String uuid = jsonObject.getString("uuid");
		long groupId = jsonObject.getLong("groupId");

		if (Validator.isNull(uuid) || (groupId == 0)) {
			return null;
		}

		try {
			return dlAppService.getFileEntryByUuidAndGroupId(uuid, groupId);
		}
		catch (Exception exception) {
			return null;
		}
	}

	private String _getFolderName(
		String trackingCode,
		Map<String, List<DDMFormFieldValue>> ddmFormFieldValuesMap,
		String fileName, Locale locale) {

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(trackingCode);

		for (String fieldReference :
				new String[] {"firstName", "lastName", "nationalCode"}) {

			if (ddmFormFieldValuesMap.containsKey(fieldReference)) {
				List<DDMFormFieldValue> ddmFormFieldValues =
					ddmFormFieldValuesMap.get(fieldReference);

				DDMFormFieldValue ddmFormFieldValue = ddmFormFieldValues.get(0);

				Value fieldValue = ddmFormFieldValue.getValue();

				Map<Locale, String> values = fieldValue.getValues();

				String value = values.get(locale);

				if (Validator.isNotNull(value)) {
					stringBuilder.append(
						"_"
					).append(
						value
					);
				}
			}
		}

		stringBuilder.append("/");
		stringBuilder.append(fileName);

		return stringBuilder.toString();
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
	private DDMFormInstanceRecordService _ddmFormInstanceRecordService;

	@Reference
	private DDMFormInstanceService _ddmFormInstanceService;

}