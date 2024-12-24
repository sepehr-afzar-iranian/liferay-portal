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

import com.liferay.oauth2.provider.model.OAuth2Application;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.user.management.util.AuditMessageHelperUtil;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(immediate = true, service = ModelListener.class)
public class OAuth2ApplicationModelListener
	extends BaseModelListener<OAuth2Application> {

	@Override
	public void onAfterCreate(OAuth2Application oAuth2Application)
		throws ModelListenerException {

		audit(EventTypes.ADD, oAuth2Application);
	}

	@Override
	public void onAfterRemove(OAuth2Application oAuth2Application)
		throws ModelListenerException {

		audit(EventTypes.DELETE, oAuth2Application);
	}

	@Override
	public void onAfterUpdate(OAuth2Application oAuth2Application)
		throws ModelListenerException {

		audit(EventTypes.UPDATE, oAuth2Application);
	}

	protected void audit(String eventType, OAuth2Application oAuth2Application)
		throws ModelListenerException {

		try {
			long oAuth2ApplicationId =
				oAuth2Application.getOAuth2ApplicationId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, OAuth2Application.class.getName(),
				oAuth2ApplicationId, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"oAuth2ApplicationId", oAuth2ApplicationId
			).put(
				"oAuth2ApplicationName", oAuth2Application.getName()
			).put(
				"oAuth2ApplicationRedirectURIs",
				oAuth2Application.getRedirectURIs()
			);

			auditMessage.setMessage(
				AuditMessageHelperUtil.getMessage(
					eventType, auditMessage.getClassName(),
					oAuth2Application.getName(), oAuth2ApplicationId));

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OAuth2ApplicationModelListener.class);

	@Reference
	private AuditRouter _auditRouter;

}