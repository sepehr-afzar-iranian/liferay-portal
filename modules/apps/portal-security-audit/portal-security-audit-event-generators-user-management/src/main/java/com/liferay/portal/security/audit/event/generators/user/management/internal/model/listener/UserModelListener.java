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
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.user.management.util.AuditMessageUserAssociationHelper;
import com.liferay.portal.security.audit.event.generators.user.management.util.OnAfterUpdateUtil;
import com.liferay.portal.security.audit.event.generators.util.Attribute;
import com.liferay.portal.security.audit.event.generators.util.AttributesBuilder;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 * @author Brian Wing Shun Chan
 */
@Component(immediate = true, service = ModelListener.class)
public class UserModelListener extends BaseModelListener<User> {

	public void onAfterAddAssociation(
		Object classPK, String associationClassName, Object associationClassP) {

		associationAudit(
			associationClassName, associationClassP, EventTypes.ADD);
	}

	public void onAfterRemoveAssociation(
		Object classPK, String associationClassName, Object associationClassP) {

		associationAudit(
			associationClassName, associationClassP, EventTypes.DELETE);
	}

	@Override
	public void onAfterUpdate(User user) throws ModelListenerException {
		OnAfterUpdateUtil.update(User.class.getName(), user.getUserId());
	}

	public void onBeforeCreate(User user) throws ModelListenerException {
		auditOnCreateOrRemove(EventTypes.ADD, user);
	}

	public void onBeforeRemove(User user) throws ModelListenerException {
		auditOnCreateOrRemove(EventTypes.DELETE, user);
	}

	public void onBeforeUpdate(User newUser) throws ModelListenerException {
		try {
			User oldUser = _userLocalService.getUser(newUser.getUserId());

			List<Attribute> attributes = getModifiedAttributes(
				newUser, oldUser);

			if (!attributes.isEmpty()) {
				AuditMessage auditMessage =
					AuditMessageBuilder.buildAuditMessage(
						EventTypes.UPDATE, User.class.getName(),
						newUser.getUserId(), attributes);

				JSONObject additionalInfoJSONObject =
					auditMessage.getAdditionalInfo();

				additionalInfoJSONObject.put(
					"emailAddress", newUser.getEmailAddress()
				).put(
					"screenName", newUser.getScreenName()
				).put(
					"userId", newUser.getUserId()
				).put(
					"userName", newUser.getFullName()
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

	protected void associationAudit(
		String associationClassName, Object associationClassP,
		String eventType) {

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				EventTypes.UPDATE, User.class.getName(),
				(long)associationClassP, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"associationClassName", associationClassName
			).put(
				"associationName",
				_auditMessageUserAssociationHelper.getName(associationClassName)
			).put(
				"associationType", eventType
			).put(
				"associationValue",
				_auditMessageUserAssociationHelper.getValue(
					associationClassName, (long)associationClassP)
			);

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	protected void auditOnCreateOrRemove(String eventType, User user)
		throws ModelListenerException {

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, User.class.getName(), user.getUserId(), null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"emailAddress", user.getEmailAddress()
			).put(
				"screenName", user.getScreenName()
			).put(
				"userId", user.getUserId()
			).put(
				"userName", user.getFullName()
			);

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	protected List<Attribute> getModifiedAttributes(
		User newUser, User oldUser) {

		AttributesBuilder attributesBuilder = new AttributesBuilder(
			newUser, oldUser);

		attributesBuilder.add("active");
		attributesBuilder.add("externalReferenceCode");
		attributesBuilder.add("userId");
		attributesBuilder.add("defaultUser");
		attributesBuilder.add("contactId");
		attributesBuilder.add("password");
		attributesBuilder.add("passwordEncrypted");
		attributesBuilder.add("passwordReset");
		attributesBuilder.add("digest");
		attributesBuilder.add("reminderQueryQuestion");
		attributesBuilder.add("reminderQueryAnswer");
		attributesBuilder.add("graceLoginCount");
		attributesBuilder.add("screenName");
		attributesBuilder.add("emailAddress");
		attributesBuilder.add("facebookId");
		attributesBuilder.add("googleUserId");
		attributesBuilder.add("ldapServerId");
		attributesBuilder.add("openId");
		attributesBuilder.add("portraitId");
		attributesBuilder.add("languageId");
		attributesBuilder.add("timeZoneId");
		attributesBuilder.add("greeting");
		attributesBuilder.add("comments");
		attributesBuilder.add("firstName");
		attributesBuilder.add("middleName");
		attributesBuilder.add("lastName");
		attributesBuilder.add("jobTitle");
		attributesBuilder.add("lockout");
		attributesBuilder.add("agreedToTermsOfUse");
		attributesBuilder.add("emailAddressVerified");
		attributesBuilder.add("status");

		List<Attribute> attributes = attributesBuilder.getAttributes();

		if (newUser.isPasswordModified()) {
			attributes.add(new Attribute("password"));
		}

		return attributes;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserModelListener.class);

	@Reference
	private AuditMessageUserAssociationHelper
		_auditMessageUserAssociationHelper;

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private UserLocalService _userLocalService;

}