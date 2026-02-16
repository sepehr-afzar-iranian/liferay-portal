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

package com.gelzobal.item.selector.criteria.internal.upload;

import com.gelzobal.item.selector.BaseItemSelectorCriterionHandler;
import com.gelzobal.item.selector.ItemSelectorCriterionHandler;
import com.gelzobal.item.selector.criteria.upload.criterion.UploadItemSelectorCriterion;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Ambrín Chaudhary
 */
@Component(service = ItemSelectorCriterionHandler.class)
public class UploadItemSelectorCriterionHandler
	extends BaseItemSelectorCriterionHandler<UploadItemSelectorCriterion> {

	@Override
	public Class<UploadItemSelectorCriterion> getItemSelectorCriterionClass() {
		return UploadItemSelectorCriterion.class;
	}

	@Activate
	@Override
	protected void activate(BundleContext bundleContext) {
		super.activate(bundleContext);
	}

	@Deactivate
	@Override
	protected void deactivate() {
		super.deactivate();
	}

}