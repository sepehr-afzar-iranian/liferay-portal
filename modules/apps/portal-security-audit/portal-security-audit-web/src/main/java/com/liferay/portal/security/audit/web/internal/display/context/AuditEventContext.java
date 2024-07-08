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

package com.liferay.portal.security.audit.web.internal.display.context;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.DropdownItemListBuilder;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.storage.comparator.AuditEventCreateDateComparator;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalServiceUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

/**
 * @author Nader Jafari
 */
public class AuditEventContext {

	public AuditEventContext(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse,
		LiferayPortletRequest liferayPortletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;
		_liferayPortletRequest = liferayPortletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_themeDisplay = (ThemeDisplay)_httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

        _portalPreferences = PortletPreferencesFactoryUtil.getPortalPreferences(
                httpServletRequest);
	}

	public Date getEndDate() throws ParseException {
		if (_endDate != null) {
			return _endDate;
		}

		String endDate = ParamUtil.getString(_httpServletRequest, "endDate");

		if ((endDate != null) && !endDate.isEmpty()) {
			_endDate = new SimpleDateFormat(
				"dd/MM/yyyy"
			).parse(
				endDate
			);
		}

		return _endDate;
	}

	public List<DropdownItem> getFilterDropdownItems() {
		return DropdownItemListBuilder.addGroup(
			dropdownGroupItem -> {
				dropdownGroupItem.setDropdownItems(
					_getFilterEventTypeDropdownItems());
				dropdownGroupItem.setLabel(
					LanguageUtil.get(
						_httpServletRequest, "filter-by-eventType"));
			}
		).build();
	}

	public String getEventType() throws ParseException {

		if (_eventType != null) {
			return _eventType;
		}

		String eventType = ParamUtil.getString(
			_httpServletRequest, "eventType");

		_eventType = eventType;

		return _eventType;
	}

	public String[]  getEventTypes(){
	    return _eventTypes;
    }

    public String getOrderByCol() {
        String orderByCol = ParamUtil.getString(
                _httpServletRequest, "orderByCol");


        if (Validator.isNotNull(orderByCol)) {
            _portalPreferences.setValue(
                    DLPortletKeys.DOCUMENT_LIBRARY, "order-by-col", orderByCol);
        }
        else {
            orderByCol = _portalPreferences.getValue(
                    DLPortletKeys.DOCUMENT_LIBRARY, "order-by-col", "createDate");
        }

        return orderByCol;
    }

    public String getOrderByType() {
        String orderByType = ParamUtil.getString(
                _httpServletRequest, "orderByType");

        if (Validator.isNotNull(orderByType)) {
            _portalPreferences.setValue(
                    DLPortletKeys.DOCUMENT_LIBRARY, "order-by-type", orderByType);
        }
        else {
            orderByType = _portalPreferences.getValue(
                    DLPortletKeys.DOCUMENT_LIBRARY, "order-by-type", "desc");
        }

        return orderByType;
    }

	public PortletURL getPortletURL() {
		PortletURL portletURL = _renderResponse.createRenderURL();

		return portletURL;
	}

	public String getSearchActionURL() {
		PortletURL searchActionURL = getPortletURL();

		return searchActionURL.toString();
	}

	public SearchContainer<AuditEvent> getSearchContainer() throws Exception {
		if (_searchContainer != null) {
			return _searchContainer;
		}

		SearchContainer<AuditEvent> searchContainer = new SearchContainer<>(
			_liferayPortletRequest, getPortletURL(), null,
			"there-are-no-audit-event");

		searchContainer.setOrderByCol(getOrderByCol());
		searchContainer.setOrderByType(getOrderByType());



		List<AuditEvent> auditEvents =
			AuditEventLocalServiceUtil.getAuditEvents(
				_themeDisplay.getCompanyId(), 0, null, getStartDate(),
				getEndDate(), getEventType(), null, null, null, null, null, 0,
				null, true, searchContainer.getStart(),
				searchContainer.getEnd(),
                new AuditEventCreateDateComparator());

		int total = AuditEventLocalServiceUtil.getAuditEventsCount(
			_themeDisplay.getCompanyId(), 0, null, getStartDate(), getEndDate(),
			getEventType(), null, null, null, null, null, 0, null, true);

		searchContainer.setTotal(total);

		searchContainer.setResults(auditEvents);

		_searchContainer = searchContainer;

		return _searchContainer;
	}

	public Date getStartDate() throws ParseException {
		if (_startDate != null) {
			return _startDate;
		}

		String startDate = ParamUtil.getString(
			_httpServletRequest, "startDate");

		if ((startDate != null) && !startDate.isEmpty()) {
			_startDate = new SimpleDateFormat(
				"dd/MM/yyyy"
			).parse(
				startDate
			);
		}

		return _startDate;
	}

    private List<DropdownItem> _getFilterEventTypeDropdownItems() {

        return new DropdownItemList() {
            {
                for (String eventType : _eventTypes) {
                    add(dropdownItem -> {
                        dropdownItem.setActive(true);
                        dropdownItem.setHref(getPortletURL(), "eventType", eventType);
                        dropdownItem.setLabel(
                                LanguageUtil.get(_httpServletRequest, eventType));
                    });
                }

                addGroup(
                        dropdownGroupItem -> {
                            dropdownGroupItem.setDropdownItems(_getOrderByDropdownItems());
                            dropdownGroupItem.setLabel(
                                    LanguageUtil.get(_httpServletRequest, "order-by"));
                });

            }
        };
    }

    private List<DropdownItem> _getOrderByDropdownItems() {
        final Map<String, String> orderColumns = HashMapBuilder.put(
                "createDate", "create-date"
        ).build();

        orderColumns.put("className", "class-name");

        return new DropdownItemList() {
            {
                for (Map.Entry<String, String> orderByColEntry :
                        orderColumns.entrySet()) {

                    String orderByCol = orderByColEntry.getKey();

                    add(
                            dropdownItem -> {
                                dropdownItem.setActive(
                                        orderByCol.equals(getOrderByCol()));
                                dropdownItem.setHref(
                                        getPortletURL(), "orderByCol",
                                        orderByCol);
                                dropdownItem.setLabel(
                                        LanguageUtil.get(
                                                _httpServletRequest,
                                                orderByColEntry.getValue()));
                            });
                }
            }
        };
    }


    public static String getPrettyPrintedJSONString(String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.setPrettyPrinting();

        Gson gson = gsonBuilder.create();

        JsonParser jsonParser = new JsonParser();

        try {
            json = gson.toJson(jsonParser.parse(json));
        }catch (Exception e){
            e.printStackTrace();
        }

        return json;
    }

	private Date _endDate;
	private final String[] _eventTypes = {
		EventTypes.ADD, EventTypes.ASSIGN, EventTypes.DELETE,
		EventTypes.IMPERSONATE, EventTypes.LOGIN, EventTypes.LOGIN_FAILURE,
		EventTypes.LOGOUT, EventTypes.UNASSIGN, EventTypes.UPDATE
	};
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
    private final PortalPreferences _portalPreferences;

    private String _eventType;
	private final RenderResponse _renderResponse;
	private SearchContainer<AuditEvent> _searchContainer;
	private Date _startDate;
	private final ThemeDisplay _themeDisplay;

}
