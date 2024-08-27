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

package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener;

import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.lock.model.Lock;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(service = ModelListener.class)
public class LockModelListener extends BaseModelListener<Lock> {

	@Override
	public void onAfterCreate(Lock lock) throws ModelListenerException {
		audit(EventTypes.ADD, lock);
	}

	@Override
	public void onAfterUpdate(Lock lock) throws ModelListenerException {
		audit(EventTypes.UPDATE, lock);
	}

	@Override
	public void onBeforeRemove(Lock lock) throws ModelListenerException {
		audit(EventTypes.DELETE, lock);
	}

	protected void audit(String eventType, Lock lock)
		throws ModelListenerException {

		try {
			long lockId = lock.getLockId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, Lock.class.getName(), lockId, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"isLockExpired", lock.isExpired()
			).put(
				"isLockInheritable", lock.isInheritable()
			).put(
				"lockClassKey", lock.getKey()
			).put(
				"lockClassName", lock.getClassName()
			).put(
				"lockExpirationDate", lock.getExpirationDate()
			).put(
				"lockId", lockId
			);

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(LockModelListener.class);

	@Reference
	private AuditRouter _auditRouter;

}