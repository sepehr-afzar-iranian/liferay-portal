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
String backUrl = ParamUtil.getString(request, "backUrl");

DDMFormInstanceRecordVersion ddmFormInstanceRecordVersion = ddmFormDisplayContext.getDDMFormInstanceRecordVersion();

renderResponse.setTitle(LanguageUtil.get(request, "preview-form"));
%>

<clay:container-fluid>
	<c:if test="<%= ddmFormInstanceRecordVersion != null %>">
		<aui:model-context bean="<%= ddmFormInstanceRecordVersion %>" model="<%= DDMFormInstanceRecordVersion.class %>" />
	</c:if>
</clay:container-fluid>

<clay:container-fluid
	cssClass="editing-form-entry-admin"
>
	<portlet:actionURL name="/dynamic_data_mapping_form/add_form_instance_record" var="editFormInstanceRecordActionURL" />

	<aui:form action="<%= editFormInstanceRecordActionURL %>" method="post" name="fm">
		<aui:input name="formInstanceRecordId" type="hidden" value="<%= ddmFormInstanceRecordVersion.getFormInstanceRecordId() %>" />
		<aui:input name="formInstanceId" type="hidden" value="<%= ddmFormInstanceRecordVersion.getFormInstanceId() %>" />
		<aui:input name="workflowAction" type="hidden" value="<%= WorkflowConstants.ACTION_PUBLISH %>" />

		<aui:input name="ddmFormValues" type="hidden" value="<%= ddmFormDisplayContext.getDDMFormValuesString(ddmFormInstanceRecordVersion.getDDMFormValues()) %>" />

		<%= ddmFormDisplayContext.getDDMFormHTML(true) %>
		<aui:button-row>
			<aui:button cssClass="float-right lfr-ddm-form-submit" type="submit" value="submit" />

			<aui:button cssClass="float-right" href="<%= backUrl %>" type="cancel" value="edit" />
		</aui:button-row>
	</aui:form>
</clay:container-fluid>