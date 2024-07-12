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

import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;

import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(service = ModelListener.class)
public class LayoutPageTemplateStructureRelModelListener
	extends BaseModelListener<LayoutPageTemplateStructureRel> {

	@Override
	public void onAfterCreate(
			LayoutPageTemplateStructureRel layoutPageTemplateStructureRel)
		throws ModelListenerException {
		audit(EventTypes.ADD, layoutPageTemplateStructureRel);
	}

	@Override
	public void onAfterRemove(
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel)
		throws ModelListenerException {
		audit(EventTypes.DELETE, layoutPageTemplateStructureRel);
	}

	@Override
	public void onAfterUpdate(
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel)
		throws ModelListenerException {
		audit(EventTypes.UPDATE, layoutPageTemplateStructureRel);
	}

	protected void audit(
		String eventType, LayoutPageTemplateStructureRel layoutPageTemplateStructureRel)
		throws ModelListenerException {
		try {
			long layoutPageTemplateStructureRelId = layoutPageTemplateStructureRel.getLayoutPageTemplateStructureRelId();
			AuditMessage auditMessage =
				AuditMessageBuilder.buildAuditMessage(eventType,
					LayoutPageTemplateStructureRel.class.getName(), layoutPageTemplateStructureRelId,
					null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"layoutPageTemplateStructureRelId", layoutPageTemplateStructureRelId
			).put(
				"layoutPageTemplateStructureId", layoutPageTemplateStructureRel.getLayoutPageTemplateStructureId()
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