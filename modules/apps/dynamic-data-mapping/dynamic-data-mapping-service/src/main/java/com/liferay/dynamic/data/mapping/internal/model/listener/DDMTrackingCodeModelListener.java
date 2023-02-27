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


package com.liferay.dynamic.data.mapping.internal.model.listener;

import com.liferay.dynamic.data.mapping.internal.util.DDMTrackingCodeCreator;
import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.dynamic.data.mapping.service.DDMTrackingCodeLocalService;
import com.liferay.dynamic.data.mapping.service.DDMTrackingCodeLocalServiceUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


/**
 * @author Marcos Martins
 */

@Component(immediate = true, service = ModelListener.class)
public class DDMTrackingCodeModelListener
	extends BaseModelListener<DDMFormInstanceRecord> {

	@Override
	public void onAfterCreate(
		DDMFormInstanceRecord ddmFormInstanceRecord)
		throws ModelListenerException {

		try {
			String trackingCode = _ddmTrackingCodeCreator.create();
			_ddmTrackingCodeLocalService.addDDMTrackingCode(
				ddmFormInstanceRecord.getFormInstanceRecordId(),
				trackingCode);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to update dynamic data mapping form instance ",
						"report for dynamic data mapping tracking code ",
						ddmFormInstanceRecord.getFormInstanceRecordId()),
					exception);
			}
		}
	}

	@Override
	public void onAfterRemove(DDMFormInstanceRecord ddmFormInstanceRecord)
		throws ModelListenerException {

		try {
			_ddmTrackingCodeLocalService.deleteDDMTrackingCode(
				ddmFormInstanceRecord.getFormInstanceRecordId());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Unable to update dynamic data mapping form instance ",
						"report for dynamic data mapping tracking code ",
						ddmFormInstanceRecord.getFormInstanceRecordId()),
					exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMTrackingCodeModelListener.class);

	@Reference
	private DDMTrackingCodeLocalService
		_ddmTrackingCodeLocalService;

	@Reference
	private DDMTrackingCodeCreator
		_ddmTrackingCodeCreator;

}
