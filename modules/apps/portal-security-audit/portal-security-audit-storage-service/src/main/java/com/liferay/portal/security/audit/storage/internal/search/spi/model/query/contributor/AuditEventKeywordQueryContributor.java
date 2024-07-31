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

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.search.*;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.query.QueryHelper;
import com.liferay.portal.search.query.StringQuery;
import com.liferay.portal.search.spi.model.query.contributor.KeywordQueryContributor;
import com.liferay.portal.search.spi.model.query.contributor.helper.KeywordQueryContributorHelper;
import com.liferay.portal.security.audit.storage.internal.search.AuditField;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Set;

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
		/*System.out.println("contributing keywords " + keywords);*/
		BooleanQuery booleanQuery1 = new BooleanQueryImpl();

		queryHelper.addSearchTerm(
			booleanQuery, keywordQueryContributorHelper.getSearchContext(),
			Field.USER_NAME, false);
		queryHelper.addSearchTerm(
				booleanQuery, keywordQueryContributorHelper.getSearchContext(),
				Field.USER_ID, false);

		queryHelper.addSearchTerm(
			booleanQuery, keywordQueryContributorHelper.getSearchContext(),
				AuditField.CLASS_PK, false);

		queryHelper.addSearchTerm(
				booleanQuery, keywordQueryContributorHelper.getSearchContext(),
				AuditField.CLASS_NAME, false);

		/*queryHelper.addSearchTerm(
				booleanQuery, keywordQueryContributorHelper.getSearchContext(),
				AuditField.EVENT_TYPE, true);*/

		queryHelper.addSearchTerm(
				booleanQuery, keywordQueryContributorHelper.getSearchContext(),
				AuditField.CLIENT_IP, false);

		SearchContext searchContext =
				keywordQueryContributorHelper.getSearchContext();

		String name = (String)searchContext.getAttribute(AuditField.EVENT_TYPE);

		if (!Validator.isBlank(name)) {
			BooleanQuery nameQuery = new BooleanQueryImpl();

			queryHelper.addSearchTerm(
					nameQuery, searchContext, AuditField.EVENT_TYPE, true);

			try {
				booleanQuery.add(nameQuery, BooleanClauseOccur.SHOULD);
			} catch (ParseException parseException) {
				throw new SystemException(parseException);
			}
		}

	}

	@Reference
	protected QueryHelper queryHelper;

}