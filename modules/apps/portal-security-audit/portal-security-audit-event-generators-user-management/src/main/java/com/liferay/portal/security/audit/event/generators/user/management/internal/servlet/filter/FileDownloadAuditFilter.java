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

package com.liferay.portal.security.audit.event.generators.user.management.internal.servlet.filter;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.BaseFilter;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(
	immediate = true,
	property = {
		"servlet-context-name=",
		"servlet-filter-name=File Operations Audit Filter", "url-pattern=*.7z",
		"url-pattern=*.bmp", "url-pattern=*.csv", "url-pattern=*.doc",
		"url-pattern=*.docx", "url-pattern=*.gif", "url-pattern=*.gz",
		"url-pattern=*.jpeg", "url-pattern=*.jpg", "url-pattern=*.lar",
		"url-pattern=*.odp", "url-pattern=*.ods", "url-pattern=*.odt",
		"url-pattern=*.pdf", "url-pattern=*.png", "url-pattern=*.ppt",
		"url-pattern=*.pptx", "url-pattern=*.rar", "url-pattern=*.rtf",
		"url-pattern=*.svg", "url-pattern=*.tar", "url-pattern=*.tif",
		"url-pattern=*.tiff", "url-pattern=*.txt", "url-pattern=*.webp",
		"url-pattern=*.xls", "url-pattern=*.xlsx", "url-pattern=*.zip",
		"url-pattern=/document/*", "url-pattern=/documents/*",
		"url-pattern=/image/*"
	},
	service = Filter.class
)
public class FileDownloadAuditFilter extends BaseFilter {

	public String extractFromDocumentUrl(String documentUrl) {
		String urlWithoutParams = documentUrl.split("\\?")[0];

		String[] parts = urlWithoutParams.split("/");

		if (parts.length > 0) {
			return parts[parts.length - 1];
		}

		return null;
	}

	@Override
	protected Log getLog() {
		return _log;
	}

	protected boolean isDownloadRequest(HttpServletRequest httpServletRequest) {
		String method = httpServletRequest.getMethod();

		if (!method.equals("GET")) {
			return false;
		}

		String uri = httpServletRequest.getRequestURI();

		String[] documentExtensions = {
			".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".txt",
			".rtf", ".csv", ".odt", ".ods", ".odp", ".zip", ".rar", ".tar",
			".gz", ".7z", ".lar", ".jpg", ".jpeg", ".png", ".gif", ".bmp",
			".tiff", ".tif", ".svg", ".webp", ".ico", ".raw", ".psd", ".ai",
			".eps"
		};

		String uriLowerCased = StringUtil.toLowerCase(uri);

		for (String extension : documentExtensions) {
			if (uriLowerCased.endsWith(extension)) {
				return true;
			}
		}

		String download = ParamUtil.getString(httpServletRequest, "download");
		String psth = httpServletRequest.getRequestURI();

		if ((Validator.isNotNull(download) && download.equals("true")) ||
			psth.contains("/document/") || psth.contains("/documents/")) {

			return true;
		}

		return false;
	}

	@Override
	protected void processFilter(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FilterChain filterChain)
		throws Exception {

		processFilter(
			FileDownloadAuditFilter.class.getName(), httpServletRequest,
			httpServletResponse, filterChain);

		String path = GetterUtil.getString(httpServletRequest.getPathInfo());

		if (path.startsWith("/company_logo") ||
			path.startsWith("/organization_logo")) {

			return;
		}

		if (!isDownloadRequest(httpServletRequest)) {
			return;
		}

		String uri = httpServletRequest.getRequestURI();
		String key = "UUID";

		if (uri.contains("/webdav/")) {
			key = "Name";
		}

		String value = extractFromDocumentUrl(uri);

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				EventTypes.DOWNLOAD, EventTypes.DOWNLOAD, 0, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(key, value);

			int status = httpServletResponse.getStatus();

			boolean successful = false;

			if ((status >= 200) && (status < 300)) {
				successful = true;
			}

			additionalInfoJSONObject.put(
				"status", status
			).put(
				"successful", successful
			);

			StringBuilder sb = new StringBuilder();

			sb.append("File with the ");
			sb.append(StringPool.SPACE);
			sb.append(key);
			sb.append(StringPool.SPACE);
			sb.append(value);
			sb.append(StringPool.SPACE);
			sb.append("was ");
			sb.append(successful ? "successfully " : "not ");
			sb.append("downloaded");
			sb.append(StringPool.PERIOD);

			auditMessage.setMessage(sb.toString());

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FileDownloadAuditFilter.class);

	@Reference
	private AuditRouter _auditRouter;

}