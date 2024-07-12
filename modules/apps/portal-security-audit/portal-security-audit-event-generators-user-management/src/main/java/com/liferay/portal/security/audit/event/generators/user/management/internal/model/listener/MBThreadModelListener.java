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
public class MBThreadModelListener extends BaseModelListener<MBThread> {

	@Override
	public void onAfterCreate(MBThread mbThread) throws ModelListenerException {
		audit(EventTypes.ADD, mbThread);
	}

	@Override
	public void onAfterRemove(MBThread mbThread) throws ModelListenerException {
		audit(EventTypes.DELETE, mbThread);
	}

	@Override
	public void onAfterUpdate(MBThread mbThread) throws ModelListenerException {
		audit(EventTypes.UPDATE, mbThread);
	}

	protected void audit(
		String eventType, MBThread mbThread)
		throws ModelListenerException {
		try {
			long mbThreadId = mbThread.getThreadId();
			AuditMessage auditMessage =
				AuditMessageBuilder.buildAuditMessage(eventType,
					MBThread.class.getName(), mbThreadId,
					null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"mbThreadId", mbThreadId
			).put(
				"mbThreadTitle", mbThread.getTitle()
			);

			MBCategory mbCategory = mbThread.getCategory();

			if (Validator.isNotNull(mbCategory)) {
				additionalInfoJSONObject.put(
					"mbThreadCategoryId", mbThread.getCategoryId()
				).put(
					"mbThreadCategoryName", mbCategory.getName()
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