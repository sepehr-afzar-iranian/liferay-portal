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

package com.liferay.portal.security.audit.storage.service.impl;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.dao.orm.*;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.*;
import com.liferay.portal.kernel.search.generic.BooleanQueryImpl;
import com.liferay.portal.kernel.util.*;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.search.sort.FieldSort;
import com.liferay.portal.search.sort.SortFieldBuilder;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.kernel.search.Sort;

import com.liferay.portal.security.audit.storage.internal.search.AuditField;
import com.liferay.portal.vulcan.util.TransformUtil;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.security.audit.storage.comparator.AuditEventCreateDateComparator;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.base.AuditEventLocalServiceBaseImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.portal.security.audit.storage.model.AuditEvent",
	service = AopService.class
)
public class AuditEventLocalServiceImpl extends AuditEventLocalServiceBaseImpl {

	@Indexable(type = IndexableType.REINDEX)
	@Override
	public AuditEvent addAuditEvent(AuditMessage auditMessage) {
		long auditEventId = counterLocalService.increment();

		AuditEvent auditEvent = auditEventPersistence.create(auditEventId);

		auditEvent.setCompanyId(auditMessage.getCompanyId());
		auditEvent.setUserId(auditMessage.getUserId());
		auditEvent.setUserName(auditMessage.getUserName());
		auditEvent.setCreateDate(auditMessage.getTimestamp());
		auditEvent.setEventType(auditMessage.getEventType());
		auditEvent.setClassName(auditMessage.getClassName());
		auditEvent.setClassPK(auditMessage.getClassPK());
		auditEvent.setMessage(auditMessage.getMessage());
		auditEvent.setClientHost(auditMessage.getClientHost());
		auditEvent.setClientIP(auditMessage.getClientIP());
		auditEvent.setServerName(auditMessage.getServerName());
		auditEvent.setServerPort(auditMessage.getServerPort());
		auditEvent.setSessionID(auditMessage.getSessionID());
		auditEvent.setAdditionalInfo(
			String.valueOf(auditMessage.getAdditionalInfo()));

		return auditEventPersistence.update(auditEvent);
	}

	@Override
	public AuditEvent fetchAuditEvent(long auditEventId) {
		return auditEventPersistence.fetchByPrimaryKey(auditEventId);
	}

	@Override
	public List<AuditEvent> getAuditEvents(long companyId, int start, int end) {
		return auditEventPersistence.findByCompanyId(
			companyId, start, end, new AuditEventCreateDateComparator());
	}

	@Override
	public List<AuditEvent> getAuditEvents(
		long companyId, int start, int end,
		OrderByComparator<AuditEvent> orderByComparator) {

		return auditEventPersistence.findByCompanyId(
			companyId, start, end, orderByComparator);
	}

	@Override
	public List<AuditEvent> getAuditEvents(
		long companyId, long userId, String userName, Date createDateGT,
		Date createDateLT, String eventType, String className, String classPK,
		String clientHost, String clientIP, String serverName, int serverPort,
		String sessionID, boolean andSearch, int start, int end) {

		return getAuditEvents(
			companyId, userId, userName, createDateGT, createDateLT, eventType,
			className, classPK, clientHost, clientIP, serverName, serverPort,
			sessionID, andSearch, start, end,
			new AuditEventCreateDateComparator());
	}

	@Override
	public List<AuditEvent> getAuditEvents(
		long companyId, long userId, String userName, Date createDateGT,
		Date createDateLT, String eventType, String className, String classPK,
		String clientHost, String clientIP, String serverName, int serverPort,
		String sessionID, boolean andSearch, int start, int end,
		OrderByComparator<AuditEvent> orderByComparator) {

		DynamicQuery dynamicQuery = buildDynamicQuery(
			companyId, userId, userName, createDateGT, createDateLT, eventType,
			className, classPK, clientHost, clientIP, serverName, serverPort,
			sessionID, andSearch);

		return dynamicQuery(dynamicQuery, start, end, orderByComparator);
	}

	@Override
	public int getAuditEventsCount(long companyId) {
		return auditEventPersistence.countByCompanyId(companyId);
	}

	@Override
	public int getAuditEventsCount(
		long companyId, long userId, String userName, Date createDateGT,
		Date createDateLT, String eventType, String className, String classPK,
		String clientHost, String clientIP, String serverName, int serverPort,
		String sessionID, boolean andSearch) {

		DynamicQuery dynamicQuery = buildDynamicQuery(
			companyId, userId, userName, createDateGT, createDateLT, eventType,
			className, classPK, clientHost, clientIP, serverName, serverPort,
			sessionID, andSearch);

		return (int)dynamicQueryCount(dynamicQuery);
	}

