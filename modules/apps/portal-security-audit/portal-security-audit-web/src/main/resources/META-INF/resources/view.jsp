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
AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(EventTypes.CHECK_AUDIT, AuditEvent.class.getName(), 0, null);

auditMessage.setMessage("Admin just checked the audits.");

try {
	AuditRouterUtil.route(auditMessage);
}
catch (Exception exception) {
	_log.warn("Unable to route audit message", exception);
}
%>

<liferay-portlet:renderURL varImpl="searchURL" />

<clay:container-fluid
	cssClass="container-view"
>
	<aui:form action="<%= searchURL %>" method="get" name="fm">
		<liferay-portlet:renderURLParams varImpl="searchURL" />

		<liferay-portlet:renderURL varImpl="iteratorURL">
			<portlet:param name="className" value="<%= className %>" />
			<portlet:param name="classPK" value="<%= classPK %>" />
			<portlet:param name="clientHost" value="<%= clientHost %>" />
			<portlet:param name="clientIP" value="<%= clientIP %>" />
			<portlet:param name="eventType" value="<%= eventType %>" />
			<portlet:param name="serverName" value="<%= serverName %>" />
			<portlet:param name="serverPort" value="<%= serverPort %>" />
			<portlet:param name="userId" value="<%= userId %>" />
			<portlet:param name="userName" value="<%= userName %>" />
			<portlet:param name="endDateAmPm" value="<%= String.valueOf(endDateAmPm) %>" />
			<portlet:param name="endDateDay" value="<%= String.valueOf(endDateDay) %>" />
			<portlet:param name="endDateHour" value="<%= String.valueOf(endDateHour) %>" />
			<portlet:param name="endDateMinute" value="<%= String.valueOf(endDateMinute) %>" />
			<portlet:param name="endDateMonth" value="<%= String.valueOf(endDateMonth) %>" />
			<portlet:param name="endDateYear" value="<%= String.valueOf(endDateYear) %>" />
			<portlet:param name="startDateAmPm" value="<%= String.valueOf(startDateAmPm) %>" />
			<portlet:param name="startDateDay" value="<%= String.valueOf(startDateDay) %>" />
			<portlet:param name="startDateHour" value="<%= String.valueOf(startDateHour) %>" />
			<portlet:param name="startDateMinute" value="<%= String.valueOf(startDateMinute) %>" />
			<portlet:param name="startDateMonth" value="<%= String.valueOf(startDateMonth) %>" />
			<portlet:param name="startDateYear" value="<%= String.valueOf(startDateYear) %>" />
			<portlet:param name="sortOrderAsc" value="<%= String.valueOf(sortOrderAsc) %>" />
		</liferay-portlet:renderURL>

		<%
		String sortToggleIcon = sortOrderAsc ? "order-arrow-up" : "order-arrow-down";
		String sortToggleLabel = sortOrderAsc ? "oldest-first" : "newest-first";
		%>

		<liferay-portlet:renderURL varImpl="sortToggleURL">
			<portlet:param name="className" value="<%= className %>" />
			<portlet:param name="classPK" value="<%= classPK %>" />
			<portlet:param name="clientHost" value="<%= clientHost %>" />
			<portlet:param name="clientIP" value="<%= clientIP %>" />
			<portlet:param name="eventType" value="<%= eventType %>" />
			<portlet:param name="serverName" value="<%= serverName %>" />
			<portlet:param name="serverPort" value="<%= serverPort %>" />
			<portlet:param name="userId" value="<%= userId %>" />
			<portlet:param name="userName" value="<%= userName %>" />
			<portlet:param name="endDateAmPm" value="<%= String.valueOf(endDateAmPm) %>" />
			<portlet:param name="endDateDay" value="<%= String.valueOf(endDateDay) %>" />
			<portlet:param name="endDateHour" value="<%= String.valueOf(endDateHour) %>" />
			<portlet:param name="endDateMinute" value="<%= String.valueOf(endDateMinute) %>" />
			<portlet:param name="endDateMonth" value="<%= String.valueOf(endDateMonth) %>" />
			<portlet:param name="endDateYear" value="<%= String.valueOf(endDateYear) %>" />
			<portlet:param name="startDateAmPm" value="<%= String.valueOf(startDateAmPm) %>" />
			<portlet:param name="startDateDay" value="<%= String.valueOf(startDateDay) %>" />
			<portlet:param name="startDateHour" value="<%= String.valueOf(startDateHour) %>" />
			<portlet:param name="startDateMinute" value="<%= String.valueOf(startDateMinute) %>" />
			<portlet:param name="startDateMonth" value="<%= String.valueOf(startDateMonth) %>" />
			<portlet:param name="startDateYear" value="<%= String.valueOf(startDateYear) %>" />
			<portlet:param name="sortOrderAsc" value="<%= String.valueOf(!sortOrderAsc) %>" />
		</liferay-portlet:renderURL>

		<div class="audit-sort-toolbar d-flex align-items-center justify-content-end mb-2">
			<clay:link
				displayType="secondary"
				href="<%= sortToggleURL.toString() %>"
				icon="<%= sortToggleIcon %>"
				label="<%= LanguageUtil.get(request, sortToggleLabel) %>"
				small="<%= true %>"
				type="button"
			/>
		</div>

		<liferay-ui:search-container
			displayTerms="<%= new DisplayTerms(renderRequest) %>"
			emptyResultsMessage="there-are-no-events"
			iteratorURL="<%= iteratorURL %>"
		>
			<liferay-ui:search-form
				page="/event_search.jsp"
				servletContext="<%= application %>"
			/>

			<%
			int endDateDayHour = (endDateAmPm != Calendar.PM) ? endDateHour : endDateHour + 12;
			int startDateDayHour = (startDateAmPm != Calendar.PM) ? startDateHour : startDateHour + 12;

			Date endDate = PortalUtil.getDate(endDateMonth, endDateDay, endDateYear, endDateDayHour, endDateMinute, timeZone, null);
			Date startDate = PortalUtil.getDate(startDateMonth, startDateDay, startDateYear, startDateDayHour, startDateMinute, timeZone, null);

			DisplayTerms displayTerms = searchContainer.getDisplayTerms();
			List<AuditEvent> auditEvents;

			if (displayTerms.isAdvancedSearch()) {
				total = AuditEventManagerUtil.getAuditEventsCount(themeDisplay.getCompanyId(), "", userId, userName, eventType, className, classPK, clientHost, clientIP, serverName, serverPort, startDate, endDate);

				searchContainer.setTotal(total);

				auditEvents = AuditEventManagerUtil.getAuditEvents(themeDisplay.getCompanyId(), "", userId, userName, eventType, className, classPK, clientHost, clientIP, serverName, serverPort, startDate, endDate, searchContainer.getStart(), searchContainer.getDelta(), sortOrderAsc);

				searchContainer.setResults(auditEvents);
			}
			else {
				String keywords = displayTerms.getKeywords();

				total = AuditEventManagerUtil.getAuditEventsCount(themeDisplay.getCompanyId(), keywords, "", "", "", "", "", "", "", "", "", null, null);

				searchContainer.setTotal(total);

				auditEvents = AuditEventManagerUtil.getAuditEvents(themeDisplay.getCompanyId(), keywords, "", "", "", "", "", "", "", "", "", null, null, searchContainer.getStart(), searchContainer.getDelta(), sortOrderAsc);

				searchContainer.setResults(auditEvents);
			}
			%>

			<liferay-ui:search-container-row
				className="com.liferay.portal.security.audit.storage.model.AuditEvent"
				escapedModel="<%= true %>"
				keyProperty="auditEventId"
				modelVar="event"
			>
				<liferay-portlet:renderURL varImpl="rowURL">
					<portlet:param name="mvcPath" value="/view_audit_event.jsp" />
					<portlet:param name="auditEventId" value="<%= String.valueOf(event.getAuditEventId()) %>" />
				</liferay-portlet:renderURL>

				<%@ include file="/search_columns.jspf" %>
			</liferay-ui:search-container-row>

			<div class="separator"><!-- --></div>

			<liferay-ui:search-iterator
				searchContainer="<%= searchContainer %>"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>

<%!
private static Log _log = LogFactoryUtil.getLog("com_liferay_portal-security-audit-web.view_jsp");
%>