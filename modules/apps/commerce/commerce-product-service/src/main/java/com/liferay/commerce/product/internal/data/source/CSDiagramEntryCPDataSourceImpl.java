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

package com.liferay.commerce.product.internal.data.source;

import com.liferay.commerce.account.model.CommerceAccount;
import com.liferay.commerce.account.model.CommerceAccountGroupRel;
import com.liferay.commerce.account.service.CommerceAccountGroupRelService;
import com.liferay.commerce.account.util.CommerceAccountHelper;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.product.catalog.CPCatalogEntry;
import com.liferay.commerce.product.constants.CPWebKeys;
import com.liferay.commerce.product.data.source.CPDataSource;
import com.liferay.commerce.product.data.source.CPDataSourceResult;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.permission.CommerceCatalogPermission;
import com.liferay.commerce.product.permission.CommerceProductViewPermission;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.util.CPDefinitionHelper;
import com.liferay.commerce.shop.by.diagram.model.CSDiagramEntry;
import com.liferay.commerce.shop.by.diagram.service.CSDiagramEntryLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Ivica Cardic
 */
@Component(
	enabled = false, immediate = true,
	property = "commerce.product.data.source.name=" + CSDiagramEntryCPDataSourceImpl.NAME,
	service = CPDataSource.class
)
public class CSDiagramEntryCPDataSourceImpl implements CPDataSource {

	public static final String NAME = "csDiagramEntryDataSource";

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, "related-diagrams");
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public CPDataSourceResult getResult(
			HttpServletRequest httpServletRequest, int start, int end)
		throws Exception {

		CPCatalogEntry cpCatalogEntry =
			(CPCatalogEntry)httpServletRequest.getAttribute(
				CPWebKeys.CP_CATALOG_ENTRY);

		if (cpCatalogEntry == null) {
			return new CPDataSourceResult(new ArrayList<>(), 0);
		}

		List<CPCatalogEntry> cpCatalogEntries = new ArrayList<>();

		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		CommerceAccount commerceAccount = commerceContext.getCommerceAccount();

		long commerceAccountId = 0;

		if (commerceAccount != null) {
			commerceAccountId = commerceAccount.getCommerceAccountId();
		}

		List<CSDiagramEntry> csDiagramEntries =
			_csDiagramEntryLocalService.getCPDefinitionRelatedCSDiagramEntries(
				cpCatalogEntry.getCPDefinitionId());

		for (CSDiagramEntry csDiagramEntry : csDiagramEntries) {
			CPDefinition cpDefinition =
				_cpDefinitionLocalService.getCPDefinition(
					csDiagramEntry.getCPDefinitionId());

			if (_isCommerceAccountEnabled(commerceAccountId, cpDefinition) &&
				_viewCatalog(cpDefinition)) {

				cpCatalogEntries.add(
					_cpDefinitionHelper.getCPCatalogEntry(
						commerceAccountId,
						commerceContext.getCommerceChannelGroupId(),
						csDiagramEntry.getCPDefinitionId(),
						_portal.getLocale(httpServletRequest)));
			}
		}

		if (cpCatalogEntries.size() > end) {
			cpCatalogEntries = cpCatalogEntries.subList(start, end);
		}

		return new CPDataSourceResult(
			cpCatalogEntries, cpCatalogEntries.size());
	}

	private boolean _isCommerceAccountEnabled(
			long commerceAccountId, CPDefinition cpDefinition)
		throws Exception {

		if (!cpDefinition.isAccountGroupFilterEnabled()) {
			return true;
		}

		List<CommerceAccountGroupRel> commerceAccountGroupRels =
			_commerceAccountGroupRelService.getCommerceAccountGroupRels(
				CPDefinition.class.getName(), cpDefinition.getCPDefinitionId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		long[] commerceAccountGroupIds =
			_commerceAccountHelper.getCommerceAccountGroupIds(
				commerceAccountId);

		for (CommerceAccountGroupRel commerceAccountGroupRel :
				commerceAccountGroupRels) {

			if (ArrayUtil.contains(
					commerceAccountGroupIds,
					commerceAccountGroupRel.getCommerceAccountGroupId())) {

				return true;
			}
		}

		return false;
	}

	private boolean _viewCatalog(CPDefinition cpDefinition) throws Exception {
		return _commerceCatalogPermission.contains(
			PermissionThreadLocal.getPermissionChecker(),
			cpDefinition.getCommerceCatalog(), ActionKeys.VIEW);
	}

	@Reference
	private CommerceAccountGroupRelService _commerceAccountGroupRelService;

	@Reference
	private CommerceAccountHelper _commerceAccountHelper;

	@Reference
	private CommerceCatalogPermission _commerceCatalogPermission;

	@Reference
	private CommerceProductViewPermission _commerceProductViewPermission;

	@Reference
	private CPDefinitionHelper _cpDefinitionHelper;

	@Reference
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@Reference
	private CSDiagramEntryLocalService _csDiagramEntryLocalService;

	@Reference
	private Portal _portal;

}