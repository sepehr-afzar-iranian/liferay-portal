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
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.RepositoryEntry;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.user.management.util.AuditMessageHelperUtil;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(service = ModelListener.class)
public class RepositoryEntryModelListener
	extends BaseModelListener<RepositoryEntry> {

	@Override
	public void onAfterCreate(RepositoryEntry repositoryEntry)
		throws ModelListenerException {

		audit(EventTypes.ADD, repositoryEntry);
	}

	@Override
	public void onAfterRemove(RepositoryEntry repositoryEntry)
		throws ModelListenerException {

		audit(EventTypes.DELETE, repositoryEntry);
	}

	@Override
	public void onAfterUpdate(RepositoryEntry repositoryEntry)
		throws ModelListenerException {

		audit(EventTypes.UPDATE, repositoryEntry);
	}

	protected void audit(String eventType, RepositoryEntry repositoryEntry)
		throws ModelListenerException {

		try {
			long repositoryEntryId = repositoryEntry.getRepositoryEntryId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, RepositoryEntry.class.getName(), repositoryEntryId,
				null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"repositoryEntryId", repositoryEntryId
			).put(
				"repositoryId", repositoryEntry.getRepositoryId()
			);

			auditMessage.setMessage(
				AuditMessageHelperUtil.getMessage(
					eventType, auditMessage.getClassName(), null,
					repositoryEntryId));

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RepositoryEntryModelListener.class);

	@Reference
	private AuditRouter _auditRouter;

}