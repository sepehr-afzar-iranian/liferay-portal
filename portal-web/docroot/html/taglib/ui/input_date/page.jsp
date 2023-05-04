<%--
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
--%>

<%@ include file="/html/taglib/init.jsp" %>

<%
String randomNamespace = PortalUtil.generateRandomKey(request, "taglib_ui_input_date_page") + StringPool.UNDERLINE;

if (GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:input-date:disableNamespace"))) {
	namespace = StringPool.BLANK;
}

String dateTogglerCheckboxLabel = GetterUtil.getString((String)request.getAttribute("liferay-ui:input-date:dateTogglerCheckboxLabel"), "disable");
boolean disabled = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:input-date:disabled"));
String dayParam = GetterUtil.getString((String)request.getAttribute("liferay-ui:input-date:dayParam"));
int dayValue = GetterUtil.getInteger((String)request.getAttribute("liferay-ui:input-date:dayValue"));
String formName = GetterUtil.getString((String)request.getAttribute("liferay-ui:input-date:formName"));
String monthParam = GetterUtil.getString((String)request.getAttribute("liferay-ui:input-date:monthParam"));
int monthValue = GetterUtil.getInteger((String)request.getAttribute("liferay-ui:input-date:monthValue"));
String name = GetterUtil.getString((String)request.getAttribute("liferay-ui:input-date:name"));
boolean nullable = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:input-date:nullable"));
boolean required = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:input-date:required"));
boolean showDisableCheckbox = GetterUtil.getBoolean((String)request.getAttribute("liferay-ui:input-date:showDisableCheckbox"));
String yearParam = GetterUtil.getString((String)request.getAttribute("liferay-ui:input-date:yearParam"));

int yearValue = GetterUtil.getInteger((String)request.getAttribute("liferay-ui:input-date:yearValue"));

// Shamsi Datepicker does not supports lower than 1301 Hijri year

if ((yearValue > 0) && (yearValue < 1922)) {
	yearValue = 1922;
}

String dayParamId = namespace + HtmlUtil.getAUICompatibleId(dayParam);
String monthParamId = namespace + HtmlUtil.getAUICompatibleId(monthParam);
String nameId = namespace + HtmlUtil.getAUICompatibleId(name);
String yearParamId = namespace + HtmlUtil.getAUICompatibleId(yearParam);

Calendar calendar = null;

if (required && (yearValue == 0) && (monthValue == -1) && (dayValue == 0)) {
	calendar = CalendarFactoryUtil.getCalendar(timeZone);

	dayValue = calendar.get(Calendar.DAY_OF_MONTH);
	monthValue = calendar.get(Calendar.MONTH);
	yearValue = calendar.get(Calendar.YEAR);
}
else {
	calendar = CalendarFactoryUtil.getCalendar(yearValue, monthValue, dayValue);
}

String mask = _MASK_YMD;
String simpleDateFormatPattern = _SIMPLE_DATE_FORMAT_PATTERN_HTML5;

DateFormat shortDateFormat = DateFormat.getDateInstance(DateFormat.SHORT);

SimpleDateFormat shortDateFormatSimpleDateFormat = (SimpleDateFormat)shortDateFormat;

simpleDateFormatPattern = shortDateFormatSimpleDateFormat.toPattern();

simpleDateFormatPattern = simpleDateFormatPattern.replaceAll("yyyy", "y");
simpleDateFormatPattern = simpleDateFormatPattern.replaceAll("yy", "y");
simpleDateFormatPattern = simpleDateFormatPattern.replaceAll("MM", "M");
simpleDateFormatPattern = simpleDateFormatPattern.replaceAll("dd", "d");

simpleDateFormatPattern = simpleDateFormatPattern.replaceAll("y", "yyyy");
simpleDateFormatPattern = simpleDateFormatPattern.replaceAll("M", "MM");
simpleDateFormatPattern = simpleDateFormatPattern.replaceAll("d", "dd");

mask = simpleDateFormatPattern;

mask = mask.replaceAll("yyyy", "%Y");
mask = mask.replaceAll("MM", "%m");
mask = mask.replaceAll("dd", "%d");

String dayAbbreviation = LanguageUtil.get(resourceBundle, "day-abbreviation");
String monthAbbreviation = LanguageUtil.get(resourceBundle, "month-abbreviation");
String yearAbbreviation = LanguageUtil.get(resourceBundle, "year-abbreviation");

