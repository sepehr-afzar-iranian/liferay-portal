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
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.user.management.util.AuditMessageHelperUtil;
import com.liferay.portal.security.audit.event.generators.user.management.util.OnAfterUpdateUtil;
import com.liferay.portal.security.audit.event.generators.util.Attribute;
import com.liferay.portal.security.audit.event.generators.util.AttributesBuilder;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(service = ModelListener.class)
public class LayoutModelListener extends BaseModelListener<Layout> {

	@Override
	public void onAfterCreate(Layout layout) throws ModelListenerException {
		audit(EventTypes.ADD, layout);
	}

	@Override
	public void onAfterUpdate(Layout layout) throws ModelListenerException {
		OnAfterUpdateUtil.update(Layout.class.getName(), layout.getLayoutId());
	}

	@Override
	public void onBeforeRemove(Layout layout) throws ModelListenerException {
		audit(EventTypes.DELETE, layout);
	}

	@Override
	public void onBeforeUpdate(Layout newLayout) throws ModelListenerException {
		try {
			if (newLayout.getPriority() == 0) {
				return;
			}

			long layoutId = newLayout.getLayoutId();

			Layout oldLayout = _layoutLocalService.getLayout(layoutId);

			List<Attribute> attributes = getModifiedAttributes(
				newLayout, oldLayout);

			if (!attributes.isEmpty()) {
				AuditMessage auditMessage =
					AuditMessageBuilder.buildAuditMessage(
						EventTypes.UPDATE, Layout.class.getName(), layoutId,
						attributes);

				String defaultLanguageId = LocaleUtil.toLanguageId(
					LocaleUtil.getSiteDefault());

				auditMessage.setMessage(
					AuditMessageHelperUtil.getMessage(
						EventTypes.UPDATE, auditMessage.getClassName(),
						newLayout.getName(defaultLanguageId), layoutId));

				_auditRouter.route(auditMessage);
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	protected void audit(String eventType, Layout layout)
		throws ModelListenerException {

		try {
			if (layout.getPriority() == 0) {
				return;
			}

			long layoutId = layout.getLayoutId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, Layout.class.getName(), layoutId, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"isPublicLayout", layout.isPublicLayout()
			).put(
				"layoutId", layoutId
			).put(
				"layoutName", layout.getName()
			).put(
				"siteId", layout.getGroupId()
			);

			Group group = layout.getGroup();

			if (!Objects.equals(group, null)) {
				additionalInfoJSONObject.put("siteName", group.getName());
			}

			String defaultLanguageId = LocaleUtil.toLanguageId(
				LocaleUtil.getSiteDefault());

			auditMessage.setMessage(
				AuditMessageHelperUtil.getMessage(
					eventType, auditMessage.getClassName(),
					layout.getName(defaultLanguageId), layoutId));

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	protected List<Attribute> getModifiedAttributes(
		Layout newLayout, Layout oldLayout) {

		AttributesBuilder attributesBuilder = new AttributesBuilder(
			newLayout, oldLayout);

		attributesBuilder.add("parentPlid");
		attributesBuilder.add("privateLayout");
		attributesBuilder.add("layoutId");
		attributesBuilder.add("parentLayoutId");
		attributesBuilder.add("name");
		attributesBuilder.add("title");
		attributesBuilder.add("description");
		attributesBuilder.add("keywords");
		attributesBuilder.add("robots");
		attributesBuilder.add("type");
		attributesBuilder.add("typeSettings");
		attributesBuilder.add("hidden");
		attributesBuilder.add("system");
		attributesBuilder.add("friendlyURL");
		attributesBuilder.add("iconImageId");
		attributesBuilder.add("themeId");
		attributesBuilder.add("colorSchemeId");
		attributesBuilder.add("styleBookEntryId");
		attributesBuilder.add("css");
		attributesBuilder.add("priority");
		attributesBuilder.add("masterLayoutPlid");
		attributesBuilder.add("layoutPrototypeUuid");
		attributesBuilder.add("layoutPrototypeLinkEnabled");
		attributesBuilder.add("sourcePrototypeLayoutUuid");
		attributesBuilder.add("publishDate");
		attributesBuilder.add("lastPublishDate");
		attributesBuilder.add("status");
		attributesBuilder.add("statusByUserId");
		attributesBuilder.add("statusByUserName");
		attributesBuilder.add("statusDate");

		return attributesBuilder.getAttributes();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutModelListener.class);

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private LayoutLocalService _layoutLocalService;

}