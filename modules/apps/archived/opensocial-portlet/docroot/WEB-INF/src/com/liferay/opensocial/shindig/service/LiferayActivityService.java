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

package com.liferay.opensocial.shindig.service;

import com.liferay.opensocial.shindig.util.HttpServletRequestThreadLocal;
import com.liferay.opensocial.shindig.util.SerializerUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.social.kernel.model.SocialActivity;
import com.liferay.social.kernel.model.SocialActivityFeedEntry;
import com.liferay.social.kernel.service.SocialActivityInterpreterLocalServiceUtil;
import com.liferay.social.kernel.service.SocialActivityLocalServiceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shindig.auth.SecurityToken;
import org.apache.shindig.common.util.ImmediateFuture;
import org.apache.shindig.protocol.ProtocolException;
import org.apache.shindig.protocol.RestfulCollection;
import org.apache.shindig.social.core.model.ActivityImpl;
import org.apache.shindig.social.core.model.MediaItemImpl;
import org.apache.shindig.social.opensocial.model.Activity;
import org.apache.shindig.social.opensocial.model.MediaItem;
import org.apache.shindig.social.opensocial.spi.ActivityService;
import org.apache.shindig.social.opensocial.spi.CollectionOptions;
import org.apache.shindig.social.opensocial.spi.GroupId;
import org.apache.shindig.social.opensocial.spi.UserId;

/**
 * @author Michael Young
 */
public class LiferayActivityService implements ActivityService {

	public Future<Void> createActivity(
			UserId userId, GroupId groupId, String appId, Set<String> fields,
			Activity activity, SecurityToken securityToken)
		throws ProtocolException {

		try {
			doCreateActivity(
				userId, groupId, appId, fields, activity, securityToken);

			return ImmediateFuture.newInstance(null);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception, exception);
			}

