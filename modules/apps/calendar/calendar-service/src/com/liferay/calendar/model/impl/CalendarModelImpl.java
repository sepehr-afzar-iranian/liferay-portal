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

package com.liferay.calendar.model.impl;

import aQute.bnd.annotation.ProviderType;

import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarModel;
import com.liferay.calendar.model.CalendarSoap;

import com.liferay.portal.LocaleException;
import com.liferay.portal.kernel.bean.AutoEscapeBeanHandler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSON;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.CacheModel;
import com.liferay.portal.model.User;
import com.liferay.portal.model.impl.BaseModelImpl;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

import com.liferay.portlet.expando.model.ExpandoBridge;
import com.liferay.portlet.expando.util.ExpandoBridgeFactoryUtil;
import com.liferay.portlet.exportimport.lar.StagedModelType;

import java.io.Serializable;

import java.sql.Types;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * The base model implementation for the Calendar service. Represents a row in the &quot;Calendar&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This implementation and its corresponding interface {@link com.liferay.calendar.model.CalendarModel} exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in {@link CalendarImpl}.
 * </p>
 *
 * @author Eduardo Lundgren
 * @see CalendarImpl
 * @see com.liferay.calendar.model.Calendar
 * @see com.liferay.calendar.model.CalendarModel
 * @generated
 */
