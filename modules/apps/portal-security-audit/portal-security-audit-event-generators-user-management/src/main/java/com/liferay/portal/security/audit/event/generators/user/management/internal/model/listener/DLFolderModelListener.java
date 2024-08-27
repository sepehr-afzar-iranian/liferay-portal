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
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(service = ModelListener.class)
public class DLFolderModelListener extends BaseModelListener<DLFolder> {

	@Override
	public void onAfterCreate(DLFolder dlFolder) throws ModelListenerException {
		audit(EventTypes.ADD, dlFolder);
	}

	@Override
	public void onAfterRemove(DLFolder dlFolder) throws ModelListenerException {
		audit(EventTypes.DELETE, dlFolder);
	}

	@Override
	public void onAfterUpdate(DLFolder dlFolder) throws ModelListenerException {
		audit(EventTypes.UPDATE, dlFolder);
	}

	protected void audit(String eventType, DLFolder dlFolder)
		throws ModelListenerException {

		try {
			long dlFolderId = dlFolder.getFolderId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, DLFolder.class.getName(), dlFolderId, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"dlFolderId", dlFolderId
			).put(
				"dlFolderName", dlFolder.getName()
			);

			DLFolder parentFolder = dlFolder.getParentFolder();

			if (!Objects.equals(parentFolder, null)) {
				additionalInfoJSONObject.put(
					"dlFolderHasParentFolder", true
				).put(
					"dlFolderParentFolderId", parentFolder.getFolderId()
				).put(
					"dlFolderParentFolderName", parentFolder.getName()
				);
			}
			else {
				additionalInfoJSONObject.put("dlFolderHasParentFolder", false);
			}

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(DLFolderModelListener.class);

	@Reference
	private AuditRouter _auditRouter;

}