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
import com.liferay.site.navigation.model.SiteNavigationMenu;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */
@Component(service = ModelListener.class)
public class SiteNavigationMenuModelListener extends BaseModelListener<SiteNavigationMenu> {

	@Override
	public void onAfterCreate(SiteNavigationMenu siteNavigationMenu) throws ModelListenerException {
		audit(EventTypes.ADD, siteNavigationMenu);
	}

	@Override
	public void onAfterUpdate(SiteNavigationMenu siteNavigationMenu) throws ModelListenerException {
		audit(EventTypes.UPDATE, siteNavigationMenu);
	}

	@Override
	public void onBeforeRemove(SiteNavigationMenu siteNavigationMenu) throws ModelListenerException {
		audit(EventTypes.DELETE, siteNavigationMenu);
	}

	protected void audit(String eventType, SiteNavigationMenu siteNavigationMenu)
		throws ModelListenerException {

		try {
			long siteNavigationMenuId = siteNavigationMenu.getSiteNavigationMenuId();
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, SiteNavigationMenu.class.getName(), siteNavigationMenuId, null);

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();
			additionalInfoJSONObject.put(
				"siteNavigationMenuId",siteNavigationMenuId
			).put(
				"siteNavigationMenuName", siteNavigationMenu.getName()
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