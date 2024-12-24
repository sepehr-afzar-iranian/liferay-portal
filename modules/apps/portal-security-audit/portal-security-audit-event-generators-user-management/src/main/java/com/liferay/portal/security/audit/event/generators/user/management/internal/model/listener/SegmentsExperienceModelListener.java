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
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsEntryLocalService;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(service = ModelListener.class)
public class SegmentsExperienceModelListener
	extends BaseModelListener<SegmentsExperience> {

	@Override
	public void onAfterCreate(SegmentsExperience segmentsExperience)
		throws ModelListenerException {

		audit(EventTypes.ADD, segmentsExperience);
	}

	@Override
	public void onAfterRemove(SegmentsExperience segmentsExperience)
		throws ModelListenerException {

		audit(EventTypes.DELETE, segmentsExperience);
	}

	@Override
	public void onAfterUpdate(SegmentsExperience segmentsExperience)
		throws ModelListenerException {

		audit(EventTypes.UPDATE, segmentsExperience);
	}

	protected void audit(
			String eventType, SegmentsExperience segmentsExperience)
		throws ModelListenerException {

		try {
			long segmentsExperienceId =
				segmentsExperience.getSegmentsExperienceId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, SegmentsExperience.class.getName(),
				segmentsExperienceId, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			long segmentsEntryId = segmentsExperience.getSegmentsEntryId();

			SegmentsEntry segmentsEntry =
				_segmentsEntryLocalService.fetchSegmentsEntry(segmentsEntryId);

			additionalInfoJSONObject.put(
				"segmentsExperienceId", segmentsExperienceId
			).put(
				"segmentsExperienceName", segmentsExperience.getName()
			);

			if (!Objects.equals(segmentsEntry, null)) {
				additionalInfoJSONObject.put(
					"segmentsEntryId", segmentsEntryId
				).put(
					"segmentsEntryName", segmentsEntry.getName()
				);
			}

			String defaultLanguageId = LocaleUtil.toLanguageId(
				LocaleUtil.getSiteDefault());

			auditMessage.setMessage(
				AuditMessageHelperUtil.getMessage(
					eventType, auditMessage.getClassName(),
					segmentsExperience.getName(defaultLanguageId),
					segmentsExperienceId));

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SegmentsExperienceModelListener.class);

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private SegmentsEntryLocalService _segmentsEntryLocalService;

}