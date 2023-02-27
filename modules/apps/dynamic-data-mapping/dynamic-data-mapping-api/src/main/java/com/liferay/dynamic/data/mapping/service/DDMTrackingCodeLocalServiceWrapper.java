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

package com.liferay.dynamic.data.mapping.service;

import com.liferay.dynamic.data.mapping.model.DDMTrackingCode;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

/**
 * Provides a wrapper for {@link DDMTrackingCodeLocalService}.
 *
 * @author Brian Wing Shun Chan
 * @see DDMTrackingCodeLocalService
 * @generated
 */
public class DDMTrackingCodeLocalServiceWrapper
	implements DDMTrackingCodeLocalService,
			   ServiceWrapper<DDMTrackingCodeLocalService> {

	public DDMTrackingCodeLocalServiceWrapper(
		DDMTrackingCodeLocalService ddmTrackingCodeLocalService) {

		_ddmTrackingCodeLocalService = ddmTrackingCodeLocalService;
	}

	/**
	 * Adds the ddm tracking code to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMTrackingCodeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmTrackingCode the ddm tracking code
	 * @return the ddm tracking code that was added
	 */
	@Override
	public DDMTrackingCode addDDMTrackingCode(DDMTrackingCode ddmTrackingCode) {
		return _ddmTrackingCodeLocalService.addDDMTrackingCode(ddmTrackingCode);
	}

	@Override
	public DDMTrackingCode addDDMTrackingCode(
			long formInstanceRecordId, String trackingCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmTrackingCodeLocalService.addDDMTrackingCode(
			formInstanceRecordId, trackingCode);
	}

	/**
	 * Creates a new ddm tracking code with the primary key. Does not add the ddm tracking code to the database.
	 *
	 * @param formInstanceRecordId the primary key for the new ddm tracking code
	 * @return the new ddm tracking code
	 */
	@Override
	public DDMTrackingCode createDDMTrackingCode(long formInstanceRecordId) {
		return _ddmTrackingCodeLocalService.createDDMTrackingCode(
			formInstanceRecordId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel createPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmTrackingCodeLocalService.createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the ddm tracking code from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMTrackingCodeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmTrackingCode the ddm tracking code
	 * @return the ddm tracking code that was removed
	 */
	@Override
	public DDMTrackingCode deleteDDMTrackingCode(
		DDMTrackingCode ddmTrackingCode) {

		return _ddmTrackingCodeLocalService.deleteDDMTrackingCode(
			ddmTrackingCode);
	}

	/**
	 * Deletes the ddm tracking code with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMTrackingCodeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param formInstanceRecordId the primary key of the ddm tracking code
	 * @return the ddm tracking code that was removed
	 * @throws PortalException if a ddm tracking code with the primary key could not be found
	 */
	@Override
	public DDMTrackingCode deleteDDMTrackingCode(long formInstanceRecordId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmTrackingCodeLocalService.deleteDDMTrackingCode(
			formInstanceRecordId);
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel deletePersistedModel(
			com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmTrackingCodeLocalService.deletePersistedModel(
			persistedModel);
	}

	@Override
	public <T> T dslQuery(com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {
		return _ddmTrackingCodeLocalService.dslQuery(dslQuery);
	}

	@Override
	public int dslQueryCount(
		com.liferay.petra.sql.dsl.query.DSLQuery dslQuery) {

		return _ddmTrackingCodeLocalService.dslQueryCount(dslQuery);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery() {
		return _ddmTrackingCodeLocalService.dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _ddmTrackingCodeLocalService.dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMTrackingCodeModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return _ddmTrackingCodeLocalService.dynamicQuery(
			dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMTrackingCodeModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Override
	public <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return _ddmTrackingCodeLocalService.dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return _ddmTrackingCodeLocalService.dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Override
	public long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return _ddmTrackingCodeLocalService.dynamicQueryCount(
			dynamicQuery, projection);
	}

	@Override
	public DDMTrackingCode fetchDDMTrackingCode(long formInstanceRecordId) {
		return _ddmTrackingCodeLocalService.fetchDDMTrackingCode(
			formInstanceRecordId);
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return _ddmTrackingCodeLocalService.getActionableDynamicQuery();
	}

	/**
	 * Returns the ddm tracking code with the primary key.
	 *
	 * @param formInstanceRecordId the primary key of the ddm tracking code
	 * @return the ddm tracking code
	 * @throws PortalException if a ddm tracking code with the primary key could not be found
	 */
	@Override
	public DDMTrackingCode getDDMTrackingCode(long formInstanceRecordId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmTrackingCodeLocalService.getDDMTrackingCode(
			formInstanceRecordId);
	}

	@Override
	public DDMTrackingCode getDDMTrackingCode(String trackingCode)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmTrackingCodeLocalService.getDDMTrackingCode(trackingCode);
	}

	/**
	 * Returns a range of all the ddm tracking codes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.dynamic.data.mapping.model.impl.DDMTrackingCodeModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm tracking codes
	 * @param end the upper bound of the range of ddm tracking codes (not inclusive)
	 * @return the range of ddm tracking codes
	 */
	@Override
	public java.util.List<DDMTrackingCode> getDDMTrackingCodes(
		int start, int end) {

		return _ddmTrackingCodeLocalService.getDDMTrackingCodes(start, end);
	}

	/**
	 * Returns the number of ddm tracking codes.
	 *
	 * @return the number of ddm tracking codes
	 */
	@Override
	public int getDDMTrackingCodesCount() {
		return _ddmTrackingCodeLocalService.getDDMTrackingCodesCount();
	}

	@Override
	public com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
		getIndexableActionableDynamicQuery() {

		return _ddmTrackingCodeLocalService.
			getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _ddmTrackingCodeLocalService.getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	@Override
	public com.liferay.portal.kernel.model.PersistedModel getPersistedModel(
			java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmTrackingCodeLocalService.getPersistedModel(primaryKeyObj);
	}

	@Override
	public String getTrackingCode(long formInstanceRecordId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _ddmTrackingCodeLocalService.getTrackingCode(
			formInstanceRecordId);
	}

	/**
	 * Updates the ddm tracking code in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect DDMTrackingCodeLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param ddmTrackingCode the ddm tracking code
	 * @return the ddm tracking code that was updated
	 */
	@Override
	public DDMTrackingCode updateDDMTrackingCode(
		DDMTrackingCode ddmTrackingCode) {

		return _ddmTrackingCodeLocalService.updateDDMTrackingCode(
			ddmTrackingCode);
	}

	@Override
	public CTPersistence<DDMTrackingCode> getCTPersistence() {
		return _ddmTrackingCodeLocalService.getCTPersistence();
	}

	@Override
	public Class<DDMTrackingCode> getModelClass() {
		return _ddmTrackingCodeLocalService.getModelClass();
	}

	@Override
	public <R, E extends Throwable> R updateWithUnsafeFunction(
			UnsafeFunction<CTPersistence<DDMTrackingCode>, R, E>
				updateUnsafeFunction)
		throws E {

		return _ddmTrackingCodeLocalService.updateWithUnsafeFunction(
			updateUnsafeFunction);
	}

	@Override
	public DDMTrackingCodeLocalService getWrappedService() {
		return _ddmTrackingCodeLocalService;
	}

	@Override
	public void setWrappedService(
		DDMTrackingCodeLocalService ddmTrackingCodeLocalService) {

		_ddmTrackingCodeLocalService = ddmTrackingCodeLocalService;
	}

	private DDMTrackingCodeLocalService _ddmTrackingCodeLocalService;

}