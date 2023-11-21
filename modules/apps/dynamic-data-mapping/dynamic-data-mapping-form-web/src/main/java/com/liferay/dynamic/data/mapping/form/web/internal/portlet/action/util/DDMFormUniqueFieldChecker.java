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

package com.liferay.dynamic.data.mapping.form.web.internal.portlet.action.util;

import com.liferay.dynamic.data.mapping.model.DDMContent;
import com.liferay.dynamic.data.mapping.service.DDMContentLocalService;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author S.Abolfazl Eftekhari
 */
@Component(immediate = true, service = DDMFormUniqueFieldChecker.class)
public class DDMFormUniqueFieldChecker {

	public boolean check(String fieldValue, String fieldReference)
		throws PortalException {

		DynamicQuery dynamicQuery = _ddmContentLocalService.dynamicQuery();

		String value = String.format("%%%s%%", fieldValue);
		String reference = String.format("%%%s%%", fieldReference);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("data"));
		dynamicQuery.add(RestrictionsFactoryUtil.like("data", value));
		dynamicQuery.add(RestrictionsFactoryUtil.like("data", reference));

		List<DDMContent> oldValues = _ddmContentLocalService.dynamicQuery(
			dynamicQuery);

		for (Object oldValue : oldValues) {
			String jsonString = String.valueOf(oldValue);

			JSONObject createJSONObject = _jsonFactory.createJSONObject(
				jsonString);

			JSONArray jsonArray = createJSONObject.getJSONArray("fieldValues");

			for (int i = 0; i < jsonArray.length(); i++) {
				String jsonReference = jsonArray.getJSONObject(
					i
				).getString(
					"fieldReference"
				);

				JSONObject jsonObject = jsonArray.getJSONObject(
					i
				).getJSONObject(
					"value"
				);

				String enUsValue = jsonObject.getString("en_US");

				if (fieldValue.equals(enUsValue) &&
					fieldReference.equals(jsonReference)) {

					return true;
				}
			}
		}

		return false;
	}

	@Reference
	private DDMContentLocalService _ddmContentLocalService;

	@Reference
	private JSONFactory _jsonFactory;

}