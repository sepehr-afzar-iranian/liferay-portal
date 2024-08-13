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
	AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
			EventTypes.CHECK_AUDIT, AuditEvent.class.getName(), 0,
			null);
	try {
		AuditRouterUtil.route(auditMessage);
	}
	catch (Exception exception) {
		throw new RuntimeException(exception);
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
		</liferay-portlet:renderURL>

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
			DisplayTerms displayTerms = searchContainer.getDisplayTerms();
			List<AuditEvent> auditEvents;

			if (displayTerms.isAdvancedSearch()) {
				total = AuditEventManagerUtil.getAuditEventsCount(themeDisplay.getCompanyId(), "", userId, userName, eventType, className, classPK, clientHost, clientIP, serverName, serverPort);

				searchContainer.setTotal(total);

				auditEvents = AuditEventManagerUtil.getAuditEvents(themeDisplay.getCompanyId(), "", userId, userName, eventType, className, classPK, clientHost, clientIP, serverName, serverPort, searchContainer.getStart(), searchContainer.getDelta());

				searchContainer.setResults(auditEvents);
			}
			else {
				String keywords = displayTerms.getKeywords();

				total = AuditEventManagerUtil.getAuditEventsCount(themeDisplay.getCompanyId(), keywords, "", "", "", "", "", "", "", "", "");

				searchContainer.setTotal(total);

				auditEvents = AuditEventManagerUtil.getAuditEvents(themeDisplay.getCompanyId(), keywords, "", "", "", "", "", "", "", "", "", searchContainer.getStart(), searchContainer.getDelta());

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