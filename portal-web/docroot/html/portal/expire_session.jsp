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
session.invalidate();

try {
	String userFullName = user.getFullName();
	StringBuilder sb = new StringBuilder();
	long userId = user.getUserId();

	sb.append(userFullName);
	sb.append("'s session was expired due to inactivity");

	AuditMessage auditMessage = new AuditMessage("INVALIDATE SESSION", user.getCompanyId(), userId, userFullName, User.class.getName(), String.valueOf(userId), sb.toString());

	AuditRouterUtil.route(auditMessage);
}
catch (Exception exception) {
	if (_log_expire.isWarnEnabled()) {
		_log_expire.warn("Unable to route audit message", exception);
	}
}
%>

<%!
private static Log _log_expire = LogFactoryUtil.getLog("portal_web.docroot.html.portal.expire_session_jsp");
%>