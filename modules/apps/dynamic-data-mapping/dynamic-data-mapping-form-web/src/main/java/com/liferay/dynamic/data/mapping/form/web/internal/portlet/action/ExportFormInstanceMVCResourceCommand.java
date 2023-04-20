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
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordExporterRequest;
import com.liferay.dynamic.data.mapping.io.exporter.DDMFormInstanceRecordExporterResponse;
import com.liferay.dynamic.data.mapping.model.DDMFormInstance;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceRecordLocalService;
import com.liferay.dynamic.data.mapping.service.DDMFormInstanceService;
import com.liferay.dynamic.data.mapping.util.comparator.DDMFormInstanceRecordModifiedDateComparator;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.PortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
			String startDateYear = (String)resourceRequest.getAttribute(
			"Year");
		System.out.println("startDateYear = " + startDateYear);


		Date startDate = getDate(resourceRequest,themeDisplay.getTimeZone(),
			"startDateDay","startDateMonth","startDateYear",
			"startDateAmPm","startDateHour","startDateMinute"
		);
		Date endDate = getDate(resourceRequest,themeDisplay.getTimeZone(),
			"endDateDay","endDateMonth","endDateYear",
			"endDateAmPm","endDateHour","endDateMinute"
		);
		boolean enableStart = ParamUtil.getBoolean(resourceRequest,"enableStart");
		System.out.println("enableStart = " + enableStart);
		boolean enableEnd = ParamUtil.getBoolean(resourceRequest,"enableEnd");
		System.out.println("enableEnd = " + enableEnd);


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
		System.out.println(
			"startDate = " + startDate);
		System.out.println(
			"endDate = " + endDate);
		for (DDMFormInstanceRecord d:ddmFormInstanceRecords
		) {
			System.out.println("date: "+d.getModifiedDate());
		}

		Stream<DDMFormInstanceRecord> recordStream =
			ddmFormInstanceRecords.stream();
		List<DDMFormInstanceRecord> selectedRecord = recordStream.filter(
			ddmFormInstanceRecord ->
				(enableStart ? !startDate.after(ddmFormInstanceRecord.getModifiedDate()):true) &&
				(enableEnd ? !endDate.before(ddmFormInstanceRecord.getModifiedDate()):true)
		).collect(
			Collectors.toList());

		int start ;
		int end ;
		if(selectedRecord.size()==0){
			start=-2;
			end = -2;
		}else {
			start = 0;
			end = selectedRecord.size();
		}

		DDMFormInstanceRecordExporterRequest
			ddmFormInstanceRecordExporterRequest = builder.withLocale(
				themeDisplay.getLocale()
			).withStatus(
				WorkflowConstants.STATUS_APPROVED
			).withOrderByComparator(
			orderByComparator
			).withStart(
				start
			).withEnd(
				end
			).build();

		DDMFormInstanceRecordExporterResponse
			ddmFormInstanceRecordExporterResponse =
				_ddmFormInstanceRecordExporter.export(
					ddmFormInstanceRecordExporterRequest);

		DDMFormInstance formInstance = _ddmFormInstanceService.getFormInstance(
			formInstanceId);

		String fileName =
			formInstance.getName(themeDisplay.getLocale()) + CharPool.PERIOD +
				fileExtension;

		PortletResponseUtil.sendFile(
			resourceRequest, resourceResponse, fileName,
			ddmFormInstanceRecordExporterResponse.getContent(),
			MimeTypesUtil.getContentType(fileName));
	}
	private Date getDate(ResourceRequest resourceRequest,TimeZone timeZone,
						 String dayParam,String monthParam,String yearParam,
						 String amPmParam,String hourParam,String minuteParam)
		throws PortalException {
		Calendar calendar = Calendar.getInstance();
		int day = ParamUtil.getInteger(resourceRequest, dayParam);
		System.out.println(day);
		int month = ParamUtil.getInteger(resourceRequest, monthParam);
		System.out.println(month);
		int year = ParamUtil.getInteger(resourceRequest, yearParam);
		System.out.println(year);
		int amPm = ParamUtil.getInteger(resourceRequest, amPmParam);
		System.out.println(amPm);
		int hour = ParamUtil.getInteger(resourceRequest, hourParam);
		System.out.println(hour);
		int minute = ParamUtil.getInteger(resourceRequest, minuteParam);
/*
		calendar.set(Calendar.DATE,day);
		calendar.set(Calendar.MONTH,month);
		calendar.set(Calendar.YEAR,year);
		calendar.set(Calendar.AM_PM,amPm);
		calendar.set(Calendar.HOUR,hour);
		calendar.set(Calendar.MINUTE,minute);
		calendar.set(Calendar.SECOND,0);*/
		/*calendar.set(Calendar.AM_PM,amPm);
		calendar.set(year,month,day,hour,minute,0);*/


		java.util.Calendar endTimeJCalendar= CalendarFactoryUtil.getCalendar(year,month,day,hour,minute,0,0,timeZone);
		endTimeJCalendar.set(Calendar.AM_PM,amPm);
		return endTimeJCalendar.getTime();
	}
	private OrderByComparator<DDMFormInstanceRecord>
	_getDDMFormInstanceOrderByComparator(String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		return new DDMFormInstanceRecordModifiedDateComparator(orderByAsc);
	}

	protected void unsetDDMFormWebConfigurationActivator(
		DDMFormWebConfigurationActivator ddmFormWebConfigurationActivator) {

		_ddmFormWebConfigurationActivator = null;
	}

	@Reference
	private DDMFormInstanceRecordExporter _ddmFormInstanceRecordExporter;

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

	@Reference
	protected DDMFormInstanceRecordLocalService
		ddmFormInstanceRecordLocalService;
	@Reference
	protected Portal _portal;

}