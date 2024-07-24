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

import com.liferay.mobile.device.rules.model.MDRRuleGroup;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(immediate = true, service = ModelListener.class)
public class MDRRuleGroupModelListener extends BaseModelListener<MDRRuleGroup> {

	@Override
	public void onAfterCreate(MDRRuleGroup mdrRuleGroup)
		throws ModelListenerException {

		audit(EventTypes.ADD, mdrRuleGroup);
	}

	@Override
	public void onAfterRemove(MDRRuleGroup mdrRuleGroup)
		throws ModelListenerException {

		audit(EventTypes.DELETE, mdrRuleGroup);
	}

	@Override
	public void onAfterUpdate(MDRRuleGroup mdrRuleGroup)
		throws ModelListenerException {

		audit(EventTypes.UPDATE, mdrRuleGroup);
	}

	protected void audit(String eventType, MDRRuleGroup mdrRuleGroup)
		throws ModelListenerException {

		try {
			long mdrRuleGroupId = mdrRuleGroup.getRuleGroupId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, MDRRuleGroup.class.getName(), mdrRuleGroupId, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"mdrRuleGroupId", mdrRuleGroupId
			).put(
				"mdrRuleGroupName", mdrRuleGroup.getName()
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