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

import com.liferay.asset.kernel.model.AssetVocabulary;
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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(immediate = true, service = ModelListener.class)
public class AssetVocabularyModelListener
	extends BaseModelListener<AssetVocabulary> {

	@Override
	public void onAfterCreate(AssetVocabulary assetVocabulary)
		throws ModelListenerException {

		audit(EventTypes.ADD, assetVocabulary);
	}

	@Override
	public void onAfterRemove(AssetVocabulary assetVocabulary)
		throws ModelListenerException {

		audit(EventTypes.DELETE, assetVocabulary);
	}

	@Override
	public void onAfterUpdate(AssetVocabulary assetVocabulary)
		throws ModelListenerException {

		audit(EventTypes.UPDATE, assetVocabulary);
	}

	protected void audit(String eventType, AssetVocabulary assetVocabulary)
		throws ModelListenerException {

		try {
			long assetVocabularyId = assetVocabulary.getVocabularyId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, AssetVocabulary.class.getName(), assetVocabularyId,
				null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"assetVocabularyId", assetVocabularyId
			).put(
				"assetVocabularyName", assetVocabulary.getName()
			);

			auditMessage.setMessage(
				AuditMessageHelperUtil.getMessage(
					eventType, auditMessage.getClassName(),
					assetVocabulary.getName(), assetVocabularyId));

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetVocabularyModelListener.class);

	@Reference
	private AuditRouter _auditRouter;

}