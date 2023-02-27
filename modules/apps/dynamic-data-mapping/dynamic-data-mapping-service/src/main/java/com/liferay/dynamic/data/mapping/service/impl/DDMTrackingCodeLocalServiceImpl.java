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

package com.liferay.dynamic.data.mapping.service.impl;

import com.liferay.dynamic.data.mapping.model.DDMTrackingCode;
import com.liferay.dynamic.data.mapping.service.base.DDMTrackingCodeLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;

import com.liferay.portal.kernel.exception.PortalException;
import org.osgi.service.component.annotations.Component;

/**
 * The implementation of the ddm tracking code local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the <code>com.liferay.dynamic.data.mapping.service.DDMTrackingCodeLocalService</code> interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DDMTrackingCodeLocalServiceBaseImpl
 */
@Component(
	property = "model.class.name=com.liferay.dynamic.data.mapping.model.DDMTrackingCode",
	service = AopService.class
)
public class DDMTrackingCodeLocalServiceImpl
	extends DDMTrackingCodeLocalServiceBaseImpl {
	public DDMTrackingCode addDDMTrackingCode(
		long formInstanceRecordId, String trackingCode)
		throws PortalException {

		DDMTrackingCode ddmTrackingCode =
			ddmTrackingCodePersistence.create(formInstanceRecordId);
		ddmTrackingCode.setTrackingCode(trackingCode);

		return ddmTrackingCodePersistence.update(ddmTrackingCode);
	}
	public String getTrackingCode(long formInstanceRecordId)
		throws PortalException {
		return getTrackingCodeByFormInstanceRecordId(formInstanceRecordId);
	}
	private String getTrackingCodeByFormInstanceRecordId(long formInstanceRecordId)
		throws PortalException {
		return ddmTrackingCodePersistence.findByPrimaryKey(formInstanceRecordId).getTrackingCode();
	}

	public DDMTrackingCode getDDMTrackingCode(String trackingCode)
		throws PortalException {
		return getDDMTrackingCodeByTrackingCode(trackingCode);
	}

	private DDMTrackingCode getDDMTrackingCodeByTrackingCode(String trackingCode)
		throws PortalException {
		return ddmTrackingCodePersistence.fetchByTrackingCode(trackingCode);
	}
}