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

import com.liferay.portal.background.task.model.BackgroundTask;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
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
public class BackgroundTaskModelListener
	extends BaseModelListener<BackgroundTask> {

	@Override
	public void onAfterCreate(BackgroundTask backgroundTask)
		throws ModelListenerException {
		audit(EventTypes.ADD, backgroundTask);
	}

	@Override
	public void onAfterUpdate(BackgroundTask backgroundTask)
		throws ModelListenerException {
		audit(EventTypes.UPDATE, backgroundTask);
	}

	@Override
	public void onAfterRemove(BackgroundTask backgroundTask)
		throws ModelListenerException {
		audit(EventTypes.DELETE, backgroundTask);
	}

	protected void audit(
		String eventType, BackgroundTask backgroundTask)
		throws ModelListenerException {

		try {
			long backgroundTaskId = backgroundTask.getBackgroundTaskId();

			AuditMessage auditMessage =
				AuditMessageBuilder.buildAuditMessage(eventType,
					BackgroundTask.class.getName(), backgroundTaskId,
					null);
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			boolean isBackgroundTaskCompleted = backgroundTask.isCompleted();

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"groupId", serviceContext.getScopeGroupId()
			).put(
				"backgroundTaskId", backgroundTaskId
			).put(
				"backgroundTaskName", backgroundTask.getName()
			).put(
				"backgroundTaskExecutorClassName", backgroundTask.getTaskExecutorClassName()
			).put(
				"backgroundTaskStatusLabel", backgroundTask.getStatusLabel()
			).put(
				"backgroundTaskStatusMessage", backgroundTask.getStatusMessage()
			).put(
				"isBackgroundTaskCompleted", isBackgroundTaskCompleted
			);

			if (isBackgroundTaskCompleted) {
				additionalInfoJSONObject.put(
					"completionDate", backgroundTask.getCompletionDate()
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