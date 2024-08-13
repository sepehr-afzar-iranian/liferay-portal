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
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseIndexer;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.Summary;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;

import java.util.Locale;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leonardo Barros
 */
@Component(enabled = false, immediate = true, service = Indexer.class)
public class AuditEventIndexer extends BaseIndexer<AuditEvent> {

	public static final String CLASS_NAME = AuditEvent.class.getName();

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	protected void doDelete(AuditEvent auditEvent) throws Exception {
		deleteDocument(auditEvent.getCompanyId(), auditEvent.getAuditEventId());
	}

	@Override
	protected Document doGetDocument(AuditEvent auditEvent) throws Exception {
		if (_log.isDebugEnabled()) {
			_log.debug("Indexing audit event " + auditEvent);
		}

		Document document = getBaseModelDocument(CLASS_NAME, auditEvent);

		document.addKeyword(
			Field.CLASS_NAME_ID,
			classNameLocalService.getClassNameId(AuditEvent.class));

		document.addKeyword(Field.CLASS_PK, auditEvent.getClassPK());
		document.addKeyword(AuditField.CLASS_NAME, auditEvent.getClassName());
		document.addNumber(Field.USER_ID, auditEvent.getUserId());

		String userName = portal.getUserName(
			auditEvent.getUserId(), auditEvent.getUserName());

		document.addKeyword(Field.USER_NAME, userName, true);

		document.addKeyword(Field.COMPANY_ID, auditEvent.getCompanyId());
		document.addDate(Field.CREATE_DATE, auditEvent.getCreateDate());
		/*	document.addKeyword(
					Field.MODIFIED_DATE, auditEvent.getCreateDate().getTime());*/
		document.addText(AuditField.EVENT_TYPE, auditEvent.getEventType());
		document.addText(AuditField.MESSAGE, auditEvent.getMessage());
		document.addKeyword(AuditField.CLIENT_HOST, auditEvent.getClientHost());
		document.addKeyword(AuditField.CLIENT_IP, auditEvent.getClientIP());
		document.addKeyword(AuditField.SERVER_NAME, auditEvent.getServerName());
		document.addNumber(AuditField.SERVER_PORT, auditEvent.getServerPort());
		document.addKeyword(AuditField.SESSION_ID, auditEvent.getSessionID());

		return document;
	}

	@Override
	protected Summary doGetSummary(
		Document document, Locale locale, String snippet,
		PortletRequest portletRequest, PortletResponse portletResponse) {

		Summary summary = createSummary(
			document, Field.CLASS_NAME_ID, AuditField.EVENT_TYPE);

		summary.setMaxContentLength(200);

		return summary;
	}

	@Override
	protected void doReindex(AuditEvent auditEvent) throws Exception {
		indexWriterHelper.updateDocument(
			getSearchEngineId(), auditEvent.getCompanyId(),
			getDocument(auditEvent), isCommitImmediately());
	}

	@Override
	protected void doReindex(String className, long classPK) throws Exception {
		AuditEvent auditEvent = auditEventLocalService.getAuditEvent(classPK);

		doReindex(auditEvent);
	}

	@Override
	protected void doReindex(String[] ids) throws Exception {
		long companyId = GetterUtil.getLong(ids[0]);

		reindexAuditEvents(companyId);
	}

	protected void reindexAuditEvents(long companyId) throws Exception {
		final IndexableActionableDynamicQuery indexableActionableDynamicQuery =
			auditEventLocalService.getIndexableActionableDynamicQuery();

		indexableActionableDynamicQuery.setCompanyId(companyId);
		indexableActionableDynamicQuery.setPerformActionMethod(
			(AuditEvent auditEvent) -> {
				try {
					Document document = getDocument(auditEvent);

					indexableActionableDynamicQuery.addDocuments(document);
				}
				catch (PortalException portalException) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"index audit event " + auditEvent.getAuditEventId(),
							portalException);
					}
				}
			});
		indexableActionableDynamicQuery.setSearchEngineId(getSearchEngineId());

		indexableActionableDynamicQuery.performActions();
	}

	protected AuditEventLocalService auditEventLocalService;

	@Reference
	protected ClassNameLocalService classNameLocalService;

	protected IndexWriterHelper indexWriterHelper;

	@Reference
	protected Portal portal;

	private static final Log _log = LogFactoryUtil.getLog(
		AuditEventIndexer.class);

}