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

import com.liferay.portal.kernel.util.StringBundler;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Yousef Ghadiri
 */
public class LicensePlateDDMFormFieldUtil {

	public static final HashMap<String, String> persianToLatin =
		new HashMap<String, String>() {
			{
				put("\u06a9", "K");
				put("\u06af", "G");
				put("\u06cc", "Y");
				put("\u062a", "T");
				put("\u062b", "Th");
				put("\u062c", "J");
				put("\u062d", "H");
				put("\u062e", "Kh");
				put("\u062f", "D");
				put("\u063a", "GH");
				put("\u067e", "P");
				put("\u0627\u0644\u0641", "A");
				put("\u0628", "B");
				put("\u0630", "Z");
				put("\u0631", "R");
				put("\u0632", "Z");
				put("\u0633", "S");
				put("\u0634", "Sh");
				put("\u0635", "S");
				put("\u0636", "Z");
				put("\u0637", "T");
				put("\u0638", "Z");
				put("\u0639", "A");
				put("\u0641", "F");
				put("\u0642", "Q");
				put("\u0644", "L");
				put("\u0645", "M");
				put("\u0646", "N");
				put("\u0647", "H");
				put("\u0648", "V");
				put("\u0686", "Ch");
				put("\u0698", "Zh");
				put("__", "__");
			}
		};

	public static Boolean advancePlateValidator(String value) {
		Matcher matcher = _advancesLicensePlatePettern.matcher(value);

		return matcher.matches();
	}

	public static Matcher matchLicensePlate(String value) {
		return _simpleLicensePlatePattern.matcher(value);
	}

	public static Boolean simplePlateValidator(String value) {
		Matcher matcher = _simpleLicensePlatePattern.matcher(value);

		return matcher.matches();
	}

	private static final Pattern _advancesLicensePlatePettern = Pattern.compile(
		StringBundler.concat(
			"^-(?!00)(\\d{2})-",
			"(\u0627\u0644\u0641|\u0628|\u067e|\u062a|\u062b|\u062c|\u0686|",
			"\u062d|\u062e|\u062f|\u0630|\u0631|\u0632|\u0698|\u0633|",
			"\u0634|\u0635|\u0636|\u0637|\u0638|\u0639|\u063a|\u0641|",
			"\u0642|\u06a9|\u06af|\u0644|\u0645|\u0646|\u0648|\u0647|",
			"\u06cc)-(?!000)(\\d{3})-(?!00)(\\d{2})-$"));
	private static final Pattern _simpleLicensePlatePattern = Pattern.compile(
		"^-(\\d{2})-(.{1,3})-(\\d{3})-(\\d{2})-$");

}