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

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.user.management.util.AuditMessageHelperUtil;
import com.liferay.portal.security.audit.event.generators.user.management.util.OnAfterUpdateUtil;
import com.liferay.portal.security.audit.event.generators.util.Attribute;
import com.liferay.portal.security.audit.event.generators.util.AttributesBuilder;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.List;
import java.util.Map;

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
public class GroupModelListener extends BaseModelListener<Group> {

	@Override
	public void onAfterCreate(Group group) throws ModelListenerException {
		audit(EventTypes.ADD, group);
	}

	@Override
	public void onAfterRemove(Group group) throws ModelListenerException {
		audit(EventTypes.DELETE, group);
	}

	@Override
	public void onAfterUpdate(Group group) throws ModelListenerException {
		if (!_auditConfiguration.enabled()) {
			return;
		}

		OnAfterUpdateUtil.update(Group.class.getName(), group.getGroupId());
	}

	@Override
	public void onBeforeUpdate(Group newGroup) throws ModelListenerException {
		if (!_auditConfiguration.enabled()) {
			return;
		}

		try {
			long groupId = newGroup.getGroupId();

			Group oldGroup = _groupLocalService.getGroup(groupId);

			List<Attribute> attributes = getModifiedAttributes(
				newGroup, oldGroup);

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				EventTypes.UPDATE, Group.class.getName(), groupId, attributes);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"groupId", groupId
			).put(
				"userName", newGroup.getName()
			);

			String defaultLanguageId = LocaleUtil.toLanguageId(
				LocaleUtil.getSiteDefault());

			auditMessage.setMessage(
				AuditMessageHelperUtil.getMessage(
					EventTypes.UPDATE, auditMessage.getClassName(),
					newGroup.getName(defaultLanguageId), groupId));

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_auditConfiguration = ConfigurableUtil.createConfigurable(
			AuditConfiguration.class, properties);
	}

	protected void audit(String eventType, Group group)
		throws ModelListenerException {

		if (!_auditConfiguration.enabled()) {
			return;
		}

		try {
			long groupId = group.getGroupId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, Group.class.getName(), groupId, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"GroupName", group.getName()
			).put(
				"ModelGroupId", groupId
			);

			String defaultLanguageId = LocaleUtil.toLanguageId(
				LocaleUtil.getSiteDefault());

			auditMessage.setMessage(
				AuditMessageHelperUtil.getMessage(
					eventType, auditMessage.getClassName(),
					group.getName(defaultLanguageId), groupId));

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	protected List<Attribute> getModifiedAttributes(
		Group newGroup, Group oldGroup) {

		AttributesBuilder attributesBuilder = new AttributesBuilder(
			newGroup, oldGroup);

		attributesBuilder.add("name");

		attributesBuilder.add("groupId");
		attributesBuilder.add("companyId");
		attributesBuilder.add("parentGroupId");
		attributesBuilder.add("liveGroupId");
		attributesBuilder.add("treePath");
		attributesBuilder.add("groupKey");
		attributesBuilder.add("description");
		attributesBuilder.add("type");
		attributesBuilder.add("typeSettings");
		attributesBuilder.add("manualMembership");
		attributesBuilder.add("membershipRestriction");
		attributesBuilder.add("friendlyURL");
		attributesBuilder.add("site");
		attributesBuilder.add("remoteStagingGroupCount");
		attributesBuilder.add("inheritContent");
		attributesBuilder.add("active");

		return attributesBuilder.getAttributes();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GroupModelListener.class);

	private volatile AuditConfiguration _auditConfiguration;

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private GroupLocalService _groupLocalService;

}