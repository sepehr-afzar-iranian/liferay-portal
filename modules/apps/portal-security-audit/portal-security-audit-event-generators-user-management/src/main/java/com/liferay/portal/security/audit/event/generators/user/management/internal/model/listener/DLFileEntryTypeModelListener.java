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

import com.liferay.document.library.kernel.model.DLFileEntryType;
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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(service = ModelListener.class)
public class DLFileEntryTypeModelListener
	extends BaseModelListener<DLFileEntryType> {

	@Override
	public void onAfterCreate(DLFileEntryType dlFileEntryType)
		throws ModelListenerException {

		audit(EventTypes.ADD, dlFileEntryType);
	}

	@Override
	public void onAfterRemove(DLFileEntryType dlFileEntryType)
		throws ModelListenerException {

		audit(EventTypes.DELETE, dlFileEntryType);
	}

	@Override
	public void onAfterUpdate(DLFileEntryType dlFileEntryType)
		throws ModelListenerException {

		audit(EventTypes.UPDATE, dlFileEntryType);
	}

	protected void audit(String eventType, DLFileEntryType dlFileEntryType)
		throws ModelListenerException {

		try {
			long dlFileEntryTypeId = dlFileEntryType.getFileEntryTypeId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, DLFileEntryType.class.getName(), dlFileEntryTypeId,
				null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"dlFileEntryTypeId", dlFileEntryTypeId
			).put(
				"dlFileEntryTypeName", dlFileEntryType.getName()
			);

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLFileEntryTypeModelListener.class);

	@Reference
	private AuditRouter _auditRouter;

}