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
import com.liferay.dynamic.data.mapping.form.web.internal.configuration.DDMFormWebConfiguration;
import com.liferay.dynamic.data.mapping.form.web.internal.configuration.activator.DDMFormWebConfigurationActivator;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordExporter;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordExporterByTrackingCode;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordExporterRequest;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordExporterResponse;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.dynamic.data.mapping.util.comparator.DDMFormInstanceRecordModifiedDateComparator;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Marcellus Tavares
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM_ADMIN,
		"mvc.command.name=/dynamic_data_mapping_form/export_form_instance"
	},
	service = MVCResourceCommand.class
)
public class ExportFormInstanceMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		String fileExtension = ParamUtil.getString(
			resourceRequest, "fileExtension");

		DDMFormWebConfiguration ddmFormWebConfiguration =
			_ddmFormWebConfigurationActivator.getDDMFormWebConfiguration();

		if (StringUtil.equals(fileExtension, "csv") &&
			StringUtil.equals(
				ddmFormWebConfiguration.csvExport(), "disabled")) {

			return;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Date startDate = _getDate(
			resourceRequest, themeDisplay.getTimeZone(),
			themeDisplay.getLocale(), "startDateDay", "startDateMonth",
			"startDateYear", "startDateAmPm", "startDateHour",
			"startDateMinute");

		Date endDate = _getDate(
			resourceRequest, themeDisplay.getTimeZone(),
			themeDisplay.getLocale(), "endDateDay", "endDateMonth",
			"endDateYear", "endDateAmPm", "endDateHour", "endDateMinute");

		boolean startEnable = ParamUtil.getBoolean(
			resourceRequest, "startEnable");
		boolean endEnable = ParamUtil.getBoolean(resourceRequest, "endEnable");

		long formInstanceId = ParamUtil.getLong(
			resourceRequest, "formInstanceId");

		DDMFormInstanceRecordExporterRequest.Builder builder =
			DDMFormInstanceRecordExporterRequest.Builder.newBuilder(
				formInstanceId, fileExtension);

		OrderByComparator<DDMFormInstanceRecord> orderByComparator =
			_getDDMFormInstanceOrderByComparator("desc");

		List<DDMFormInstanceRecord> ddmFormInstanceRecords =
			ddmFormInstanceRecordLocalService.getFormInstanceRecords(
				formInstanceId, 0, -1, -1, orderByComparator);

		Stream<DDMFormInstanceRecord> recordStream =
			ddmFormInstanceRecords.stream();

		List<DDMFormInstanceRecord> selectedRecord = recordStream.filter(
			ddmFormInstanceRecord ->
				(startEnable ?
					!startDate.after(ddmFormInstanceRecord.getModifiedDate()) :
						true) &&
				(endEnable ?
					!endDate.before(ddmFormInstanceRecord.getModifiedDate()) :
						true)
		).collect(
			Collectors.toList()
		);

		int start;
		int end;

		if (selectedRecord.isEmpty()) {
			start = -2;
			end = -2;
		}
		else {
			start = 0;
			end = selectedRecord.size();
		}

		DDMFormInstanceRecordExporterRequest
			ddmFormInstanceRecordExporterRequest = builder.withLocale(
				themeDisplay.getLocale()
			).withStatus(
				WorkflowConstants.STATUS_APPROVED
			).withStart(
				start
			).withEnd(
				end
			).withOrderByComparator(
				orderByComparator
			).build();

		DDMFormInstance formInstance = _ddmFormInstanceService.getFormInstance(
			formInstanceId);

		List<DDMFormField> ddmFormFieldList = getFormFields(formInstance);

		boolean formHasPriceField = false;

		for (DDMFormField ddmFormField : ddmFormFieldList) {
			if (Validator.isNotNull(ddmFormField.getProperty("priceField")) &&
				(Boolean)ddmFormField.getProperty("priceField")) {

				formHasPriceField = true;

				break;
			}
		}

		DDMFormInstanceRecordExporterResponse
			ddmFormInstanceRecordExporterResponse;

		if (formHasPriceField)
			ddmFormInstanceRecordExporterResponse =
				_ddmFormInstanceRecordExporter.export(
					ddmFormInstanceRecordExporterRequest);
			else
			ddmFormInstanceRecordExporterResponse =
				_ddmFormInstanceRecordExporterByTrackingCode.export(
					ddmFormInstanceRecordExporterRequest);

		String fileName =
			formInstance.getName(themeDisplay.getLocale()) + CharPool.PERIOD +
				fileExtension;

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse, fileName,
			ddmFormInstanceRecordExporterResponse.getContent(),
			MimeTypesUtil.getContentType(fileName));
	}

	protected List<DDMFormField> getFormFields(DDMFormInstance ddmFormInstance)
		throws Exception {

		DDMStructure ddmStructure = ddmFormInstance.getStructure();

		return ddmStructure.getDDMFormFields(false);
	}

	protected void unsetDDMFormWebConfigurationActivator(
		DDMFormWebConfigurationActivator ddmFormWebConfigurationActivator) {

		_ddmFormWebConfigurationActivator = null;
	}

	@Reference
	protected DDMFormInstanceRecordLocalService
		ddmFormInstanceRecordLocalService;

	private Date _getDate(
		ResourceRequest resourceRequest, TimeZone timeZone, Locale locale,
		String dayParam, String monthParam, String yearParam, String amPmParam,
		String hourParam, String minuteParam) {

		int day = ParamUtil.getInteger(resourceRequest, dayParam);
		int month = ParamUtil.getInteger(resourceRequest, monthParam);
		int year = ParamUtil.getInteger(resourceRequest, yearParam);
		int amPm = ParamUtil.getInteger(resourceRequest, amPmParam);
		int hour = ParamUtil.getInteger(resourceRequest, hourParam);
		int minute = ParamUtil.getInteger(resourceRequest, minuteParam);

		Calendar calendar = CalendarFactoryUtil.getCalendar(timeZone, locale);

		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.HOUR, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.AM_PM, amPm);

		return calendar.getTime();
	}

	private OrderByComparator<DDMFormInstanceRecord>
		_getDDMFormInstanceOrderByComparator(String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		return new DDMFormInstanceRecordModifiedDateComparator(orderByAsc);
	}

	@Reference
	private DDMFormInstanceRecordExporter _ddmFormInstanceRecordExporter;

	@Reference
	private DDMFormInstanceRecordExporterByTrackingCode
		_ddmFormInstanceRecordExporterByTrackingCode;

	@Reference
	private DDMFormInstanceService _ddmFormInstanceService;

	@Reference(
		cardinality = ReferenceCardinality.OPTIONAL,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		unbind = "unsetDDMFormWebConfigurationActivator"
	)
	private volatile DDMFormWebConfigurationActivator
		_ddmFormWebConfigurationActivator;

}