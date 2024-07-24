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

import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.MBThreadLocalService;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(immediate = true, service = ModelListener.class)
public class MBDiscussionModelListener extends BaseModelListener<MBDiscussion> {

	@Override
	public void onAfterCreate(MBDiscussion mbDiscussion)
		throws ModelListenerException {

		audit(EventTypes.ADD, mbDiscussion);
	}

	@Override
	public void onAfterRemove(MBDiscussion mbDiscussion)
		throws ModelListenerException {

		audit(EventTypes.DELETE, mbDiscussion);
	}

	@Override
	public void onAfterUpdate(MBDiscussion mbDiscussion)
		throws ModelListenerException {

		audit(EventTypes.UPDATE, mbDiscussion);
	}

	protected void audit(String eventType, MBDiscussion mbDiscussion)
		throws ModelListenerException {

		try {
			long mbDiscussionId = mbDiscussion.getDiscussionId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, MBDiscussion.class.getName(), mbDiscussionId, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put("mbDiscussionId", mbDiscussionId);

			long mbThreadId = mbDiscussion.getThreadId();

			MBThread mbThread = _mbThreadLocalService.fetchMBThread(mbThreadId);

			if (!Objects.equals(mbThread, null)) {
				additionalInfoJSONObject.put(
					"mbThreadId", mbThreadId
				).put(
					"mbThreadTitle", mbThread.getTitle()
				);
			}

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private MBThreadLocalService _mbThreadLocalService;

}