			throw new ProtocolException(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				exception.getMessage(), exception);
		}
	}

	public Future<Void> deleteActivities(
			UserId userId, GroupId groupId, String appId,
			Set<String> activityIds, SecurityToken securityToken)
		throws ProtocolException {

		try {
			doDeleteActivities(
				userId, groupId, appId, activityIds, securityToken);

			return ImmediateFuture.newInstance(null);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception, exception);
			}

			throw new ProtocolException(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				exception.getMessage(), exception);
		}
	}

	public void doCreateActivity(
			UserId userId, GroupId groupId, String appId, Set<String> fields,
			Activity activity, SecurityToken securityToken)
		throws Exception {

		long userIdLong = GetterUtil.getLong(userId.getUserId(securityToken));

		String activityAppId = activity.getAppId();

		JSONObject extraDataJSONObject = JSONFactoryUtil.createJSONObject();

		SerializerUtil.copyProperties(
			activity, extraDataJSONObject, _ACTIVITY_FIELDS);

		SocialActivityLocalServiceUtil.addActivity(
			userIdLong, 0L, Activity.class.getName(), activity.getPostedTime(),
			activityAppId.hashCode(), extraDataJSONObject.toString(), 0L);
	}

	public void doDeleteActivities(
			UserId userId, GroupId groupId, String appId,
			Set<String> activityIds, SecurityToken securityToken)
		throws Exception {

		for (String activityId : activityIds) {
			long activityIdLong = GetterUtil.getLong(activityId);

			SocialActivityLocalServiceUtil.deleteActivity(activityIdLong);
		}
	}

	public RestfulCollection<Activity> doGetActivities(
			Set<UserId> userIds, GroupId groupId, String appId,
			Set<String> fields, CollectionOptions collectionOptions,
			SecurityToken securityToken)
		throws Exception {

		ThemeDisplay themeDisplay = getThemeDisplay(securityToken);

		List<Activity> activities = new ArrayList<>();

		for (UserId userId : userIds) {
			long userIdLong = GetterUtil.getLong(
				userId.getUserId(securityToken));

			List<Activity> personActivities = getActivities(
				themeDisplay, userIdLong);

			activities.addAll(personActivities);
		}

		return new RestfulCollection<>(
			activities, collectionOptions.getFirst(), activities.size(),
			collectionOptions.getMax());
	}

	public RestfulCollection<Activity> doGetActivities(
			UserId userId, GroupId groupId, String appId, Set<String> fields,
			CollectionOptions collectionOptions, Set<String> activityIds,
			SecurityToken securityToken)
		throws Exception {

		ThemeDisplay themeDisplay = getThemeDisplay(securityToken);

		long userIdLong = GetterUtil.getLong(userId.getUserId(securityToken));

		List<Activity> activities = getActivities(themeDisplay, userIdLong);

		return new RestfulCollection<>(
			activities, collectionOptions.getFirst(), activities.size(),
			collectionOptions.getMax());
	}

	public Activity doGetActivity(
			UserId userId, GroupId groupId, String appId, Set<String> fields,
			String activityId, SecurityToken securityToken)
		throws Exception {

		ThemeDisplay themeDisplay = getThemeDisplay(securityToken);

		long activityIdLong = GetterUtil.getLong(activityId);

		SocialActivity socialActivity =
			SocialActivityLocalServiceUtil.getActivity(activityIdLong);

		return getActivity(themeDisplay, socialActivity);
	}

	public Future<RestfulCollection<Activity>> getActivities(
			Set<UserId> userIds, GroupId groupId, String appId,
			Set<String> fields, CollectionOptions collectionOptions,
			SecurityToken securityToken)
		throws ProtocolException {

		try {
			RestfulCollection<Activity> activities = doGetActivities(
				userIds, groupId, appId, fields, collectionOptions,
				securityToken);

			return ImmediateFuture.newInstance(activities);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception, exception);
			}

			throw new ProtocolException(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				exception.getMessage(), exception);
		}
	}

	public Future<RestfulCollection<Activity>> getActivities(
			UserId userId, GroupId groupId, String appId, Set<String> fields,
			CollectionOptions collectionOptions, Set<String> activityIds,
			SecurityToken securityToken)
		throws ProtocolException {

		try {
			RestfulCollection<Activity> activities = doGetActivities(
				userId, groupId, appId, fields, collectionOptions, activityIds,
				securityToken);

			return ImmediateFuture.newInstance(activities);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception, exception);
			}

			throw new ProtocolException(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				exception.getMessage(), exception);
		}
	}

	public Future<Activity> getActivity(
			UserId userId, GroupId groupId, String appId, Set<String> fields,
			String activityId, SecurityToken securityToken)
		throws ProtocolException {

		try {
			Activity activity = doGetActivity(
				userId, groupId, appId, fields, activityId, securityToken);

			return ImmediateFuture.newInstance(activity);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception, exception);
			}

			throw new ProtocolException(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
				exception.getMessage(), exception);
		}
	}

	protected List<Activity> getActivities(
			ThemeDisplay themeDisplay, long userId)
		throws Exception {

		List<Activity> activities = new ArrayList<>();

		List<SocialActivity> socialActivities =
			SocialActivityLocalServiceUtil.getUserActivities(userId, 0, 20);

		for (SocialActivity socialActivity : socialActivities) {
			activities.add(getActivity(themeDisplay, socialActivity));
		}

		return activities;
	}

	protected Activity getActivity(
			ThemeDisplay themeDisplay, SocialActivity socialActivity)
		throws Exception {

		Activity activity = null;

		String className = socialActivity.getClassName();

		if (className.equals(Activity.class.getName())) {
			activity = getExternalActivity(socialActivity);
		}
		else {
			activity = new ActivityImpl(
				String.valueOf(socialActivity.getClassPK()),
				String.valueOf(socialActivity.getUserId()));

			HttpServletRequest httpServletRequest =
				HttpServletRequestThreadLocal.getHttpServletRequest();

			httpServletRequest.setAttribute(
				WebKeys.THEME_DISPLAY, themeDisplay);

			ServiceContext serviceContext = ServiceContextFactory.getInstance(
				httpServletRequest);

			serviceContext.setCompanyId(themeDisplay.getCompanyId());
			serviceContext.setUserId(themeDisplay.getUserId());

			SocialActivityFeedEntry socialActivityFeedEntry =
				SocialActivityInterpreterLocalServiceUtil.interpret(
					StringPool.BLANK, socialActivity, serviceContext);

			activity.setBody(socialActivityFeedEntry.getBody());
			activity.setTitle(socialActivityFeedEntry.getTitle());
			activity.setUrl(socialActivityFeedEntry.getLink());
		}

		return activity;
	}

	protected Activity getExternalActivity(SocialActivity socialActivity)
		throws Exception {

		Activity activity = new ActivityImpl(
			String.valueOf(socialActivity.getClassPK()),
			String.valueOf(socialActivity.getUserId()));

		JSONObject extraDataJSONObject = JSONFactoryUtil.createJSONObject(
			socialActivity.getExtraData());

		SerializerUtil.copyProperties(
			extraDataJSONObject, activity, _ACTIVITY_FIELDS);

		return activity;
	}

	protected List<MediaItem> getMediaItems(JSONArray mediaItemsJSONArray) {
		if (mediaItemsJSONArray == null) {
			return null;
		}

		List<MediaItem> mediaItems = new ArrayList<>();

		for (int i = 0; i < mediaItemsJSONArray.length(); i++) {
			JSONObject mediaItemsJSONObject = mediaItemsJSONArray.getJSONObject(
				i);

			MediaItem mediaItem = new MediaItemImpl(
				mediaItemsJSONObject.getString("mimeType"),
				MediaItem.Type.valueOf(mediaItemsJSONObject.getString("type")),
				mediaItemsJSONObject.getString("url"));

			mediaItems.add(mediaItem);
		}

		return mediaItems;
	}

	protected JSONArray getMediaItems(List<MediaItem> mediaItems) {
		if (mediaItems == null) {
			return null;
		}

		JSONArray mediaItemsJSONArray = JSONFactoryUtil.createJSONArray();

		for (MediaItem mediaItem : mediaItems) {
			mediaItemsJSONArray.put(
				JSONUtil.put(
					"mimeType", mediaItem.getMimeType()
				).put(
					"type", String.valueOf(mediaItem.getType())
				).put(
					"url", mediaItem.getUrl()
				));
		}

		return mediaItemsJSONArray;
	}

	protected Map<String, String> getTemplateParams(
		JSONArray templateParamsJSONArray) {

		if (templateParamsJSONArray == null) {
			return null;
		}

		Map<String, String> templateParams = new HashMap<>();

		for (int i = 0; i < templateParamsJSONArray.length(); i++) {
			JSONObject templateParamJSONObject =
				templateParamsJSONArray.getJSONObject(i);

			JSONArray namesJSONArray = templateParamJSONObject.names();

			for (int j = 0; j < namesJSONArray.length(); j++) {
				String name = namesJSONArray.getString(j);

				String value = templateParamJSONObject.getString(name);

				templateParams.put(name, value);
			}
		}

		return templateParams;
	}

	protected JSONArray getTemplateParams(Map<String, String> map) {
		if (map == null) {
			return null;
		}

		JSONArray templateParamsJSONArray = JSONFactoryUtil.createJSONArray();

		for (Map.Entry<String, String> entry : map.entrySet()) {
			String name = entry.getKey();

			JSONObject templateParamJSONObject = JSONUtil.put(
				name, entry.getValue());

			templateParamsJSONArray.put(templateParamJSONObject);
		}

		return templateParamsJSONArray;
	}

	protected ThemeDisplay getThemeDisplay(SecurityToken securityToken)
		throws Exception {

		long userIdLong = GetterUtil.getLong(securityToken.getViewerId());

		User user = UserLocalServiceUtil.getUserById(userIdLong);

		Company company = CompanyLocalServiceUtil.getCompanyById(
			user.getCompanyId());

		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(company);
		themeDisplay.setLocale(user.getLocale());
		themeDisplay.setUser(user);

		return themeDisplay;
	}

	private static final Activity.Field[] _ACTIVITY_FIELDS = {
		Activity.Field.APP_ID, Activity.Field.BODY, Activity.Field.BODY_ID,
		Activity.Field.EXTERNAL_ID, Activity.Field.MEDIA_ITEMS,
		Activity.Field.POSTED_TIME, Activity.Field.PRIORITY,
		Activity.Field.STREAM_FAVICON_URL, Activity.Field.STREAM_SOURCE_URL,
		Activity.Field.STREAM_TITLE, Activity.Field.STREAM_URL,
		Activity.Field.TEMPLATE_PARAMS, Activity.Field.TITLE,
		Activity.Field.TITLE_ID, Activity.Field.URL
	};

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayActivityService.class);

}