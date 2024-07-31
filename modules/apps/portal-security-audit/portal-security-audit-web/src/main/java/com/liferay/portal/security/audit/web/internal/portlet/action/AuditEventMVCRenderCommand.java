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
 *//*


package com.liferay.portal.security.audit.web.internal.portlet.action;



import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.service.EmailAddressLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.audit.storage.exception.NoSuchEventException;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;
import com.liferay.portal.security.audit.web.internal.constants.AuditPortletKeys;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.security.audit.web.internal.display.context.AuditEventContext;
import com.liferay.portal.security.audit.web.internal.portlet.AuditField;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

*/
/**
 * @author Javad sarlak
 *//*

@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + AuditPortletKeys.AUDIT,
		"mvc.command.name=/"
	},
	service = MVCRenderCommand.class
)
public class AuditEventMVCRenderCommand implements MVCRenderCommand {

	@Override
	@SuppressWarnings("unchecked")
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
			throws PortletException {

		try {
			System.out.println(1);
			*/
/*ServiceContext serviceContext = ServiceContextFactory.getInstance(
					renderRequest);

			String keywords = ParamUtil.getString(
					renderRequest, "keywords");

			int currentPage = ParamUtil.getInteger(
					renderRequest, "currentPage", 1);

			int delta = ParamUtil.getInteger(
					renderRequest, "delta", 20);

			String orderByCol = ParamUtil.getString(
					renderRequest, "orderByCol", "createDate");

			String orderByType = ParamUtil.getString(
					renderRequest, "orderByType", "desc");

			if (currentPage == 0) {
				currentPage = 1;
			}



			List<AuditEvent> auditEvents = _auditEventLocalService.searchAuditEvents(
					_portal.getCompanyId(renderRequest), keywords, start, end,
					new Sort[]{new Sort(orderByCol, Sort.STRING_TYPE, orderByType.equals("asc"))});

			int total = _auditEventLocalService.searchAuditEventsCount(
					_portal.getCompanyId(renderRequest), keywords);

			renderRequest.setAttribute("auditEvents", auditEvents);
			renderRequest.setAttribute("total", total);*//*


			ServiceContext serviceContext = ServiceContextFactory.getInstance(
					renderRequest);
			String keywords = ParamUtil.getString(
					renderRequest, "keywords");
			System.out.println("keywords = " + keywords);
			LinkedHashMap<String, Object> params = new LinkedHashMap<>();

			String className = ParamUtil.getString(
					renderRequest, AuditField.CLASS_NAME);
			params.put(
					AuditField.CLASS_NAME,
					className);

			long classPK = ParamUtil.getLong(
					renderRequest, AuditField.CLASS_PK);
			params.put(
					AuditField.CLASS_PK,
					classPK);

			String clientHost = ParamUtil.getString(
					renderRequest, AuditField.CLIENT_HOST);
			params.put(
					AuditField.CLIENT_HOST,
					clientHost);

			String clientIP = ParamUtil.getString(
					renderRequest, AuditField.CLIENT_IP);
			params.put(
					AuditField.CLIENT_IP,
					clientIP);

			String eventType = ParamUtil.getString(
					renderRequest, AuditField.EVENT_TYPE);
			params.put(
					AuditField.EVENT_TYPE,
					eventType);

			String serverName = ParamUtil.getString(
					renderRequest, AuditField.SERVER_NAME);
			params.put(
					AuditField.SERVER_NAME,
					serverName);

			int serverPort = ParamUtil.getInteger(
					renderRequest, AuditField.SERVER_PORT);
			params.put(
					AuditField.SERVER_PORT,
					serverPort);

			String sessionID = ParamUtil.getString(
					renderRequest, AuditField.SESSION_ID);
			params.put(
					AuditField.SESSION_ID,
					sessionID);

			BaseModelSearchResult<AuditEvent> result = _auditEventLocalService.search(
					serviceContext.getCompanyId(), keywords, params,
					0,0,null,false);

			List<AuditEvent> auditEvents =
					result.getBaseModels();
			System.out.println(auditEvents.size());
				*/
/*List<AuditEvent> auditEvents = _auditEventLocalService.searchAuditEvents(
						serviceContext.getCompanyId(), keywords, 0, 0,
						null);*//*


			renderRequest.setAttribute(
					"auditEvents",
					auditEvents);
			renderRequest.setAttribute(
					"auditEventsCount",
					auditEvents.size());

			AuditEventContext auditEventContext = new AuditEventContext(renderRequest,_auditEventLocalService);

			renderRequest.setAttribute(
					AuditPortletKeys.PORTLET_DISPLAY_CONTEXT,
					auditEventContext);



		} catch (Exception exception) {
			if (exception instanceof NoSuchEventException){
				SessionErrors.add(renderRequest, exception.getClass());
				return "/error.jsp";
			}

			System.out.println(exception);
			throw new PortletException(exception);
		}

		System.out.println(4);
		return getPath();
	}

		*/
/*protected int allPages ( int uniSize){
			int all = 0;

			if (uniSize <= _DELTA) {
				all = 1;
			} else {
				int remind = uniSize % _DELTA;

				if (remind > 0) {
					all++;
				}

				all += uniSize / _DELTA;
			}

			return all - 1;
		}
	*//*

		protected String getPath () {
			return "/view01.jsp";
		}

	@Reference
	private AuditEventLocalService _auditEventLocalService;

	@Reference
	private Portal _portal;
}*/
