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
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.user.management.util.AuditMessageHelperUtil;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;
import com.liferay.segments.model.SegmentsEntry;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(immediate = true, service = ModelListener.class)
public class SegmentsEntryModelListener
	extends BaseModelListener<SegmentsEntry> {

	@Override
	public void onAfterCreate(SegmentsEntry segmentsEntry)
		throws ModelListenerException {

		audit(EventTypes.ADD, segmentsEntry);
	}

	@Override
	public void onAfterRemove(SegmentsEntry segmentsEntry)
		throws ModelListenerException {

		audit(EventTypes.DELETE, segmentsEntry);
	}

	@Override
	public void onAfterUpdate(SegmentsEntry segmentsEntry)
		throws ModelListenerException {

		audit(EventTypes.UPDATE, segmentsEntry);
	}

	protected void audit(String eventType, SegmentsEntry segmentsEntry)
		throws ModelListenerException {

		try {
			long segmentsEntryId = segmentsEntry.getSegmentsEntryId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, SegmentsEntry.class.getName(), segmentsEntryId,
				null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"segmentsEntryId", segmentsEntryId
			).put(
				"segmentsEntryName", segmentsEntry.getName()
			);

			String defaultLanguageId = LocaleUtil.toLanguageId(
				LocaleUtil.getSiteDefault());

			auditMessage.setMessage(
				AuditMessageHelperUtil.getMessage(
					eventType, auditMessage.getClassName(),
					segmentsEntry.getName(defaultLanguageId), segmentsEntryId));

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsEntryModelListener.class);

	@Reference
	private AuditRouter _auditRouter;

}