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

package com.liferay.dynamic.data.mapping.form.field.type.internal.date;

import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;

import com.liferay.dynamic.data.mapping.form.field.type.DDMFormFieldValueRenderer;
import com.liferay.dynamic.data.mapping.model.Value;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.text.ParseException;

import java.util.Date;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;

/**
 * @author Bruno Basto
 */
@Component(
	immediate = true, property = "ddm.form.field.type.name=date",
	service = {
		DateDDMFormFieldValueRenderer.class, DDMFormFieldValueRenderer.class
	}
)
public class DateDDMFormFieldValueRenderer
	implements DDMFormFieldValueRenderer {

	@Override
	public String render(DDMFormFieldValue ddmFormFieldValue, Locale locale) {
		Value value = ddmFormFieldValue.getValue();

		return render(value.getString(locale), locale);
	}

	protected String render(String valueString, Locale locale) {
		if (Validator.isNotNull(valueString)) {
			try {
				String languageId = LanguageUtil.getLanguageId(locale);

				if (languageId.equals("fa_IR")) {
					SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy-MM-dd", LocaleUtil.getDefault());

					NumberFormat numberFormat = NumberFormat.getNumberInstance(
						LocaleUtil.ENGLISH);

					formatter.setNumberFormat(numberFormat);

					Date value = DateUtil.parseDate(
						"yyyy-MM-dd", valueString, locale);

					ULocale uLocale = ULocale.forLocale(
						LocaleUtil.fromLanguageId("fa_IR"));

					Calendar calendar = Calendar.getInstance(uLocale);

					calendar.setTime(value);

					return formatter.format(calendar);
				}

				return DateUtil.formatDate("yyyy-MM-dd", valueString, locale);
			}
			catch (ParseException parseException) {
				_log.error("Unable to parse date", parseException);
			}
		}

		return StringPool.BLANK;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DateDDMFormFieldValueRenderer.class);

}