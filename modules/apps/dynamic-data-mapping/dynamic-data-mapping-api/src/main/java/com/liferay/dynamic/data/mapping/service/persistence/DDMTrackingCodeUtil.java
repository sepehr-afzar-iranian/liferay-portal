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

package com.liferay.dynamic.data.mapping.service.persistence;

import com.liferay.dynamic.data.mapping.model.DDMTrackingCode;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The persistence utility for the ddm tracking code service. This utility wraps <code>com.liferay.dynamic.data.mapping.service.persistence.impl.DDMTrackingCodePersistenceImpl</code> and provides direct access to the database for CRUD operations. This utility should only be used by the service layer, as it must operate within a transaction. Never access this utility in a JSP, controller, model, or other front-end class.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DDMTrackingCodePersistence
 * @generated
 */
public class DDMTrackingCodeUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache()
	 */
	public static void clearCache() {
		getPersistence().clearCache();
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#clearCache(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static void clearCache(DDMTrackingCode ddmTrackingCode) {
		getPersistence().clearCache(ddmTrackingCode);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#countWithDynamicQuery(DynamicQuery)
	 */
	public static long countWithDynamicQuery(DynamicQuery dynamicQuery) {
		return getPersistence().countWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#fetchByPrimaryKeys(Set)
	 */
	public static Map<Serializable, DDMTrackingCode> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		return getPersistence().fetchByPrimaryKeys(primaryKeys);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery)
	 */
	public static List<DDMTrackingCode> findWithDynamicQuery(
		DynamicQuery dynamicQuery) {

		return getPersistence().findWithDynamicQuery(dynamicQuery);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int)
	 */
	public static List<DDMTrackingCode> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getPersistence().findWithDynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#findWithDynamicQuery(DynamicQuery, int, int, OrderByComparator)
	 */
	public static List<DDMTrackingCode> findWithDynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<DDMTrackingCode> orderByComparator) {

		return getPersistence().findWithDynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel)
	 */
	public static DDMTrackingCode update(DDMTrackingCode ddmTrackingCode) {
		return getPersistence().update(ddmTrackingCode);
	}

	/**
	 * @see com.liferay.portal.kernel.service.persistence.BasePersistence#update(com.liferay.portal.kernel.model.BaseModel, ServiceContext)
	 */
	public static DDMTrackingCode update(
		DDMTrackingCode ddmTrackingCode, ServiceContext serviceContext) {

		return getPersistence().update(ddmTrackingCode, serviceContext);
	}

	/**
	 * Returns the ddm tracking code where trackingCode = &#63; or throws a <code>NoSuchTrackingCodeException</code> if it could not be found.
	 *
	 * @param trackingCode the tracking code
	 * @return the matching ddm tracking code
	 * @throws NoSuchTrackingCodeException if a matching ddm tracking code could not be found
	 */
	public static DDMTrackingCode findByTrackingCode(String trackingCode)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchTrackingCodeException {

		return getPersistence().findByTrackingCode(trackingCode);
	}

	/**
	 * Returns the ddm tracking code where trackingCode = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param trackingCode the tracking code
	 * @return the matching ddm tracking code, or <code>null</code> if a matching ddm tracking code could not be found
	 */
	public static DDMTrackingCode fetchByTrackingCode(String trackingCode) {
		return getPersistence().fetchByTrackingCode(trackingCode);
	}

	/**
	 * Returns the ddm tracking code where trackingCode = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param trackingCode the tracking code
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm tracking code, or <code>null</code> if a matching ddm tracking code could not be found
	 */
	public static DDMTrackingCode fetchByTrackingCode(
		String trackingCode, boolean useFinderCache) {

		return getPersistence().fetchByTrackingCode(
			trackingCode, useFinderCache);
	}

	/**
	 * Removes the ddm tracking code where trackingCode = &#63; from the database.
	 *
	 * @param trackingCode the tracking code
	 * @return the ddm tracking code that was removed
	 */
	public static DDMTrackingCode removeByTrackingCode(String trackingCode)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchTrackingCodeException {

		return getPersistence().removeByTrackingCode(trackingCode);
	}

	/**
	 * Returns the number of ddm tracking codes where trackingCode = &#63;.
	 *
	 * @param trackingCode the tracking code
	 * @return the number of matching ddm tracking codes
	 */
	public static int countByTrackingCode(String trackingCode) {
		return getPersistence().countByTrackingCode(trackingCode);
	}

	/**
	 * Caches the ddm tracking code in the entity cache if it is enabled.
	 *
	 * @param ddmTrackingCode the ddm tracking code
	 */
	public static void cacheResult(DDMTrackingCode ddmTrackingCode) {
		getPersistence().cacheResult(ddmTrackingCode);
	}

	/**
	 * Caches the ddm tracking codes in the entity cache if it is enabled.
	 *
	 * @param ddmTrackingCodes the ddm tracking codes
	 */
	public static void cacheResult(List<DDMTrackingCode> ddmTrackingCodes) {
		getPersistence().cacheResult(ddmTrackingCodes);
	}

	/**
	 * Creates a new ddm tracking code with the primary key. Does not add the ddm tracking code to the database.
	 *
	 * @param formInstanceRecordId the primary key for the new ddm tracking code
	 * @return the new ddm tracking code
	 */
	public static DDMTrackingCode create(long formInstanceRecordId) {
		return getPersistence().create(formInstanceRecordId);
	}

	/**
	 * Removes the ddm tracking code with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param formInstanceRecordId the primary key of the ddm tracking code
	 * @return the ddm tracking code that was removed
	 * @throws NoSuchTrackingCodeException if a ddm tracking code with the primary key could not be found
	 */
	public static DDMTrackingCode remove(long formInstanceRecordId)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchTrackingCodeException {

		return getPersistence().remove(formInstanceRecordId);
	}

	public static DDMTrackingCode updateImpl(DDMTrackingCode ddmTrackingCode) {
		return getPersistence().updateImpl(ddmTrackingCode);
	}

	/**
	 * Returns the ddm tracking code with the primary key or throws a <code>NoSuchTrackingCodeException</code> if it could not be found.
	 *
	 * @param formInstanceRecordId the primary key of the ddm tracking code
	 * @return the ddm tracking code
	 * @throws NoSuchTrackingCodeException if a ddm tracking code with the primary key could not be found
	 */
	public static DDMTrackingCode findByPrimaryKey(long formInstanceRecordId)
		throws com.liferay.dynamic.data.mapping.exception.
			NoSuchTrackingCodeException {

		return getPersistence().findByPrimaryKey(formInstanceRecordId);
	}

	/**
	 * Returns the ddm tracking code with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param formInstanceRecordId the primary key of the ddm tracking code
	 * @return the ddm tracking code, or <code>null</code> if a ddm tracking code with the primary key could not be found
	 */
	public static DDMTrackingCode fetchByPrimaryKey(long formInstanceRecordId) {
		return getPersistence().fetchByPrimaryKey(formInstanceRecordId);
	}

	/**
	 * Returns all the ddm tracking codes.
	 *
	 * @return the ddm tracking codes
	 */
	public static List<DDMTrackingCode> findAll() {
		return getPersistence().findAll();
	}

	/**
	 * Returns a range of all the ddm tracking codes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTrackingCodeModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm tracking codes
	 * @param end the upper bound of the range of ddm tracking codes (not inclusive)
	 * @return the range of ddm tracking codes
	 */
	public static List<DDMTrackingCode> findAll(int start, int end) {
		return getPersistence().findAll(start, end);
	}

	/**
	 * Returns an ordered range of all the ddm tracking codes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTrackingCodeModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm tracking codes
	 * @param end the upper bound of the range of ddm tracking codes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of ddm tracking codes
	 */
	public static List<DDMTrackingCode> findAll(
		int start, int end,
		OrderByComparator<DDMTrackingCode> orderByComparator) {

		return getPersistence().findAll(start, end, orderByComparator);
	}

	/**
	 * Returns an ordered range of all the ddm tracking codes.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>DDMTrackingCodeModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of ddm tracking codes
	 * @param end the upper bound of the range of ddm tracking codes (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @param useFinderCache whether to use the finder cache
	 * @return the ordered range of ddm tracking codes
	 */
	public static List<DDMTrackingCode> findAll(
		int start, int end,
		OrderByComparator<DDMTrackingCode> orderByComparator,
		boolean useFinderCache) {

		return getPersistence().findAll(
			start, end, orderByComparator, useFinderCache);
	}

	/**
	 * Removes all the ddm tracking codes from the database.
	 */
	public static void removeAll() {
		getPersistence().removeAll();
	}

	/**
	 * Returns the number of ddm tracking codes.
	 *
	 * @return the number of ddm tracking codes
	 */
	public static int countAll() {
		return getPersistence().countAll();
	}

	public static DDMTrackingCodePersistence getPersistence() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker
		<DDMTrackingCodePersistence, DDMTrackingCodePersistence>
			_serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			DDMTrackingCodePersistence.class);

		ServiceTracker<DDMTrackingCodePersistence, DDMTrackingCodePersistence>
			serviceTracker =
				new ServiceTracker
					<DDMTrackingCodePersistence, DDMTrackingCodePersistence>(
						bundle.getBundleContext(),
						DDMTrackingCodePersistence.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}