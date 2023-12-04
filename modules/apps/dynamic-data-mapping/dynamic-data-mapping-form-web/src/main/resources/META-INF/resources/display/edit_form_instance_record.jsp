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

<%@ include file="/display/init.jsp" %>

<%
DDMFormInstance formInstance = ddmFormDisplayContext.getFormInstance();

DDMFormInstanceRecord formInstanceRecord = ddmFormDisplayContext.getFormInstanceRecord();

DDMFormInstanceRecordVersion formInstanceRecordVersion = null;

if (formInstanceRecord != null) {
	formInstanceRecordVersion = formInstanceRecord.getLatestFormInstanceRecordVersion();
}

String title = ParamUtil.getString(request, "title");

renderResponse.setTitle(GetterUtil.get(title, LanguageUtil.get(request, "view-form")));

String submitLabel = LanguageUtil.get(request, "preview");
%>

<clay:container-fluid>
	<c:if test="<%= formInstanceRecordVersion != null %>">
		<aui:model-context bean="<%= formInstanceRecordVersion %>" model="<%= DDMFormInstanceRecordVersion.class %>" />
	</c:if>
</clay:container-fluid>

<clay:container-fluid
	cssClass="ddm-form-builder-app editing-form-entry"
>
	<portlet:actionURL name="/dynamic_data_mapping_form/add_form_instance_record" var="editFormInstanceRecordActionURL" />

	<aui:form action="<%= editFormInstanceRecordActionURL %>" data-DDMFormInstanceId="<%= ddmFormDisplayContext.getFormInstanceId() %>" data-senna-off="true" method="post" name="fm">
		<aui:input name="back" type="hidden" value="<%= PortalUtil.getCurrentURL(request) %>" />
		<aui:input name="confirmOnSubmit" type="hidden" value="<%= true %>" />
		<aui:input name="formInstanceRecordId" type="hidden" value="<%= ddmFormDisplayContext.getFormInstanceRecordId() %>" />
		<aui:input name="formInstanceId" type="hidden" value="<%= ddmFormDisplayContext.getFormInstanceId() %>" />
		<aui:input name="workflowAction" type="hidden" value="<%= WorkflowConstants.ACTION_SAVE_DRAFT %>" />

		<div class="ddm-form-basic-info">

			<%
			String languageId = ddmFormDisplayContext.getDefaultLanguageId();

			Locale displayLocale = LocaleUtil.fromLanguageId(languageId);
			%>

			<h1 class="ddm-form-name"><%= HtmlUtil.escape(formInstance.getName(displayLocale)) %></h1>

			<%
			String description = HtmlUtil.escape(formInstance.getDescription(displayLocale));
			%>

			<c:if test="<%= Validator.isNotNull(description) %>">
				<h5 class="ddm-form-description"><%= description %></h5>
			</c:if>
		</div>

		<liferay-ui:error exception="<%= DDMFormValuesValidationException.UniqueValue.class %>">

			<%
			DDMFormValuesValidationException.UniqueValue uv = (DDMFormValuesValidationException.UniqueValue)errorException;
			%>

			<liferay-ui:message arguments="<%= HtmlUtil.escape(uv.getFieldName()) %>" key="this-value-already-exists-for-the-field-x" translateArguments="<%= false %>" />
		</liferay-ui:error>

		<%= ddmFormDisplayContext.getDDMFormHTML(false, submitLabel) %>
	</aui:form>
</clay:container-fluid>