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
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
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

				synchronized (_pendingMessages) {
					PendingMessage pendingMessage = _pendingMessages.get(
						messageKey);

					if ((pendingMessage != null) &&
						_areMessagesSimilar(
							auditMessage, pendingMessage.getLatestMessage())) {

						pendingMessage.updateMessage(auditMessage);

						return;
					}

					PendingMessage newPendingMessage = new PendingMessage(
						auditMessage);

					_pendingMessages.put(messageKey, newPendingMessage);

					ScheduledFuture<?> future =
						_scheduledExecutorService.schedule(
							() -> {
								try {
									synchronized (_pendingMessages) {
										PendingMessage msgToRoute =
											_pendingMessages.remove(messageKey);

										if (msgToRoute != null) {
											_originalAuditRouter.route(
												msgToRoute.getLatestMessage());
										}
									}
								}
								catch (Exception exception) {
									_log.error(
										"Error routing delayed audit: " +
											messageKey,
										exception);
								}
							},
							_MERGE_WINDOW_MS, TimeUnit.MILLISECONDS);

					newPendingMessage.setScheduledFuture(future);
				}

				_cleanupOldMessages();
			}
			else {
				_originalAuditRouter.route(auditMessage);
			}
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

	@Activate
	protected void activate() {
		_scheduledExecutorService = Executors.newScheduledThreadPool(2);
	}

	@Deactivate
	protected void deactivate() {
		if (_scheduledExecutorService != null) {
			_scheduledExecutorService.shutdown();

			try {
				if (!_scheduledExecutorService.awaitTermination(
						5, TimeUnit.SECONDS)) {

					_scheduledExecutorService.shutdownNow();
				}
			}
			catch (InterruptedException interruptedException) {
				_scheduledExecutorService.shutdownNow();

				Thread currentThread = Thread.currentThread();

				currentThread.interrupt();
			}
		}
	}

	private boolean _areMessagesSimilar(AuditMessage msg1, AuditMessage msg2) {
		if (Objects.equals(msg1.getClassName(), msg2.getClassName()) &&
			Objects.equals(msg1.getClassPK(), msg2.getClassPK()) &&
			Objects.equals(msg1.getUserId(), msg2.getUserId()) &&
			Objects.equals(msg1.getEventType(), msg2.getEventType())) {

			return true;
		}

		return false;
	}

	private void _cleanupOldMessages() {
		if (_pendingMessages.size() > _MAX_CACHED_MESSAGES) {
			long cutoffTime =
				System.currentTimeMillis() - (_MERGE_WINDOW_MS * 2);
			Set<Map.Entry<String, PendingMessage>> entrySet =
				_pendingMessages.entrySet();

			entrySet.removeIf(
				entry -> {
					PendingMessage pendingMessage = entry.getValue();

					if (pendingMessage.getCreationTime() < cutoffTime) {
						ScheduledFuture<?> future =
							pendingMessage.getScheduledFuture();

						if ((future != null) && !future.isDone()) {
							future.cancel(false);
						}

						return true;
					}

					return false;
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

	private static final int _MAX_CACHED_MESSAGES = 1000;

	private static final long _MERGE_WINDOW_MS = 6 * 1000;

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

	private final Map<String, PendingMessage> _pendingMessages =
		new ConcurrentHashMap<>();
	private ScheduledExecutorService _scheduledExecutorService;

	private static class PendingMessage {

		public PendingMessage(AuditMessage message) {
			_latestMessage = message;
			_creationTime = System.currentTimeMillis();
			_mergeCount = 1;
		}

		public long getCreationTime() {
			return _creationTime;
		}

		public AuditMessage getLatestMessage() {
			if (_mergeCount > 1) {
				JSONObject additionalInfoJSONObject =
					_latestMessage.getAdditionalInfo();

				additionalInfoJSONObject.put("mergeCount", _mergeCount);
			}

			return _latestMessage;
		}

		public ScheduledFuture<?> getScheduledFuture() {
			return _scheduledFuture;
		}

		public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
			_scheduledFuture = scheduledFuture;
		}

		public void updateMessage(AuditMessage newMessage) {
			_mergeCount++;
			_mergeAttributes(_latestMessage, newMessage);
			_latestMessage = newMessage;
		}

		private void _mergeAttributes(
			AuditMessage existingMessage, AuditMessage newMessage) {

			try {
				JSONObject existingAdditionalInfoJSONObject =
					existingMessage.getAdditionalInfo();
				JSONObject newAdditionalInfoJSONObject =
					newMessage.getAdditionalInfo();

				Object existingAttributesObject =
					existingAdditionalInfoJSONObject.get("attributes");
				Object newAttributesObject = newAdditionalInfoJSONObject.get(
					"attributes");

				if ((existingAttributesObject != null) &&
					(newAttributesObject != null)) {

					JSONArray existingAttributesJSONArray =
						(JSONArray)existingAttributesObject;
					JSONArray newAttributesJSONArray =
						(JSONArray)newAttributesObject;
					Map<String, JSONObject> existingAttributeMap =
						new HashMap<>();

					for (int i = 0; i < existingAttributesJSONArray.length();
						 i++) {

						JSONObject attrJSONObject =
							existingAttributesJSONArray.getJSONObject(i);

						String attributeName = attrJSONObject.getString("name");

						existingAttributeMap.put(attributeName, attrJSONObject);
					}

					for (int i = 0; i < newAttributesJSONArray.length(); i++) {
						JSONObject newAttrJSONObject =
							newAttributesJSONArray.getJSONObject(i);

						String attributeName = newAttrJSONObject.getString(
							"name");

						JSONObject existingAttrJSONObject =
							existingAttributeMap.get(attributeName);

						if (existingAttrJSONObject != null) {
							existingAttrJSONObject.put(
								"newValue", newAttrJSONObject.get("newValue"));
						}
						else {
							existingAttributesJSONArray.put(newAttrJSONObject);
						}
					}
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Error merging audit message attributes", exception);
				}
			}
		}

		private final long _creationTime;
		private AuditMessage _latestMessage;
		private int _mergeCount;
		private ScheduledFuture<?> _scheduledFuture;

	}

}