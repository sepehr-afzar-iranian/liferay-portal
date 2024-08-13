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

package com.liferay.portal.security.audit.storage.internal.search.spi.model.index.contributor;


import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.audit.storage.internal.search.AuditField;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import com.liferay.portal.security.audit.storage.model.AuditEvent;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.xml.bind.ValidationEvent;

/**
 * @author Mohammad Mehdi Tamehri
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.portal.security.audit.storage.model.AuditEvent",
	service = ModelDocumentContributor.class
)
public class AuditEventModelDocumentContributor
	implements ModelDocumentContributor<AuditEvent> {

	@Override
	public void contribute(
		Document document, AuditEvent auditEvent) {

		try {
			document.addKeyword(
				Field.CLASS_NAME_ID,
				classNameLocalService.getClassNameId(AuditEvent.class));
			document.addKeyword(
				Field.CLASS_PK, auditEvent.getClassPK());
			document.addKeyword(
					AuditField.CLASS_NAME, auditEvent.getClassName());
			document.addKeyword(AuditField.SHORT_CLASS_NAME,getShortClassName(auditEvent.getClassName()));
			document.addNumber(
					Field.USER_ID, auditEvent.getUserId());
			String userName = portal.getUserName(
					auditEvent.getUserId(), auditEvent.getUserName());
			document.addKeyword(Field.USER_NAME, userName, true);
			document.addKeyword(
				Field.COMPANY_ID, auditEvent.getCompanyId());
			document.addDate(
				Field.CREATE_DATE, auditEvent.getCreateDate());
			/*document.addKeyword(
					Field.MODIFIED_DATE, auditEvent.getCreateDate().getTime());*/
			document.addText(
				AuditField.EVENT_TYPE, auditEvent.getEventType());
			document.addText(
				AuditField.MESSAGE,auditEvent.getMessage());
			document.addKeyword(
					AuditField.CLIENT_HOST,auditEvent.getClientHost());
			document.addKeyword(
				AuditField.CLIENT_IP,auditEvent.getClientIP());
			document.addKeyword(
				AuditField.SERVER_NAME,auditEvent.getServerName());
			document.addNumber(
				AuditField.SERVER_PORT,auditEvent.getServerPort());


		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception, exception);
			}
		}
	}

	private String getShortClassName(String className) {
		String shortClassName = null;
		if(Validator.isNotNull(className)) {
			shortClassName = className.substring(className.lastIndexOf('.') + 1);
		}
		return shortClassName;
	}

	@Reference
	protected ClassNameLocalService classNameLocalService;

	private static final Log _log = LogFactoryUtil.getLog(
			AuditEventModelDocumentContributor.class);

	@Reference
	protected Portal portal;
}