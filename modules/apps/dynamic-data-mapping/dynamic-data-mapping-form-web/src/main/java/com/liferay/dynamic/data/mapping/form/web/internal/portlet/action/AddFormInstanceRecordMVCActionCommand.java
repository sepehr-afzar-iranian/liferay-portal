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

import com.liferay.captcha.util.CaptchaUtil;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.exception.FormInstanceNotPublishedException;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.form.web.internal.constants.DDMFormWebKeys;
import com.liferay.dynamic.data.mapping.form.web.internal.instance.lifecycle.AddDefaultSharedFormLayoutPortalInstanceLifecycleListener;
import com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.util.DDMFormUniqueFieldChecker;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeRequest;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesDeserializerDeserializeResponse;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecordVersion;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceSettings;
import com.liferay.dynamic.data.mapping.model.DDMFormSuccessPageSettings;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMContentLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordVersionLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayActionResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.List;
import java.util.Objects;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marcellus Tavares
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM,
		"javax.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
		"mvc.command.name=/dynamic_data_mapping_form/add_form_instance_record"
	},
	service = MVCActionCommand.class
)
public class AddFormInstanceRecordMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		PortletSession portletSession = actionRequest.getPortletSession();

		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		boolean confirmOnSubmit = ParamUtil.getBoolean(
			actionRequest, "confirmOnSubmit");

		if (groupId == 0) {
			groupId = GetterUtil.getLong(
				portletSession.getAttribute(DDMFormWebKeys.GROUP_ID));
		}

		long formInstanceId = ParamUtil.getLong(
			actionRequest, "formInstanceId");

		if (formInstanceId == 0) {
			formInstanceId = GetterUtil.getLong(
				portletSession.getAttribute(
					DDMFormWebKeys.DYNAMIC_DATA_MAPPING_FORM_INSTANCE_ID));
		}

		DDMFormInstance ddmFormInstance =
			_ddmFormInstanceService.getFormInstance(formInstanceId);

		_validatePublishStatus(actionRequest, ddmFormInstance);

		validateCaptcha(actionRequest, ddmFormInstance);

		DDMForm ddmForm = getDDMForm(ddmFormInstance);

		DDMFormValues ddmFormValues = _getDDMFormValues(actionRequest, ddmForm);

		if (Objects.equals(ddmFormValues, null)) {
			ddmFormValues = _ddmFormValuesFactory.create(
				actionRequest, ddmForm);
		}

		long ddmFormInstanceRecordId = ParamUtil.getLong(
			actionRequest, "formInstanceRecordId");

		List<DDMFormFieldValue> ddmFormFieldValueList =
			ddmFormValues.getDDMFormFieldValues();

		for (DDMFormFieldValue fieldValue : ddmFormFieldValueList) {
			DDMFormField getDDMFormFieldValue = fieldValue.getDDMFormField();

			if (Validator.isNotNull(
					getDDMFormFieldValue.getProperty("uniqueField")) &&
				(Boolean)getDDMFormFieldValue.getProperty("uniqueField")) {

				if (ddmFormInstanceRecordId != 0) {
					_ddmFormUniqueFieldChecker.checkForEdit(
						actionRequest, actionResponse, fieldValue,
						ddmFormInstance);
				}
				else {
					_ddmFormUniqueFieldChecker.checkForAdd(fieldValue);
				}
			}
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_addFormInstanceMVCCommandHelper.
			updateRequiredFieldsAccordingToVisibility(
				actionRequest, ddmForm, ddmFormValues,
				LocaleUtil.fromLanguageId(
					LanguageUtil.getLanguageId(actionRequest)));

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			DDMFormInstanceRecord.class.getName(), actionRequest);

		serviceContext.setRequest(_portal.getHttpServletRequest(actionRequest));

		DDMFormInstanceRecord ddmFormInstanceRecord = _updateFormInstanceRecord(
			actionRequest, ddmFormInstance, ddmFormValues, groupId,
			serviceContext, themeDisplay.getUserId());

		if (!SessionErrors.isEmpty(actionRequest)) {
			return;
		}

		if (confirmOnSubmit) {
			LiferayActionResponse liferayActionResponse =
				(LiferayActionResponse)actionResponse;

			PortletURL portletURL = liferayActionResponse.createRenderURL();

			portletURL.setParameter("mvcPath", "/display/preview.jsp");
			portletURL.setParameter(
				"formInstanceId", String.valueOf(formInstanceId));
			portletURL.setParameter(
				"formInstanceRecordId",
				String.valueOf(
					ddmFormInstanceRecord.getFormInstanceRecordId()));

			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(actionResponse);

			httpServletResponse.sendRedirect(portletURL.toString());
		}

		boolean formHasPriceField = false;
		List<DDMFormField> ddmFormFieldList = ddmForm.getDDMFormFields();

		for (DDMFormField ddmFormField : ddmFormFieldList) {
			if (Validator.isNotNull(ddmFormField.getProperty("priceField")) &&
				(Boolean)ddmFormField.getProperty("priceField")) {

				formHasPriceField = true;

				break;
			}
		}

		if (formHasPriceField) {
			List<Layout> layoutList = _layoutLocalService.getLayouts(
				themeDisplay.getScopeGroupId(), false);
			String definitionPortletId =
				"ir_sain_definition_epayment_DefinitionEPaymentPortlet";

			for (Layout layout : layoutList) {
				long count =
					_portletPreferencesLocalService.getPortletPreferencesCount(
						PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
						definitionPortletId);

				if (count > 0) {
					PortletURL epaymentPortletURL =
						PortletURLFactoryUtil.create(
							actionRequest, definitionPortletId,
							layout.getPlid(), PortletRequest.ACTION_PHASE);

					epaymentPortletURL.setWindowState(
						LiferayWindowState.NORMAL);
					epaymentPortletURL.setParameter(
						"javax.portlet.action",
						"/definition_epayment/add_token");
					epaymentPortletURL.setParameter(
						"ddmFormInstanceRecordId",
						String.valueOf(
							ddmFormInstanceRecord.getFormInstanceRecordId()));

					HttpServletResponse httpServletResponse =
						_portal.getHttpServletResponse(actionResponse);

					httpServletResponse.sendRedirect(
						epaymentPortletURL.toString());

					break;
				}
			}
		}

		DDMFormInstanceSettings formInstanceSettings =
			ddmFormInstance.getSettingsModel();

		String redirectURL = ParamUtil.getString(
			actionRequest, "redirect", formInstanceSettings.redirectURL());

		if ((ddmFormInstanceRecordId != 0) &&
			Validator.isNotNull(redirectURL)) {

			portletSession.setAttribute(
				DDMFormWebKeys.DYNAMIC_DATA_MAPPING_FORM_INSTANCE_ID,
				formInstanceId);
			portletSession.setAttribute(DDMFormWebKeys.GROUP_ID, groupId);

			sendRedirect(actionRequest, actionResponse, redirectURL);
		}
		else {
			DDMFormSuccessPageSettings ddmFormSuccessPageSettings =
				ddmForm.getDDMFormSuccessPageSettings();

			if (ddmFormSuccessPageSettings.isEnabled()) {
				String portletId = _portal.getPortletId(actionRequest);

				SessionMessages.add(
					actionRequest,
					portletId.concat(
						SessionMessages.
							KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE));
			}

			LiferayActionResponse liferayActionResponse =
				(LiferayActionResponse)actionResponse;

			PortletURL portletURL = liferayActionResponse.createRenderURL();

			portletURL.setParameter(
				"trackingCode", ddmFormInstanceRecord.getTrackingCode());

			sendRedirect(actionRequest, actionResponse, portletURL.toString());
		}
	}

	protected DDMForm getDDMForm(DDMFormInstance ddmFormInstance)
		throws PortalException {

		DDMStructure ddmStructure = ddmFormInstance.getStructure();

		return ddmStructure.getDDMForm();
	}

	@Reference(unbind = "-")
	protected void setDDMFormInstanceRecordService(
		DDMFormInstanceRecordService ddmFormInstanceRecordService) {

		_ddmFormInstanceRecordService = ddmFormInstanceRecordService;
	}

	@Reference(unbind = "-")
	protected void setDDMFormInstanceService(
		DDMFormInstanceService ddmFormInstanceService) {

		_ddmFormInstanceService = ddmFormInstanceService;
	}

	@Reference(unbind = "-")
	protected void setDDMFormValuesFactory(
		DDMFormValuesFactory ddmFormValuesFactory) {

		_ddmFormValuesFactory = ddmFormValuesFactory;
	}

	protected void validateCaptcha(
			ActionRequest actionRequest, DDMFormInstance ddmFormInstance)
		throws Exception {

		DDMFormInstanceSettings formInstanceSettings =
			ddmFormInstance.getSettingsModel();

		if (formInstanceSettings.requireCaptcha()) {
			CaptchaUtil.check(actionRequest);
		}
	}

	@Reference(target = "(ddm.form.values.deserializer.type=json)")
	protected DDMFormValuesDeserializer jsonDDMFormValuesDeserializer;

	private DDMFormValues _deserialize(String content, DDMForm ddmForm) {
		DDMFormValuesDeserializerDeserializeRequest.Builder builder =
			DDMFormValuesDeserializerDeserializeRequest.Builder.newBuilder(
				content, ddmForm);

		DDMFormValuesDeserializerDeserializeResponse
			ddmFormValuesDeserializerDeserializeResponse =
				jsonDDMFormValuesDeserializer.deserialize(builder.build());

		return ddmFormValuesDeserializerDeserializeResponse.getDDMFormValues();
	}

	private DDMFormValues _getDDMFormValues(
		ActionRequest actionRequest, DDMForm ddmForm) {

		String serializedDDMFormValues = ParamUtil.getString(
			actionRequest, "ddmFormValues");

		if (Validator.isNull(serializedDDMFormValues))

			return null;

		return _deserialize(serializedDDMFormValues, ddmForm);
	}

	private DDMFormInstanceRecord _updateFormInstanceRecord(
			ActionRequest actionRequest, DDMFormInstance ddmFormInstance,
			DDMFormValues ddmFormValues, long groupId,
			ServiceContext serviceContext, long userId)
		throws Exception {

		long ddmFormInstanceRecordId = ParamUtil.getLong(
			actionRequest, "formInstanceRecordId");
		DDMFormInstanceRecord ddmFormInstanceRecord = null;

		if (ddmFormInstanceRecordId != 0) {
			ddmFormInstanceRecord =
				_ddmFormInstanceRecordService.getFormInstanceRecord(
					ddmFormInstanceRecordId);

			long prevStorageId = ddmFormInstanceRecord.getStorageId();

			ddmFormInstanceRecord =
				_ddmFormInstanceRecordService.updateFormInstanceRecord(
					ddmFormInstanceRecordId, false, ddmFormValues,
					serviceContext);

			long newStorageId = ddmFormInstanceRecord.getStorageId();

			if (prevStorageId != newStorageId) {
				_ddmContentLocalService.deleteContent(
					_ddmContentLocalService.getContent(prevStorageId));
			}
		}
		else {
			DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion =
				_ddmFormInstanceRecordVersionLocalService.
					fetchLatestFormInstanceRecordVersion(
						userId, ddmFormInstance.getFormInstanceId(),
						ddmFormInstance.getVersion(),
						WorkflowConstants.STATUS_DRAFT);

			if (ddmFormInstanceRecordVersion == null) {
				ddmFormInstanceRecord =
					_ddmFormInstanceRecordService.addFormInstanceRecord(
						groupId, ddmFormInstance.getFormInstanceId(),
						ddmFormValues, serviceContext);
			}
			else {
				ddmFormInstanceRecord =
					_ddmFormInstanceRecordService.updateFormInstanceRecord(
						ddmFormInstanceRecordVersion.getFormInstanceRecordId(),
						false, ddmFormValues, serviceContext);
			}
		}

		return ddmFormInstanceRecord;
	}

	private void _validatePublishStatus(
			ActionRequest actionRequest, DDMFormInstance ddmFormInstance)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String currentURL = ParamUtil.getString(actionRequest, "currentURL");

		DDMFormInstanceSettings ddmFormInstanceSettings =
			ddmFormInstance.getSettingsModel();

		String formLayoutURL =
			_addDefaultSharedFormLayoutPortalInstanceLifecycleListener.
				getFormLayoutURL(
					themeDisplay,
					ddmFormInstanceSettings.requireAuthentication());

		if (StringUtil.startsWith(currentURL, formLayoutURL) &&
			!ddmFormInstanceSettings.published()) {

			throw new FormInstanceNotPublishedException(
				"Form instance " + ddmFormInstance.getFormInstanceId() +
					" is not published");
		}
	}

	@Reference
	private AddDefaultSharedFormLayoutPortalInstanceLifecycleListener
		_addDefaultSharedFormLayoutPortalInstanceLifecycleListener;

	@Reference
	private AddFormInstanceRecordMVCCommandHelper
		_addFormInstanceMVCCommandHelper;

	@Reference
	private DDMContentLocalService _ddmContentLocalService;

	private DDMFormInstanceRecordService _ddmFormInstanceRecordService;

	@Reference
	private DDMFormInstanceRecordVersionLocalService
		_ddmFormInstanceRecordVersionLocalService;

	private DDMFormInstanceService _ddmFormInstanceService;

	@Reference
	private DDMFormUniqueFieldChecker _ddmFormUniqueFieldChecker;

	private DDMFormValuesFactory _ddmFormValuesFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

}