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

package com.liferay.portal.security.audit.storage.internal.search;

import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.security.audit.storage.model.AuditEvent;

import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class reindexes AuditEvent entities.
 */
@Component(
		immediate = true,
		service = AuditEventReindexer.class
)
public class AuditEventReindexerImpl
		implements AuditEventReindexer {

	@Override
	public void reindex(long auditEventId, long companyId) {
		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
				_auditEventLocalService.getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setAddCriteriaMethod(
				dynamicQuery -> {
					Property auditEventIdProperty =
							PropertyFactoryUtil.forName("auditEventId");
					dynamicQuery.add(
							auditEventIdProperty.eq(auditEventId));
				});
		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
				(AuditEvent auditEvent) ->
						indexableActionableDynamicQuery.addDocuments(
								_indexer.getDocument(auditEvent)));

		try {
			indexableActionableDynamicQuery.performActions();
		}
		catch (PortalException portalException) {
			Logger.getLogger(AuditEventReindexerImpl.class.getName()).log(Level.SEVERE, null, portalException);
		}
	}

	@Reference(
			target = "(indexer.class.name=com.liferay.portal.security.audit.storage.model.AuditEvent)"
	)
	protected Indexer<AuditEvent> _indexer;

	@Reference
	protected AuditEventLocalService _auditEventLocalService;

}
