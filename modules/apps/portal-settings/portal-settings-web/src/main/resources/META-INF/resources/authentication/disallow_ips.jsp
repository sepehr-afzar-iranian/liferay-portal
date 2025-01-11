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

<aui:fieldset>
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />

	<aui:input helpMessage="enter-ips-to-disallow-uesrs-to-authentiacate" label="ip-ranges" name='<%= "settings--" + PropsKeys.AUTH_LOGIN_DISALLOW_IPS + "--" %>' type="textarea" value="<%= PrefsPropsUtil.getString(company.getCompanyId(), PropsKeys.AUTH_LOGIN_DISALLOW_IPS) %>" />
</aui:fieldset>