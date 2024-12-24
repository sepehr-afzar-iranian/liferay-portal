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
@Component(service = ModelListener.class)
public class LayoutPageTemplateCollectionModelListener
	extends BaseModelListener<LayoutPageTemplateCollection> {

	@Override
	public void onAfterCreate(
			LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws ModelListenerException {

		audit(EventTypes.ADD, layoutPageTemplateCollection);
	}

	@Override
	public void onAfterUpdate(
			LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws ModelListenerException {

		audit(EventTypes.UPDATE, layoutPageTemplateCollection);
	}

	@Override
	public void onBeforeRemove(
			LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws ModelListenerException {

		audit(EventTypes.DELETE, layoutPageTemplateCollection);
	}

	protected void audit(
			String eventType,
			LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws ModelListenerException {

		try {
			long layoutPageTemplateCollectionId =
				layoutPageTemplateCollection.
					getLayoutPageTemplateCollectionId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, LayoutPageTemplateCollection.class.getName(),
				layoutPageTemplateCollectionId, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"layoutPageTemplateCollectionId", layoutPageTemplateCollectionId
			).put(
				"layoutPageTemplateCollectionName",
				layoutPageTemplateCollection.getName()
			);

			auditMessage.setMessage(
				AuditMessageHelperUtil.getMessage(
					eventType, auditMessage.getClassName(),
					layoutPageTemplateCollection.getName(),
					layoutPageTemplateCollectionId));

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutPageTemplateCollectionModelListener.class);

	@Reference
	private AuditRouter _auditRouter;

}