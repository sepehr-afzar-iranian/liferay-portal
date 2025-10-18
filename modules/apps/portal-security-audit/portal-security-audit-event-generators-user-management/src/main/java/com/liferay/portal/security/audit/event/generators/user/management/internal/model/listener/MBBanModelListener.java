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

package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener;

import com.liferay.message.boards.model.MBBan;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.user.management.util.AuditMessageHelperUtil;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(
	configurationPid = "com.liferay.portal.security.audit.configuration.AuditConfiguration",
	immediate = true, service = ModelListener.class
)
public class MBBanModelListener extends BaseModelListener<MBBan> {

	@Override
	public void onAfterCreate(MBBan mbBan) throws ModelListenerException {
		audit(EventTypes.ADD, mbBan);
	}

	@Override
	public void onAfterRemove(MBBan mbBan) throws ModelListenerException {
		audit(EventTypes.DELETE, mbBan);
	}

	@Override
	public void onAfterUpdate(MBBan mbBan) throws ModelListenerException {
		audit(EventTypes.UPDATE, mbBan);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_auditConfiguration = ConfigurableUtil.createConfigurable(
			AuditConfiguration.class, properties);
	}

	protected void audit(String eventType, MBBan mbBan)
		throws ModelListenerException {

		if (!_auditConfiguration.enabled()) {
			return;
		}

		try {
			long mbBanId = mbBan.getBanId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, MBBan.class.getName(), mbBanId, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			long banUserId = mbBan.getBanUserId();

			User banUser = _userLocalService.fetchUser(banUserId);

			String userFullName = null;

			additionalInfoJSONObject.put(
				"banUserId", banUserId
			).put(
				"mbBanId", mbBanId
			);

			if (!Objects.equals(banUser, null)) {
				userFullName = banUser.getFullName();

				additionalInfoJSONObject.put("banUserName", userFullName);
			}

			auditMessage.setMessage(
				AuditMessageHelperUtil.getMessage(
					eventType, auditMessage.getClassName(), userFullName,
					banUserId));

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MBBanModelListener.class);

	private volatile AuditConfiguration _auditConfiguration;

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private UserLocalService _userLocalService;

}