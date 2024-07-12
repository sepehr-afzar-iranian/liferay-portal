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
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;
import com.liferay.subscription.model.Subscription;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(immediate = true, service = ModelListener.class)
public class SubscriptionModelListener extends BaseModelListener<Subscription> {

	@Override
	public void onAfterCreate(Subscription subscription) throws ModelListenerException {
		audit(EventTypes.ADD, subscription);
	}

	@Override
	public void onAfterRemove(Subscription subscription) throws ModelListenerException {
		audit(EventTypes.DELETE, subscription);
	}

	@Override
	public void onAfterUpdate(Subscription subscription) throws ModelListenerException {
		audit(EventTypes.UPDATE, subscription);
	}

	protected void audit(String eventType, Subscription subscription)
		throws ModelListenerException {

		try {
			long subscriptionId = subscription.getSubscriptionId();
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, Subscription.class.getName(), subscriptionId, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();
			additionalInfoJSONObject.put(
				"subscriptionId", subscriptionId
			).put(
				"userId", subscription.getUserId()
			).put(
				"userName", subscription.getUserName()
			).put(
				"subscriptionClassName", subscription.getClassName()
			).put(
				"subscriptionClassPK", subscription.getClassPK()
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