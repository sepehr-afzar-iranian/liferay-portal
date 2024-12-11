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

package com.liferay.portal.security.audit.router.internal;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.scheduler.SchedulerEntryImpl;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;
import com.liferay.portal.security.audit.router.configuration.AuditMessageAutoDeleterConfiguration;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;

import java.sql.Timestamp;

import java.util.Date;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(
	configurationPid = "com.liferay.portal.security.audit.router.configuration.AuditMessageAutoDeleterConfiguration",
	immediate = true, service = DeleteAuditMessagesListener.class
)
public class DeleteAuditMessagesListener extends BaseMessageListener {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_auditMessageAutoDeleterConfiguration =
			ConfigurableUtil.createConfigurable(
				AuditMessageAutoDeleterConfiguration.class, properties);

		if (!_auditMessageAutoDeleterConfiguration.enabled()) {
			return;
		}

		String cronExpression =
			_auditMessageAutoDeleterConfiguration.cronExpression();
		Class<?> clazz = getClass();

		Trigger trigger = _triggerFactory.createTrigger(
			clazz.getName(), clazz.getName(), new Date(), null, cronExpression);

		_schedulerEntryImpl = new SchedulerEntryImpl(clazz.getName(), trigger);

		_schedulerEngineHelper.register(
			this, _schedulerEntryImpl, DestinationNames.SCHEDULER_DISPATCH);
	}

	@Deactivate
	protected void deactivate() {
		_schedulerEngineHelper.unregister(this);
	}

	@Override
	protected void doReceive(Message message) throws Exception {
		if (!_auditMessageAutoDeleterConfiguration.enabled()) {
			return;
		}

		int month = _auditMessageAutoDeleterConfiguration.month();

		Timestamp thresholdTimestamp = new Timestamp(
			System.currentTimeMillis() -
				(month * 30L * 24L * 60L * 60L * 1000L));

		try {
			ActionableDynamicQuery actionableDynamicQuery =
				_auditEventLocalService.getActionableDynamicQuery();

			actionableDynamicQuery.setAddCriteriaMethod(
				dynamicQuery -> dynamicQuery.add(
					RestrictionsFactoryUtil.lt(
						"createDate", thresholdTimestamp)));
			actionableDynamicQuery.setPerformActionMethod(
				(AuditEvent auditEvent) ->
					_auditEventLocalService.deleteAuditEvent(
						auditEvent.getAuditEventId()));
			actionableDynamicQuery.performActions();

			try {
				AuditMessage auditMessage =
					AuditMessageBuilder.buildAuditMessage(
						EventTypes.AUADIT_AUTO_DELETE, User.class.getName(), 0,
						null);

				_auditRouter.route(auditMessage);
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to route audit message", exception);
				}
			}
		}
		catch (Exception exception) {
			_log.error("Error deleting old audit events", exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeleteAuditMessagesListener.class);

	@Reference
	private AuditEventLocalService _auditEventLocalService;

	private volatile AuditMessageAutoDeleterConfiguration
		_auditMessageAutoDeleterConfiguration;

	@Reference
	private AuditRouter _auditRouter;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private SchedulerEngineHelper _schedulerEngineHelper;

	private SchedulerEntryImpl _schedulerEntryImpl;

	@Reference
	private TriggerFactory _triggerFactory;

}