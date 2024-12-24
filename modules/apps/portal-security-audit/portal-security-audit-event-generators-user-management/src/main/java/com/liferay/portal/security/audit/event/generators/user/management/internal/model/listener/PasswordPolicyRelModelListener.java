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

import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.PasswordPolicyRel;
import com.liferay.portal.kernel.service.PasswordPolicyLocalService;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.user.management.util.AuditMessageHelperUtil;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(immediate = true, service = ModelListener.class)
public class PasswordPolicyRelModelListener
	extends BaseModelListener<PasswordPolicyRel> {

	@Override
	public void onAfterCreate(PasswordPolicyRel passwordPolicyRel)
		throws ModelListenerException {

		audit(EventTypes.ADD, passwordPolicyRel);
	}

	@Override
	public void onAfterRemove(PasswordPolicyRel passwordPolicyRel)
		throws ModelListenerException {

		audit(EventTypes.DELETE, passwordPolicyRel);
	}

	@Override
	public void onAfterUpdate(PasswordPolicyRel passwordPolicyRel)
		throws ModelListenerException {

		audit(EventTypes.UPDATE, passwordPolicyRel);
	}

	protected void audit(String eventType, PasswordPolicyRel passwordPolicyRel)
		throws ModelListenerException {

		try {
			long passwordPolicyRelId =
				passwordPolicyRel.getPasswordPolicyRelId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, PasswordPolicyRel.class.getName(),
				passwordPolicyRelId, null);

			long passwordPolicyId = passwordPolicyRel.getPasswordPolicyId();

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			PasswordPolicy passwordPolicy =
				_passwordPolicyLocalService.fetchPasswordPolicy(
					passwordPolicyId);

			additionalInfoJSONObject.put(
				"passwordPolicyRelId", passwordPolicyRelId);

			if (!Objects.equals(passwordPolicy, null)) {
				additionalInfoJSONObject.put(
					"passwordPolicyId", passwordPolicyId
				).put(
					"passwordPolicyName", passwordPolicy.getName()
				);
			}

			auditMessage.setMessage(
				AuditMessageHelperUtil.getMessage(
					eventType, auditMessage.getClassName(),
					passwordPolicy.getName(), passwordPolicyId));

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PasswordPolicyRelModelListener.class);

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private PasswordPolicyLocalService _passwordPolicyLocalService;

}