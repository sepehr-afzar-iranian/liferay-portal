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

package com.liferay.portlet.documentlibrary.service.base;

import com.liferay.asset.kernel.service.persistence.AssetCategoryFinder;
import com.liferay.asset.kernel.service.persistence.AssetCategoryPersistence;
import com.liferay.asset.kernel.service.persistence.AssetEntryFinder;
import com.liferay.asset.kernel.service.persistence.AssetEntryPersistence;
import com.liferay.asset.kernel.service.persistence.AssetLinkFinder;
import com.liferay.asset.kernel.service.persistence.AssetLinkPersistence;
import com.liferay.asset.kernel.service.persistence.AssetTagFinder;
import com.liferay.asset.kernel.service.persistence.AssetTagPersistence;
import com.liferay.document.library.kernel.service.DLAppHelperLocalService;
import com.liferay.document.library.kernel.service.DLAppHelperLocalServiceUtil;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryFinder;
import com.liferay.document.library.kernel.service.persistence.DLFileEntryPersistence;
import com.liferay.document.library.kernel.service.persistence.DLFileShortcutPersistence;
import com.liferay.document.library.kernel.service.persistence.DLFileVersionPersistence;
import com.liferay.document.library.kernel.service.persistence.DLFolderFinder;
import com.liferay.document.library.kernel.service.persistence.DLFolderPersistence;
import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdate;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdateFactoryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.service.BaseLocalServiceImpl;
import com.liferay.portal.kernel.service.persistence.ClassNamePersistence;
import com.liferay.portal.kernel.service.persistence.WorkflowInstanceLinkPersistence;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.ratings.kernel.service.persistence.RatingsStatsPersistence;
import com.liferay.trash.kernel.service.persistence.TrashEntryPersistence;
import com.liferay.trash.kernel.service.persistence.TrashVersionPersistence;

import java.lang.reflect.Field;

import javax.sql.DataSource;

/**
 * Provides the base implementation for the dl app helper local service.
 *
 * <p>
 * This implementation exists only as a container for the default service methods generated by ServiceBuilder. All custom service methods should be put in {@link com.liferay.portlet.documentlibrary.service.impl.DLAppHelperLocalServiceImpl}.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see com.liferay.portlet.documentlibrary.service.impl.DLAppHelperLocalServiceImpl
 * @generated
 */
