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

package com.liferay.portal.security.audit.storage.internal.search.spi.model.query.contributor;

import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.QueryConfig;

import com.liferay.portal.search.query.QueryHelper;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.helper.KeywordQueryContributorHelper;
import com.liferay.portal.security.audit.storage.internal.search.AuditField;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.portal.security.audit.storage.model.AuditEvent",
	service = KeywordQueryContributor.class
)
public class AuditEventKeywordQueryContributor
	implements KeywordQueryContributor {

	@Override
	public void contribute(
		String keywords, BooleanQuery booleanQuery,
		KeywordQueryContributorHelper keywordQueryContributorHelper) {
		SearchContext searchContext =
				keywordQueryContributorHelper.getSearchContext();

		queryHelper.addSearchTerm(
			booleanQuery, searchContext,
			Field.USER_NAME, false);
		queryHelper.addSearchTerm(
				booleanQuery, searchContext,
				Field.USER_ID, false);

		queryHelper.addSearchTerm(
			booleanQuery, searchContext,
				AuditField.CLASS_PK, false);

		queryHelper.addSearchTerm(
				booleanQuery, searchContext,
				AuditField.CLASS_NAME, false);

		queryHelper.addSearchTerm(
				booleanQuery, searchContext,
				AuditField.SHORT_CLASS_NAME, false);

		queryHelper.addSearchTerm(
				booleanQuery, searchContext,
				AuditField.EVENT_TYPE, false);

		queryHelper.addSearchTerm(
				booleanQuery, searchContext,
				AuditField.CLIENT_IP, false);

	}

	@Reference
	protected QueryHelper queryHelper;

}