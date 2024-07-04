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

package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener;

import com.liferay.dynamic.data.mapping.model.DDMFormInstanceRecord;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;

import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(immediate = true, service = ModelListener.class)
public class DDMFormInstanceRecordModelListener
	extends BaseModelListener<DDMFormInstanceRecord> {

	@Override
	public void onAfterCreate(DDMFormInstanceRecord ddmFormInstanceRecord) {
		audit(EventTypes.ADD, ddmFormInstanceRecord);
	}

	@Override
	public void onAfterRemove(DDMFormInstanceRecord ddmFormInstanceRecord) {
		audit(EventTypes.DELETE, ddmFormInstanceRecord);
	}

	@Override
	public void onAfterUpdate(DDMFormInstanceRecord ddmFormInstanceRecord) {
		audit(EventTypes.UPDATE, ddmFormInstanceRecord);
	}

	protected void audit(
		String eventType, DDMFormInstanceRecord ddmFormInstanceRecord)
		throws ModelListenerException {

		try {
			long formInstanceRecordId =
				ddmFormInstanceRecord.getFormInstanceRecordId();
			AuditMessage auditMessage =
				AuditMessageBuilder.buildAuditMessage(eventType,
					DDMFormInstanceRecord.class.getName(), formInstanceRecordId,
					null);
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"groupId", serviceContext.getScopeGroupId()
			).put(
				"formInstanceRecordId", formInstanceRecordId
			).put(
				"formInstanceId", ddmFormInstanceRecord.getFormInstanceId()
			).put(
				"formInstanceName", ddmFormInstanceRecord.getFormInstance().getName()
			);

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private AuditRouter _auditRouter;

}