	protected DynamicQuery buildDynamicQuery(
		long companyId, long userId, String userName, Date createDateGT,
		Date createDateLT, String eventType, String className, String classPK,
		String clientHost, String clientIP, String serverName, int serverPort,
		String sessionID, boolean andSearch) {

		Junction junction = null;

		if (andSearch) {
			junction = RestrictionsFactoryUtil.conjunction();
		}
		else {
			junction = RestrictionsFactoryUtil.disjunction();
		}

		if (userId > 0) {
			Property property = PropertyFactoryUtil.forName("userId");

			junction.add(property.eq(userId));
		}

		if (Validator.isNotNull(userName)) {
			junction.add(
				RestrictionsFactoryUtil.ilike(
					"userName",
					StringPool.PERCENT + userName + StringPool.PERCENT));
		}

		if (Validator.isNotNull(eventType)) {
			Property property = PropertyFactoryUtil.forName("eventType");

			String value =
				StringPool.PERCENT + StringUtil.toUpperCase(eventType) +
					StringPool.PERCENT;

			junction.add(property.like(value));
		}

		if (Validator.isNotNull(className)) {
			junction.add(
				RestrictionsFactoryUtil.ilike(
					"className",
					StringPool.PERCENT + className + StringPool.PERCENT));
		}

		if (Validator.isNotNull(classPK)) {
			Property property = PropertyFactoryUtil.forName("classPK");

			junction.add(property.eq(classPK));
		}

		if (Validator.isNotNull(clientHost)) {
			junction.add(
				RestrictionsFactoryUtil.ilike(
					"clientHost",
					StringPool.PERCENT + clientHost + StringPool.PERCENT));
		}

		if (Validator.isNotNull(clientIP)) {
			junction.add(
				RestrictionsFactoryUtil.ilike(
					"clientIP",
					StringPool.PERCENT + clientIP + StringPool.PERCENT));
		}

		if (Validator.isNotNull(serverName)) {
			junction.add(
				RestrictionsFactoryUtil.ilike(
					"serverName",
					StringPool.PERCENT + serverName + StringPool.PERCENT));
		}

		if (serverPort > 0) {
			Property property = PropertyFactoryUtil.forName("serverPort");

			junction.add(property.eq(serverPort));
		}

		if (Validator.isNotNull(sessionID)) {
			junction.add(
				RestrictionsFactoryUtil.ilike(
					"sessionID",
					StringPool.PERCENT + sessionID + StringPool.PERCENT));
		}

		DynamicQuery dynamicQuery = dynamicQuery();

		if (companyId > 0) {
			Property property = PropertyFactoryUtil.forName("companyId");

			dynamicQuery.add(property.eq(companyId));
		}

		if (createDateGT != null) {
			Property property = PropertyFactoryUtil.forName("createDate");

			dynamicQuery.add(property.gt(createDateGT));
		}

		if (createDateLT != null) {
			Property property = PropertyFactoryUtil.forName("createDate");

			dynamicQuery.add(property.lt(createDateLT));
		}

		return dynamicQuery.add(junction);
	}


	public List<AuditEvent> searchAuditEvents(long companyId, String keywords, int start, int end, Sort[] sorts){
		Indexer<AuditEvent> indexer = IndexerRegistryUtil.nullSafeGetIndexer(AuditEvent.class);

		SearchContext searchContext = new SearchContext();
		searchContext.setCompanyId(companyId);
		/*searchContext.setStart(start);
		searchContext.setEnd(end);*/
		searchContext.setSorts(sorts);

		if (Validator.isNotNull(keywords)) {
			searchContext.setKeywords(keywords);
		}

		QueryConfig queryConfig = searchContext.getQueryConfig();
		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

        Hits hits = null;
        try {
            hits = indexer.search(searchContext);
        } catch (SearchException e) {
            throw new RuntimeException(e);
        }

        List<AuditEvent> auditEvents = new ArrayList<>();


		for (Document document : hits.toList()) {
			long auditEventId = GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK));

			AuditEvent auditEvent = fetchAuditEvent(auditEventId);

