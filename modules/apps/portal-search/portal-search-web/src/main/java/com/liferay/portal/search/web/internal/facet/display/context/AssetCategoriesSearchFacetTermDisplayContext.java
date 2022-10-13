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

package com.liferay.portal.search.web.internal.facet.display.context;

/**
 * @author Lino Alves
 */
public class AssetCategoriesSearchFacetTermDisplayContext
	extends BucketDisplayContext {

	public long getAssetCategoryId() {
		return _assetCategoryId;
	}

	public String getDisplayName() {
		return _displayName;
	}

	public void setAssetCategoryId(long assetCategoryId) {
		_assetCategoryId = assetCategoryId;
	}

	public void setDisplayName(String title) {
		_displayName = title;
	}

	private long _assetCategoryId;
	private String _displayName;

}