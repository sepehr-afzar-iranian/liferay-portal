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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/** @author Mohammad Mehdi Tamehri */
@Component(immediate = true, service = AuditEventReindexer.class)
public class AuditEventReindexerImpl implements AuditEventReindexer {

	@Override
	public void reindex(long auditEventId, long companyId) {
		IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			auditEventLocalService.getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setAddCriteriaMethod(
			dynamicQuery -> {
				Property auditEventIdProperty = PropertyFactoryUtil.forName(
					"auditEventId");

				dynamicQuery.add(auditEventIdProperty.eq(auditEventId));
			});
		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(AuditEvent auditEvent) ->
				indexableActionableDynamicQuery.addDocuments(
					indexer.getDocument(auditEvent)));

		try {
			indexableActionableDynamicQuery.performActions();
		}
		catch (PortalException portalException) {
			_logger.log(Level.SEVERE, null, portalException);
		}
	}

	@Reference
	protected AuditEventLocalService auditEventLocalService;

	@Reference(
		target = "(indexer.class.name=com.liferay.portal.security.audit.storage.model.AuditEvent)"
	)
	protected Indexer<AuditEvent> indexer;

	private static final Logger _logger = Logger.getLogger(
		AuditEventReindexer.class.getName());

}