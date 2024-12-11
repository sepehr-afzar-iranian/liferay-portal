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
import com.liferay.portal.kernel.service.PasswordPolicyLocalService;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.user.management.util.OnAfterUpdateUtil;
import com.liferay.portal.security.audit.event.generators.util.Attribute;
import com.liferay.portal.security.audit.event.generators.util.AttributesBuilder;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(immediate = true, service = ModelListener.class)
public class PasswordPolicyModelListener
	extends BaseModelListener<PasswordPolicy> {

	@Override
	public void onAfterCreate(PasswordPolicy passwordPolicy)
		throws ModelListenerException {

		audit(EventTypes.ADD, passwordPolicy);
	}

	@Override
	public void onAfterRemove(PasswordPolicy passwordPolicy)
		throws ModelListenerException {

		audit(EventTypes.DELETE, passwordPolicy);
	}

	@Override
	public void onAfterUpdate(PasswordPolicy passwordPolicy)
		throws ModelListenerException {

		OnAfterUpdateUtil.update(
			PasswordPolicy.class.getName(),
			passwordPolicy.getPasswordPolicyId());
	}

	@Override
	public void onBeforeUpdate(PasswordPolicy newPasswordPolicy)
		throws ModelListenerException {

		try {
			long passwordPolicyId = newPasswordPolicy.getPasswordPolicyId();

			PasswordPolicy oldPasswordPolicy =
				_passwordPolicyLocalService.getPasswordPolicy(passwordPolicyId);

			List<Attribute> attributes = getModifiedAttributes(
				newPasswordPolicy, oldPasswordPolicy);

			if (!attributes.isEmpty()) {
				AuditMessage auditMessage =
					AuditMessageBuilder.buildAuditMessage(
						EventTypes.UPDATE, PasswordPolicy.class.getName(),
						passwordPolicyId, attributes);

				JSONObject additionalInfoJSONObject =
					auditMessage.getAdditionalInfo();

				additionalInfoJSONObject.put(
					"passwordPolicyId", passwordPolicyId
				).put(
					"passwordPolicyName", newPasswordPolicy.getName()
				);

				_auditRouter.route(auditMessage);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	protected void audit(String eventType, PasswordPolicy passwordPolicy)
		throws ModelListenerException {

		try {
			long passwordPolicyId = passwordPolicy.getPasswordPolicyId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, PasswordPolicy.class.getName(), passwordPolicyId,
				null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"passwordPolicyId", passwordPolicyId
			).put(
				"passwordPolicyName", passwordPolicy.getName()
			);

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	protected List<Attribute> getModifiedAttributes(
		PasswordPolicy newPasswordPolicy, PasswordPolicy oldPasswordPolicy) {

		AttributesBuilder attributesBuilder = new AttributesBuilder(
			newPasswordPolicy, oldPasswordPolicy);

		attributesBuilder.add("passwordPolicyId");
		attributesBuilder.add("defaultPolicy");
		attributesBuilder.add("name");
		attributesBuilder.add("description");
		attributesBuilder.add("changeable");
		attributesBuilder.add("changeRequired");
		attributesBuilder.add("minAge");
		attributesBuilder.add("checkSyntax");
		attributesBuilder.add("allowDictionaryWords");
		attributesBuilder.add("minAlphanumeric");
		attributesBuilder.add("minLength");
		attributesBuilder.add("minLowerCase");
		attributesBuilder.add("minNumbers");
		attributesBuilder.add("minSymbols");
		attributesBuilder.add("minUpperCase");
		attributesBuilder.add("regex");
		attributesBuilder.add("history");
		attributesBuilder.add("historyCount");
		attributesBuilder.add("expireable");
		attributesBuilder.add("maxAge");
		attributesBuilder.add("warningTime");
		attributesBuilder.add("graceLimit");
		attributesBuilder.add("lockout");
		attributesBuilder.add("maxFailure");
		attributesBuilder.add("lockoutDuration");
		attributesBuilder.add("requireUnlock");
		attributesBuilder.add("resetFailureCount");
		attributesBuilder.add("resetTicketMaxAge");

		return attributesBuilder.getAttributes();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PasswordPolicyModelListener.class);

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private PasswordPolicyLocalService _passwordPolicyLocalService;

}