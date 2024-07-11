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

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(immediate = true, service = ModelListener.class)
public class AssetCategoryModelListener
	extends BaseModelListener<AssetCategory> {

	@Override
	public void onAfterCreate(AssetCategory assetCategory)
		throws ModelListenerException {
		audit(EventTypes.ADD, assetCategory);
	}

	@Override
	public void onAfterRemove(AssetCategory assetCategory)
		throws ModelListenerException {
		audit(EventTypes.DELETE, assetCategory);
	}

	@Override
	public void onAfterUpdate(AssetCategory assetCategory)
		throws ModelListenerException {
		audit(EventTypes.UPDATE, assetCategory);
	}

	protected void audit(String eventType, AssetCategory assetCategory)
		throws ModelListenerException {

		try {
			long assetCategoryId = assetCategory.getCategoryId();
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, AssetCategory.class.getName(), assetCategoryId, null);

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			AssetVocabulary assetVocabulary =
				_assetVocabularyLocalService.fetchAssetVocabulary(assetCategoryId);
			
			long assetVocabularyId = assetCategory.getVocabularyId();
			
			additionalInfoJSONObject.put(
				"groupId", serviceContext.getScopeGroupId()
			).put(
				"assetCategoryId", assetCategoryId
			).put(
				"assetCategoryName", assetCategory.getName()
			);

			if (Validator.isNotNull(assetVocabulary)) {
				additionalInfoJSONObject.put(
					"assetVocabularyId", assetVocabularyId
				).put(
					"assetVocabularyName", assetVocabulary.getName()
				);
			}

			AssetCategory parentAssetCategory = assetCategory.getParentCategory();

			if (Validator.isNotNull(parentAssetCategory)) {
				additionalInfoJSONObject.put(
					"parentAssetCategoryId", parentAssetCategory.getCategoryId()
				).put(
					"parentAssetCategoryName", parentAssetCategory.getName()
				);
			}

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private AuditRouter _auditRouter;
	
	@Reference
	private AssetVocabularyLocalService _assetVocabularyLocalService;

}