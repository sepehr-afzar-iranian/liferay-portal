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

import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.model.KBFolder;
import com.liferay.knowledge.base.service.KBFolderLocalService;
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
@Component(immediate = true, service = ModelListener.class)
public class KBArticleModelListener extends BaseModelListener<KBArticle> {

	@Override
	public void onAfterCreate(KBArticle kbArticle)
		throws ModelListenerException {

		audit(EventTypes.ADD, kbArticle);
	}

	@Override
	public void onAfterRemove(KBArticle kbArticle)
		throws ModelListenerException {

		audit(EventTypes.DELETE, kbArticle);
	}

	@Override
	public void onAfterUpdate(KBArticle kbArticle)
		throws ModelListenerException {

		audit(EventTypes.UPDATE, kbArticle);
	}

	protected void audit(String eventType, KBArticle kbArticle)
		throws ModelListenerException {

		try {
			long kbArticleId = kbArticle.getKbArticleId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, KBArticle.class.getName(), kbArticleId, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"kbArticleId", kbArticleId
			).put(
				"kbArticleTitle", kbArticle.getTitle()
			);

			long kbFolderId = kbArticle.getKbFolderId();

			KBFolder kbFolder = _kbFolderLocalService.fetchKBFolder(kbFolderId);

			if (!Objects.equals(kbFolder, null)) {
				additionalInfoJSONObject.put(
					"kbFolderId", kbFolderId
				).put(
					"kbFolderName", kbFolder.getName()
				);
			}

			KBArticle parentKBArticle = kbArticle.getParentKBArticle();

			if (!Objects.equals(parentKBArticle, null)) {
				additionalInfoJSONObject.put(
					"parentKBArticleId", parentKBArticle.getKbArticleId()
				).put(
					"parentKBArticleTitle", parentKBArticle.getTitle()
				);
			}

			auditMessage.setMessage(
				AuditMessageHelperUtil.getMessage(
					eventType, auditMessage.getClassName(),
					kbArticle.getTitle(), kbArticleId));

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KBArticleModelListener.class);

	@Reference
	private AuditRouter _auditRouter;

	@Reference
	private KBFolderLocalService _kbFolderLocalService;

}