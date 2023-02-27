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

package com.liferay.dynamic.data.mapping.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link DDMTrackingCodeService}.
 *
 * @author Brian Wing Shun Chan
 * @see DDMTrackingCodeService
 * @generated
 */
public class DDMTrackingCodeServiceWrapper
	implements DDMTrackingCodeService, ServiceWrapper<DDMTrackingCodeService> {

	public DDMTrackingCodeServiceWrapper(
		DDMTrackingCodeService ddmTrackingCodeService) {

		_ddmTrackingCodeService = ddmTrackingCodeService;
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ddmTrackingCodeService.getOSGiServiceIdentifier();
	}

	@Override
	public DDMTrackingCodeService getWrappedService() {
		return _ddmTrackingCodeService;
	}

	@Override
	public void setWrappedService(
		DDMTrackingCodeService ddmTrackingCodeService) {

		_ddmTrackingCodeService = ddmTrackingCodeService;
	}

	private DDMTrackingCodeService _ddmTrackingCodeService;

}