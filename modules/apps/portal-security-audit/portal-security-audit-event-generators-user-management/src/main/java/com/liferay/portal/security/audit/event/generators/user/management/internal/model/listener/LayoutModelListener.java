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
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ModelListener;

import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

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
		audit(EventTypes.UPDATE, layout);
	}

	@Override
	public void onBeforeRemove(Layout layout) throws ModelListenerException {
		audit(EventTypes.DELETE, layout);
	}

	protected void audit(String eventType, Layout layout)
		throws ModelListenerException {

		try {
			if (layout.getPriority() == 0){
				return;
			}
			long layoutId = layout.getLayoutId();
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, Layout.class.getName(), layoutId, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();
			additionalInfoJSONObject.put(
				"layoutId", layoutId
			).put(
				"layoutName", layout.getName()
			).put(
				"siteId", layout.getGroupId()
			).put(
				"siteName", layout.getGroup().getName()
			).put(
				"isPublicLayout", layout.isPublicLayout()
			);

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private AuditRouter _auditRouter;

}