			if (auditEvent != null) {
				auditEvents.add(auditEvent);
			}
		}

		return auditEvents;
	}

	public int searchAuditEventsCount(long companyId, String keywords) {
		Indexer<AuditEvent> indexer = IndexerRegistryUtil.nullSafeGetIndexer(AuditEvent.class);

		SearchContext searchContext = new SearchContext();
		searchContext.setCompanyId(companyId);

		if (Validator.isNotNull(keywords)) {
			searchContext.setKeywords(keywords);
		}

		QueryConfig queryConfig = searchContext.getQueryConfig();
		queryConfig.setHighlightEnabled(false);
		queryConfig.setScoreEnabled(false);

        try {
            return (int) indexer.searchCount(searchContext);
        } catch (SearchException e) {
            throw new RuntimeException(e);
        }
    }
	@Override
	public BaseModelSearchResult<AuditEvent> search(
			long companyId, String keywords, LinkedHashMap<String, Object> params,int cur,
			int delta,String orderByField, boolean reverse) {

		SearchResponse searchResponse = _searcher.search(
				_getSearchRequest(
						companyId, keywords, params,cur,delta, orderByField,
						reverse));

		SearchHits searchHits = searchResponse.getSearchHits();

		List<AuditEvent> accountEntries = TransformUtil.transform(
				searchHits.getSearchHits(),
				searchHit -> {
					com.liferay.portal.search.document.Document document = searchHit.getDocument();

					long auditEventId = document.getLong(Field.ENTRY_CLASS_PK);

					AuditEvent auditEvent = fetchAuditEvent(auditEventId);

					if (auditEvent == null) {
						Indexer<AuditEvent> indexer =
								IndexerRegistryUtil.getIndexer(AuditEvent.class);

						indexer.delete(auditEvent);
					}

					return auditEvent;
				});

		return new BaseModelSearchResult<>(
				accountEntries, searchResponse.getTotalHits());
	}

	private SearchRequest _getSearchRequest(
			long companyId, String keywords, LinkedHashMap<String, Object> params,
			int cur,int delta, String orderByField, boolean reverse) {

		SearchRequestBuilder searchRequestBuilder =
				_searchRequestBuilderFactory.builder();

		searchRequestBuilder.entryClassNames(
				AuditEvent.class.getName()
		).emptySearchEnabled(
				true
		).highlightEnabled(
				false
		).withSearchContext(
				searchContext -> _populateSearchContext(
						searchContext, companyId, keywords, params)
		);

		if (cur != QueryUtil.ALL_POS) {
			searchRequestBuilder.from(cur);
			searchRequestBuilder.size(delta);
		}

		if (Validator.isNotNull(orderByField)) {
			SortOrder sortOrder = SortOrder.ASC;

			if (reverse) {
				sortOrder = SortOrder.DESC;
			}

			FieldSort fieldSort = _sorts.field(
					_sortFieldBuilder.getSortField(
							AuditEvent.class.getName(), orderByField),
					sortOrder);

			searchRequestBuilder.sorts(fieldSort);
		}

		return searchRequestBuilder.build();
	}

	private void _populateSearchContext(
			SearchContext searchContext, long companyId, String keywords,
			LinkedHashMap<String, Object> params) {

		searchContext.setCompanyId(companyId);
		/*searchContext.setStart(start);
		searchContext.setEnd(end);*/

		if (Validator.isNotNull(keywords)) {
			searchContext.setKeywords(keywords);
		}

		if (MapUtil.isEmpty(params)) {
			return;
		}

		String eventType = (String) params.get(AuditField.EVENT_TYPE);
		if (Validator.isNotNull(eventType)) {
			searchContext.setAttribute(AuditField.EVENT_TYPE, eventType);
		}

		String userId = (String) params.get(AuditField.USER_ID);
		if (Validator.isNotNull(userId)) {
			searchContext.setAttribute(AuditField.USER_ID, Long.valueOf(userId));
		}

		String userName = (String) params.get(Field.USER_NAME);
		if (Validator.isNotNull(userName)) {
			searchContext.setAttribute(Field.USER_NAME, userName);
		}

		String classPK = (String) params.get(AuditField.CLASS_PK);
		if (Validator.isNotNull(classPK)) {
			searchContext.setAttribute(AuditField.CLASS_PK, classPK);
		}

		String className = (String) params.get(AuditField.CLASS_NAME);
		if (Validator.isNotNull(className)) {
			searchContext.setAttribute(AuditField.CLASS_NAME, className);
		}


		String sessionId = (String) params.get(AuditField.SESSION_ID);
		if (Validator.isNotNull(sessionId)) {
			searchContext.setAttribute(AuditField.SESSION_ID, sessionId);
		}

		String clientIP = (String) params.get(AuditField.CLIENT_IP);
		if (Validator.isNotNull(clientIP)) {
			searchContext.setAttribute(AuditField.CLIENT_IP, clientIP);
		}

		String clientHost = (String) params.get(AuditField.CLIENT_HOST);
		if (Validator.isNotNull(clientHost)) {
			searchContext.setAttribute(AuditField.CLIENT_HOST, clientHost);
		}


		String serverName = (String) params.get(AuditField.SERVER_NAME);
		if (Validator.isNotNull(serverName)) {
			searchContext.setAttribute(AuditField.SERVER_NAME, serverName);
		}

		String serverPort = (String) params.get(AuditField.SERVER_PORT);
		if (Validator.isNotNull(serverPort)) {
			searchContext.setAttribute(AuditField.SERVER_PORT, Integer.valueOf(serverPort));
		}

	}

	@Reference
	private Searcher _searcher;

	@Reference
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Reference
	private Sorts _sorts;

	@Reference
	private SortFieldBuilder _sortFieldBuilder;


}