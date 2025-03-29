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

<%@ include file="/html/portal/init.jsp" %>

<%
session.removeAttribute(WebKeys.SESSION_TERMINATED_REASON);

String loginURL = themeDisplay.getURLSignIn();
%>

<%@ include file="/html/portal/expire_session.jsp" %>
<div class="alert alert-warning session-expired-alert" style="position: fixed; bottom: 10px; right: 10px; z-index: 9999; width: 400px; padding: 15px; box-shadow: 0 2px 5px rgba(0,0,0,.2); direction: rtl;">
	<button aria-label="<liferay-ui:message key="close" />" class="close" data-dismiss="alert" type="button">
		<span aria-hidden="true">&times;</span>
	</button>

	<strong><i class="icon-exclamation-sign"></i><liferay-ui:message key="your-session-has-expired-due-to-simultaneous-login-from-another-location" /></strong>
</div>

<h3 class="alert alert-danger">
	<liferay-ui:message key="session-expired" />
</h3>

<liferay-ui:message key="your-session-has-expired-due-to-simultaneous-login-from-another-location" />

<br /><br />
<p>
	<liferay-ui:message key="if-you-were-not-redirected-to-the-login-page" />

	<a href= <%= loginURL %> ><liferay-ui:message key="click-here" /></a></p>
<script>
	setTimeout(function() {
		window.location.href = '<%= loginURL %>';
	}, 5000);
</script>