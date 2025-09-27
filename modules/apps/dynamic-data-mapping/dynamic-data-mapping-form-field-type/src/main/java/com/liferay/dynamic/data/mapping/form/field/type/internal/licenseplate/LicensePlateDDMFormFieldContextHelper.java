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

import java.util.regex.Pattern;

/**
 * @author Yousef Ghadiri
 */
public class LicensePlateDDMFormFieldContextHelper {
	private static final Pattern ADVANCE_IRANIAN_LICENSE_PLATE_PATTERN =
			Pattern.compile("^-(?!00)(\\d{2})-(\u0627\u0644\u0641|\u0628|\u067e|\u062a|\u062b|\u062c|\u0686|\u062d|\u062e|\u062f|\u0630|\u0631|\u0632|\u0698|\u0633|\u0634|\u0635|\u0636|\u0637|\u0638|\u0639|\u063a|\u0641|\u0642|\u06a9|\u06af|\u0644|\u0645|\u0646|\u0648|\u0647|\u06cc)-(?!000)(\\d{3})-(?!00)(\\d{2})-$");

	private static final Pattern SIMPLE_IRANIAN_LICENSE_PLATE_PATTERN =
			Pattern.compile("^-(\\d{2})-(.{1,3})-(\\d{3})-(\\d{2})-$");

	public Boolean advancePlateValidator(String value) {
		return ADVANCE_IRANIAN_LICENSE_PLATE_PATTERN.matcher(value).matches();
	}
	public Boolean simplePlateValidator(String value) {
		return SIMPLE_IRANIAN_LICENSE_PLATE_PATTERN.matcher(value).matches();
	}
	public java.util.regex.Matcher matchLicensePlate(String value) {
		return SIMPLE_IRANIAN_LICENSE_PLATE_PATTERN.matcher(value);
	}
}