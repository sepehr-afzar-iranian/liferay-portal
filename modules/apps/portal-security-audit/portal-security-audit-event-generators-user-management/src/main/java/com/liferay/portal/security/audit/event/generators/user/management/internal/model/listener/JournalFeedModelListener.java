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

import com.liferay.journal.model.JournalFeed;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(immediate = true, service = ModelListener.class)
public class JournalFeedModelListener extends BaseModelListener<JournalFeed> {

	@Override
	public void onAfterCreate(JournalFeed journalFeed)
		throws ModelListenerException {

		audit(EventTypes.ADD, journalFeed);
	}

	@Override
	public void onAfterRemove(JournalFeed journalFeed)
		throws ModelListenerException {

		audit(EventTypes.DELETE, journalFeed);
	}

	@Override
	public void onAfterUpdate(JournalFeed journalFeed)
		throws ModelListenerException {

		audit(EventTypes.UPDATE, journalFeed);
	}

	protected void audit(String eventType, JournalFeed journalFeed)
		throws ModelListenerException {

		try {
			long id = journalFeed.getId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, JournalFeed.class.getName(), id, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"id", id
			).put(
				"journalFeedId", journalFeed.getFeedId()
			).put(
				"journalFeedId", journalFeed.getFeedId()
			).put(
				"journalFeedName", journalFeed.getName()
			);

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(JournalFeedModelListener.class);

	@Reference
	private AuditRouter _auditRouter;

}