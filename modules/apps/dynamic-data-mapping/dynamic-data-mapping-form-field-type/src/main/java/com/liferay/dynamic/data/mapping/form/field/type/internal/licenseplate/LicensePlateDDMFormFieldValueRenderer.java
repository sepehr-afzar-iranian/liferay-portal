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

package com.liferay.dynamic.data.mapping.form.field.type.internal.licenseplate;

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
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;

/**
 * @author Yousef Ghadiri
 */
@Component(
	immediate = true, property = "ddm.form.field.type.name=licenseplate",
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
		LicensePlateDDMFormFieldContextHelper licensePlateDDMFormFieldContextHelper = new LicensePlateDDMFormFieldContextHelper();
		java.util.regex.Matcher matcher = licensePlateDDMFormFieldContextHelper.matchLicensePlate(valueStr);
		if (matcher.find()) {
			StringBuilder sb = getFormattedValue(matcher);

			return sb.toString();
		}

		return valueStr;
	}

	private static StringBuilder getFormattedValue(Matcher matcher) {
		String firstPart = matcher.group(1);
		String letterPart = matcher.group(2);
		String secondPart = matcher.group(3);
		String regionPart = matcher.group(4);

		Locale locale = LocaleThreadLocal.getThemeDisplayLocale();

		StringBuilder sb = new StringBuilder();
		sb.append(firstPart);
		sb.append(StringPool.SPACE);
		if (locale.getLanguage().equals("fa")) {
			sb.append(letterPart);
		} else {
			sb.append(PERSIAN_ARABIC_TO_LATIN.getOrDefault(letterPart, letterPart));
		}
		sb.append(StringPool.SPACE);
		sb.append(secondPart);
		sb.append(StringPool.SPACE);
		sb.append(StringPool.PIPE);
		sb.append(StringPool.SPACE);
		sb.append(LanguageUtil.get(getResourceBundle(locale), "license-plate-ir-country-label"));
		sb.append(StringPool.SPACE);
		sb.append(regionPart);

		return sb;
	}

	protected static ResourceBundle getResourceBundle(Locale locale) {
		ResourceBundle portalResourceBundle = PortalUtil.getResourceBundle(locale);

		ResourceBundle moduleResourceBundle = ResourceBundleUtil.getBundle(
				"content.Language", locale, LicensePlateDDMFormFieldValueRenderer.class);

		return new AggregateResourceBundle(
				moduleResourceBundle, portalResourceBundle);
	}

	public static final HashMap<String, String> PERSIAN_ARABIC_TO_LATIN;

	static {
		PERSIAN_ARABIC_TO_LATIN = new HashMap<>();

		PERSIAN_ARABIC_TO_LATIN.put("__", "__");
		PERSIAN_ARABIC_TO_LATIN.put("\u0627\u0644\u0641", "A");
		PERSIAN_ARABIC_TO_LATIN.put("\u0628", "B");
		PERSIAN_ARABIC_TO_LATIN.put("\u067e", "P");
		PERSIAN_ARABIC_TO_LATIN.put("\u062a", "T");
		PERSIAN_ARABIC_TO_LATIN.put("\u062b", "Th");
		PERSIAN_ARABIC_TO_LATIN.put("\u062c", "J");
		PERSIAN_ARABIC_TO_LATIN.put("\u0686", "Ch");
		PERSIAN_ARABIC_TO_LATIN.put("\u062d", "H");
		PERSIAN_ARABIC_TO_LATIN.put("\u062e", "Kh");
		PERSIAN_ARABIC_TO_LATIN.put("\u062f", "D");
		PERSIAN_ARABIC_TO_LATIN.put("\u0630", "Z");
		PERSIAN_ARABIC_TO_LATIN.put("\u0631", "R");
		PERSIAN_ARABIC_TO_LATIN.put("\u0632", "Z");
		PERSIAN_ARABIC_TO_LATIN.put("\u0698", "Zh");
		PERSIAN_ARABIC_TO_LATIN.put("\u0633", "S");
		PERSIAN_ARABIC_TO_LATIN.put("\u0634", "Sh");
		PERSIAN_ARABIC_TO_LATIN.put("\u0635", "S");
		PERSIAN_ARABIC_TO_LATIN.put("\u0636", "Z");
		PERSIAN_ARABIC_TO_LATIN.put("\u0637", "T");
		PERSIAN_ARABIC_TO_LATIN.put("\u0638", "Z");
		PERSIAN_ARABIC_TO_LATIN.put("\u0639", "A");
		PERSIAN_ARABIC_TO_LATIN.put("\u063a", "GH");
		PERSIAN_ARABIC_TO_LATIN.put("\u0641", "F");
		PERSIAN_ARABIC_TO_LATIN.put("\u0642", "Q");
		PERSIAN_ARABIC_TO_LATIN.put("\u06a9", "K");
		PERSIAN_ARABIC_TO_LATIN.put("\u06af", "G");
		PERSIAN_ARABIC_TO_LATIN.put("\u0644", "L");
		PERSIAN_ARABIC_TO_LATIN.put("\u0645", "M");
		PERSIAN_ARABIC_TO_LATIN.put("\u0646", "N");
		PERSIAN_ARABIC_TO_LATIN.put("\u0648", "V");
		PERSIAN_ARABIC_TO_LATIN.put("\u0647", "H");
		PERSIAN_ARABIC_TO_LATIN.put("\u06cc", "Y");
	}

	@Reference
	protected Portal portal;
}