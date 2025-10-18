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
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.security.audit.configuration.AuditConfiguration;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.user.management.util.AuditMessageHelperUtil;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(
	configurationPid = "com.liferay.portal.security.audit.configuration.AuditConfiguration",
	immediate = true, service = ModelListener.class
)
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

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_auditConfiguration = ConfigurableUtil.createConfigurable(
			AuditConfiguration.class, properties);
	}

	protected void audit(String eventType, MDRRuleGroup mdrRuleGroup)
		throws ModelListenerException {

		if (!_auditConfiguration.enabled()) {
			return;
		}

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

			String defaultLanguageId = LocaleUtil.toLanguageId(
				LocaleUtil.getSiteDefault());

			auditMessage.setMessage(
				AuditMessageHelperUtil.getMessage(
					eventType, auditMessage.getClassName(),
					mdrRuleGroup.getName(defaultLanguageId), mdrRuleGroupId));

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MDRRuleGroupModelListener.class);

	private volatile AuditConfiguration _auditConfiguration;

	@Reference
	private AuditRouter _auditRouter;

}