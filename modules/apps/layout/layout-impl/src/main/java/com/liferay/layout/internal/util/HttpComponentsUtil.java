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

package com.liferay.layout.internal.util;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tina Tian
 */
public class HttpComponentsUtil {

	public static String addParameter(String url, String name, String value) {
		if (url == null) {
			return null;
		}

		String[] urlArray = PortalUtil.stripURLAnchor(url, StringPool.POUND);

		url = urlArray[0];

		String anchor = urlArray[1];

		StringBundler sb = new StringBundler(6);

		sb.append(url);

		if (url.indexOf(CharPool.QUESTION) == -1) {
			sb.append(StringPool.QUESTION);
		}
		else if (!url.endsWith(StringPool.QUESTION) &&
				 !url.endsWith(StringPool.AMPERSAND)) {

			sb.append(StringPool.AMPERSAND);
		}

		sb.append(name);
		sb.append(StringPool.EQUAL);
		sb.append(URLCodec.encodeURL(value));
		sb.append(anchor);

		return shortenURL(sb.toString());
	}

	public static String shortenURL(String url) {
		if (url.length() <= Http.URL_MAXIMUM_LENGTH) {
			return url;
		}

		return _shortenURL(
			url, 0, StringPool.QUESTION, StringPool.AMPERSAND,
			StringPool.EQUAL);
	}

	private static String _shortenURL(
		String encodedURL, int currentLength, String encodedQuestion,
		String encodedAmpersand, String encodedEqual) {

		if ((currentLength + encodedURL.length()) <= Http.URL_MAXIMUM_LENGTH) {
			return encodedURL;
		}

		int index = encodedURL.indexOf(encodedQuestion);

		if (index == -1) {
			return encodedURL;
		}

		StringBundler sb = new StringBundler();

		sb.append(encodedURL.substring(0, index));
		sb.append(encodedQuestion);

		String queryString = encodedURL.substring(
			index + encodedQuestion.length());

		String[] params = StringUtil.split(queryString, encodedAmpersand);

		params = ArrayUtil.unique(params);

		List<String> encodedRedirectParams = new ArrayList<>();

		for (String param : params) {
			if (param.contains("_backURL" + encodedEqual) ||
				param.contains("_redirect" + encodedEqual) ||
				param.contains("_returnToFullPageURL" + encodedEqual) ||
				(param.startsWith("redirect") &&
				 (param.indexOf(encodedEqual) != -1))) {

				encodedRedirectParams.add(param);
			}
			else {
				sb.append(param);
				sb.append(encodedAmpersand);
			}
		}

		if ((currentLength + sb.length()) > Http.URL_MAXIMUM_LENGTH) {
			sb.setIndex(sb.index() - 1);

			return sb.toString();
		}

		for (String encodedRedirectParam : encodedRedirectParams) {
			int pos = encodedRedirectParam.indexOf(encodedEqual);

			String key = encodedRedirectParam.substring(0, pos);

			String redirect = encodedRedirectParam.substring(
				pos + encodedEqual.length());

			sb.append(key);
			sb.append(encodedEqual);

			int newLength = sb.length();

			redirect = _shortenURL(
				redirect, currentLength + newLength,
				URLCodec.encodeURL(encodedQuestion),
				URLCodec.encodeURL(encodedAmpersand),
				URLCodec.encodeURL(encodedEqual));

			newLength += redirect.length();

			if ((currentLength + newLength) > Http.URL_MAXIMUM_LENGTH) {
				sb.setIndex(sb.index() - 2);
			}
			else {
				sb.append(redirect);
				sb.append(encodedAmpersand);
			}
		}

		sb.setIndex(sb.index() - 1);

		return sb.toString();
	}

}