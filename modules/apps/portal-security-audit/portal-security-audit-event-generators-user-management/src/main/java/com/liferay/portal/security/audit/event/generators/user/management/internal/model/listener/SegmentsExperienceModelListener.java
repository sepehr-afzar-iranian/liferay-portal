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
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsEntryLocalService;

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

	protected void audit(String eventType, SegmentsExperience segmentsExperience)
		throws ModelListenerException {

		try {
			long segmentsExperienceId = segmentsExperience.getSegmentsExperienceId();
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, SegmentsExperience.class.getName(), segmentsExperienceId, null);

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			long segmentsEntryId = segmentsExperience.getSegmentsEntryId();

			additionalInfoJSONObject.put(
				"groupId", serviceContext.getScopeGroupId()
			).put(
				"segmentsExperienceId", segmentsExperienceId
			).put(
				"segmentsExperienceName", segmentsExperience.getName()
			).put(
				"segmentsEntryId", segmentsEntryId
			).put(
				"segmentsEntryName", _segmentsEntryLocalService.fetchSegmentsEntry(segmentsEntryId).getName()
			);

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private SegmentsEntryLocalService _segmentsEntryLocalService;

}