public abstract class DLAppHelperLocalServiceBaseImpl
	extends BaseLocalServiceImpl
	implements DLAppHelperLocalService, IdentifiableOSGiService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Use <code>DLAppHelperLocalService</code> via injection or a <code>org.osgi.util.tracker.ServiceTracker</code> or use <code>DLAppHelperLocalServiceUtil</code>.
	 */

	/**
	 * Returns the dl app helper local service.
	 *
	 * @return the dl app helper local service
	 */
	public DLAppHelperLocalService getDLAppHelperLocalService() {
		return dlAppHelperLocalService;
	}

	/**
	 * Sets the dl app helper local service.
	 *
	 * @param dlAppHelperLocalService the dl app helper local service
	 */
	public void setDLAppHelperLocalService(
		DLAppHelperLocalService dlAppHelperLocalService) {

		this.dlAppHelperLocalService = dlAppHelperLocalService;
	}

	/**
	 * Returns the counter local service.
	 *
	 * @return the counter local service
	 */
	public com.liferay.counter.kernel.service.CounterLocalService
		getCounterLocalService() {

		return counterLocalService;
	}

	/**
	 * Sets the counter local service.
	 *
	 * @param counterLocalService the counter local service
	 */
	public void setCounterLocalService(
		com.liferay.counter.kernel.service.CounterLocalService
			counterLocalService) {

		this.counterLocalService = counterLocalService;
	}

	/**
	 * Returns the class name local service.
	 *
	 * @return the class name local service
	 */
	public com.liferay.portal.kernel.service.ClassNameLocalService
		getClassNameLocalService() {

		return classNameLocalService;
	}

	/**
	 * Sets the class name local service.
	 *
	 * @param classNameLocalService the class name local service
	 */
	public void setClassNameLocalService(
		com.liferay.portal.kernel.service.ClassNameLocalService
			classNameLocalService) {

		this.classNameLocalService = classNameLocalService;
	}

	/**
	 * Returns the class name persistence.
	 *
	 * @return the class name persistence
	 */
	public ClassNamePersistence getClassNamePersistence() {
		return classNamePersistence;
	}

	/**
	 * Sets the class name persistence.
	 *
	 * @param classNamePersistence the class name persistence
	 */
	public void setClassNamePersistence(
		ClassNamePersistence classNamePersistence) {

		this.classNamePersistence = classNamePersistence;
	}

	/**
	 * Returns the workflow instance link local service.
	 *
	 * @return the workflow instance link local service
	 */
	public com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService
		getWorkflowInstanceLinkLocalService() {

		return workflowInstanceLinkLocalService;
	}

	/**
	 * Sets the workflow instance link local service.
	 *
	 * @param workflowInstanceLinkLocalService the workflow instance link local service
	 */
	public void setWorkflowInstanceLinkLocalService(
		com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService
			workflowInstanceLinkLocalService) {

		this.workflowInstanceLinkLocalService =
			workflowInstanceLinkLocalService;
	}

	/**
	 * Returns the workflow instance link persistence.
	 *
	 * @return the workflow instance link persistence
	 */
	public WorkflowInstanceLinkPersistence
		getWorkflowInstanceLinkPersistence() {

		return workflowInstanceLinkPersistence;
	}

	/**
	 * Sets the workflow instance link persistence.
	 *
	 * @param workflowInstanceLinkPersistence the workflow instance link persistence
	 */
	public void setWorkflowInstanceLinkPersistence(
		WorkflowInstanceLinkPersistence workflowInstanceLinkPersistence) {

		this.workflowInstanceLinkPersistence = workflowInstanceLinkPersistence;
	}

	/**
	 * Returns the asset category local service.
	 *
	 * @return the asset category local service
	 */
	public com.liferay.asset.kernel.service.AssetCategoryLocalService
		getAssetCategoryLocalService() {

		return assetCategoryLocalService;
	}

	/**
	 * Sets the asset category local service.
	 *
	 * @param assetCategoryLocalService the asset category local service
	 */
	public void setAssetCategoryLocalService(
		com.liferay.asset.kernel.service.AssetCategoryLocalService
			assetCategoryLocalService) {

		this.assetCategoryLocalService = assetCategoryLocalService;
	}

	/**
	 * Returns the asset category persistence.
	 *
	 * @return the asset category persistence
	 */
	public AssetCategoryPersistence getAssetCategoryPersistence() {
		return assetCategoryPersistence;
	}

	/**
	 * Sets the asset category persistence.
	 *
	 * @param assetCategoryPersistence the asset category persistence
	 */
	public void setAssetCategoryPersistence(
		AssetCategoryPersistence assetCategoryPersistence) {

		this.assetCategoryPersistence = assetCategoryPersistence;
	}

	/**
	 * Returns the asset category finder.
	 *
	 * @return the asset category finder
	 */
	public AssetCategoryFinder getAssetCategoryFinder() {
		return assetCategoryFinder;
	}

	/**
	 * Sets the asset category finder.
	 *
	 * @param assetCategoryFinder the asset category finder
	 */
	public void setAssetCategoryFinder(
		AssetCategoryFinder assetCategoryFinder) {

		this.assetCategoryFinder = assetCategoryFinder;
	}

	/**
	 * Returns the asset entry local service.
	 *
	 * @return the asset entry local service
	 */
	public com.liferay.asset.kernel.service.AssetEntryLocalService
		getAssetEntryLocalService() {

		return assetEntryLocalService;
	}

	/**
	 * Sets the asset entry local service.
	 *
	 * @param assetEntryLocalService the asset entry local service
	 */
	public void setAssetEntryLocalService(
		com.liferay.asset.kernel.service.AssetEntryLocalService
			assetEntryLocalService) {

		this.assetEntryLocalService = assetEntryLocalService;
	}

	/**
	 * Returns the asset entry persistence.
	 *
	 * @return the asset entry persistence
	 */
	public AssetEntryPersistence getAssetEntryPersistence() {
		return assetEntryPersistence;
	}

	/**
	 * Sets the asset entry persistence.
	 *
	 * @param assetEntryPersistence the asset entry persistence
	 */
	public void setAssetEntryPersistence(
		AssetEntryPersistence assetEntryPersistence) {

		this.assetEntryPersistence = assetEntryPersistence;
	}

	/**
	 * Returns the asset entry finder.
	 *
	 * @return the asset entry finder
	 */
	public AssetEntryFinder getAssetEntryFinder() {
		return assetEntryFinder;
	}

	/**
	 * Sets the asset entry finder.
	 *
	 * @param assetEntryFinder the asset entry finder
	 */
	public void setAssetEntryFinder(AssetEntryFinder assetEntryFinder) {
		this.assetEntryFinder = assetEntryFinder;
	}

	/**
	 * Returns the asset link local service.
	 *
	 * @return the asset link local service
	 */
	public com.liferay.asset.kernel.service.AssetLinkLocalService
		getAssetLinkLocalService() {

		return assetLinkLocalService;
	}

	/**
	 * Sets the asset link local service.
	 *
	 * @param assetLinkLocalService the asset link local service
	 */
	public void setAssetLinkLocalService(
		com.liferay.asset.kernel.service.AssetLinkLocalService
			assetLinkLocalService) {

		this.assetLinkLocalService = assetLinkLocalService;
	}

	/**
	 * Returns the asset link persistence.
	 *
	 * @return the asset link persistence
	 */
	public AssetLinkPersistence getAssetLinkPersistence() {
		return assetLinkPersistence;
	}

	/**
	 * Sets the asset link persistence.
	 *
	 * @param assetLinkPersistence the asset link persistence
	 */
	public void setAssetLinkPersistence(
		AssetLinkPersistence assetLinkPersistence) {

		this.assetLinkPersistence = assetLinkPersistence;
	}

	/**
	 * Returns the asset link finder.
	 *
	 * @return the asset link finder
	 */
	public AssetLinkFinder getAssetLinkFinder() {
		return assetLinkFinder;
	}

	/**
	 * Sets the asset link finder.
	 *
	 * @param assetLinkFinder the asset link finder
	 */
	public void setAssetLinkFinder(AssetLinkFinder assetLinkFinder) {
		this.assetLinkFinder = assetLinkFinder;
	}

	/**
	 * Returns the asset tag local service.
	 *
	 * @return the asset tag local service
	 */
	public com.liferay.asset.kernel.service.AssetTagLocalService
		getAssetTagLocalService() {

		return assetTagLocalService;
	}

	/**
	 * Sets the asset tag local service.
	 *
	 * @param assetTagLocalService the asset tag local service
	 */
	public void setAssetTagLocalService(
		com.liferay.asset.kernel.service.AssetTagLocalService
			assetTagLocalService) {

		this.assetTagLocalService = assetTagLocalService;
	}

	/**
	 * Returns the asset tag persistence.
	 *
	 * @return the asset tag persistence
	 */
	public AssetTagPersistence getAssetTagPersistence() {
		return assetTagPersistence;
	}

	/**
	 * Sets the asset tag persistence.
	 *
	 * @param assetTagPersistence the asset tag persistence
	 */
	public void setAssetTagPersistence(
		AssetTagPersistence assetTagPersistence) {

		this.assetTagPersistence = assetTagPersistence;
	}

	/**
	 * Returns the asset tag finder.
	 *
	 * @return the asset tag finder
	 */
	public AssetTagFinder getAssetTagFinder() {
		return assetTagFinder;
	}

	/**
	 * Sets the asset tag finder.
	 *
	 * @param assetTagFinder the asset tag finder
	 */
	public void setAssetTagFinder(AssetTagFinder assetTagFinder) {
		this.assetTagFinder = assetTagFinder;
	}

	/**
	 * Returns the dl app local service.
	 *
	 * @return the dl app local service
	 */
	public com.liferay.document.library.kernel.service.DLAppLocalService
		getDLAppLocalService() {

		return dlAppLocalService;
	}

	/**
	 * Sets the dl app local service.
	 *
	 * @param dlAppLocalService the dl app local service
	 */
	public void setDLAppLocalService(
		com.liferay.document.library.kernel.service.DLAppLocalService
			dlAppLocalService) {

		this.dlAppLocalService = dlAppLocalService;
	}

	/**
	 * Returns the ratings stats local service.
	 *
	 * @return the ratings stats local service
	 */
	public com.liferay.ratings.kernel.service.RatingsStatsLocalService
		getRatingsStatsLocalService() {

		return ratingsStatsLocalService;
	}

	/**
	 * Sets the ratings stats local service.
	 *
	 * @param ratingsStatsLocalService the ratings stats local service
	 */
	public void setRatingsStatsLocalService(
		com.liferay.ratings.kernel.service.RatingsStatsLocalService
			ratingsStatsLocalService) {

		this.ratingsStatsLocalService = ratingsStatsLocalService;
	}

	/**
	 * Returns the ratings stats persistence.
	 *
	 * @return the ratings stats persistence
	 */
	public RatingsStatsPersistence getRatingsStatsPersistence() {
		return ratingsStatsPersistence;
	}

	/**
	 * Sets the ratings stats persistence.
	 *
	 * @param ratingsStatsPersistence the ratings stats persistence
	 */
	public void setRatingsStatsPersistence(
		RatingsStatsPersistence ratingsStatsPersistence) {

		this.ratingsStatsPersistence = ratingsStatsPersistence;
	}

	/**
	 * Returns the trash entry local service.
	 *
	 * @return the trash entry local service
	 */
	@SuppressWarnings("deprecation")
	public com.liferay.trash.kernel.service.TrashEntryLocalService
		getTrashEntryLocalService() {

		return trashEntryLocalService;
	}

	/**
	 * Sets the trash entry local service.
	 *
	 * @param trashEntryLocalService the trash entry local service
	 */
	@SuppressWarnings("deprecation")
	public void setTrashEntryLocalService(
		com.liferay.trash.kernel.service.TrashEntryLocalService
			trashEntryLocalService) {

		this.trashEntryLocalService = trashEntryLocalService;
	}

	/**
	 * Returns the trash entry persistence.
	 *
	 * @return the trash entry persistence
	 */
	public TrashEntryPersistence getTrashEntryPersistence() {
		return trashEntryPersistence;
	}

	/**
	 * Sets the trash entry persistence.
	 *
	 * @param trashEntryPersistence the trash entry persistence
	 */
	public void setTrashEntryPersistence(
		TrashEntryPersistence trashEntryPersistence) {

		this.trashEntryPersistence = trashEntryPersistence;
	}

	/**
	 * Returns the trash version local service.
	 *
	 * @return the trash version local service
	 */
	@SuppressWarnings("deprecation")
	public com.liferay.trash.kernel.service.TrashVersionLocalService
		getTrashVersionLocalService() {

		return trashVersionLocalService;
	}

	/**
	 * Sets the trash version local service.
	 *
	 * @param trashVersionLocalService the trash version local service
	 */
	@SuppressWarnings("deprecation")
	public void setTrashVersionLocalService(
		com.liferay.trash.kernel.service.TrashVersionLocalService
			trashVersionLocalService) {

		this.trashVersionLocalService = trashVersionLocalService;
	}

	/**
	 * Returns the trash version persistence.
	 *
	 * @return the trash version persistence
	 */
	public TrashVersionPersistence getTrashVersionPersistence() {
		return trashVersionPersistence;
	}

	/**
	 * Sets the trash version persistence.
	 *
	 * @param trashVersionPersistence the trash version persistence
	 */
	public void setTrashVersionPersistence(
		TrashVersionPersistence trashVersionPersistence) {

		this.trashVersionPersistence = trashVersionPersistence;
	}

	/**
	 * Returns the document library file entry local service.
	 *
	 * @return the document library file entry local service
	 */
	public com.liferay.document.library.kernel.service.DLFileEntryLocalService
		getDLFileEntryLocalService() {

		return dlFileEntryLocalService;
	}

	/**
	 * Sets the document library file entry local service.
	 *
	 * @param dlFileEntryLocalService the document library file entry local service
	 */
	public void setDLFileEntryLocalService(
		com.liferay.document.library.kernel.service.DLFileEntryLocalService
			dlFileEntryLocalService) {

		this.dlFileEntryLocalService = dlFileEntryLocalService;
	}

	/**
	 * Returns the document library file entry persistence.
	 *
	 * @return the document library file entry persistence
	 */
	public DLFileEntryPersistence getDLFileEntryPersistence() {
		return dlFileEntryPersistence;
	}

	/**
	 * Sets the document library file entry persistence.
	 *
	 * @param dlFileEntryPersistence the document library file entry persistence
	 */
	public void setDLFileEntryPersistence(
		DLFileEntryPersistence dlFileEntryPersistence) {

		this.dlFileEntryPersistence = dlFileEntryPersistence;
	}

	/**
	 * Returns the document library file entry finder.
	 *
	 * @return the document library file entry finder
	 */
	public DLFileEntryFinder getDLFileEntryFinder() {
		return dlFileEntryFinder;
	}

	/**
	 * Sets the document library file entry finder.
	 *
	 * @param dlFileEntryFinder the document library file entry finder
	 */
	public void setDLFileEntryFinder(DLFileEntryFinder dlFileEntryFinder) {
		this.dlFileEntryFinder = dlFileEntryFinder;
	}

	/**
	 * Returns the document library file shortcut local service.
	 *
	 * @return the document library file shortcut local service
	 */
	public
		com.liferay.document.library.kernel.service.DLFileShortcutLocalService
			getDLFileShortcutLocalService() {

		return dlFileShortcutLocalService;
	}

	/**
	 * Sets the document library file shortcut local service.
	 *
	 * @param dlFileShortcutLocalService the document library file shortcut local service
	 */
	public void setDLFileShortcutLocalService(
		com.liferay.document.library.kernel.service.DLFileShortcutLocalService
			dlFileShortcutLocalService) {

		this.dlFileShortcutLocalService = dlFileShortcutLocalService;
	}

	/**
	 * Returns the document library file shortcut persistence.
	 *
	 * @return the document library file shortcut persistence
	 */
	public DLFileShortcutPersistence getDLFileShortcutPersistence() {
		return dlFileShortcutPersistence;
	}

	/**
	 * Sets the document library file shortcut persistence.
	 *
	 * @param dlFileShortcutPersistence the document library file shortcut persistence
	 */
	public void setDLFileShortcutPersistence(
		DLFileShortcutPersistence dlFileShortcutPersistence) {

		this.dlFileShortcutPersistence = dlFileShortcutPersistence;
	}

	/**
	 * Returns the document library file version local service.
	 *
	 * @return the document library file version local service
	 */
	public com.liferay.document.library.kernel.service.DLFileVersionLocalService
		getDLFileVersionLocalService() {

		return dlFileVersionLocalService;
	}

	/**
	 * Sets the document library file version local service.
	 *
	 * @param dlFileVersionLocalService the document library file version local service
	 */
	public void setDLFileVersionLocalService(
		com.liferay.document.library.kernel.service.DLFileVersionLocalService
			dlFileVersionLocalService) {

		this.dlFileVersionLocalService = dlFileVersionLocalService;
	}

	/**
	 * Returns the document library file version persistence.
	 *
	 * @return the document library file version persistence
	 */
	public DLFileVersionPersistence getDLFileVersionPersistence() {
		return dlFileVersionPersistence;
	}

	/**
	 * Sets the document library file version persistence.
	 *
	 * @param dlFileVersionPersistence the document library file version persistence
	 */
	public void setDLFileVersionPersistence(
		DLFileVersionPersistence dlFileVersionPersistence) {

		this.dlFileVersionPersistence = dlFileVersionPersistence;
	}

	/**
	 * Returns the document library folder local service.
	 *
	 * @return the document library folder local service
	 */
	public com.liferay.document.library.kernel.service.DLFolderLocalService
		getDLFolderLocalService() {

		return dlFolderLocalService;
	}

	/**
	 * Sets the document library folder local service.
	 *
	 * @param dlFolderLocalService the document library folder local service
	 */
	public void setDLFolderLocalService(
		com.liferay.document.library.kernel.service.DLFolderLocalService
			dlFolderLocalService) {

		this.dlFolderLocalService = dlFolderLocalService;
	}

	/**
	 * Returns the document library folder persistence.
	 *
	 * @return the document library folder persistence
	 */
	public DLFolderPersistence getDLFolderPersistence() {
		return dlFolderPersistence;
	}

	/**
	 * Sets the document library folder persistence.
	 *
	 * @param dlFolderPersistence the document library folder persistence
	 */
	public void setDLFolderPersistence(
		DLFolderPersistence dlFolderPersistence) {

		this.dlFolderPersistence = dlFolderPersistence;
	}

	/**
	 * Returns the document library folder finder.
	 *
	 * @return the document library folder finder
	 */
	public DLFolderFinder getDLFolderFinder() {
		return dlFolderFinder;
	}

	/**
	 * Sets the document library folder finder.
	 *
	 * @param dlFolderFinder the document library folder finder
	 */
	public void setDLFolderFinder(DLFolderFinder dlFolderFinder) {
		this.dlFolderFinder = dlFolderFinder;
	}

	public void afterPropertiesSet() {
		_setLocalServiceUtilService(dlAppHelperLocalService);
	}

	public void destroy() {
		_setLocalServiceUtilService(null);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return DLAppHelperLocalService.class.getName();
	}

	/**
	 * Performs a SQL query.
	 *
	 * @param sql the sql query
	 */
	protected void runSQL(String sql) {
		try {
			DataSource dataSource = InfrastructureUtil.getDataSource();

			DB db = DBManagerUtil.getDB();

			sql = db.buildSQL(sql);
			sql = PortalUtil.transformSQL(sql);

			SqlUpdate sqlUpdate = SqlUpdateFactoryUtil.getSqlUpdate(
				dataSource, sql);

			sqlUpdate.update();
		}
		catch (Exception exception) {
			throw new SystemException(exception);
		}
	}

	private void _setLocalServiceUtilService(
		DLAppHelperLocalService dlAppHelperLocalService) {

		try {
			Field field = DLAppHelperLocalServiceUtil.class.getDeclaredField(
				"_service");

			field.setAccessible(true);

			field.set(null, dlAppHelperLocalService);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new RuntimeException(reflectiveOperationException);
		}
	}

	@BeanReference(type = DLAppHelperLocalService.class)
	protected DLAppHelperLocalService dlAppHelperLocalService;

	@BeanReference(
		type = com.liferay.counter.kernel.service.CounterLocalService.class
	)
	protected com.liferay.counter.kernel.service.CounterLocalService
		counterLocalService;

	@BeanReference(
		type = com.liferay.portal.kernel.service.ClassNameLocalService.class
	)
	protected com.liferay.portal.kernel.service.ClassNameLocalService
		classNameLocalService;

	@BeanReference(type = ClassNamePersistence.class)
	protected ClassNamePersistence classNamePersistence;

	@BeanReference(
		type = com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService.class
	)
	protected com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService
		workflowInstanceLinkLocalService;

	@BeanReference(type = WorkflowInstanceLinkPersistence.class)
	protected WorkflowInstanceLinkPersistence workflowInstanceLinkPersistence;

	@BeanReference(
		type = com.liferay.asset.kernel.service.AssetCategoryLocalService.class
	)
	protected com.liferay.asset.kernel.service.AssetCategoryLocalService
		assetCategoryLocalService;

	@BeanReference(type = AssetCategoryPersistence.class)
	protected AssetCategoryPersistence assetCategoryPersistence;

	@BeanReference(type = AssetCategoryFinder.class)
	protected AssetCategoryFinder assetCategoryFinder;

	@BeanReference(
		type = com.liferay.asset.kernel.service.AssetEntryLocalService.class
	)
	protected com.liferay.asset.kernel.service.AssetEntryLocalService
		assetEntryLocalService;

	@BeanReference(type = AssetEntryPersistence.class)
	protected AssetEntryPersistence assetEntryPersistence;

	@BeanReference(type = AssetEntryFinder.class)
	protected AssetEntryFinder assetEntryFinder;

	@BeanReference(
		type = com.liferay.asset.kernel.service.AssetLinkLocalService.class
	)
	protected com.liferay.asset.kernel.service.AssetLinkLocalService
		assetLinkLocalService;

	@BeanReference(type = AssetLinkPersistence.class)
	protected AssetLinkPersistence assetLinkPersistence;

	@BeanReference(type = AssetLinkFinder.class)
	protected AssetLinkFinder assetLinkFinder;

	@BeanReference(
		type = com.liferay.asset.kernel.service.AssetTagLocalService.class
	)
	protected com.liferay.asset.kernel.service.AssetTagLocalService
		assetTagLocalService;

	@BeanReference(type = AssetTagPersistence.class)
	protected AssetTagPersistence assetTagPersistence;

	@BeanReference(type = AssetTagFinder.class)
	protected AssetTagFinder assetTagFinder;

	@BeanReference(
		type = com.liferay.document.library.kernel.service.DLAppLocalService.class
	)
	protected com.liferay.document.library.kernel.service.DLAppLocalService
		dlAppLocalService;

	@BeanReference(
		type = com.liferay.ratings.kernel.service.RatingsStatsLocalService.class
	)
	protected com.liferay.ratings.kernel.service.RatingsStatsLocalService
		ratingsStatsLocalService;

	@BeanReference(type = RatingsStatsPersistence.class)
	protected RatingsStatsPersistence ratingsStatsPersistence;

	@BeanReference(
		type = com.liferay.trash.kernel.service.TrashEntryLocalService.class
	)
	@SuppressWarnings("deprecation")
	protected com.liferay.trash.kernel.service.TrashEntryLocalService
		trashEntryLocalService;

	@BeanReference(type = TrashEntryPersistence.class)
	protected TrashEntryPersistence trashEntryPersistence;

	@BeanReference(
		type = com.liferay.trash.kernel.service.TrashVersionLocalService.class
	)
	@SuppressWarnings("deprecation")
	protected com.liferay.trash.kernel.service.TrashVersionLocalService
		trashVersionLocalService;

	@BeanReference(type = TrashVersionPersistence.class)
	protected TrashVersionPersistence trashVersionPersistence;

	@BeanReference(
		type = com.liferay.document.library.kernel.service.DLFileEntryLocalService.class
	)
	protected
		com.liferay.document.library.kernel.service.DLFileEntryLocalService
			dlFileEntryLocalService;

	@BeanReference(type = DLFileEntryPersistence.class)
	protected DLFileEntryPersistence dlFileEntryPersistence;

	@BeanReference(type = DLFileEntryFinder.class)
	protected DLFileEntryFinder dlFileEntryFinder;

	@BeanReference(
		type = com.liferay.document.library.kernel.service.DLFileShortcutLocalService.class
	)
	protected
		com.liferay.document.library.kernel.service.DLFileShortcutLocalService
			dlFileShortcutLocalService;

	@BeanReference(type = DLFileShortcutPersistence.class)
	protected DLFileShortcutPersistence dlFileShortcutPersistence;

	@BeanReference(
		type = com.liferay.document.library.kernel.service.DLFileVersionLocalService.class
	)
	protected
		com.liferay.document.library.kernel.service.DLFileVersionLocalService
			dlFileVersionLocalService;

	@BeanReference(type = DLFileVersionPersistence.class)
	protected DLFileVersionPersistence dlFileVersionPersistence;

	@BeanReference(
		type = com.liferay.document.library.kernel.service.DLFolderLocalService.class
	)
	protected com.liferay.document.library.kernel.service.DLFolderLocalService
		dlFolderLocalService;

	@BeanReference(type = DLFolderPersistence.class)
	protected DLFolderPersistence dlFolderPersistence;

	@BeanReference(type = DLFolderFinder.class)
	protected DLFolderFinder dlFolderFinder;

}