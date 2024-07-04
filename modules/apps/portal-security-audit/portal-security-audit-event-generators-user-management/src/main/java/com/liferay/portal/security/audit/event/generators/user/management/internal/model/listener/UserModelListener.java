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
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.user.management.util.AuditMessageClassNameUtil;
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

	public void onAfterAddAssociation(Object classPK, String associationClassName,
									  Object associationClassP) {
		try{
			String additName = _auditMessageClassNameUtil.getName(associationClassName);
			String additValue = _auditMessageClassNameUtil.getValue(associationClassName, associationClassP);

			AuditMessage auditMessage =
				AuditMessageBuilder.buildAuditMessage(
					EventTypes.UPDATE, User.class.getName(),
					(long)associationClassP, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			additionalInfoJSONObject.put(
				"associationType", EventTypes.ADD
			).put(
				"associationName", additName
			).put(
				"associationValue", additValue
			).put(
				"groupId", serviceContext.getScopeGroupId()
			);

			_auditRouter.route(auditMessage);
		}
		catch (Exception e){
			System.out.println("e = " + e);
		}
	}

	public void onAfterRemoveAssociation(Object classPK, String associationClassName,
									  Object associationClassP) {
		try{
			String additName = _auditMessageClassNameUtil.getName(associationClassName);
			String additValue = _auditMessageClassNameUtil.getValue(associationClassName, associationClassP);

			AuditMessage auditMessage =
				AuditMessageBuilder.buildAuditMessage(
					EventTypes.UPDATE, User.class.getName(),
					(long)associationClassP, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			additionalInfoJSONObject.put(
				"associationType", EventTypes.DELETE
			).put(
				"associationName", additName
			).put(
				"associationValue", additValue
			).put(
				"groupId", serviceContext.getScopeGroupId()
			);

			_auditRouter.route(auditMessage);
		}
		catch (Exception e){
			System.out.println("e = " + e);
		}
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
				).put(
					"groupId", newUser.getGroupId()
				);

				_auditRouter.route(auditMessage);
			}
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	protected void auditOnCreateOrRemove(String eventType, User user)
		throws ModelListenerException {

		try {
			long userId = user.getUserId();
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, User.class.getName(), userId, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"emailAddress", user.getEmailAddress()
			).put(
				"screenName", user.getScreenName()
			).put(
				"userId", userId
			).put(
				"userName", user.getFullName()
			).put(
				"groupId", user.getGroupId()
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
		attributesBuilder.add("agreedToTermsOfUse");
		attributesBuilder.add("comments");
		attributesBuilder.add("emailAddress");
		attributesBuilder.add("languageId");
		attributesBuilder.add("reminderQueryAnswer");
		attributesBuilder.add("reminderQueryQuestion");
		attributesBuilder.add("screenName");
		attributesBuilder.add("timeZoneId");

		List<Attribute> attributes = attributesBuilder.getAttributes();

		if (newUser.isPasswordModified()) {
			attributes.add(new Attribute("password"));
		}

		return attributes;
	}

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private AuditMessageClassNameUtil _auditMessageClassNameUtil;

}