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

package com.liferay.dynamic.data.mapping.internal.util;

import com.liferay.dynamic.data.mapping.model.DDMTrackingCode;
import com.liferay.dynamic.data.mapping.service.DDMTrackingCodeLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Mohammad Mehdi Tamehri
 * @review
 */
@Component(service = DDMTrackingCodeCreator.class)
public class DDMTrackingCodeCreator {

	public String create() throws PortalException {
		String trackingCode;
		DDMTrackingCode ddmTrackingCode;

		do {
			trackingCode = StringUtil.randomString(10);

			ddmTrackingCode =
				DDMTrackingCodeLocalServiceUtil.getDDMTrackingCode(
					trackingCode);
		}
		while (!Objects.equals(ddmTrackingCode, null));

		return trackingCode;
	}

}