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

import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
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
public class AssetListEntryModelListener
	extends BaseModelListener<AssetListEntry> {

	@Override
	public void onAfterCreate(AssetListEntry assetListEntry)
		throws ModelListenerException {

		audit(EventTypes.ADD, assetListEntry);
	}

	@Override
	public void onAfterRemove(AssetListEntry assetListEntry) {
		audit(EventTypes.DELETE, assetListEntry);
	}

	@Override
	public void onAfterUpdate(AssetListEntry assetListEntry)
		throws ModelListenerException {

		audit(EventTypes.UPDATE, assetListEntry);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_auditConfiguration = ConfigurableUtil.createConfigurable(
			AuditConfiguration.class, properties);
	}

	protected void audit(String eventType, AssetListEntry assetListEntry)
		throws ModelListenerException {

		if (!_auditConfiguration.enabled()) {
			return;
		}

		try {
			long assetListEntryId = assetListEntry.getAssetListEntryId();

			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, AssetListEntry.class.getName(), assetListEntryId,
				null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"assetListEntryId", assetListEntryId
			).put(
				"assetListEntryTitle", assetListEntry.getTitle()
			).put(
				"typeLabel", assetListEntry.getTypeLabel()
			);

			auditMessage.setMessage(
				AuditMessageHelperUtil.getMessage(
					eventType, auditMessage.getClassName(),
					assetListEntry.getTitle(), assetListEntryId));

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to route audit message", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AssetListEntryModelListener.class);

	private volatile AuditConfiguration _auditConfiguration;

	@Reference
	private AuditRouter _auditRouter;

}