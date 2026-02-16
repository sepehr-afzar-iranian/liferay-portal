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

package com.gelzobal.item.selector.editor.configuration.internal;

import com.gelzobal.item.selector.ItemSelector;
import com.gelzobal.item.selector.ItemSelectorCriterion;
import com.gelzobal.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Map;

import javax.portlet.PortletURL;

/**
 * @author Sergio González
 */
public abstract class BaseEditorConfigContributor
	extends com.liferay.portal.kernel.editor.configuration.
				BaseEditorConfigContributor {

	public PortletURL getItemSelectorPortletURL(
		Map<String, Object> inputEditorTaglibAttributes,
		RequestBackedPortletURLFactory requestBackedPortletURLFactory,
		ItemSelectorCriterion... itemSelectorCriteria) {

		ItemSelector itemSelector = getItemSelector();

		String name = GetterUtil.getString(
			inputEditorTaglibAttributes.get("liferay-ui:input-editor:name"));

		boolean inlineEdit = GetterUtil.getBoolean(
			inputEditorTaglibAttributes.get(
				"liferay-ui:input-editor:inlineEdit"));

		if (!inlineEdit) {
			String namespace = GetterUtil.getString(
				inputEditorTaglibAttributes.get(
					"liferay-ui:input-editor:namespace"));

			name = namespace + name;
		}

		for (ItemSelectorCriterion itemSelectorCriterion :
				itemSelectorCriteria) {

			itemSelectorCriterion.setDesiredItemSelectorReturnTypes(
				new URLItemSelectorReturnType());
		}

		return itemSelector.getItemSelectorURL(
			requestBackedPortletURLFactory, name + "selectItem",
			itemSelectorCriteria);
	}

	protected abstract ItemSelector getItemSelector();

}