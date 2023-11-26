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

package com.liferay.journal.web.internal.portlet.action;

import com.liferay.exportimport.kernel.configuration.ExportImportConfigurationSettingsMapFactory;
import com.liferay.exportimport.kernel.configuration.constants.ExportImportConfigurationConstants;
import com.liferay.exportimport.kernel.lar.ExportImportHelper;
import com.liferay.exportimport.kernel.lar.PortletDataHandlerKeys;
import com.liferay.exportimport.kernel.model.ExportImportConfiguration;
import com.liferay.exportimport.kernel.service.ExportImportConfigurationLocalService;
import com.liferay.exportimport.kernel.service.ExportImportService;
import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.exception.ArticleIdException;
import com.liferay.journal.exception.DuplicateArticleIdException;
import com.liferay.journal.exception.NoSuchArticleException;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.Serializable;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 * @author Eduardo García
 */
@Component(
	immediate = true,
	property = {
		"javax.portlet.name=" + JournalPortletKeys.JOURNAL,
		"mvc.command.name=exportArticle"
	},
	service = MVCActionCommand.class
)
public class ExportArticleMVCActionCommand extends BaseMVCActionCommand {

	protected void copyArticle(ActionRequest actionRequest) throws Exception {
		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		String[] exportArticleIds;

		String articleId = ParamUtil.getString(actionRequest, "articleId");

		if (Validator.isNotNull(articleId)) {
			exportArticleIds = new String[] {articleId};
		}
		else {
			exportArticleIds = ParamUtil.getParameterValues(
				actionRequest, "rowIdsJournalArticle");
		}

		Portlet portlet = ActionUtil.getPortlet(actionRequest);

		long plid = themeDisplay.getPlid();

		Group group = themeDisplay.getScopeGroup();

		_exportArticle(
			actionRequest, groupId, exportArticleIds, plid,
			_exportImportHelper.getPortletExportFileName(portlet), themeDisplay,
			group);
	}

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			copyArticle(actionRequest);
		}
		catch (Exception exception) {
			if (exception instanceof NoSuchArticleException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());

				PortletSession portletSession =
					actionRequest.getPortletSession();

				PortletContext portletContext =
					portletSession.getPortletContext();

				PortletRequestDispatcher portletRequestDispatcher =
					portletContext.getRequestDispatcher("/error.jsp");

				portletRequestDispatcher.include(actionRequest, actionResponse);
			}
			else if (exception instanceof ArticleIdException ||
					 exception instanceof DuplicateArticleIdException) {

				SessionErrors.add(actionRequest, exception.getClass());
			}
			else {
				throw exception;
			}
		}
	}

	@Reference(unbind = "-")
	protected void setExportImportConfigurationLocalService(
		ExportImportConfigurationLocalService
			exportImportConfigurationLocalService) {

		_exportImportConfigurationLocalService =
			exportImportConfigurationLocalService;
	}

	@Reference(unbind = "-")
	protected void setExportImportService(
		ExportImportService exportImportService) {

		_exportImportService = exportImportService;
	}

	@Reference(unbind = "-")
	protected void setJournalArticleLocalService(
		JournalArticleLocalService journalArticleLocalService) {

		_journalArticleLocalService = journalArticleLocalService;
	}

	@Reference(unbind = "-")
	protected void setJournalArticleService(
		JournalArticleService journalArticleService) {

		_journalArticleService = journalArticleService;
	}

	private void _exportArticle(
		ActionRequest actionRequest, long groupId, String[] articleIds,
		long plid, String fileName, ThemeDisplay themeDisplay, Group group) {

		try {
			Portlet portlet = ActionUtil.getPortlet(actionRequest);

			Map<String, Serializable> exportPortletSettingsMap =
				_exportImportConfigurationSettingsMapFactory.
					buildExportPortletSettingsMap(
						themeDisplay.getUserId(), plid, groupId,
						portlet.getPortletId(),
						HashMapBuilder.putAll(
							actionRequest.getParameterMap()
						).put(
							PortletDataHandlerKeys.PORTLET_DATA,
							new String[] {"true"}
						).put(
							"_journal_article-ids", ArrayUtil.clone(articleIds)
						).put(
							"_journal_referenced-content", new String[] {"true"}
						).put(
							"_journal_referenced-content-behavior",
							new String[] {"include-always"}
						).put(
							"_journal_version-history", new String[] {"true"}
						).put(
							"_journal_web-content", new String[] {"true"}
						).put(
							"PORTLET_DATA_com_liferay_journal_web" +
								"_portlet_JournalPortlet",
							new String[] {"true"}
						).put(
							"PORTLET_DATA_CONTROL_DEFAULT",
							new String[] {"false"}
						).put(
							"PORTLET_SETUP_com_liferay_journal" +
								"_web_portlet_JournalPortlet",
							new String[] {"true"}
						).put(
							"portletResource",
							new String[] {
								"com_liferay_journal_web_portlet_JournalPortlet"
							}
						).put(
							"range", new String[] {"all"}
						).put(
							"stagingSite",
							new String[] {
								String.valueOf(group.isStagingGroup())
							}
						).build(),
						themeDisplay.getLocale(), themeDisplay.getTimeZone(),
						fileName);

			ExportImportConfiguration exportImportConfiguration =
				_exportImportConfigurationLocalService.
					addDraftExportImportConfiguration(
						themeDisplay.getUserId(),
						ExportImportConfigurationConstants.TYPE_EXPORT_PORTLET,
						exportPortletSettingsMap);

			_exportImportService.exportPortletInfoAsFileInBackground(
				exportImportConfiguration);
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private ExportImportConfigurationLocalService
		_exportImportConfigurationLocalService;

	@Reference
	private ExportImportConfigurationSettingsMapFactory
		_exportImportConfigurationSettingsMapFactory;

	@Reference
	private ExportImportHelper _exportImportHelper;

	private ExportImportService _exportImportService;
	private JournalArticleLocalService _journalArticleLocalService;
	private JournalArticleService _journalArticleService;

	@Reference
	private PortletLocalService _portletLocalService;

}