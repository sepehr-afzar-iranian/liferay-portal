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

package com.liferay.dynamic.data.mapping.form.field.type.internal.license.plate;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.AggregateResourceBundle;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(
	immediate = true, property = "ddm.form.field.type.name=license_plate",
	service = DDMFormFieldValueRenderer.class
)
public class LicensePlateDDMFormFieldValueRenderer
	implements DDMFormFieldValueRenderer {

	@Override
	public String render(DDMFormFieldValue ddmFormFieldValue, Locale locale) {
		Value value = ddmFormFieldValue.getValue();

		if (value == null) {
			return StringPool.BLANK;
		}

		String valueStr = value.getString(locale);

		Matcher matcher = LicensePlateDDMFormFieldUtil.matchLicensePlate(
			valueStr);

		if (matcher.find()) {
			StringBuilder sb = _getFormattedValue(matcher);

			return sb.toString();
		}

		return valueStr;
	}

	protected static ResourceBundle getResourceBundle(Locale locale) {
		ResourceBundle portalResourceBundle = PortalUtil.getResourceBundle(
			locale);

		ResourceBundle moduleResourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale,
			LicensePlateDDMFormFieldValueRenderer.class);

		return new AggregateResourceBundle(
			moduleResourceBundle, portalResourceBundle);
	}

	@Reference
	protected Portal portal;

	private StringBuilder _getFormattedValue(Matcher matcher) {
		String firstPart = matcher.group(1);
		String letterPart = matcher.group(2);
		String secondPart = matcher.group(3);
		String regionPart = matcher.group(4);

		Locale locale = LocaleThreadLocal.getThemeDisplayLocale();

		String language = locale.getLanguage();

		StringBuilder sb = new StringBuilder();

		sb.append(firstPart);
		sb.append(StringPool.SPACE);

		if (language.equals("fa")) {
			sb.append(letterPart);
		}
		else {
			sb.append(
				LicensePlateDDMFormFieldUtil.persianToLatin.getOrDefault(
					letterPart, letterPart));
		}

		sb.append(StringPool.SPACE);
		sb.append(secondPart);
		sb.append(StringPool.SPACE);
		sb.append(StringPool.PIPE);
		sb.append(StringPool.SPACE);
		sb.append(
			LanguageUtil.get(
				getResourceBundle(locale), "license-plate-ir-country-label"));
		sb.append(StringPool.SPACE);
		sb.append(regionPart);

		return sb;
	}

}