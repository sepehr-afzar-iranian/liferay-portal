<%--
/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 *
 *
 *
 */
--%>

<%@ include file="/init.jsp" %>

<%
String referrer = (String)request.getAttribute(WebKeys.REFERER);

long auditEventId = ParamUtil.getLong(request, "auditEventId");

AuditEvent auditEvent = null;

String eventTypeAction = StringPool.BLANK;

if (auditEventId > 0) {
	auditEvent = AuditEventManagerUtil.fetchAuditEvent(auditEventId);

	if (auditEvent != null) {
		auditEvent = auditEvent.toEscapedModel();

		eventTypeAction = (String)PortalClassInvoker.invoke(new MethodKey(ClassResolverUtil.resolve("com.liferay.portal.kernel.security.permission.ResourceActionsUtil", PortalClassLoaderUtil.getClassLoader()), "getAction", HttpServletRequest.class, String.class), request, auditEvent.getEventType());
	}
}
%>

<clay:container-fluid
        cssClass="widget-mode-detail-header"
>

    <liferay-ui:header
            backURL='<%= Validator.isNotNull(referrer) ? referrer : "javascript:history.go(-1);" %>'
            escapeXml="<%= false %>"
            localizeTitle="<%= auditEvent == null %>"
            title='<%= (auditEvent == null) ? "audit-event" : auditEvent.getEventType() + " (" + eventTypeAction + ")" %>'
    />
    <c:choose>
        <c:when test="<%= auditEvent == null %>">
            <div class="portlet-msg-error">
                <liferay-ui:message key="the-event-could-not-be-found"/>
            </div>
        </c:when>
        <c:otherwise>
            <clay:sheet-section>
                <div class="table-responsive">

                    <table class="table table-autofit">
                        <thead>
                        <tr>
                            <th class="table-cell-expand table-title">
                                <liferay-ui:message key="title"/>
                            </th>


                            <th>
                                <liferay-ui:message key="value"/>
                            </th>
                        </tr>
                        </thead>

                        <tbody>
                        <tr>
                            <td>
                                <liferay-ui:message key="event-id"/>
                            </td>
                            <td>
                                <%= auditEvent.getAuditEventId() %>
                            </td>
                        </tr>


                        <tr>
                            <td>
                                <liferay-ui:message
                                        key="message"/>
                            </td>
                            <td>
                                <%= Validator.isNotNull(auditEvent.getMessage()) ? auditEvent.getMessage() : LanguageUtil.get(request, "none") %>
                            </td>
                        </tr>

                        <tr>
                            <td>
                                <liferay-ui:message key="create-date"/>
                            </td>
                            <td>
                                <%= dateFormatDateTime.format(auditEvent.getCreateDate()) %>
                            </td>
                        </tr>

                        <tr>
                            <td>
                                <liferay-ui:message key="resource-id"/>
                            </td>
                            <td>
                                <%= auditEvent.getClassPK() %>
                            </td>
                        </tr>

                        <tr>
                            <td>
                                <liferay-ui:message key="resource-name"/>
                            </td>
                            <td>
                                <%= auditEvent.getClassName() %>
                                (<%= (String) PortalClassInvoker.invoke(new MethodKey(ClassResolverUtil.resolve("com.liferay.portal.kernel.security.permission.ResourceActionsUtil", PortalClassLoaderUtil.getClassLoader()), "getModelResource", HttpServletRequest.class, String.class), request, auditEvent.getClassName()) %>
                                )
                            </td>
                        </tr>

                        <tr>
                            <td>
                                <liferay-ui:message key="resource-action"/>
                            </td>
                            <td>
                                <%= auditEvent.getEventType() %>

                                (<%= eventTypeAction %>)
                            </td>
                        </tr>

                        <tr>
                            <td>
                                <liferay-ui:message key="user-id"/>
                            </td>
                            <td>
                                <%= auditEvent.getUserId() %>

                            </td>
                        </tr>
                        <tr>
                            <td>
                                <liferay-ui:message key="user-name"/>
                            </td>
                            <td>
                                <%= auditEvent.getUserName() %>

                            </td>
                        </tr>
                        <tr>
                            <td>
                                <liferay-ui:message key="client-host"/>
                            </td>
                            <td>
                                <%= Validator.isNotNull(auditEvent.getClientHost()) ? auditEvent.getClientHost() : LanguageUtil.get(request, "none") %>

                            </td>
                        </tr>
                        <tr>
                            <td>
                                <liferay-ui:message key="client-ip"/>
                            </td>
                            <td>
                                <%= Validator.isNotNull(auditEvent.getClientIP()) ? auditEvent.getClientIP() : LanguageUtil.get(request, "none") %>

                            </td>
                        </tr>
                        <tr>
                            <td>
                                <liferay-ui:message key="server-name"/>
                            </td>
                            <td>
                                <%= Validator.isNotNull(auditEvent.getServerName()) ? auditEvent.getServerName() : LanguageUtil.get(request, "none") %>

                            </td>
                        </tr>

                        <tr>
                            <td>
                                <liferay-ui:message key="session-id"/>
                            </td>
                            <td>
                                <%= Validator.isNotNull(auditEvent.getSessionID()) ? auditEvent.getSessionID() : LanguageUtil.get(request, "none") %>
                            </td>
                        </tr>

                        <tr>
                            <td>
                                <liferay-ui:message
                                        key="additional-information"/>
                            </td>
                            <td style="text-align: left; direction: ltr;">
                                <% if (Validator.isNotNull(auditEvent.getAdditionalInfo())){
                                        String str = auditEvent.getAdditionalInfo();
                                        String str1 = HtmlUtil.unescape(str);
                                        if (Validator.isNotNull(str1)) {
                                            JSONObject additionalInfoJSON = JSONFactoryUtil.createJSONObject(str1);
                                            for (String key : additionalInfoJSON.keySet()) {
                                                if (key.equals("attributes"))
                                %>
                                    <table class="table">
                                        <tr>
                                            <td><%= key %>
                                            </td>
                                            <td><%= additionalInfoJSON.getString(key) %>
                                            </td>
                                        </tr>
                                    </table>
                                    <%
                                            }
                                        }
                                    }else{
                                    %>
                                    <%= LanguageUtil.get(request, "none") %>
                                    <%
                                        }
                                    %>
                            </td>
                        </tr>
                        </tbody>
                    </table>

                </div>

            </clay:sheet-section>
        </c:otherwise>
    </c:choose>


</clay:container-fluid>
