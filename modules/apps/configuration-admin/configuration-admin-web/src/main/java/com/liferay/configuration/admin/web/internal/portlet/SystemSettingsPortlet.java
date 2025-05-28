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

package com.liferay.configuration.admin.web.internal.portlet;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.display.ConfigurationFormRenderer;
import com.liferay.configuration.admin.web.internal.display.context.ConfigurationScopeDisplayContext;
import com.liferay.configuration.admin.web.internal.display.context.ConfigurationScopeDisplayContextFactory;
import com.liferay.configuration.admin.web.internal.model.ConfigurationModel;
import com.liferay.configuration.admin.web.internal.util.ConfigurationFormRendererRetriever;
import com.liferay.configuration.admin.web.internal.util.ConfigurationModelRetriever;
import com.liferay.configuration.admin.web.internal.util.ConfigurationModelToDDMFormConverter;
import com.liferay.configuration.admin.web.internal.util.DDMFormValuesToPropertiesConverter;
import com.liferay.configuration.admin.web.internal.util.ResourceBundleLoaderProvider;
import com.liferay.document.library.kernel.exception.FileExtensionException;
import com.liferay.dynamic.data.mapping.form.values.factory.DDMFormValuesFactory;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.resource.manager.ClassLoaderResourceManager;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.settings.LocationVariableResolver;
import com.liferay.portal.kernel.settings.SettingsLocatorHelper;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;

import java.util.Dictionary;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Kamesh Sampath
 * @author Raymond Augé
 */
@Component(
	immediate = true,
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-system-settings",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.instanceable=false",
		"com.liferay.portlet.private-request-attributes=false",
		"javax.portlet.display-name=System Settings",
		"javax.portlet.info.keywords=osgi,system,settings",
		"javax.portlet.init-param.template-path=/META-INF/resources/",
		"javax.portlet.init-param.view-template=/view.jsp",
		"javax.portlet.name=" + ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
		"javax.portlet.resource-bundle=content.Language",
		"javax.portlet.security-role-ref=administrator"
	},
	service = Portlet.class
)
public class SystemSettingsPortlet extends MVCPortlet {

	@Override
	public void processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws IOException, PortletException {

		String factoryPid = ParamUtil.getString(actionRequest, "factoryPid");

		String pid = ParamUtil.getString(actionRequest, "pid", factoryPid);

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ConfigurationModel configurationModel = null;

		ConfigurationScopeDisplayContext configurationScopeDisplayContext =
			ConfigurationScopeDisplayContextFactory.create(actionRequest);

		Map<String, ConfigurationModel> configurationModels =
			_configurationModelRetriever.getConfigurationModels(
				themeDisplay.getLanguageId(),
				configurationScopeDisplayContext.getScope(),
				configurationScopeDisplayContext.getScopePK());

		if (Validator.isNotNull(factoryPid)) {
			configurationModel = configurationModels.get(factoryPid);
		}
		else {
			configurationModel = configurationModels.get(pid);
		}

		ResourceBundleLoader resourceBundleLoader =
			_resourceBundleLoaderProvider.getResourceBundleLoader(
				configurationModel.getBundleSymbolicName());

		ResourceBundle resourceBundle = resourceBundleLoader.loadResourceBundle(
			themeDisplay.getLocale());

		Dictionary<String, Object> properties = getDDMRequestParameters(
			actionRequest, configurationModel, resourceBundle);

		String[] fileExtensionArray = GetterUtil.getStringValues(
			properties.get("fileExtensions"));

		long fileMaxSize = GetterUtil.getLong(properties.get("fileMaxSize"));

		if (ArrayUtil.contains(fileExtensionArray, StringPool.STAR)) {
			SessionErrors.add(actionRequest, FileExtensionException.class);

			sendRedirect(actionRequest, actionResponse);
		}
		else if (fileMaxSize == 0) {
			SessionErrors.add(actionRequest, "fileMaxSizeZeroNotValid");

			sendRedirect(actionRequest, actionResponse);
		}
		else {
			checkOmniAdmin();

			super.processAction(actionRequest, actionResponse);
		}
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		checkOmniAdmin();

		super.render(renderRequest, renderResponse);
	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		checkOmniAdmin();

		super.serveResource(resourceRequest, resourceResponse);
	}

	protected void checkOmniAdmin() throws PortletException {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (!permissionChecker.isOmniadmin()) {
			PrincipalException principalException =
				new PrincipalException.MustBeCompanyAdmin(
					permissionChecker.getUserId());

			throw new PortletException(principalException);
		}
	}

	protected DDMFormValues getDDMFormValues(
		ActionRequest actionRequest, DDMForm ddmForm) {

		return _ddmFormValuesFactory.create(actionRequest, ddmForm);
	}

	protected Dictionary<String, Object> getDDMRequestParameters(
		ActionRequest actionRequest, ConfigurationModel configurationModel,
		ResourceBundle resourceBundle) {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ConfigurationModelToDDMFormConverter
			configurationModelToDDMFormConverter =
				new ConfigurationModelToDDMFormConverter(
					configurationModel, themeDisplay.getLocale(),
					resourceBundle);

		DDMFormValues ddmFormValues = getDDMFormValues(
			actionRequest, configurationModelToDDMFormConverter.getDDMForm());

		LocationVariableResolver locationVariableResolver =
			new LocationVariableResolver(
				new ClassLoaderResourceManager(
					configurationModel.getClassLoader()),
				_settingsLocatorHelper);

		DDMFormValuesToPropertiesConverter ddmFormValuesToPropertiesConverter =
			new DDMFormValuesToPropertiesConverter(
				configurationModel, ddmFormValues, _jsonFactory,
				themeDisplay.getLocale(), locationVariableResolver);

		return ddmFormValuesToPropertiesConverter.getProperties();
	}

	protected Map<String, Object> getRequestParameters(
		ActionRequest actionRequest, String pid) {

		ConfigurationFormRenderer configurationFormRenderer =
			_configurationFormRendererRetriever.getConfigurationFormRenderer(
				pid);

		return configurationFormRenderer.getRequestParameters(
			_portal.getHttpServletRequest(actionRequest));
	}

	@Reference
	private ConfigurationFormRendererRetriever
		_configurationFormRendererRetriever;

	@Reference(target = "(filter.visibility=*)")
	private ConfigurationModelRetriever _configurationModelRetriever;

	@Reference
	private DDMFormValuesFactory _ddmFormValuesFactory;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

	@Reference
	private ResourceBundleLoaderProvider _resourceBundleLoaderProvider;

	@Reference
	private SettingsLocatorHelper _settingsLocatorHelper;

}