String[] dateAbbreviations = {"M", "d", "y"};
String[] localizedDateAbbreviations = {monthAbbreviation, dayAbbreviation, yearAbbreviation};

boolean nullDate = false;

if (nullable && !required && (dayValue == 0) && (monthValue == -1) && (yearValue == 0)) {
	nullDate = true;
}

String dateString = null;

Format format = FastDateFormatFactoryUtil.getSimpleDateFormat(simpleDateFormatPattern, locale);

if (nullable && nullDate) {
	dateString = StringPool.BLANK;
}
else {
	dateString = format.format(calendar.getTime());
}
%>

<span class="lfr-input-date" id="<%= randomNamespace %>displayDate">
	<span id="<%= nameId %>displayDate"></span>

	<input <%= disabled ? "disabled=\"disabled\"" : "" %> id="<%= dayParamId %>" name="<%= namespace + HtmlUtil.escapeAttribute(dayParam) %>" type="hidden" value="<%= dayValue %>" />
	<input <%= disabled ? "disabled=\"disabled\"" : "" %> id="<%= monthParamId %>" name="<%= namespace + HtmlUtil.escapeAttribute(monthParam) %>" type="hidden" value="<%= monthValue %>" />
	<input <%= disabled ? "disabled=\"disabled\"" : "" %> id="<%= yearParamId %>" name="<%= namespace + HtmlUtil.escapeAttribute(yearParam) %>" type="hidden" value="<%= yearValue %>" />
</span>

<c:if test="<%= nullable && !required && showDisableCheckbox %>">

	<%
	String dateTogglerCheckboxName = TextFormatter.format(dateTogglerCheckboxLabel, TextFormatter.M);
	%>

	<aui:input label="<%= dateTogglerCheckboxLabel %>" name="<%= randomNamespace + dateTogglerCheckboxName %>" type="checkbox" value="<%= disabled %>" />

	<script>
		(function() {
			var form = document.<%= namespace + formName %>;

			var checkbox = document.getElementById('<%= namespace + randomNamespace + dateTogglerCheckboxName %>');

			if (checkbox) {
				checkbox.addEventListener(
					'click',
					function(event) {
						var checked = checkbox.checked;

						if (!form) {
							form = checkbox.form;
						}

						var dayField = Liferay.Util.getFormElement(form, '<%= HtmlUtil.escapeJS(dayParam) %>');

						if (dayField) {
							dayField.disabled = checked;

							if (checked) {
								dayField.value = '';
							}
						}

						var inputDateField = Liferay.Util.getFormElement(form, '<%= HtmlUtil.getAUICompatibleId(name) %>');

						if (inputDateField) {
							inputDateField.disabled = checked;

							if (checked) {
								inputDateField.value = '';
							}
						}

						var monthField = Liferay.Util.getFormElement(form, '<%= HtmlUtil.escapeJS(monthParam) %>');

						if (monthField) {
							monthField.disabled = checked;

							if (checked) {
								monthField.value = '';
							}
						}

						var yearField = Liferay.Util.getFormElement(form, '<%= HtmlUtil.escapeJS(yearParam) %>');

						if (yearField) {
							yearField.disabled = checked;

							if (checked) {
								yearField.value = '';
							}
						}
					}
				);
			}
		})();
	</script>
</c:if>

<aui:script require="frontend-js-web/liferay/toast/commands/DatePicker.es as datePicker">
	datePicker.datePicker(
		'<%=nameId%>displayDate',
		{
			spritemap: '<%= themeDisplay.getPathThemeImages() %>/lexicon/icons.svg',
			yearParamId: '<%= yearParamId %>',
			monthParamId: '<%= monthParamId %>',
			dayParamId: '<%= dayParamId %>',
			local: '<%=themeDisplay.getLocale().getLanguage().equals("fa") ? "fa" : "en" %>',
			value: '<%=dateString%>',
			disabled: <%=disabled%>,
			inputId: '<%=nameId%>'
		}
	);
</aui:script>

<%!
private static final String _SIMPLE_DATE_FORMAT_PATTERN_HTML5 = "yyyy-MM-dd";

private static final String _MASK_YMD = "%Y/%m/%d";
%>