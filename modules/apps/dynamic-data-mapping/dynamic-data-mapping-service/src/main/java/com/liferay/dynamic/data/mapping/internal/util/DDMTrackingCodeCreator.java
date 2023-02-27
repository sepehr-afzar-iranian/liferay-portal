/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dynamic.data.mapping.internal.util;

import com.liferay.dynamic.data.mapping.service.DDMTrackingCodeLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.StringUtil;
import org.osgi.service.component.annotations.Component;

/**
 * @author Mohammad Mehdi Tamehri
 * @review
 */
@Component(service = DDMTrackingCodeCreator.class)
public class DDMTrackingCodeCreator {
	public String create() throws PortalException {
		String trackingCode;
		do {
				trackingCode = StringUtil.randomString(10);
			}
			while (DDMTrackingCodeLocalServiceUtil.getDDMTrackingCode(trackingCode)!=null);
		return trackingCode;
	}
}