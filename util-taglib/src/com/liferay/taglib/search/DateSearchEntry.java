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

package com.liferay.taglib.search;

import com.liferay.ibm.icu.text.SimpleDateFormat;
import com.liferay.ibm.icu.util.Calendar;
import com.liferay.ibm.icu.util.ULocale;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Julio Camarero
 */
public class DateSearchEntry extends TextSearchEntry {

	public Date getDate() {
		return _date;
	}

	@Override
	public String getName(HttpServletRequest httpServletRequest) {
		if (_date != null) {
			Object[] localeAndTimeZone = getLocaleAndTimeZone(
				httpServletRequest);

			Locale locale = (Locale)localeAndTimeZone[0];

			SimpleDateFormat formatter = new SimpleDateFormat(
				_DATE_FORMAT_PATTERN, locale);

			ULocale uLocale = ULocale.forLocale(locale);

			TimeZone javaTimeZone = (TimeZone)localeAndTimeZone[1];

			com.liferay.ibm.icu.util.TimeZone timeZone =
				com.liferay.ibm.icu.util.TimeZone.getTimeZone(
					javaTimeZone.getID());

			Calendar calendar = Calendar.getInstance(uLocale);

			calendar.setTime(_date);
			calendar.setTimeZone(timeZone);

			StringBundler sb = new StringBundler(5);

			sb.append("<span class=\"lfr-portal-tooltip\" title=\"");
			sb.append(formatter.format(calendar));
			sb.append("\">");

			sb.append(
				LanguageUtil.format(
					locale, _getMessageKey(),
					new Object[] {
						LanguageUtil.getTimeDescription(
							locale, _getTimeDelta(), true),
						HtmlUtil.escape(_userName)
					},
					false));

			sb.append("</span>");

			return sb.toString();
		}

		return StringPool.BLANK;
	}

	public void setDate(Date date) {
		_date = date;
	}

	public void setUserName(String userName) {
		_userName = userName;
	}

	protected Object[] getLocaleAndTimeZone(
		HttpServletRequest httpServletRequest) {

		if ((_locale != null) && (_timeZone != null)) {
			return new Object[] {_locale, _timeZone};
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		_locale = themeDisplay.getLocale();
		_timeZone = themeDisplay.getTimeZone();

		return new Object[] {_locale, _timeZone};
	}

	private String _getMessageKey() {
		if (_date.before(new Date())) {
			if (_userName == null) {
				return "x-ago";
			}

			return "x-ago-by-x";
		}

		if (_userName == null) {
			return "within-x";
		}

		return "within-x-by-x";
	}

	private long _getTimeDelta() {
		if (_date.before(new Date())) {
			return System.currentTimeMillis() - _date.getTime();
		}

		return _date.getTime() - System.currentTimeMillis();
	}

	private static final String _DATE_FORMAT_PATTERN = "dd MMMM yyyy HH:mm";

	private Date _date;
	private Locale _locale;
	private TimeZone _timeZone;
	private String _userName;

}