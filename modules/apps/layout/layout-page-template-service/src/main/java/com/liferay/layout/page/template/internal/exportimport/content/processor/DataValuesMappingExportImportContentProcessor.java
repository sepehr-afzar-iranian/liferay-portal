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

package com.liferay.layout.page.template.internal.exportimport.content.processor;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMTemplate;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.exportimport.content.processor.ExportImportContentProcessor;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.exportimport.kernel.lar.PortletDataException;
import com.liferay.exportimport.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.info.search.InfoSearchClassMapperRegistry;
import com.liferay.item.selector.criteria.InfoListItemSelectorReturnType;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassedModel;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.staging.StagingGroupHelper;
import com.liferay.staging.StagingGroupHelperUtil;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "content.processor.type=LayoutPageTemplateStructureRelReferences",
	service = ExportImportContentProcessor.class
)
public class DataValuesMappingExportImportContentProcessor
	implements ExportImportContentProcessor<String> {

	@Override
	public String replaceExportContentReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			String data, boolean exportReferencedContent, boolean escapeContent)
		throws Exception {

		if (!JSONUtil.isValid(data)) {
			return data;
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(data);

		_exportContentReferences(
			jsonObject, portletDataContext, stagedModel,
			exportReferencedContent);

		return jsonObject.toString();
	}

	@Override
	public String replaceImportContentReferences(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			String data)
		throws Exception {

		if (!JSONUtil.isValid(data)) {
			return data;
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject(data);

		_replaceImportContentReferences(jsonObject, portletDataContext);

		return jsonObject.toString();
	}

	@Override
	public void validateContentReferences(long groupId, String data) {
	}

	private void _exportCollectionContentReferences(
		JSONObject itemJSONObject, PortletDataContext portletDataContext,
		StagedModel stagedModel) {

		if (!itemJSONObject.has("config")) {
			return;
		}

		JSONObject configJSONObject = itemJSONObject.getJSONObject("config");

		if (!configJSONObject.has("collection")) {
			return;
		}

		JSONObject collectionJSONObject = configJSONObject.getJSONObject(
			"collection");

		String type = collectionJSONObject.getString("type");

		if (!Objects.equals(
				type, InfoListItemSelectorReturnType.class.getName())) {

			return;
		}

		long classPK = collectionJSONObject.getLong("classPK");

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.fetchAssetListEntry(classPK);

		if (assetListEntry != null) {
			try {
				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, assetListEntry, stagedModel,
					PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
			}
			catch (PortletDataException portletDataException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portletDataException);
				}
			}
		}
	}

	private void _exportContainerContentReferences(
			JSONObject itemJSONObject, PortletDataContext portletDataContext,
			StagedModel stagedModel, boolean exportReferencedContent)
		throws Exception {

		if (!itemJSONObject.has("config")) {
			return;
		}

		JSONObject configJSONObject = itemJSONObject.getJSONObject("config");

		if (configJSONObject.has("link")) {
			JSONObject linkJSONObject = configJSONObject.getJSONObject("link");

			_exportMappedFieldContentReference(
				linkJSONObject, portletDataContext, stagedModel,
				exportReferencedContent);

			if (linkJSONObject.has("layout")) {
				_exportLayoutContentReference(
					portletDataContext, stagedModel,
					linkJSONObject.getJSONObject("layout"),
					exportReferencedContent);
			}
		}

		if (!configJSONObject.has("styles")) {
			return;
		}

		JSONObject stylesJSONObject = configJSONObject.getJSONObject("styles");

		if (stylesJSONObject.has("backgroundImage")) {
			_exportMappedFieldContentReference(
				stylesJSONObject.getJSONObject("backgroundImage"),
				portletDataContext, stagedModel, exportReferencedContent);
		}
	}

	private void _exportContentReferences(
			JSONObject jsonObject, PortletDataContext portletDataContext,
			StagedModel stagedModel, boolean exportReferencedContent)
		throws Exception {

		if (!jsonObject.has("items")) {
			return;
		}

		JSONObject itemsJSONObject = jsonObject.getJSONObject("items");

		if (itemsJSONObject == null) {
			return;
		}

		for (String key : itemsJSONObject.keySet()) {
			JSONObject itemJSONObject = itemsJSONObject.getJSONObject(key);

			if (Objects.equals(
					itemJSONObject.get("type"),
					LayoutDataItemTypeConstants.TYPE_CONTAINER)) {

				_exportContainerContentReferences(
					itemJSONObject, portletDataContext, stagedModel,
					exportReferencedContent);
			}
			else if (Objects.equals(
						itemJSONObject.get("type"),
						LayoutDataItemTypeConstants.TYPE_COLLECTION)) {

				_exportCollectionContentReferences(
					itemJSONObject, portletDataContext, stagedModel);
			}
		}
	}

	private void _exportDDMTemplateReference(
			PortletDataContext portletDataContext, StagedModel stagedModel,
			JSONObject editableJSONObject)
		throws Exception {

		String mappedField = editableJSONObject.getString(
			"mappedField", editableJSONObject.getString("fieldId"));

		if (!mappedField.startsWith(_DDM_TEMPLATE)) {
			return;
		}

		String ddmTemplateKey = mappedField.substring(_DDM_TEMPLATE.length());

		DDMTemplate ddmTemplate = _ddmTemplateLocalService.fetchTemplate(
			portletDataContext.getScopeGroupId(),
			_portal.getClassNameId(DDMStructure.class), ddmTemplateKey);

		if (ddmTemplate != null) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, stagedModel, ddmTemplate,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}
	}

	private void _exportLayoutContentReference(
			PortletDataContext portletDataContext,
			StagedModel referrerStagedModel, JSONObject layoutJSONObject,
			boolean exportReferencedContent)
		throws Exception {

		if (layoutJSONObject.length() == 0) {
			return;
		}

		Layout layout = _layoutLocalService.fetchLayout(
			layoutJSONObject.getLong("groupId"),
			layoutJSONObject.getBoolean("privateLayout"),
			layoutJSONObject.getLong("layoutId"));

		if (layout == null) {
			return;
		}

		layoutJSONObject.put("plid", layout.getPlid());

		if (exportReferencedContent) {
			StagedModelDataHandlerUtil.exportReferenceStagedModel(
				portletDataContext, referrerStagedModel, layout,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
		}
		else {
			Element entityElement = portletDataContext.getExportDataElement(
				referrerStagedModel);

			portletDataContext.addReferenceElement(
				referrerStagedModel, entityElement, layout,
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY, true);
		}
	}

	private void _exportMappedFieldContentReference(
			JSONObject jsonObject, PortletDataContext portletDataContext,
			StagedModel stagedModel, boolean exportReferencedContent)
		throws Exception {

		long classNameId = jsonObject.getLong("classNameId");
		long classPK = jsonObject.getLong("classPK");

		if ((classNameId == 0) || (classPK == 0)) {
			return;
		}

		_exportDDMTemplateReference(
			portletDataContext, stagedModel, jsonObject);

		String className = _portal.getClassName(classNameId);

		jsonObject.put("className", className);

		className = _infoSearchClassMapperRegistry.getSearchClassName(
			className);

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			className, classPK);

		if (assetEntry == null) {
			return;
		}

		AssetRenderer<?> assetRenderer = assetEntry.getAssetRenderer();

		if (assetRenderer == null) {
			return;
		}

		AssetRendererFactory<?> assetRendererFactory =
			assetRenderer.getAssetRendererFactory();

		StagingGroupHelper stagingGroupHelper =
			StagingGroupHelperUtil.getStagingGroupHelper();

		if (ExportImportThreadLocal.isStagingInProcess() &&
			!stagingGroupHelper.isStagedPortlet(
				portletDataContext.getScopeGroupId(),
				assetRendererFactory.getPortletId())) {

			return;
		}

		if (exportReferencedContent) {
			try {
				StagedModelDataHandlerUtil.exportReferenceStagedModel(
					portletDataContext, stagedModel,
					(StagedModel)assetRenderer.getAssetObject(),
					PortletDataContext.REFERENCE_TYPE_DEPENDENCY);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					String errorMessage = StringBundler.concat(
						"Staged model with class name ",
						stagedModel.getModelClassName(), " and primary key ",
						stagedModel.getPrimaryKeyObj(),
						" references asset entry with class primary key ",
						classPK, " and class name ",
						_portal.getClassName(classNameId),
						" that could not be exported due to ", exception);

					if (Validator.isNotNull(exception.getMessage())) {
						errorMessage = StringBundler.concat(
							errorMessage, ": ", exception.getMessage());
					}

					_log.debug(errorMessage, exception);
				}
			}
		}
		else {
			Element entityElement = portletDataContext.getExportDataElement(
				stagedModel);

			portletDataContext.addReferenceElement(
				stagedModel, entityElement,
				(ClassedModel)assetRenderer.getAssetObject(),
				PortletDataContext.REFERENCE_TYPE_DEPENDENCY, true);
		}
	}

	private void _replaceCollectionImportContentReferences(
		JSONObject itemJSONObject, PortletDataContext portletDataContext) {

		if (!itemJSONObject.has("config")) {
			return;
		}

		JSONObject configJSONObject = itemJSONObject.getJSONObject("config");

		if (!configJSONObject.has("collection")) {
			return;
		}

		JSONObject collectionJSONObject = configJSONObject.getJSONObject(
			"collection");

		String type = collectionJSONObject.getString("type");

		if (!Objects.equals(
				type, InfoListItemSelectorReturnType.class.getName())) {

			return;
		}

		long classPK = collectionJSONObject.getLong("classPK");

		Map<Long, Long> assetListEntryNewPrimaryKeys =
			(Map<Long, Long>)portletDataContext.getNewPrimaryKeysMap(
				AssetListEntry.class.getName());

		long newClassPK = MapUtil.getLong(
			assetListEntryNewPrimaryKeys, classPK, classPK);

		AssetListEntry assetListEntry =
			_assetListEntryLocalService.fetchAssetListEntry(newClassPK);

		if (assetListEntry != null) {
			collectionJSONObject.put(
				"classNameId",
				_portal.getClassNameId(assetListEntry.getAssetEntryType())
			).put(
				"classPK", String.valueOf(newClassPK)
			).put(
				"itemSubtype", assetListEntry.getAssetEntrySubtype()
			).put(
				"itemType", assetListEntry.getAssetEntryType()
			).put(
				"title", assetListEntry.getTitle()
			);
		}
	}

	private void _replaceImportContentReferences(
		JSONObject jsonObject, PortletDataContext portletDataContext) {

		if (!jsonObject.has("items")) {
			return;
		}

		JSONObject itemsJSONObject = jsonObject.getJSONObject("items");

		if (itemsJSONObject == null) {
			return;
		}

		for (String key : itemsJSONObject.keySet()) {
			JSONObject itemJSONObject = itemsJSONObject.getJSONObject(key);

			if (Objects.equals(
					itemJSONObject.get("type"),
					LayoutDataItemTypeConstants.TYPE_COLLECTION)) {

				_replaceCollectionImportContentReferences(
					itemJSONObject, portletDataContext);
			}
		}
	}

	private static final String _DDM_TEMPLATE = "ddmTemplate_";

	private static final Log _log = LogFactoryUtil.getLog(
		DataValuesMappingExportImportContentProcessor.class);

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private InfoSearchClassMapperRegistry _infoSearchClassMapperRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

}