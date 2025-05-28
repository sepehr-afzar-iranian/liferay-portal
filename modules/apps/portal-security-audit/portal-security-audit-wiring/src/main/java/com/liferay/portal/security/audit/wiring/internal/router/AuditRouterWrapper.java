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

package com.liferay.portal.security.audit.wiring.internal.router;

import com.liferay.portal.kernel.audit.AuditException;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(
	immediate = true, property = "service.ranking:Integer=100",
	service = AuditRouter.class
)
public class AuditRouterWrapper implements AuditRouter {

	@Override
	public boolean isDeployed() {
		return false;
	}

	@Override
	public void route(AuditMessage auditMessage) throws AuditException {
		try {
			if (_shouldCheckForMerge(auditMessage)) {
				String messageKey = _generateMessageKey(auditMessage);

				synchronized (_recentMessages) {
					RecentMessage recentMessage = _recentMessages.get(
						messageKey);

					if ((recentMessage != null) &&
						_shouldMerge(auditMessage, recentMessage)) {

						if (_log.isDebugEnabled()) {
							_log.debug("Merged audit message: " + messageKey);
						}

						return;
					}

					_recentMessages.put(
						messageKey, new RecentMessage(auditMessage));
					_cleanupOldMessages();
				}
			}

			_originalAuditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Error in audit message merging, routing original message",
					exception);
			}

			_originalAuditRouter.route(auditMessage);
		}
	}

	private boolean _areMessagesSimilar(AuditMessage msg1, AuditMessage msg2) {
		if (Objects.equals(msg1.getMessage(), msg2.getMessage()) &&
			Objects.equals(msg1.getClassName(), msg2.getClassName()) &&
			Objects.equals(msg1.getClassPK(), msg2.getClassPK()) &&
			Objects.equals(msg1.getUserId(), msg2.getUserId()) &&
			Objects.equals(msg1.getEventType(), msg2.getEventType())) {

			return true;
		}

		return false;
	}

	private void _cleanupOldMessages() {
		if (_recentMessages.size() > _MAX_CACHED_MESSAGES) {
			long cutoffTime = System.currentTimeMillis() - _MERGE_WINDOW_MS;
			Set<Map.Entry<String, RecentMessage>> entrySet =
				_recentMessages.entrySet();

			entrySet.removeIf(
				entry -> {
					RecentMessage recentMessage = entry.getValue();

					return recentMessage.getLastUpdateTime() < cutoffTime;
				});
		}
	}

	private String _generateMessageKey(AuditMessage auditMessage) {
		StringBuilder sb = new StringBuilder();

		sb.append(auditMessage.getClassName());
		sb.append("|");
		sb.append(auditMessage.getClassPK());
		sb.append("|");
		sb.append(auditMessage.getUserId());
		sb.append("|");
		sb.append(auditMessage.getEventType());

		return sb.toString();
	}

	private boolean _shouldCheckForMerge(AuditMessage auditMessage) {
		String eventType = auditMessage.getEventType();
		String className = auditMessage.getClassName();

		if ((eventType != null) && eventType.equals("UPDATE") &&
			_mergableClasses.contains(className)) {

			return true;
		}

		return false;
	}

	private boolean _shouldMerge(
		AuditMessage newMessage, RecentMessage recentMessage) {

		Date newMessageTimestamp = newMessage.getTimestamp();

		long timeDiff =
			newMessageTimestamp.getTime() - recentMessage.getLastUpdateTime();

		if ((timeDiff <= _MERGE_WINDOW_MS) &&
			_areMessagesSimilar(newMessage, recentMessage.getMessage())) {

			return true;
		}

		return false;
	}

	private static final int _MAX_CACHED_MESSAGES = 1000;

	private static final long _MERGE_WINDOW_MS = 2 * 1000;

	private static final Log _log = LogFactoryUtil.getLog(
		AuditRouterWrapper.class);

	private static final Set<String> _mergableClasses = new HashSet<String>() {
		{
			add("com.liferay.portal.kernel.model.Layout");
		}
	};

	@Reference(
		target = "(!(component.name=com.liferay.portal.security.audit.wiring.internal.router.AuditRouterWrapper))"
	)
	private AuditRouter _originalAuditRouter;

	private final Map<String, RecentMessage> _recentMessages =
		new ConcurrentHashMap<>();

	private static class RecentMessage {

		public RecentMessage(AuditMessage message) {
			_message = message;

			_lastUpdateTime = System.currentTimeMillis();
		}

		public long getLastUpdateTime() {
			return _lastUpdateTime;
		}

		public AuditMessage getMessage() {
			return _message;
		}

		private final long _lastUpdateTime;
		private final AuditMessage _message;

	}

}