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

<%@ include file="/init.jsp" %>

<%
SearchContainer<?> searchContainer = (SearchContainer<?>)request.getAttribute("liferay-ui:search:searchContainer");
%>

<liferay-ui:search-toggle
	buttonLabel="search"
	displayTerms="<%= searchContainer.getDisplayTerms() %>"
	id="toggle_id_audit_event_search"
>
	<aui:select label="event-type" name="eventType">

		<%
		for (String selEventType : eventTypes) {
		%>

		<aui:option label="<%= HtmlUtil.escapeAttribute(selEventType) %>" selected="<%= eventType.equals(selEventType) %>" value="<%= selEventType %>" />

		<%
		}
		%>

	</aui:select>

	<aui:input label="user-id" name="userId" value="<%= userId %>" />

	<aui:input label="user-name" name="userName" value="<%= userName %>" />

	<aui:input label="resource-id" name="classPK" value="<%= classPK %>" />

	<%--<aui:input label="resource-name" name="className" value="<%= className %>" />--%>
	<aui:select label="resource-name" name="className">

		<%
			for (String selResourceName : resourceNames) {
		%>

		<aui:option label="<%= HtmlUtil.escapeAttribute(selResourceName) %>" selected="<%= eventType.equals(selResourceName) %>" value="<%= selResourceName %>" />

		<%
			}
		%>

	</aui:select>

	<aui:input label="client-ip" name="clientIP" value="<%= clientIP %>" />

	<aui:input label="client-host" name="clientHost" value="<%= clientHost %>" />

	<aui:input label="server-name" name="serverName" value="<%= serverName %>" />

	<aui:input label="server-port" name="serverPort" value="<%= serverPort %>" />

	<aui:field-wrapper label="start-date">
		<liferay-ui:input-date
				dayParam="startDateDay"
				dayValue="<%= startDateDay %>"
				monthParam="startDateMonth"
				monthValue="<%= startDateMonth %>"
				name="startDate"
				yearParam="startDateYear"
				yearValue="<%= startDateYear %>"
		/>

		<liferay-ui:input-time
				amPmParam="startDateAmPm"
				amPmValue="<%= startDateAmPm %>"
				hourParam="startDateHour"
				hourValue="<%= startDateHour %>"
				minuteParam="startDateMinute"
				minuteValue="<%= startDateMinute %>"
				name="startDateTime"
		/>
	</aui:field-wrapper>

	<aui:field-wrapper label="end-date">
		<liferay-ui:input-date
				dayParam="endDateDay"
				dayValue="<%= endDateDay %>"
				monthParam="endDateMonth"
				monthValue="<%= endDateMonth %>"
				name="endDate"
				yearParam="endDateYear"
				yearValue="<%= endDateYear %>"
		/>

		<liferay-ui:input-time
				amPmParam="endDateAmPm"
				amPmValue="<%= endDateAmPm %>"
				hourParam="endDateHour"
				hourValue="<%= endDateHour %>"
				minuteParam="endDateMinute"
				minuteValue="<%= endDateMinute %>"
				name="endDateTime"
		/>
	</aui:field-wrapper>
</liferay-ui:search-toggle>