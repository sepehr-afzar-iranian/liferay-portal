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

import com.liferay.polls.model.PollsChoice;
import com.liferay.polls.model.PollsVote;
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
@Component(immediate = true, service = ModelListener.class)
public class PollsVoteModelListener
	extends BaseModelListener<PollsVote> {

	@Override
	public void onAfterCreate(PollsVote pollsVote)
		throws ModelListenerException {
		audit(EventTypes.ADD, pollsVote);
	}

	@Override
	public void onAfterRemove(PollsVote pollsVote)
		throws ModelListenerException {
		audit(EventTypes.DELETE, pollsVote);
	}

	@Override
	public void onAfterUpdate(PollsVote pollsVote)
		throws ModelListenerException {
		audit(EventTypes.UPDATE, pollsVote);
	}

	protected void audit(String eventType, PollsVote pollsVote)
		throws ModelListenerException {

		try {
			long pollsVoteId = pollsVote.getVoteId();
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, PollsVote.class.getName(), pollsVoteId, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();
			additionalInfoJSONObject.put(
				"pollsVoteId", pollsVoteId
			);

			PollsChoice pollsChoice = null;

			try {
				pollsChoice = pollsVote.getChoice();
				additionalInfoJSONObject.put(
					"pollsChoiceId", pollsChoice.getChoiceId()
				).put(
					"pollsChoiceName", pollsChoice.getName()
				).put(
					"pollsChoiceDescription", pollsChoice.getDescription()
				);
			} catch (Exception ignored) {}

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private AuditRouter _auditRouter;

}