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

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.security.audit.web.internal.constants.AuditPortletKeys;
import com.liferay.portal.security.audit.web.internal.portlet.AuditField;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import sun.awt.windows.WPrinterJob;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import java.util.LinkedHashMap;

*/
/**
 * @author Javad Sarlak
 *//*

@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + AuditPortletKeys.AUDIT,
		"mvc.command.name=/audit_event/search"
	},
	service = MVCActionCommand.class
)
public class AuditEventMVCActionCommand extends BaseMVCActionCommand {

	public void search(ActionRequest actionRequest) {
		System.out.println( 5);

		String keywords = ParamUtil.getString(
				actionRequest, "keywords");

		String className = ParamUtil.getString(
				actionRequest, AuditField.CLASS_NAME);

		long classPK = ParamUtil.getLong(
				actionRequest, AuditField.CLASS_PK);

		String clientHost = ParamUtil.getString(
				actionRequest, AuditField.CLIENT_HOST);

		String clientIP = ParamUtil.getString(
				actionRequest, AuditField.CLIENT_IP);


		String eventType = ParamUtil.getString(
				actionRequest, AuditField.EVENT_TYPE);


		String serverName = ParamUtil.getString(
				actionRequest, AuditField.SERVER_NAME);


		int serverPort = ParamUtil.getInteger(
				actionRequest, AuditField.SERVER_PORT);


		String sessionID = ParamUtil.getString(
				actionRequest, AuditField.SESSION_ID);


		actionRequest.setAttribute("keywords", keywords);
		actionRequest.setAttribute(AuditField.CLASS_NAME, className);
		actionRequest.setAttribute(AuditField.CLASS_PK, classPK);
		actionRequest.setAttribute(AuditField.CLIENT_HOST, clientHost);
		actionRequest.setAttribute(AuditField.CLIENT_IP, clientIP);
		actionRequest.setAttribute(AuditField.EVENT_TYPE, eventType);
		actionRequest.setAttribute(AuditField.SERVER_NAME, serverName);
		actionRequest.setAttribute(AuditField.SERVER_PORT, serverPort);
		actionRequest.setAttribute(AuditField.SESSION_ID, sessionID);

	}

	protected String[] convertListObjectToStringList() {
		return null;
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (cmd.equals(Constants.SEARCH)) {
			search(actionRequest);
		}
	}
}*/
