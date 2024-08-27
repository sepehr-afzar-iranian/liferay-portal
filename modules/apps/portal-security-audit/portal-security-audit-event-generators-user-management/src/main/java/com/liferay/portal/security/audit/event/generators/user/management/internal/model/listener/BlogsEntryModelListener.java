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

import com.liferay.blogs.model.BlogsEntry;
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
@Component(immediate = true, service = ModelListener.class)
public class BlogsEntryModelListener extends BaseModelListener<BlogsEntry> {

	@Override
	public void onAfterCreate(BlogsEntry blogsEntry)
		throws ModelListenerException {

		audit(EventTypes.ADD, blogsEntry);
	}

	@Override
	public void onAfterRemove(BlogsEntry blogsEntry)
		throws ModelListenerException {

		audit(EventTypes.DELETE, blogsEntry);
	}

	@Override
	public void onAfterUpdate(BlogsEntry blogsEntry)
		throws ModelListenerException {

		audit(EventTypes.UPDATE, blogsEntry);
	}

	protected void audit(String eventType, BlogsEntry blogsEntry)
		throws ModelListenerException {

		try {
			long blogsEntryId = blogsEntry.getEntryId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, BlogsEntry.class.getName(), blogsEntryId, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"blogsEntryId", blogsEntryId
			).put(
				"blogsEntryTitle", blogsEntry.getTitle()
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
		BlogsEntryModelListener.class);

	@Reference
	private AuditRouter _auditRouter;

}