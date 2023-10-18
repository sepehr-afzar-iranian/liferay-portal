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

<%@ include file="/journal_article/init.jsp" %>

<liferay-util:dynamic-include key="com.liferay.journal.taglib#/journal_article/page.jsp#pre" />

<%
JournalArticleDisplay articleDisplay = (JournalArticleDisplay)request.getAttribute("liferay-journal:journal-article:articleDisplay");
String wrapperCssClass = (String)request.getAttribute("liferay-journal:journal-article:wrapperCssClass");

AssetRendererFactory<JournalArticle> assetRendererFactory = AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(JournalArticle.class);

AssetRenderer<JournalArticle> latestArticleAssetRenderer = assetRendererFactory.getAssetRenderer(articleDisplay.getResourcePrimKey(), AssetRendererFactory.TYPE_LATEST_APPROVED);

PortletURL portletURL = latestArticleAssetRenderer.getURLEdit(request);

portletURL.setParameter("redirect", PortalUtil.getCurrentURL(request));

JournalArticle article = JournalArticleLocalServiceUtil.getLatestArticle(articleDisplay.getResourcePrimKey());

boolean hasPermission = JournalArticlePermission.contains(themeDisplay.getPermissionChecker(), article, ActionKeys.UPDATE);

portletURL.setParameter("portletResource", portletDisplay.getPortletName());
%>

<div class="journal-content-article <%= Validator.isNotNull(wrapperCssClass) ? wrapperCssClass : StringPool.BLANK %>" data-analytics-asset-id="<%= articleDisplay.getArticleId() %>" data-analytics-asset-title="<%= HtmlUtil.escapeAttribute(articleDisplay.getTitle()) %>" data-analytics-asset-type="web-content">
	<c:if test='<%= GetterUtil.getBoolean((String)request.getAttribute("liferay-journal:journal-article:showTitle")) %>'>
		<%= HtmlUtil.escape(articleDisplay.getTitle()) %>
	</c:if>

	<%= articleDisplay.getContent() %>

	<clay:content-col
		cssClass="print-action user-tool-asset-addon-entry"
	>
		<c:if test="<%= hasPermission %>">
			<liferay-ui:icon
				icon="pencil"
				markupView="lexicon"
				message="edit"
				url="<%= portletURL.toString() %>"
			/>
		</c:if>
	</clay:content-col>
</div>

<%
List<AssetTag> assetTags = AssetTagLocalServiceUtil.getTags(JournalArticleDisplay.class.getName(), articleDisplay.getResourcePrimKey());

PortalUtil.setPageKeywords(ListUtil.toString(assetTags, AssetTag.NAME_ACCESSOR), request);
%>

<liferay-util:dynamic-include key="com.liferay.journal.taglib#/journal_article/page.jsp#post" />