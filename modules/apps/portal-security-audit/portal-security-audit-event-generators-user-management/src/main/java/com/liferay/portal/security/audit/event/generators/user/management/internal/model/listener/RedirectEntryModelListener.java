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
import com.liferay.redirect.model.RedirectEntry;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(immediate = true, service = ModelListener.class)
public class RedirectEntryModelListener
	extends BaseModelListener<RedirectEntry> {

	@Override
	public void onAfterCreate(RedirectEntry redirectEntry)
		throws ModelListenerException {

		audit(EventTypes.ADD, redirectEntry);
	}

	@Override
	public void onAfterRemove(RedirectEntry redirectEntry)
		throws ModelListenerException {

		audit(EventTypes.DELETE, redirectEntry);
	}

	@Override
	public void onAfterUpdate(RedirectEntry redirectEntry)
		throws ModelListenerException {

		audit(EventTypes.UPDATE, redirectEntry);
	}

	protected void audit(String eventType, RedirectEntry redirectEntry)
		throws ModelListenerException {

		try {
			long redirectEntryId = redirectEntry.getRedirectEntryId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, RedirectEntry.class.getName(), redirectEntryId,
				null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"redirectEntryDestinationURL", redirectEntry.getDestinationURL()
			).put(
				"redirectEntryId", redirectEntryId
			).put(
				"redirectEntrySourceURL", redirectEntry.getSourceURL()
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