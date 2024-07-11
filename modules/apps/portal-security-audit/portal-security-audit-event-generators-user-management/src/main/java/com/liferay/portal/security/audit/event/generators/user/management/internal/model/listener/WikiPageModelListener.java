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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.event.generators.util.AuditMessageBuilder;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Yousef Ghadiri
 */

@Component(immediate = true, service = ModelListener.class)
public class WikiPageModelListener extends BaseModelListener<WikiPage> {

	@Override
	public void onAfterCreate(WikiPage wikiPage) throws ModelListenerException {
		audit(EventTypes.ADD, wikiPage);
	}

	@Override
	public void onAfterRemove(WikiPage wikiPage) throws ModelListenerException {
		audit(EventTypes.DELETE, wikiPage);
	}

	@Override
	public void onAfterUpdate(WikiPage wikiPage) throws ModelListenerException {
		audit(EventTypes.UPDATE, wikiPage);
	}

	protected void audit(
		String eventType, WikiPage wikiPage)
		throws ModelListenerException {
		try {
			long wikiPageId = wikiPage.getPageId();
			AuditMessage auditMessage =
				AuditMessageBuilder.buildAuditMessage(eventType,
					WikiPage.class.getName(), wikiPageId,
					null);
			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			JSONObject additionalInfoJSONObject =
				auditMessage.getAdditionalInfo();

			additionalInfoJSONObject.put(
				"groupId", serviceContext.getScopeGroupId()
			).put(
				"wikiPageId", wikiPageId
			).put(
				"wikiPageTitle", wikiPage.getTitle()
			).put(
				"wikiNodeId", wikiPage.getNodeId()
			);

			WikiNode wikiNode = wikiPage.getNode();

			if (Validator.isNotNull(wikiNode)) {
				additionalInfoJSONObject.put(
					"wikiNodeName", wikiNode.getName()
				);
			}

			_auditRouter.route(auditMessage);
		}
		catch (Exception exception) {
			throw new ModelListenerException(exception);
		}
	}

	@Reference
	private AuditRouter _auditRouter;

}