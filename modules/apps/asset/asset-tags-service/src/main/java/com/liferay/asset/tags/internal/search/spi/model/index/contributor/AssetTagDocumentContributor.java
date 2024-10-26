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

package com.liferay.asset.tags.internal.search.spi.model.index.contributor;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentContributor;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(immediate = true, service = DocumentContributor.class)
public class AssetTagDocumentContributor
	implements DocumentContributor<AssetTag> {

	@Override
	public void contribute(Document document, BaseModel<AssetTag> baseModel) {
		String className = document.get(Field.ENTRY_CLASS_NAME);

		long classNameId = portal.getClassNameId(className);

		long classPK = GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK));

		List<AssetTag> assetTags = assetTagLocalService.getTags(
			classNameId, classPK);

		if (ListUtil.isEmpty(assetTags)) {
			return;
		}

		contributeAssetTagIds(document, assetTags);
		contributeAssetTagNamesLocalized(document, assetTags, baseModel);
		contributeAssetTagNamesRaw(document, assetTags);
	}

	protected void contributeAssetTagIds(
		Document document, List<AssetTag> assetTags) {

		document.addKeyword(Field.ASSET_TAG_IDS, getTagIds(assetTags));
	}

	protected void contributeAssetTagNamesLocalized(
		Document document, List<AssetTag> assetTags,
		BaseModel<AssetTag> baseModel) {

		Long groupId = getGroupId(baseModel);

		if (groupId == null) {
			return;
		}

		Localization localization = getLocalization();

		document.addText(
			localization.getLocalizedName(
				Field.ASSET_TAG_NAMES,
				LocaleUtil.toLanguageId(getSiteDefaultLocale(groupId))),
			getNames(assetTags));
	}

	protected void contributeAssetTagNamesRaw(
		Document document, List<AssetTag> assetTags) {

		document.addText(Field.ASSET_TAG_NAMES, getNames(assetTags));
	}

	protected Long getGroupId(BaseModel<?> baseModel) {
		if (baseModel instanceof GroupedModel) {
			GroupedModel groupedModel = (GroupedModel)baseModel;

			return groupedModel.getGroupId();
		}

		if (baseModel instanceof Organization) {
			Organization organization = (Organization)baseModel;

			return organization.getGroupId();
		}

		if (baseModel instanceof User) {
			User user = (User)baseModel;

			return user.getGroupId();
		}

		return null;
	}

	protected Localization getLocalization() {

		// See LPS-72507 and LPS-76500

		if (localization != null) {
			return localization;
		}

		return LocalizationUtil.getLocalization();
	}

	protected String[] getNames(List<AssetTag> assetTags) {
		Stream<AssetTag> stream = assetTags.stream();

		return stream.map(
			AssetTag::getName
		).toArray(
			String[]::new
		);
	}

	protected Locale getSiteDefaultLocale(long groupId) {
		try {
			return portal.getSiteDefaultLocale(groupId);
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	protected Long[] getTagIds(List<AssetTag> assetTags) {
		Stream<AssetTag> stream = assetTags.stream();

		return stream.map(
			AssetTag::getTagId
		).toArray(
			Long[]::new
		);
	}

	@Reference
	protected AssetTagLocalService assetTagLocalService;

	protected Localization localization;

	@Reference
	protected Portal portal;

}