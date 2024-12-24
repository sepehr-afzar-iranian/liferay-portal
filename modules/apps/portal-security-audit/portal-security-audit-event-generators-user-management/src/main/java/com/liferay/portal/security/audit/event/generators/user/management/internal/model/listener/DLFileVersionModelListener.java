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
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.user.management.util.AuditMessageHelperUtil;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(service = ModelListener.class)
public class DLFileVersionModelListener
	extends BaseModelListener<DLFileVersion> {

	@Override
	public void onAfterCreate(DLFileVersion dlFileVersion)
		throws ModelListenerException {

		audit(EventTypes.ADD, dlFileVersion);
	}

	@Override
	public void onAfterRemove(DLFileVersion dlFileVersion)
		throws ModelListenerException {

		audit(EventTypes.DELETE, dlFileVersion);
	}

	@Override
	public void onAfterUpdate(DLFileVersion dlFileVersion)
		throws ModelListenerException {

		audit(EventTypes.UPDATE, dlFileVersion);
	}

	protected void audit(String eventType, DLFileVersion dlFileVersion)
		throws ModelListenerException {

		try {
			long dlFileVersionId = dlFileVersion.getFileVersionId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, DLFileVersion.class.getName(), dlFileVersionId,
				null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"dlFileVersionFolderId", dlFileVersion.getFolderId()
			).put(
				"dlFileVersionId", dlFileVersionId
			).put(
				"dlFolderId", dlFileVersion.getFolderId()
			).put(
				"fileName", dlFileVersion.getFileName()
			).put(
				"fileVersion", dlFileVersion.getVersion()
			);

			DLFolder dlFolder = dlFileVersion.getFolder();

			if (!Objects.equals(dlFolder, null)) {
				additionalInfoJSONObject.put(
					"dlFileEntryFolderName", dlFolder.getName());
			}

			DLFileEntry dlFileEntry = dlFileVersion.getFileEntry();

			if (!Objects.equals(dlFileEntry, null)) {
				additionalInfoJSONObject.put(
					"dlFileEntryIsCheckedOut", dlFileEntry.isCheckedOut());
			}

			auditMessage.setMessage(
				AuditMessageHelperUtil.getMessage(
					eventType, auditMessage.getClassName(),
					dlFileEntry.getFileName(), dlFileVersionId));

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileVersionModelListener.class);

	@Reference
	private AuditRouter _auditRouter;

}