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

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.*;
import com.liferay.portal.kernel.util.ArrayUtil;


import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.audit.storage.internal.search.AuditField;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.spi.model.query.contributor.ModelPreFilterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Rafael Praxedes
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.portal.security.audit.storage.model.AuditEvent",
	service = ModelPreFilterContributor.class
)
public class AuditEventModelPreFilterContributor
	implements ModelPreFilterContributor {

	@Override
	public void contribute(
			BooleanFilter booleanFilter, ModelSearchSettings modelSearchSettings,
			SearchContext searchContext) {
		/*System.out.println("contributing " + booleanFilter);*/

		//_filterByUserId(booleanFilter, searchContext);
		_filterByClientIp(booleanFilter,searchContext);
		//_filterByEventType(booleanFilter, searchContext);
	}

	/*private void _filterByUserId(
			BooleanFilter booleanFilter, SearchContext searchContext) {

		long userId = (long)searchContext.getAttribute(Field.USER_ID);
		//String[] userIds = (String[])searchContext.getAttribute(Field.USER_ID);

		if (Validator.isNotNull(userId)) {
			TermsFilter userIdTermsFilter = new TermsFilter(Field.USER_ID);

			userIdTermsFilter.addValues(String.valueOf(userId));

			booleanFilter.add(userIdTermsFilter, BooleanClauseOccur.MUST);
		}
	}*/

	private void _filterByClientIp(
			BooleanFilter booleanFilter, SearchContext searchContext) {

		String[] clientIps = (String[])searchContext.getAttribute(AuditField.CLIENT_IP);

		if (ArrayUtil.isNotEmpty(clientIps)) {
			TermsFilter clientIpTermsFilter = new TermsFilter(AuditField.CLIENT_IP);

			clientIpTermsFilter.addValues(clientIps);

			booleanFilter.add(clientIpTermsFilter, BooleanClauseOccur.MUST);
		}
	}

	private void _filterByEventType(
			BooleanFilter booleanFilter, SearchContext searchContext) {

		String[] eventTypes = (String[])searchContext.getAttribute(AuditField.EVENT_TYPE);

		if (ArrayUtil.isNotEmpty(eventTypes)) {
			TermsFilter eventTypeTermsFilter = new TermsFilter(AuditField.EVENT_TYPE);

			eventTypeTermsFilter.addValues(eventTypes);

			booleanFilter.add(eventTypeTermsFilter, BooleanClauseOccur.MUST);
		}
	}

}