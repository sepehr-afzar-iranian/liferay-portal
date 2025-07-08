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

package com.liferay.roles.admin.web.internal.display.context;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelCategory;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemListBuilder;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.RoleServiceUtil;
import com.liferay.portal.kernel.service.permission.RolePermissionUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;
import com.liferay.roles.admin.web.internal.role.type.contributor.util.RoleTypeContributorRetrieverUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Pei-Jung Lan
 */
public class RoleDisplayContext {

	public RoleDisplayContext(
		HttpServletRequest httpServletRequest, RenderResponse renderResponse) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;

		_currentRoleTypeContributor =
			RoleTypeContributorRetrieverUtil.getCurrentRoleTypeContributor(
				httpServletRequest);
	}

	public List<NavigationItem> getEditRoleNavigationItems() throws Exception {
		List<String> tabsNames = _getTabsNames();
		Map<String, String> tabsURLs = _getTabsURLs();

		String tabs1 = ParamUtil.getString(_httpServletRequest, "tabs1");

		return new NavigationItemList() {
			{
				for (String tabsName : tabsNames) {
					add(
						navigationItem -> {
							navigationItem.setActive(tabsName.equals(tabs1));
							navigationItem.setHref(tabsURLs.get(tabsName));
							navigationItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, tabsName));
						});
				}
			}
		};
	}

	public String getKey(PanelApp panelApp, PanelCategory panelCategory) {
		if ((panelApp == null) || (panelCategory == null)) {
			return _normalizeKey(StringPool.BLANK);
		}

		String panelAppKey = panelApp.getKey();

		if ((panelAppKey == null) || panelAppKey.isEmpty()) {
			return _normalizeKey(StringPool.BLANK);
		}

		String extractedAppKey = _extractAfterLastDelimiter(
			panelAppKey, StringPool.PERIOD);

		String categoryKey = getKey(panelCategory);

		String baseCategoryKey = _removeSuffix(categoryKey);

		String combinedKey =
			baseCategoryKey + StringPool.DASH + extractedAppKey;

		return _normalizeKey(combinedKey);
	}

	public String getKey(PanelCategory panelCategory) {
		if (panelCategory == null) {
			return _normalizeKey(StringPool.BLANK);
		}

		return _normalizeKey(panelCategory.getKey());
	}

	public String getKey(Portlet portlet, String preKey) {
		if ((portlet == null) || (preKey == null)) {
			return _normalizeKey(StringPool.BLANK);
		}

		String portletId = portlet.getPortletId();

		if ((portletId == null) || portletId.isEmpty()) {
			return _normalizeKey(StringPool.BLANK);
		}

		String extractedPortletId = _extractAfterLastDelimiter(
			portletId, StringPool.UNDERLINE);

		String combinedKey = preKey + StringPool.DASH + extractedPortletId;

		return _normalizeKey(combinedKey);
	}

	public String getKey(String key) {
		return _normalizeKey(key);
	}

	public String getKey(
		String applicationPermissions, String portletResource) {

		String extractedPortletResource = StringPool.BLANK;

		if (portletResource != null) {
			extractedPortletResource = _extractAfterLastDelimiter(
				portletResource, StringPool.UNDERLINE);
		}

		StringBuilder sb = new StringBuilder();

		if ((applicationPermissions != null) &&
			!applicationPermissions.equals(StringPool.BLANK)) {

			sb.append(applicationPermissions);
			sb.append(StringPool.DASH);
		}

		if (!extractedPortletResource.equals(StringPool.BLANK)) {
			sb.append(extractedPortletResource);
		}

		return _normalizeKey(sb.toString());
	}

	public String getKey(
		String applicationPermissions, String portletResource,
		String modelResource) {

		String extractedPortletResource = StringPool.BLANK;

		if (portletResource != null) {
			extractedPortletResource = _extractAfterLastDelimiter(
				portletResource, StringPool.UNDERLINE);
		}

		StringBuilder extractedModelResource = new StringBuilder(
			StringPool.BLANK);

		if (modelResource != null) {
			String[] models = modelResource.split("-");
			extractedModelResource = new StringBuilder();

			for (String model : models) {
				extractedModelResource.append(
					_extractAfterLastDelimiter(model, StringPool.PERIOD));
				extractedModelResource.append(StringPool.DASH);
			}
		}

		StringBuilder sb = new StringBuilder();

		if ((applicationPermissions != null) &&
			!applicationPermissions.equals(StringPool.BLANK)) {

			sb.append(applicationPermissions);
			sb.append(StringPool.DASH);
		}

		if (!extractedPortletResource.equals(StringPool.BLANK)) {
			sb.append(extractedPortletResource);
		}

		String extractedModelResourceString = extractedModelResource.toString();

		if (!extractedModelResourceString.equals(StringPool.BLANK)) {
			sb.append(StringPool.DASH);

			int lastIndex = extractedModelResource.length() - 1;

			if (extractedModelResource.charAt(lastIndex) == '-') {
				extractedModelResource.deleteCharAt(lastIndex);
			}

			sb.append(extractedModelResource);
		}

		return _normalizeKey(sb.toString());
	}

	public String getKey(
		String applicationPermissions, String portletResource,
		String modelResource, String actionId) {

		String extractedPortletResource = StringPool.BLANK;

		if (portletResource != null) {
			extractedPortletResource = _extractAfterLastDelimiter(
				portletResource, StringPool.UNDERLINE);
		}

		StringBuilder extractedModelResource = new StringBuilder(
			StringPool.BLANK);

		if (modelResource != null) {
			String[] models = modelResource.split("-");
			extractedModelResource = new StringBuilder();

			for (String model : models) {
				extractedModelResource.append(
					_extractAfterLastDelimiter(model, StringPool.PERIOD));
				extractedModelResource.append(StringPool.DASH);
			}
		}

		StringBuilder sb = new StringBuilder();

		if ((applicationPermissions != null) &&
			!applicationPermissions.equals(StringPool.BLANK)) {

			sb.append(applicationPermissions);
			sb.append(StringPool.DASH);
		}

		if (!extractedPortletResource.equals(StringPool.BLANK)) {
			sb.append(extractedPortletResource);
			sb.append(StringPool.DASH);
		}

		String extractedModelResourceString = extractedModelResource.toString();

		if (!extractedModelResourceString.equals(StringPool.BLANK)) {
			sb.append(extractedModelResource);
		}

		sb.append(actionId);

		return _normalizeKey(sb.toString());
	}

	public List<NavigationItem> getRoleAssignmentsNavigationItems(
			PortletURL portletURL)
		throws Exception {

		String tabs2 = ParamUtil.getString(
			_httpServletRequest, "tabs2", "users");

		return new NavigationItemList() {
			{
				for (String assigneeTypeName : _ASSIGNEE_TYPE_NAMES) {
					add(
						navigationItem -> {
							navigationItem.setActive(
								assigneeTypeName.equals(tabs2));
							navigationItem.setHref(
								portletURL, "tabs2", assigneeTypeName);
							navigationItem.setLabel(
								LanguageUtil.get(
									_httpServletRequest, assigneeTypeName));
						});
				}
			}
		};
	}

	public List<NavigationItem> getSelectAssigneesNavigationItems(
			PortletURL portletURL)
		throws Exception {

		return NavigationItemListBuilder.add(
			navigationItem -> {
				navigationItem.setActive(true);
				navigationItem.setHref(portletURL, "tabs2", "users");

				String tabs2 = ParamUtil.getString(
					_httpServletRequest, "tabs2", "users");

				navigationItem.setLabel(
					LanguageUtil.get(_httpServletRequest, tabs2));
			}
		).build();
	}

	public List<NavigationItem> getViewRoleNavigationItems(
			LiferayPortletResponse liferayPortletResponse,
			PortletURL portletURL)
		throws Exception {

		NavigationItemList navigationItemList = new NavigationItemList();

		for (RoleTypeContributor roleTypeContributor :
				RoleTypeContributorRetrieverUtil.getRoleTypeContributors(
					_httpServletRequest)) {

			navigationItemList.add(
				navigationItem -> {
					navigationItem.setActive(
						_currentRoleTypeContributor.getType() ==
							roleTypeContributor.getType());

					PortletURL viewRegularRoleNavigationURL =
						PortletURLUtil.clone(
							portletURL, liferayPortletResponse);

					navigationItem.setHref(
						viewRegularRoleNavigationURL, "roleType",
						roleTypeContributor.getType());

					navigationItem.setLabel(
						LanguageUtil.get(
							_httpServletRequest,
							roleTypeContributor.getTabTitle(
								_httpServletRequest.getLocale())));
				});
		}

		return navigationItemList;
	}

	public boolean isAutomaticallyAssigned(Role role) {
		List<RoleTypeContributor> roleTypeContributors =
			RoleTypeContributorRetrieverUtil.getRoleTypeContributors(
				_httpServletRequest);

		for (RoleTypeContributor roleTypeContributor : roleTypeContributors) {
			if (roleTypeContributor.isAutomaticallyAssigned(role)) {
				return true;
			}
		}

		return false;
	}

	private String _extractAfterLastDelimiter(String input, String delimiter) {
		int lastIndex = input.lastIndexOf(delimiter);

		if ((lastIndex != -1) && (lastIndex < (input.length() - 1))) {
			return input.substring(lastIndex + 1);
		}

		return input;
	}

	private List<String> _getTabsNames() throws Exception {
		List<String> tabsNames = new ArrayList<>();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		long roleId = ParamUtil.getLong(_httpServletRequest, "roleId");

		Role role = RoleServiceUtil.fetchRole(roleId);

		if (RolePermissionUtil.contains(
				permissionChecker, role.getRoleId(), ActionKeys.UPDATE)) {

			tabsNames.add("details");
		}

		if (_currentRoleTypeContributor.isAllowDefinePermissions(role) &&
			RolePermissionUtil.contains(
				permissionChecker, role.getRoleId(),
				ActionKeys.DEFINE_PERMISSIONS)) {

			tabsNames.add("define-permissions");
		}

		if (_currentRoleTypeContributor.isAllowAssignMembers(role) &&
			RolePermissionUtil.contains(
				permissionChecker, role.getRoleId(),
				ActionKeys.ASSIGN_MEMBERS)) {

			tabsNames.add("assignees");
		}

		return tabsNames;
	}

	private Map<String, String> _getTabsURLs() throws Exception {
		String redirect = ParamUtil.getString(_httpServletRequest, "redirect");

		String backURL = ParamUtil.getString(
			_httpServletRequest, "backURL", redirect);

		long roleId = ParamUtil.getLong(_httpServletRequest, "roleId");

		Role role = RoleServiceUtil.fetchRole(roleId);

		return HashMapBuilder.put(
			"assignees",
			() -> {
				PortletURL assignMembersURL = _renderResponse.createRenderURL();

				assignMembersURL.setParameter(
					"mvcPath", "/edit_role_assignments.jsp");
				assignMembersURL.setParameter("tabs1", "assignees");
				assignMembersURL.setParameter("redirect", redirect);
				assignMembersURL.setParameter("backURL", backURL);
				assignMembersURL.setParameter(
					"roleId", String.valueOf(role.getRoleId()));

				return assignMembersURL.toString();
			}
		).put(
			"define-permissions",
			() -> {
				PortletURL definePermissionsURL =
					_renderResponse.createRenderURL();

				definePermissionsURL.setParameter(
					"mvcPath", "/edit_role_permissions.jsp");
				definePermissionsURL.setParameter(
					"tabs1", "define-permissions");
				definePermissionsURL.setParameter("redirect", redirect);
				definePermissionsURL.setParameter("backURL", backURL);
				definePermissionsURL.setParameter(
					Constants.CMD, Constants.VIEW);
				definePermissionsURL.setParameter(
					"roleId", String.valueOf(role.getRoleId()));

				return definePermissionsURL.toString();
			}
		).put(
			"details",
			() -> {
				PortletURL editRoleURL = _renderResponse.createRenderURL();

				editRoleURL.setParameter("mvcPath", "/edit_role.jsp");
				editRoleURL.setParameter("tabs1", "details");
				editRoleURL.setParameter("redirect", redirect);
				editRoleURL.setParameter("backURL", backURL);
				editRoleURL.setParameter(
					"roleId", String.valueOf(role.getRoleId()));

				return editRoleURL.toString();
			}
		).build();
	}

	private String _normalizeKey(String key) {
		if ((key == null) || key.isEmpty()) {
			return _SECTION_SUFFIX;
		}

		return key.replaceAll("[_.]", StringPool.DASH) + _SECTION_SUFFIX;
	}

	private String _removeSuffix(String input) {
		if (input.endsWith(_SECTION_SUFFIX)) {
			return input.substring(
				0, input.length() - _SECTION_SUFFIX.length());
		}

		return input;
	}

	private static final String[] _ASSIGNEE_TYPE_NAMES = {
		"users", "sites", "organizations", "user-groups", "segments"
	};

	private static final String _SECTION_SUFFIX = "-section";

	private final RoleTypeContributor _currentRoleTypeContributor;
	private final HttpServletRequest _httpServletRequest;
	private final RenderResponse _renderResponse;

}