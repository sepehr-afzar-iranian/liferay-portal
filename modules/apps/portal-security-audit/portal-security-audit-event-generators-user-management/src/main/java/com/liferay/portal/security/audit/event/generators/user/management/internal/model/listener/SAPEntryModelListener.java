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
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;
import com.liferay.portal.security.service.access.policy.model.SAPEntry;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(immediate = true, service = ModelListener.class)
public class SAPEntryModelListener extends BaseModelListener<SAPEntry> {

	@Override
	public void onAfterCreate(SAPEntry sapEntry) throws ModelListenerException {
		audit(EventTypes.ADD, sapEntry);
	}

	@Override
	public void onAfterRemove(SAPEntry sapEntry) throws ModelListenerException {
		audit(EventTypes.DELETE, sapEntry);
	}

	@Override
	public void onAfterUpdate(SAPEntry sapEntry) throws ModelListenerException {
		audit(EventTypes.UPDATE, sapEntry);
	}

	protected void audit(String eventType, SAPEntry sapEntry)
		throws ModelListenerException {

		try {
			long sapEntryId = sapEntry.getSapEntryId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, SAPEntry.class.getName(), sapEntryId, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"sapEntryId", sapEntryId
			).put(
				"sapEntryName", sapEntry.getName()
			).put(
				"sapEntryTitle", sapEntry.getTitle()
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