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

package com.liferay.commerce.channel.web.internal.health.status;

import com.liferay.commerce.constants.CommerceHealthStatusConstants;
import com.liferay.commerce.product.channel.CommerceChannelHealthStatus;
import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.constants.CommerceChannelConstants;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CommerceChannelService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTemplate;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	enabled = false, immediate = true,
	property = {
		"commerce.channel.health.status.display.order:Integer=10",
		"commerce.channel.health.status.key=" + CommerceHealthStatusConstants.CP_CONTENT_COMMERCE_HEALTH_STATUS_KEY
	},
	service = CommerceChannelHealthStatus.class
)
public class CPContentHealthStatus implements CommerceChannelHealthStatus {

	@Override
	public void fixIssue(long companyId, long commerceChannelId)
		throws PortalException {

		if (isFixed(companyId, commerceChannelId)) {
			return;
		}

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(commerceChannelId);

		String name = "Product";

		String friendlyURL =
			StringPool.FORWARD_SLASH + StringUtil.toLowerCase(name);

		boolean privateLayout = true;

		List<Layout> layouts = _layoutService.getLayouts(
			commerceChannel.getSiteGroupId(), true);

		if (layouts.isEmpty()) {
			privateLayout = false;
		}

		Layout layout = _layoutService.addLayout(
			commerceChannel.getSiteGroupId(), privateLayout,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, name, name, null,
			LayoutConstants.TYPE_PORTLET, true, friendlyURL,
			new ServiceContext());

		LayoutTypePortlet layoutTypePortlet =
			(LayoutTypePortlet)layout.getLayoutType();

		layoutTypePortlet.addPortletId(
			PrincipalThreadLocal.getUserId(), CPPortletKeys.CP_CONTENT_WEB);

		_layoutService.updateLayout(
			layout.getGroupId(), layout.isPrivateLayout(), layout.getLayoutId(),
			layout.getTypeSettings());
	}

	@Override
	public String getDescription(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return LanguageUtil.get(
			resourceBundle,
			CommerceHealthStatusConstants.
				CP_CONTENT_COMMERCE_HEALTH_STATUS_DESCRIPTION);
	}

	@Override
	public String getKey() {
		return CommerceHealthStatusConstants.
			CP_CONTENT_COMMERCE_HEALTH_STATUS_KEY;
	}

	@Override
	public String getName(Locale locale) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());

		return LanguageUtil.get(
			resourceBundle,
			CommerceHealthStatusConstants.
				CP_CONTENT_COMMERCE_HEALTH_STATUS_KEY);
	}

	@Override
	public boolean isFixed(long companyId, long commerceChannelId)
		throws PortalException {

		CommerceChannel commerceChannel =
			_commerceChannelService.getCommerceChannel(commerceChannelId);

		String commerceChannelType = commerceChannel.getType();

		if (!commerceChannelType.equals(
				CommerceChannelConstants.CHANNEL_TYPE_SITE)) {

			return true;
		}

		long plid = _getPlidFromPortletId(
			commerceChannel.getSiteGroupId(), CPPortletKeys.CP_CONTENT_WEB);

		if (plid > 0) {
			return true;
		}

		return false;
	}

	private long _getPlidFromPortletId(
		List<Layout> layouts, String portletId, long scopeGroupId) {

		for (Layout layout : layouts) {
			LayoutTypePortlet layoutTypePortlet =
				(LayoutTypePortlet)layout.getLayoutType();

			if (_hasNonstaticPortletId(layoutTypePortlet, portletId) &&
				(_portal.getScopeGroupId(layout, portletId) == scopeGroupId)) {

				return layout.getPlid();
			}
		}

		return LayoutConstants.DEFAULT_PLID;
	}

	private long _getPlidFromPortletId(long groupId, String portletId) {
		long scopeGroupId = groupId;

		try {
			Group group = _groupLocalService.getGroup(groupId);

			if (group.isLayout()) {
				Layout scopeLayout = _layoutLocalService.getLayout(
					group.getClassPK());

				groupId = scopeLayout.getGroupId();
			}

			String[] validLayoutTypes = {
				LayoutConstants.TYPE_CONTENT, LayoutConstants.TYPE_PORTLET,
				LayoutConstants.TYPE_FULL_PAGE_APPLICATION,
				LayoutConstants.TYPE_PANEL
			};

			for (String layoutType : validLayoutTypes) {
				List<Layout> layouts = _layoutLocalService.getLayouts(
					groupId, false, layoutType);

				long plid = _getPlidFromPortletId(
					layouts, portletId, scopeGroupId);

				if (plid == LayoutConstants.DEFAULT_PLID) {
					layouts = _layoutLocalService.getLayouts(
						groupId, true, layoutType);

					plid = _getPlidFromPortletId(
						layouts, portletId, scopeGroupId);
				}

				if (plid != LayoutConstants.DEFAULT_PLID) {
					return plid;
				}
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception, exception);
			}
		}

		return LayoutConstants.DEFAULT_PLID;
	}

	private boolean _hasNonstaticPortletId(
		LayoutTypePortlet layoutTypePortlet, String portletId) {

		LayoutTemplate layoutTemplate = layoutTypePortlet.getLayoutTemplate();

		for (String columnId : layoutTemplate.getColumns()) {
			String[] columnValues = StringUtil.split(
				layoutTypePortlet.getTypeSettingsProperty(columnId));

			for (String nonstaticPortletId : columnValues) {
				if (nonstaticPortletId.equals(portletId)) {
					return true;
				}

				String decodedNonstaticPortletName =
					PortletIdCodec.decodePortletName(nonstaticPortletId);

				if (decodedNonstaticPortletName.equals(portletId)) {
					return true;
				}
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPContentHealthStatus.class);

	@Reference
	private CommerceChannelService _commerceChannelService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutService _layoutService;

	@Reference
	private Portal _portal;

}