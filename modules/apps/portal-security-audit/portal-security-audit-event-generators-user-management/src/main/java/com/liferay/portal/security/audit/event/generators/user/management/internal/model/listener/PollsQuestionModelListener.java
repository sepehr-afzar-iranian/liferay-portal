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
import com.liferay.polls.model.PollsQuestion;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
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
@Component(immediate = true, service = ModelListener.class)
public class PollsQuestionModelListener
	extends BaseModelListener<PollsQuestion> {

	@Override
	public void onAfterCreate(PollsQuestion pollsQuestion)
		throws ModelListenerException {
		audit(EventTypes.ADD, pollsQuestion);
	}

	@Override
	public void onAfterRemove(PollsQuestion pollsQuestion)
		throws ModelListenerException {
		audit(EventTypes.DELETE, pollsQuestion);
	}

	@Override
	public void onAfterUpdate(PollsQuestion pollsQuestion)
		throws ModelListenerException {
		audit(EventTypes.UPDATE, pollsQuestion);
	}

	protected void audit(String eventType, PollsQuestion pollsQuestion)
		throws ModelListenerException {

		try {
			long pollsQuestionId = pollsQuestion.getQuestionId();
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, PollsQuestion.class.getName(), pollsQuestionId, null);

			JSONArray choices = JSONFactoryUtil.createJSONArray();
			
			for (PollsChoice pollsChoice: pollsQuestion.getChoices()) {
				JSONObject choice = JSONFactoryUtil.createJSONObject();
				choice.put(
					"choiceId", pollsChoice.getChoiceId()
				).put(
					"choiceName", pollsChoice.getName()
				).put(
					"choiceDescription", pollsChoice.getDescription()
				);
				choices.put(choice);
			}

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();
			additionalInfoJSONObject.put(
				"groupId", serviceContext.getScopeGroupId()
			).put(
				"pollsQuestionId", pollsQuestionId
			).put(
				"pollsQuestionTitle", pollsQuestion.getTitle()
			).put(
				"pollsQuestionDescription", pollsQuestion.getDescription()
			).put(
				"pollsQuestionChoices", choices
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