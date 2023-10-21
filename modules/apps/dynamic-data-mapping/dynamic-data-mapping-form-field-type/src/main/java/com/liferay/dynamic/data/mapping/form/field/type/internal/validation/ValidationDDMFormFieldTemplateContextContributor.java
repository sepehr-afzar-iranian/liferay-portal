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

package com.liferay.dynamic.data.mapping.form.field.type.internal.validation;

import com.liferay.dynamic.data.mapping.data.provider.DDMDataProvider;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderInvoker;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderOutputParametersSettings;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderParameterSettings;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderTracker;
import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldTemplateContextContributor;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.dynamic.data.mapping.util.DDMFormInstanceFactory;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Basto
 */
@Component(
	immediate = true, property = "ddm.form.field.type.name=validation",
	service = {
		DDMFormFieldTemplateContextContributor.class,
		ValidationDDMFormFieldTemplateContextContributor.class
	}
)
public class ValidationDDMFormFieldTemplateContextContributor
	implements DDMFormFieldTemplateContextContributor {

	@Override
	public Map<String, Object> getParameters(
		DDMFormField ddmFormField,
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		try {
			long groupId = portal.getScopeGroupId(
				ddmFormFieldRenderingContext.getHttpServletRequest());

			List<DDMDataProviderInstance> dataProviderInstances =
				ddmDataProviderInstanceLocalService.getDataProviderInstances(
					new long[] {groupId});

			ArrayList<HashMap<String, Object>> dataproviders =
				new ArrayList<>();

			for (DDMDataProviderInstance ddmDataProviderInstance :
					dataProviderInstances) {

				DDMDataProvider ddmDataProvider =
					ddmDataProviderTracker.getDDMDataProvider(
						ddmDataProviderInstance.getType());

				DDMFormValues dataProviderFormValues =
					getDataProviderInstanceFormValues(
						ddmDataProvider, ddmDataProviderInstance);

				DDMDataProviderParameterSettings
					ddmDataProviderParameterSetting =
						DDMFormInstanceFactory.create(
							DDMDataProviderParameterSettings.class,
							dataProviderFormValues);

				DDMDataProviderOutputParametersSettings[]
					ddmDataProvidersOutputParametersSettings =
						ddmDataProviderParameterSetting.outputParameters();

				ArrayList<HashMap<String, Object>> dataproviderOutputs =
					new ArrayList<>();

				for (DDMDataProviderOutputParametersSettings
						ddmDataProviderOutputParametersSettings :
							ddmDataProvidersOutputParametersSettings) {

					if (Objects.equals(
							ddmDataProviderOutputParametersSettings.
								outputParameterType(),
							"list")) {

						dataproviderOutputs.add(
							HashMapBuilder.<String, Object>put(
								"label",
								ddmDataProviderOutputParametersSettings.
									outputParameterName()
							).put(
								"value",
								ddmDataProviderOutputParametersSettings.
									outputParameterId()
							).build());
					}
				}

				if (!dataproviderOutputs.isEmpty()) {
					dataproviders.add(
						HashMapBuilder.<String, Object>put(
							"label",
							ddmDataProviderInstance.getName(
								ddmFormFieldRenderingContext.getLocale())
						).put(
							"outputs", dataproviderOutputs
						).put(
							"value",
							ddmDataProviderInstance.getDataProviderInstanceId()
						).build());
				}
			}

			return HashMapBuilder.<String, Object>put(
				"dataProviders", dataproviders
			).put(
				"value", getValue(ddmFormFieldRenderingContext)
			).build();
		}
		catch (Exception exception1) {
			try {
				return HashMapBuilder.<String, Object>put(
					"value", getValue(ddmFormFieldRenderingContext)
				).build();
			}
			catch (Exception exception2) {
				return new HashMap<>();
			}
		}
	}

	protected DDMFormValues deserialize(String content, DDMForm ddmForm) {
		DDMFormValuesDeserializerDeserializeRequest.Builder builder =
			DDMFormValuesDeserializerDeserializeRequest.Builder.newBuilder(
				content, ddmForm);

		DDMFormValuesDeserializerDeserializeResponse
			ddmFormValuesDeserializerDeserializeResponse =
				jsonDDMFormValuesDeserializer.deserialize(builder.build());

		return ddmFormValuesDeserializerDeserializeResponse.getDDMFormValues();
	}

	protected DDMFormValues getDataProviderInstanceFormValues(
		DDMDataProvider ddmDataProvider,
		DDMDataProviderInstance ddmDataProviderInstance) {

		DDMForm ddmForm = DDMFormFactory.create(ddmDataProvider.getSettings());

		return deserialize(ddmDataProviderInstance.getDefinition(), ddmForm);
	}

	protected Map<String, Object> getValue(
		DDMFormFieldRenderingContext ddmFormFieldRenderingContext) {

		String valueString = ddmFormFieldRenderingContext.getValue();

		if (Validator.isNotNull(valueString)) {
			try {
				JSONObject valueJSONObject = jsonFactory.createJSONObject(
					valueString);

				return HashMapBuilder.<String, Object>put(
					"errorMessage",
					valueJSONObject.getJSONObject("errorMessage")
				).put(
					"expression", valueJSONObject.getJSONObject("expression")
				).put(
					"parameter", valueJSONObject.getJSONObject("parameter")
				).build();
			}
			catch (JSONException jsonException) {
				if (_log.isWarnEnabled()) {
					_log.warn(jsonException, jsonException);
				}
			}
		}

		return HashMapBuilder.<String, Object>put(
			"errorMessage", jsonFactory.createJSONObject()
		).put(
			"expression", jsonFactory.createJSONObject()
		).put(
			"parameter", jsonFactory.createJSONObject()
		).build();
	}

	@Reference
	protected DDMDataProviderInstanceLocalService
		ddmDataProviderInstanceLocalService;

	@Reference
	protected DDMDataProviderInvoker ddmDataProviderInvoker;

	@Reference
	protected DDMDataProviderTracker ddmDataProviderTracker;

	@Reference(target = "(ddm.form.values.deserializer.type=json)")
	protected DDMFormValuesDeserializer jsonDDMFormValuesDeserializer;

	@Reference
	protected JSONFactory jsonFactory;

	@Reference
	protected Portal portal;

	private static final Log _log = LogFactoryUtil.getLog(
		ValidationDDMFormFieldTemplateContextContributor.class);

}