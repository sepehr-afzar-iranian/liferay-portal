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

import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;

import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(service = ModelListener.class)
public class LayoutPageTemplateEntryModelListener
	extends BaseModelListener<LayoutPageTemplateEntry> {

	@Override
	public void onAfterCreate(
		LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws ModelListenerException {

		audit(EventTypes.ADD, layoutPageTemplateEntry);

	}

	@Override
	public void onAfterUpdate(
		LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws ModelListenerException {

		audit(EventTypes.UPDATE, layoutPageTemplateEntry);

	}

	@Override
	public void onBeforeRemove(
		LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws ModelListenerException {

		audit(EventTypes.DELETE, layoutPageTemplateEntry);
	}

	protected void audit(
		String eventType, LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws ModelListenerException {

		try {
			long layoutPageTemplateEntryId =
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId();
			AuditMessage auditMessage =
				AuditMessageBuilder.buildAuditMessage(eventType,
					LayoutPageTemplateEntry.class.getName(), layoutPageTemplateEntryId,
					null);
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			LayoutPageTemplateCollection layoutPageTemplateCollection = _layoutPageTemplateCollectionLocalService.getLayoutPageTemplateCollection(
				layoutPageTemplateEntry.getLayoutPageTemplateCollectionId());

			additionalInfoJSONObject.put(
				"groupId", serviceContext.getScopeGroupId()
			).put(
				"layoutPageTemplateEntryId", layoutPageTemplateEntryId
			).put(
				"layoutPageTemplateEntryName", layoutPageTemplateEntry.getName()
			).put(
				"layoutPageTemplateEntryCollectionId", layoutPageTemplateEntry.getLayoutPageTemplateCollectionId()
			).put(
				"layoutPageTemplateEntryCollectionName", layoutPageTemplateCollection.getName()
			);

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

}