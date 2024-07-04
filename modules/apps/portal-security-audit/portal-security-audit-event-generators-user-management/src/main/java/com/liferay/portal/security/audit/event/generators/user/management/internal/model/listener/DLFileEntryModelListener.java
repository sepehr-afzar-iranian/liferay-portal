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

import com.liferay.document.library.kernel.model.DLFileEntry;
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
@Component(service = ModelListener.class)
public class DLFileEntryModelListener
	extends BaseModelListener<DLFileEntry> {

	@Override
	public void onAfterCreate(DLFileEntry dlFileEntry)
		throws ModelListenerException {
		audit(EventTypes.ADD, dlFileEntry);
	}

	@Override
	public void onAfterUpdate(DLFileEntry dlFileEntry)
		throws ModelListenerException {
		audit(EventTypes.UPDATE, dlFileEntry);
	}

	@Override
	public void onAfterRemove(DLFileEntry dlFileEntry)
		throws ModelListenerException {
		audit(EventTypes.DELETE, dlFileEntry);
	}

	protected void audit(String eventType, DLFileEntry dlFileEntry)
		throws ModelListenerException {

		try {
			long dlFileEntryId = dlFileEntry.getFileEntryId();
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, DLFileEntry.class.getName(), dlFileEntryId, null);

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"groupId", serviceContext.getScopeGroupId()
			).put(
				"dlFileEntryId", dlFileEntryId
			).put(
				"dlEntryName", dlFileEntry.getName()
			).put(
				"dlFileEntryFolderId", dlFileEntry.getFolderId()
			).put(
				"dlFileEntryFolderName", dlFileEntry.getFolder().getName()
			).put(
				"dlFileEntryName", dlFileEntry.getFileName()
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