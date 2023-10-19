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

import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayActionResponse;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletURL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pedro Queiroz
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
		"mvc.command.name=/dynamic_data_mapping_form/import_form_instance"
	},
	service = MVCActionCommand.class
)
public class ImportFormInstanceMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	protected DDMFormValues createFormInstanceSettingsDDMFormValues(
			DDMFormInstance formInstance)
		throws Exception {

		DDMFormValues settingsDDMFormValuesCopy =
			formInstance.getSettingsDDMFormValues();

		setDefaultPublishedDDMFormFieldValue(settingsDDMFormValuesCopy);

		return settingsDDMFormValuesCopy;
	}

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		long formInstanceId = ParamUtil.getLong(
			actionRequest, "formInstanceId");
		String redirect = ParamUtil.getString(actionRequest, "backUrl");

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		List<DDMFormInstance> ddmFormInstances =
			ddmFormInstanceLocalService.getFormInstances(
				themeDisplay.getScopeGroupId());

		DDMFormInstance formInstance = ddmFormInstanceService.getFormInstance(
			formInstanceId);

		Stream<DDMFormInstance> ddmFormInstancesStream =
			ddmFormInstances.stream();

		List<DDMFormInstance> selectedInstance = ddmFormInstancesStream.filter(
			ddmFormInstance -> Objects.equals(
				ddmFormInstance.getName(), formInstance.getName())
		).collect(
			Collectors.toList()
		);

		DDMStructure ddmStructure = formInstance.getStructure();

		Locale defaultLocale = LocaleUtil.fromLanguageId(
			ddmStructure.getDefaultLanguageId());

		DDMFormValues settingsDDMFormValues =
			createFormInstanceSettingsDDMFormValues(formInstance);

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDMFormInstance.class.getName(), actionRequest);

		DDMFormInstance ddmFormInstance =
			ddmFormInstanceService.copyFormInstance(
				groupId,
				getNameMap(
					formInstance, defaultLocale, selectedInstance.isEmpty()),
				formInstance, settingsDDMFormValues, serviceContext);

		LiferayActionResponse liferayActionResponse =
			(LiferayActionResponse)actionResponse;

		PortletURL portletURL = liferayActionResponse.createRenderURL();

		portletURL.setParameter(
			"mvcRenderCommandName", "/admin/edit_form_instance");
		portletURL.setParameter("redirect", redirect);
		portletURL.setParameter(
			"formInstanceId",
			String.valueOf(ddmFormInstance.getFormInstanceId()));

		actionResponse.sendRedirect(portletURL.toString());
	}

	protected Map<Locale, String> getNameMap(
		DDMFormInstance formInstance, Locale defaultLocale,
		boolean formNotExists) {

		Map<Locale, String> nameMap = formInstance.getNameMap();

		if (formNotExists)

			return nameMap;

		String name = LanguageUtil.format(
			getResourceBundle(defaultLocale), "copy-of-x",
			nameMap.get(defaultLocale));

		nameMap.put(defaultLocale, name);

		return nameMap;
	}

	protected ResourceBundle getResourceBundle(Locale locale) {
		ResourceBundle portalResourceBundle = portal.getResourceBundle(locale);

		ResourceBundle moduleResourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return new AggregateResourceBundle(
			moduleResourceBundle, portalResourceBundle);
	}

	protected void setDefaultPublishedDDMFormFieldValue(
		DDMFormValues ddmFormValues) {

		for (DDMFormFieldValue ddmFormFieldValue :
				ddmFormValues.getDDMFormFieldValues()) {

			if (Objects.equals(ddmFormFieldValue.getName(), "published")) {
				ddmFormFieldValue.setValue(new UnlocalizedValue("false"));
			}
		}
	}

	@Reference
	protected DDMFormInstanceLocalService ddmFormInstanceLocalService;

	@Reference
	protected DDMFormInstanceService ddmFormInstanceService;

	@Reference
	protected DDMStructureService ddmStructureService;

	@Reference
	protected Portal portal;

	@Reference
	protected SaveFormInstanceMVCCommandHelper saveFormInstanceMVCCommandHelper;

}