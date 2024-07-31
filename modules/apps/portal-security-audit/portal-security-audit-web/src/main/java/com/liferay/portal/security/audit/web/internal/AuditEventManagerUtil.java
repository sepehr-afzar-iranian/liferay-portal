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

package com.liferay.portal.security.audit.web.internal;

import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.util.OrderByComparator;

import com.liferay.portal.security.audit.AuditEventManager;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.web.internal.portlet.AuditField;


import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Greenwald
 * @author Prathima Shreenath
 */
@Component(immediate = true, service = {})
public class AuditEventManagerUtil {

	public static List<AuditEvent> getAuditEvents(
		long companyId, String keywords,String userId, String userName,
		 String eventType, String className, String classPK,
		String clientHost, String clientIP, String serverName, String serverPort,
		String sessionID, int cur, int delta) {
		LinkedHashMap<String, Object> params = new LinkedHashMap<>();

		params.put(
				AuditField.USER_ID,userId);

		params.put(
				AuditField.USER_NAME,userName);

		params.put(
				AuditField.CLASS_NAME, className);

		params.put(
				AuditField.CLASS_PK,classPK);

		params.put(
				AuditField.CLIENT_HOST, clientHost);

		params.put(
				AuditField.CLIENT_IP, clientIP);

		params.put(
				AuditField.EVENT_TYPE, eventType);

		params.put(
				AuditField.SERVER_NAME, serverName);
		;
		params.put(
				AuditField.SERVER_PORT, serverPort);

		params.put(
				AuditField.SESSION_ID, sessionID);
		BaseModelSearchResult<AuditEvent> result =  _auditEventLocalService.search(
				companyId,keywords,params,cur,delta,null,false);

		return result.getBaseModels();
	}

	public static int getAuditEventsCount(long companyId, String keywords,String userId, String userName,
												  String eventType, String className, String classPK,
												  String clientHost, String clientIP, String serverName, String serverPort,
												  String sessionID){
		List<AuditEvent> auditEvents= getAuditEvents(
				companyId,keywords,userId,userName,eventType,
				className,classPK,clientHost,clientIP,serverName,
				serverPort,sessionID,-1,-1);
		return auditEvents.size();
	}

	@Reference(unbind = "-")
	protected void setAuditEventLocalService(
			AuditEventLocalService auditEventLocalService) {

		_auditEventLocalService =
				auditEventLocalService;
	}
	private static AuditEventLocalService _auditEventLocalService;

}