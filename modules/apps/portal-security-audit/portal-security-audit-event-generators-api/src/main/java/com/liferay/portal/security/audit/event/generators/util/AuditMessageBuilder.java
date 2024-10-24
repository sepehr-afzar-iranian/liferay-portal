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

package com.liferay.portal.security.audit.event.generators.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRequestThreadLocal;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import java.util.List;
import java.util.Objects;

/**
 * @author Mika Koivisto
 * @author Brian Wing Shun Chan
 */
public class AuditMessageBuilder {

	public static AuditMessage buildAuditMessage(
		String eventType, String className, long classPK,
		List<Attribute> attributes) {

		long companyId = CompanyThreadLocal.getCompanyId();

		long userId = 0;

		if (PrincipalThreadLocal.getName() != null) {
			userId = GetterUtil.getLong(PrincipalThreadLocal.getName());
		}

		AuditRequestThreadLocal auditRequestThreadLocal =
			AuditRequestThreadLocal.getAuditThreadLocal();

		long realUserId = auditRequestThreadLocal.getRealUserId();

		String realUserName = PortalUtil.getUserName(
			realUserId, StringPool.BLANK);

		JSONObject additionalInfoJSONObject =
			JSONFactoryUtil.createJSONObject();

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (!Objects.equals(serviceContext, null)) {
			long groupId = serviceContext.getScopeGroupId();

			additionalInfoJSONObject.put(
				"groupId", serviceContext.getScopeGroupId());

			Group group = GroupLocalServiceUtil.fetchGroup(groupId);

			if (!Objects.equals(group, null)) {
				try {
					additionalInfoJSONObject.put(
						"siteName", group.getDescriptiveName());
				}
				catch (Exception exception) {
				}
			}
		}

		if ((realUserId > 0) && (userId != realUserId)) {
			additionalInfoJSONObject.put(
				"doAsUserId", String.valueOf(userId)
			).put(
				"doAsUserName", PortalUtil.getUserName(userId, StringPool.BLANK)
			);
		}

		if (attributes != null) {
			additionalInfoJSONObject.put(
				"attributes", _getAttributesJSONArray(attributes));
		}

		return new AuditMessage(
			eventType, companyId, realUserId, realUserName, className,
			String.valueOf(classPK), null, additionalInfoJSONObject);
	}

	private static JSONArray _getAttributesJSONArray(
		List<Attribute> attributes) {

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (Attribute attribute : attributes) {
			jsonArray.put(
				JSONUtil.put(
					"name", attribute.getName()
				).put(
					"newValue", attribute.getNewValue()
				).put(
					"oldValue", attribute.getOldValue()
				));
		}

		return jsonArray;
	}

}