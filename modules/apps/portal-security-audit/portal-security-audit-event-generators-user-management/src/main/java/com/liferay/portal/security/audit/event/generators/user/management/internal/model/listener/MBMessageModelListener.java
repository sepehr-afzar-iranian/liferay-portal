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

import com.liferay.message.boards.model.MBCategory;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.model.MBThread;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;

import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(immediate = true, service = ModelListener.class)
public class MBMessageModelListener
	extends BaseModelListener<MBMessage> {

	@Override
	public void onAfterCreate(MBMessage mbMessage) throws ModelListenerException {
		audit(EventTypes.ADD, mbMessage);
	}

	@Override
	public void onAfterRemove(MBMessage mbMessage) throws ModelListenerException {
		audit(EventTypes.DELETE, mbMessage);
	}

	@Override
	public void onAfterUpdate(MBMessage mbMessage) throws ModelListenerException {
		audit(EventTypes.UPDATE, mbMessage);
	}

	protected void audit(
		String eventType, MBMessage mbMessage)
		throws ModelListenerException {
		try {
			long mbMessageId = mbMessage.getMessageId();
			AuditMessage auditMessage =
				AuditMessageBuilder.buildAuditMessage(eventType,
					MBMessage.class.getName(), mbMessageId,
					null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"mbMessageId", mbMessageId
			).put(
				"mbMessageBody", mbMessage.getBody()
			);

			MBCategory mbCategory = mbMessage.getCategory();
			
			if (Validator.isNotNull(mbCategory)) {
				additionalInfoJSONObject.put(
					"mbCategoryId", mbCategory.getCategoryId()
				).put(
					"mbCategoryName", mbCategory.getName()
				);
			}

			MBThread mbThread = mbMessage.getThread();

			if (Validator.isNotNull(mbThread)) {
				additionalInfoJSONObject.put(
					"mbThreadId", mbThread.getThreadId()
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

}