@JSON(strict = true)
@ProviderType
public class CalendarModelImpl extends BaseModelImpl<Calendar>
	implements CalendarModel {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. All methods that expect a calendar model instance should use the {@link com.liferay.calendar.model.Calendar} interface instead.
	 */
	public static final String TABLE_NAME = "Calendar";
	public static final Object[][] TABLE_COLUMNS = {
			{ "uuid_", Types.VARCHAR },
			{ "calendarId", Types.BIGINT },
			{ "groupId", Types.BIGINT },
			{ "companyId", Types.BIGINT },
			{ "userId", Types.BIGINT },
			{ "userName", Types.VARCHAR },
			{ "createDate", Types.TIMESTAMP },
			{ "modifiedDate", Types.TIMESTAMP },
			{ "resourceBlockId", Types.BIGINT },
			{ "calendarResourceId", Types.BIGINT },
			{ "name", Types.VARCHAR },
			{ "description", Types.VARCHAR },
			{ "timeZoneId", Types.VARCHAR },
			{ "color", Types.INTEGER },
			{ "defaultCalendar", Types.BOOLEAN },
			{ "enableComments", Types.BOOLEAN },
			{ "enableRatings", Types.BOOLEAN },
			{ "lastPublishDate", Types.TIMESTAMP }
		};
	public static final Map<String, Integer> TABLE_COLUMNS_MAP = new HashMap<String, Integer>();

	static {
		TABLE_COLUMNS_MAP.put("uuid_", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("calendarId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("groupId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("companyId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("userId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("userName", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("createDate", Types.TIMESTAMP);
		TABLE_COLUMNS_MAP.put("modifiedDate", Types.TIMESTAMP);
		TABLE_COLUMNS_MAP.put("resourceBlockId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("calendarResourceId", Types.BIGINT);
		TABLE_COLUMNS_MAP.put("name", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("description", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("timeZoneId", Types.VARCHAR);
		TABLE_COLUMNS_MAP.put("color", Types.INTEGER);
		TABLE_COLUMNS_MAP.put("defaultCalendar", Types.BOOLEAN);
		TABLE_COLUMNS_MAP.put("enableComments", Types.BOOLEAN);
		TABLE_COLUMNS_MAP.put("enableRatings", Types.BOOLEAN);
		TABLE_COLUMNS_MAP.put("lastPublishDate", Types.TIMESTAMP);
	}

	public static final String TABLE_SQL_CREATE = "create table Calendar (uuid_ VARCHAR(75) null,calendarId LONG not null primary key,groupId LONG,companyId LONG,userId LONG,userName VARCHAR(75) null,createDate DATE null,modifiedDate DATE null,resourceBlockId LONG,calendarResourceId LONG,name STRING null,description STRING null,timeZoneId VARCHAR(75) null,color INTEGER,defaultCalendar BOOLEAN,enableComments BOOLEAN,enableRatings BOOLEAN,lastPublishDate DATE null)";
	public static final String TABLE_SQL_DROP = "drop table Calendar";
	public static final String ORDER_BY_JPQL = " ORDER BY calendar.name ASC";
	public static final String ORDER_BY_SQL = " ORDER BY Calendar.name ASC";
	public static final String DATA_SOURCE = "liferayDataSource";
	public static final String SESSION_FACTORY = "liferaySessionFactory";
	public static final String TX_MANAGER = "liferayTransactionManager";
	public static final boolean ENTITY_CACHE_ENABLED = GetterUtil.getBoolean(com.liferay.calendar.service.util.ServiceProps.get(
				"value.object.entity.cache.enabled.Calendar"), true);
	public static final boolean FINDER_CACHE_ENABLED = GetterUtil.getBoolean(com.liferay.calendar.service.util.ServiceProps.get(
				"value.object.finder.cache.enabled.Calendar"), true);
	public static final boolean COLUMN_BITMASK_ENABLED = GetterUtil.getBoolean(com.liferay.calendar.service.util.ServiceProps.get(
				"value.object.column.bitmask.enabled.Calendar"), true);
	public static final long CALENDARRESOURCEID_COLUMN_BITMASK = 1L;
	public static final long COMPANYID_COLUMN_BITMASK = 2L;
	public static final long DEFAULTCALENDAR_COLUMN_BITMASK = 4L;
	public static final long GROUPID_COLUMN_BITMASK = 8L;
	public static final long RESOURCEBLOCKID_COLUMN_BITMASK = 16L;
	public static final long UUID_COLUMN_BITMASK = 32L;
	public static final long NAME_COLUMN_BITMASK = 64L;

	/**
	 * Converts the soap model instance into a normal model instance.
	 *
	 * @param soapModel the soap model instance to convert
	 * @return the normal model instance
	 */
	public static Calendar toModel(CalendarSoap soapModel) {
		if (soapModel == null) {
			return null;
		}

		Calendar model = new CalendarImpl();

		model.setUuid(soapModel.getUuid());
		model.setCalendarId(soapModel.getCalendarId());
		model.setGroupId(soapModel.getGroupId());
		model.setCompanyId(soapModel.getCompanyId());
		model.setUserId(soapModel.getUserId());
		model.setUserName(soapModel.getUserName());
		model.setCreateDate(soapModel.getCreateDate());
		model.setModifiedDate(soapModel.getModifiedDate());
		model.setResourceBlockId(soapModel.getResourceBlockId());
		model.setCalendarResourceId(soapModel.getCalendarResourceId());
		model.setName(soapModel.getName());
		model.setDescription(soapModel.getDescription());
		model.setTimeZoneId(soapModel.getTimeZoneId());
		model.setColor(soapModel.getColor());
		model.setDefaultCalendar(soapModel.getDefaultCalendar());
		model.setEnableComments(soapModel.getEnableComments());
		model.setEnableRatings(soapModel.getEnableRatings());
		model.setLastPublishDate(soapModel.getLastPublishDate());

		return model;
	}

	/**
	 * Converts the soap model instances into normal model instances.
	 *
	 * @param soapModels the soap model instances to convert
	 * @return the normal model instances
	 */
	public static List<Calendar> toModels(CalendarSoap[] soapModels) {
		if (soapModels == null) {
			return null;
		}

		List<Calendar> models = new ArrayList<Calendar>(soapModels.length);

		for (CalendarSoap soapModel : soapModels) {
			models.add(toModel(soapModel));
		}

		return models;
	}

	public static final long LOCK_EXPIRATION_TIME = GetterUtil.getLong(com.liferay.calendar.service.util.ServiceProps.get(
				"lock.expiration.time.Calendar"));

	public CalendarModelImpl() {
	}

	@Override
	public long getPrimaryKey() {
		return _calendarId;
	}

	@Override
	public void setPrimaryKey(long primaryKey) {
		setCalendarId(primaryKey);
	}

	@Override
	public Serializable getPrimaryKeyObj() {
		return _calendarId;
	}

	@Override
	public void setPrimaryKeyObj(Serializable primaryKeyObj) {
		setPrimaryKey(((Long)primaryKeyObj).longValue());
	}

	@Override
	public Class<?> getModelClass() {
		return Calendar.class;
	}

	@Override
	public String getModelClassName() {
		return Calendar.class.getName();
	}

	@Override
	public Map<String, Object> getModelAttributes() {
		Map<String, Object> attributes = new HashMap<String, Object>();

		attributes.put("uuid", getUuid());
		attributes.put("calendarId", getCalendarId());
		attributes.put("groupId", getGroupId());
		attributes.put("companyId", getCompanyId());
		attributes.put("userId", getUserId());
		attributes.put("userName", getUserName());
		attributes.put("createDate", getCreateDate());
		attributes.put("modifiedDate", getModifiedDate());
		attributes.put("resourceBlockId", getResourceBlockId());
		attributes.put("calendarResourceId", getCalendarResourceId());
		attributes.put("name", getName());
		attributes.put("description", getDescription());
		attributes.put("timeZoneId", getTimeZoneId());
		attributes.put("color", getColor());
		attributes.put("defaultCalendar", getDefaultCalendar());
		attributes.put("enableComments", getEnableComments());
		attributes.put("enableRatings", getEnableRatings());
		attributes.put("lastPublishDate", getLastPublishDate());

		attributes.put("entityCacheEnabled", isEntityCacheEnabled());
		attributes.put("finderCacheEnabled", isFinderCacheEnabled());

		return attributes;
	}

	@Override
	public void setModelAttributes(Map<String, Object> attributes) {
		String uuid = (String)attributes.get("uuid");

		if (uuid != null) {
			setUuid(uuid);
		}

		Long calendarId = (Long)attributes.get("calendarId");

		if (calendarId != null) {
			setCalendarId(calendarId);
		}

		Long groupId = (Long)attributes.get("groupId");

		if (groupId != null) {
			setGroupId(groupId);
		}

		Long companyId = (Long)attributes.get("companyId");

		if (companyId != null) {
			setCompanyId(companyId);
		}

		Long userId = (Long)attributes.get("userId");

		if (userId != null) {
			setUserId(userId);
		}

		String userName = (String)attributes.get("userName");

		if (userName != null) {
			setUserName(userName);
		}

		Date createDate = (Date)attributes.get("createDate");

		if (createDate != null) {
			setCreateDate(createDate);
		}

		Date modifiedDate = (Date)attributes.get("modifiedDate");

		if (modifiedDate != null) {
			setModifiedDate(modifiedDate);
		}

		Long resourceBlockId = (Long)attributes.get("resourceBlockId");

		if (resourceBlockId != null) {
			setResourceBlockId(resourceBlockId);
		}

		Long calendarResourceId = (Long)attributes.get("calendarResourceId");

		if (calendarResourceId != null) {
			setCalendarResourceId(calendarResourceId);
		}

		String name = (String)attributes.get("name");

		if (name != null) {
			setName(name);
		}

		String description = (String)attributes.get("description");

		if (description != null) {
			setDescription(description);
		}

		String timeZoneId = (String)attributes.get("timeZoneId");

		if (timeZoneId != null) {
			setTimeZoneId(timeZoneId);
		}

		Integer color = (Integer)attributes.get("color");

		if (color != null) {
			setColor(color);
		}

		Boolean defaultCalendar = (Boolean)attributes.get("defaultCalendar");

		if (defaultCalendar != null) {
			setDefaultCalendar(defaultCalendar);
		}

		Boolean enableComments = (Boolean)attributes.get("enableComments");

		if (enableComments != null) {
			setEnableComments(enableComments);
		}

		Boolean enableRatings = (Boolean)attributes.get("enableRatings");

		if (enableRatings != null) {
			setEnableRatings(enableRatings);
		}

		Date lastPublishDate = (Date)attributes.get("lastPublishDate");

		if (lastPublishDate != null) {
			setLastPublishDate(lastPublishDate);
		}
	}

	@JSON
	@Override
	public String getUuid() {
		if (_uuid == null) {
			return StringPool.BLANK;
		}
		else {
			return _uuid;
		}
	}

	@Override
	public void setUuid(String uuid) {
		if (_originalUuid == null) {
			_originalUuid = _uuid;
		}

		_uuid = uuid;
	}

	public String getOriginalUuid() {
		return GetterUtil.getString(_originalUuid);
	}

	@JSON
	@Override
	public long getCalendarId() {
		return _calendarId;
	}

	@Override
	public void setCalendarId(long calendarId) {
		_calendarId = calendarId;
	}

	@JSON
	@Override
	public long getGroupId() {
		return _groupId;
	}

	@Override
	public void setGroupId(long groupId) {
		_columnBitmask |= GROUPID_COLUMN_BITMASK;

		if (!_setOriginalGroupId) {
			_setOriginalGroupId = true;

			_originalGroupId = _groupId;
		}

		_groupId = groupId;
	}

	public long getOriginalGroupId() {
		return _originalGroupId;
	}

	@JSON
	@Override
	public long getCompanyId() {
		return _companyId;
	}

	@Override
	public void setCompanyId(long companyId) {
		_columnBitmask |= COMPANYID_COLUMN_BITMASK;

		if (!_setOriginalCompanyId) {
			_setOriginalCompanyId = true;

			_originalCompanyId = _companyId;
		}

		_companyId = companyId;
	}

	public long getOriginalCompanyId() {
		return _originalCompanyId;
	}

	@JSON
	@Override
	public long getUserId() {
		return _userId;
	}

	@Override
	public void setUserId(long userId) {
		_userId = userId;
	}

	@Override
	public String getUserUuid() {
		try {
			User user = UserLocalServiceUtil.getUserById(getUserId());

			return user.getUuid();
		}
		catch (PortalException pe) {
			return StringPool.BLANK;
		}
	}

	@Override
	public void setUserUuid(String userUuid) {
	}

	@JSON
	@Override
	public String getUserName() {
		if (_userName == null) {
			return StringPool.BLANK;
		}
		else {
			return _userName;
		}
	}

	@Override
	public void setUserName(String userName) {
		_userName = userName;
	}

	@JSON
	@Override
	public Date getCreateDate() {
		return _createDate;
	}

	@Override
	public void setCreateDate(Date createDate) {
		_createDate = createDate;
	}

	@JSON
	@Override
	public Date getModifiedDate() {
		return _modifiedDate;
	}

	public boolean hasSetModifiedDate() {
		return _setModifiedDate;
	}

	@Override
	public void setModifiedDate(Date modifiedDate) {
		_setModifiedDate = true;

		_modifiedDate = modifiedDate;
	}

	@JSON
	@Override
	public long getResourceBlockId() {
		return _resourceBlockId;
	}

	@Override
	public void setResourceBlockId(long resourceBlockId) {
		_columnBitmask |= RESOURCEBLOCKID_COLUMN_BITMASK;

		if (!_setOriginalResourceBlockId) {
			_setOriginalResourceBlockId = true;

			_originalResourceBlockId = _resourceBlockId;
		}

		_resourceBlockId = resourceBlockId;
	}

	public long getOriginalResourceBlockId() {
		return _originalResourceBlockId;
	}

	@JSON
	@Override
	public long getCalendarResourceId() {
		return _calendarResourceId;
	}

	@Override
	public void setCalendarResourceId(long calendarResourceId) {
		_columnBitmask |= CALENDARRESOURCEID_COLUMN_BITMASK;

		if (!_setOriginalCalendarResourceId) {
			_setOriginalCalendarResourceId = true;

			_originalCalendarResourceId = _calendarResourceId;
		}

		_calendarResourceId = calendarResourceId;
	}

	public long getOriginalCalendarResourceId() {
		return _originalCalendarResourceId;
	}

	@JSON
	@Override
	public String getName() {
		if (_name == null) {
			return StringPool.BLANK;
		}
		else {
			return _name;
		}
	}

	@Override
	public String getName(Locale locale) {
		String languageId = LocaleUtil.toLanguageId(locale);

		return getName(languageId);
	}

	@Override
	public String getName(Locale locale, boolean useDefault) {
		String languageId = LocaleUtil.toLanguageId(locale);

		return getName(languageId, useDefault);
	}

	@Override
	public String getName(String languageId) {
		return LocalizationUtil.getLocalization(getName(), languageId);
	}

	@Override
	public String getName(String languageId, boolean useDefault) {
		return LocalizationUtil.getLocalization(getName(), languageId,
			useDefault);
	}

	@Override
	public String getNameCurrentLanguageId() {
		return _nameCurrentLanguageId;
	}

	@JSON
	@Override
	public String getNameCurrentValue() {
		Locale locale = getLocale(_nameCurrentLanguageId);

		return getName(locale);
	}

	@Override
	public Map<Locale, String> getNameMap() {
		return LocalizationUtil.getLocalizationMap(getName());
	}

	@Override
	public void setName(String name) {
		_columnBitmask = -1L;

		_name = name;
	}

	@Override
	public void setName(String name, Locale locale) {
		setName(name, locale, LocaleUtil.getSiteDefault());
	}

	@Override
	public void setName(String name, Locale locale, Locale defaultLocale) {
		String languageId = LocaleUtil.toLanguageId(locale);
		String defaultLanguageId = LocaleUtil.toLanguageId(defaultLocale);

		if (Validator.isNotNull(name)) {
			setName(LocalizationUtil.updateLocalization(getName(), "Name",
					name, languageId, defaultLanguageId));
		}
		else {
			setName(LocalizationUtil.removeLocalization(getName(), "Name",
					languageId));
		}
	}

	@Override
	public void setNameCurrentLanguageId(String languageId) {
		_nameCurrentLanguageId = languageId;
	}

	@Override
	public void setNameMap(Map<Locale, String> nameMap) {
		setNameMap(nameMap, LocaleUtil.getSiteDefault());
	}

	@Override
	public void setNameMap(Map<Locale, String> nameMap, Locale defaultLocale) {
		if (nameMap == null) {
			return;
		}

		setName(LocalizationUtil.updateLocalization(nameMap, getName(), "Name",
				LocaleUtil.toLanguageId(defaultLocale)));
	}

	@JSON
	@Override
	public String getDescription() {
		if (_description == null) {
			return StringPool.BLANK;
		}
		else {
			return _description;
		}
	}

	@Override
	public String getDescription(Locale locale) {
		String languageId = LocaleUtil.toLanguageId(locale);

		return getDescription(languageId);
	}

	@Override
	public String getDescription(Locale locale, boolean useDefault) {
		String languageId = LocaleUtil.toLanguageId(locale);

		return getDescription(languageId, useDefault);
	}

	@Override
	public String getDescription(String languageId) {
		return LocalizationUtil.getLocalization(getDescription(), languageId);
	}

	@Override
	public String getDescription(String languageId, boolean useDefault) {
		return LocalizationUtil.getLocalization(getDescription(), languageId,
			useDefault);
	}

	@Override
	public String getDescriptionCurrentLanguageId() {
		return _descriptionCurrentLanguageId;
	}

	@JSON
	@Override
	public String getDescriptionCurrentValue() {
		Locale locale = getLocale(_descriptionCurrentLanguageId);

		return getDescription(locale);
	}

	@Override
	public Map<Locale, String> getDescriptionMap() {
		return LocalizationUtil.getLocalizationMap(getDescription());
	}

	@Override
	public void setDescription(String description) {
		_description = description;
	}

	@Override
	public void setDescription(String description, Locale locale) {
		setDescription(description, locale, LocaleUtil.getSiteDefault());
	}

	@Override
	public void setDescription(String description, Locale locale,
		Locale defaultLocale) {
		String languageId = LocaleUtil.toLanguageId(locale);
		String defaultLanguageId = LocaleUtil.toLanguageId(defaultLocale);

		if (Validator.isNotNull(description)) {
			setDescription(LocalizationUtil.updateLocalization(
					getDescription(), "Description", description, languageId,
					defaultLanguageId));
		}
		else {
			setDescription(LocalizationUtil.removeLocalization(
					getDescription(), "Description", languageId));
		}
	}

	@Override
	public void setDescriptionCurrentLanguageId(String languageId) {
		_descriptionCurrentLanguageId = languageId;
	}

	@Override
	public void setDescriptionMap(Map<Locale, String> descriptionMap) {
		setDescriptionMap(descriptionMap, LocaleUtil.getSiteDefault());
	}

	@Override
	public void setDescriptionMap(Map<Locale, String> descriptionMap,
		Locale defaultLocale) {
		if (descriptionMap == null) {
			return;
		}

		setDescription(LocalizationUtil.updateLocalization(descriptionMap,
				getDescription(), "Description",
				LocaleUtil.toLanguageId(defaultLocale)));
	}

	@JSON
	@Override
	public String getTimeZoneId() {
		if (_timeZoneId == null) {
			return StringPool.BLANK;
		}
		else {
			return _timeZoneId;
		}
	}

	@Override
	public void setTimeZoneId(String timeZoneId) {
		_timeZoneId = timeZoneId;
	}

	@JSON
	@Override
	public int getColor() {
		return _color;
	}

	@Override
	public void setColor(int color) {
		_color = color;
	}

	@JSON
	@Override
	public boolean getDefaultCalendar() {
		return _defaultCalendar;
	}

	@Override
	public boolean isDefaultCalendar() {
		return _defaultCalendar;
	}

	@Override
	public void setDefaultCalendar(boolean defaultCalendar) {
		_columnBitmask |= DEFAULTCALENDAR_COLUMN_BITMASK;

		if (!_setOriginalDefaultCalendar) {
			_setOriginalDefaultCalendar = true;

			_originalDefaultCalendar = _defaultCalendar;
		}

		_defaultCalendar = defaultCalendar;
	}

	public boolean getOriginalDefaultCalendar() {
		return _originalDefaultCalendar;
	}

	@JSON
	@Override
	public boolean getEnableComments() {
		return _enableComments;
	}

	@Override
	public boolean isEnableComments() {
		return _enableComments;
	}

	@Override
	public void setEnableComments(boolean enableComments) {
		_enableComments = enableComments;
	}

	@JSON
	@Override
	public boolean getEnableRatings() {
		return _enableRatings;
	}

	@Override
	public boolean isEnableRatings() {
		return _enableRatings;
	}

	@Override
	public void setEnableRatings(boolean enableRatings) {
		_enableRatings = enableRatings;
	}

	@JSON
	@Override
	public Date getLastPublishDate() {
		return _lastPublishDate;
	}

	@Override
	public void setLastPublishDate(Date lastPublishDate) {
		_lastPublishDate = lastPublishDate;
	}

	@Override
	public StagedModelType getStagedModelType() {
		return new StagedModelType(PortalUtil.getClassNameId(
				Calendar.class.getName()));
	}

	public long getColumnBitmask() {
		return _columnBitmask;
	}

	@Override
	public ExpandoBridge getExpandoBridge() {
		return ExpandoBridgeFactoryUtil.getExpandoBridge(getCompanyId(),
			Calendar.class.getName(), getPrimaryKey());
	}

	@Override
	public void setExpandoBridgeAttributes(ServiceContext serviceContext) {
		ExpandoBridge expandoBridge = getExpandoBridge();

		expandoBridge.setAttributes(serviceContext);
	}

	@Override
	public String[] getAvailableLanguageIds() {
		Set<String> availableLanguageIds = new TreeSet<String>();

		Map<Locale, String> nameMap = getNameMap();

		for (Map.Entry<Locale, String> entry : nameMap.entrySet()) {
			Locale locale = entry.getKey();
			String value = entry.getValue();

			if (Validator.isNotNull(value)) {
				availableLanguageIds.add(LocaleUtil.toLanguageId(locale));
			}
		}

		Map<Locale, String> descriptionMap = getDescriptionMap();

		for (Map.Entry<Locale, String> entry : descriptionMap.entrySet()) {
			Locale locale = entry.getKey();
			String value = entry.getValue();

			if (Validator.isNotNull(value)) {
				availableLanguageIds.add(LocaleUtil.toLanguageId(locale));
			}
		}

		return availableLanguageIds.toArray(new String[availableLanguageIds.size()]);
	}

	@Override
	public String getDefaultLanguageId() {
		String xml = getName();

		if (xml == null) {
			return StringPool.BLANK;
		}

		Locale defaultLocale = LocaleUtil.getSiteDefault();

		return LocalizationUtil.getDefaultLanguageId(xml, defaultLocale);
	}

	@Override
	public void prepareLocalizedFieldsForImport() throws LocaleException {
		Locale defaultLocale = LocaleUtil.fromLanguageId(getDefaultLanguageId());

		Locale[] availableLocales = LocaleUtil.fromLanguageIds(getAvailableLanguageIds());

		Locale defaultImportLocale = LocalizationUtil.getDefaultImportLocale(Calendar.class.getName(),
				getPrimaryKey(), defaultLocale, availableLocales);

		prepareLocalizedFieldsForImport(defaultImportLocale);
	}

	@Override
	@SuppressWarnings("unused")
	public void prepareLocalizedFieldsForImport(Locale defaultImportLocale)
		throws LocaleException {
		Locale defaultLocale = LocaleUtil.getSiteDefault();

		String modelDefaultLanguageId = getDefaultLanguageId();

		String name = getName(defaultLocale);

		if (Validator.isNull(name)) {
			setName(getName(modelDefaultLanguageId), defaultLocale);
		}
		else {
			setName(getName(defaultLocale), defaultLocale, defaultLocale);
		}

		String description = getDescription(defaultLocale);

		if (Validator.isNull(description)) {
			setDescription(getDescription(modelDefaultLanguageId), defaultLocale);
		}
		else {
			setDescription(getDescription(defaultLocale), defaultLocale,
				defaultLocale);
		}
	}

	@Override
	public Calendar toEscapedModel() {
		if (_escapedModel == null) {
			_escapedModel = (Calendar)ProxyUtil.newProxyInstance(_classLoader,
					_escapedModelInterfaces, new AutoEscapeBeanHandler(this));
		}

		return _escapedModel;
	}

	@Override
	public Object clone() {
		CalendarImpl calendarImpl = new CalendarImpl();

		calendarImpl.setUuid(getUuid());
		calendarImpl.setCalendarId(getCalendarId());
		calendarImpl.setGroupId(getGroupId());
		calendarImpl.setCompanyId(getCompanyId());
		calendarImpl.setUserId(getUserId());
		calendarImpl.setUserName(getUserName());
		calendarImpl.setCreateDate(getCreateDate());
		calendarImpl.setModifiedDate(getModifiedDate());
		calendarImpl.setResourceBlockId(getResourceBlockId());
		calendarImpl.setCalendarResourceId(getCalendarResourceId());
		calendarImpl.setName(getName());
		calendarImpl.setDescription(getDescription());
		calendarImpl.setTimeZoneId(getTimeZoneId());
		calendarImpl.setColor(getColor());
		calendarImpl.setDefaultCalendar(getDefaultCalendar());
		calendarImpl.setEnableComments(getEnableComments());
		calendarImpl.setEnableRatings(getEnableRatings());
		calendarImpl.setLastPublishDate(getLastPublishDate());

		calendarImpl.resetOriginalValues();

		return calendarImpl;
	}

	@Override
	public int compareTo(Calendar calendar) {
		int value = 0;

		value = getName().compareTo(calendar.getName());

		if (value != 0) {
			return value;
		}

		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Calendar)) {
			return false;
		}

		Calendar calendar = (Calendar)obj;

		long primaryKey = calendar.getPrimaryKey();

		if (getPrimaryKey() == primaryKey) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return (int)getPrimaryKey();
	}

	@Override
	public boolean isEntityCacheEnabled() {
		return ENTITY_CACHE_ENABLED;
	}

	@Override
	public boolean isFinderCacheEnabled() {
		return FINDER_CACHE_ENABLED;
	}

	@Override
	public void resetOriginalValues() {
		CalendarModelImpl calendarModelImpl = this;

		calendarModelImpl._originalUuid = calendarModelImpl._uuid;

		calendarModelImpl._originalGroupId = calendarModelImpl._groupId;

		calendarModelImpl._setOriginalGroupId = false;

		calendarModelImpl._originalCompanyId = calendarModelImpl._companyId;

		calendarModelImpl._setOriginalCompanyId = false;

		calendarModelImpl._setModifiedDate = false;

		calendarModelImpl._originalResourceBlockId = calendarModelImpl._resourceBlockId;

		calendarModelImpl._setOriginalResourceBlockId = false;

		calendarModelImpl._originalCalendarResourceId = calendarModelImpl._calendarResourceId;

		calendarModelImpl._setOriginalCalendarResourceId = false;

		calendarModelImpl._originalDefaultCalendar = calendarModelImpl._defaultCalendar;

		calendarModelImpl._setOriginalDefaultCalendar = false;

		calendarModelImpl._columnBitmask = 0;
	}

	@Override
	public CacheModel<Calendar> toCacheModel() {
		CalendarCacheModel calendarCacheModel = new CalendarCacheModel();

		calendarCacheModel.uuid = getUuid();

		String uuid = calendarCacheModel.uuid;

		if ((uuid != null) && (uuid.length() == 0)) {
			calendarCacheModel.uuid = null;
		}

		calendarCacheModel.calendarId = getCalendarId();

		calendarCacheModel.groupId = getGroupId();

		calendarCacheModel.companyId = getCompanyId();

		calendarCacheModel.userId = getUserId();

		calendarCacheModel.userName = getUserName();

		String userName = calendarCacheModel.userName;

		if ((userName != null) && (userName.length() == 0)) {
			calendarCacheModel.userName = null;
		}

		Date createDate = getCreateDate();

		if (createDate != null) {
			calendarCacheModel.createDate = createDate.getTime();
		}
		else {
			calendarCacheModel.createDate = Long.MIN_VALUE;
		}

		Date modifiedDate = getModifiedDate();

		if (modifiedDate != null) {
			calendarCacheModel.modifiedDate = modifiedDate.getTime();
		}
		else {
			calendarCacheModel.modifiedDate = Long.MIN_VALUE;
		}

		calendarCacheModel.resourceBlockId = getResourceBlockId();

		calendarCacheModel.calendarResourceId = getCalendarResourceId();

		calendarCacheModel.name = getName();

		String name = calendarCacheModel.name;

		if ((name != null) && (name.length() == 0)) {
			calendarCacheModel.name = null;
		}

		calendarCacheModel.description = getDescription();

		String description = calendarCacheModel.description;

		if ((description != null) && (description.length() == 0)) {
			calendarCacheModel.description = null;
		}

		calendarCacheModel.timeZoneId = getTimeZoneId();

		String timeZoneId = calendarCacheModel.timeZoneId;

		if ((timeZoneId != null) && (timeZoneId.length() == 0)) {
			calendarCacheModel.timeZoneId = null;
		}

		calendarCacheModel.color = getColor();

		calendarCacheModel.defaultCalendar = getDefaultCalendar();

		calendarCacheModel.enableComments = getEnableComments();

		calendarCacheModel.enableRatings = getEnableRatings();

		Date lastPublishDate = getLastPublishDate();

		if (lastPublishDate != null) {
			calendarCacheModel.lastPublishDate = lastPublishDate.getTime();
		}
		else {
			calendarCacheModel.lastPublishDate = Long.MIN_VALUE;
		}

		return calendarCacheModel;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(37);

		sb.append("{uuid=");
		sb.append(getUuid());
		sb.append(", calendarId=");
		sb.append(getCalendarId());
		sb.append(", groupId=");
		sb.append(getGroupId());
		sb.append(", companyId=");
		sb.append(getCompanyId());
		sb.append(", userId=");
		sb.append(getUserId());
		sb.append(", userName=");
		sb.append(getUserName());
		sb.append(", createDate=");
		sb.append(getCreateDate());
		sb.append(", modifiedDate=");
		sb.append(getModifiedDate());
		sb.append(", resourceBlockId=");
		sb.append(getResourceBlockId());
		sb.append(", calendarResourceId=");
		sb.append(getCalendarResourceId());
		sb.append(", name=");
		sb.append(getName());
		sb.append(", description=");
		sb.append(getDescription());
		sb.append(", timeZoneId=");
		sb.append(getTimeZoneId());
		sb.append(", color=");
		sb.append(getColor());
		sb.append(", defaultCalendar=");
		sb.append(getDefaultCalendar());
		sb.append(", enableComments=");
		sb.append(getEnableComments());
		sb.append(", enableRatings=");
		sb.append(getEnableRatings());
		sb.append(", lastPublishDate=");
		sb.append(getLastPublishDate());
		sb.append("}");

		return sb.toString();
	}

	@Override
	public String toXmlString() {
		StringBundler sb = new StringBundler(58);

		sb.append("<model><model-name>");
		sb.append("com.liferay.calendar.model.Calendar");
		sb.append("</model-name>");

		sb.append(
			"<column><column-name>uuid</column-name><column-value><![CDATA[");
		sb.append(getUuid());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>calendarId</column-name><column-value><![CDATA[");
		sb.append(getCalendarId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>groupId</column-name><column-value><![CDATA[");
		sb.append(getGroupId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>companyId</column-name><column-value><![CDATA[");
		sb.append(getCompanyId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>userId</column-name><column-value><![CDATA[");
		sb.append(getUserId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>userName</column-name><column-value><![CDATA[");
		sb.append(getUserName());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>createDate</column-name><column-value><![CDATA[");
		sb.append(getCreateDate());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>modifiedDate</column-name><column-value><![CDATA[");
		sb.append(getModifiedDate());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>resourceBlockId</column-name><column-value><![CDATA[");
		sb.append(getResourceBlockId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>calendarResourceId</column-name><column-value><![CDATA[");
		sb.append(getCalendarResourceId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>name</column-name><column-value><![CDATA[");
		sb.append(getName());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>description</column-name><column-value><![CDATA[");
		sb.append(getDescription());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>timeZoneId</column-name><column-value><![CDATA[");
		sb.append(getTimeZoneId());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>color</column-name><column-value><![CDATA[");
		sb.append(getColor());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>defaultCalendar</column-name><column-value><![CDATA[");
		sb.append(getDefaultCalendar());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>enableComments</column-name><column-value><![CDATA[");
		sb.append(getEnableComments());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>enableRatings</column-name><column-value><![CDATA[");
		sb.append(getEnableRatings());
		sb.append("]]></column-value></column>");
		sb.append(
			"<column><column-name>lastPublishDate</column-name><column-value><![CDATA[");
		sb.append(getLastPublishDate());
		sb.append("]]></column-value></column>");

		sb.append("</model>");

		return sb.toString();
	}

	private static final ClassLoader _classLoader = Calendar.class.getClassLoader();
	private static final Class<?>[] _escapedModelInterfaces = new Class[] {
			Calendar.class
		};
	private String _uuid;
	private String _originalUuid;
	private long _calendarId;
	private long _groupId;
	private long _originalGroupId;
	private boolean _setOriginalGroupId;
	private long _companyId;
	private long _originalCompanyId;
	private boolean _setOriginalCompanyId;
	private long _userId;
	private String _userName;
	private Date _createDate;
	private Date _modifiedDate;
	private boolean _setModifiedDate;
	private long _resourceBlockId;
	private long _originalResourceBlockId;
	private boolean _setOriginalResourceBlockId;
	private long _calendarResourceId;
	private long _originalCalendarResourceId;
	private boolean _setOriginalCalendarResourceId;
	private String _name;
	private String _nameCurrentLanguageId;
	private String _description;
	private String _descriptionCurrentLanguageId;
	private String _timeZoneId;
	private int _color;
	private boolean _defaultCalendar;
	private boolean _originalDefaultCalendar;
	private boolean _setOriginalDefaultCalendar;
	private boolean _enableComments;
	private boolean _enableRatings;
	private Date _lastPublishDate;
	private long _columnBitmask;
	private Calendar _escapedModel;
}