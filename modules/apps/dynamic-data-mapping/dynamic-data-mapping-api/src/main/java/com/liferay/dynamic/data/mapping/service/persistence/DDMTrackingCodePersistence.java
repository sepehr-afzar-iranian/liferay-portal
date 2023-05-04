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

import com.liferay.dynamic.data.mapping.exception.NoSuchTrackingCodeException;
import com.liferay.dynamic.data.mapping.model.DDMTrackingCode;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.service.persistence.change.tracking.CTPersistence;

import org.osgi.annotation.versioning.ProviderType;

/**
 * The persistence interface for the ddm tracking code service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see DDMTrackingCodeUtil
 * @generated
 */
@ProviderType
public interface DDMTrackingCodePersistence
	extends BasePersistence<DDMTrackingCode>, CTPersistence<DDMTrackingCode> {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link DDMTrackingCodeUtil} to access the ddm tracking code persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this interface.
	 */

	/**
	 * Returns the ddm tracking code where trackingCode = &#63; or throws a <code>NoSuchTrackingCodeException</code> if it could not be found.
	 *
	 * @param trackingCode the tracking code
	 * @return the matching ddm tracking code
	 * @throws NoSuchTrackingCodeException if a matching ddm tracking code could not be found
	 */
	public DDMTrackingCode findByTrackingCode(String trackingCode)
		throws NoSuchTrackingCodeException;

	/**
	 * Returns the ddm tracking code where trackingCode = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param trackingCode the tracking code
	 * @return the matching ddm tracking code, or <code>null</code> if a matching ddm tracking code could not be found
	 */
	public DDMTrackingCode fetchByTrackingCode(String trackingCode);

	/**
	 * Returns the ddm tracking code where trackingCode = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param trackingCode the tracking code
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm tracking code, or <code>null</code> if a matching ddm tracking code could not be found
	 */
	public DDMTrackingCode fetchByTrackingCode(
		String trackingCode, boolean useFinderCache);

	/**
	 * Removes the ddm tracking code where trackingCode = &#63; from the database.
	 *
	 * @param trackingCode the tracking code
	 * @return the ddm tracking code that was removed
	 */
	public DDMTrackingCode removeByTrackingCode(String trackingCode)
		throws NoSuchTrackingCodeException;

	/**
	 * Returns the number of ddm tracking codes where trackingCode = &#63;.
	 *
	 * @param trackingCode the tracking code
	 * @return the number of matching ddm tracking codes
	 */
	public int countByTrackingCode(String trackingCode);

	/**
	 * Caches the ddm tracking code in the entity cache if it is enabled.
	 *
	 * @param ddmTrackingCode the ddm tracking code
	 */
	public void cacheResult(DDMTrackingCode ddmTrackingCode);

	/**
	 * Caches the ddm tracking codes in the entity cache if it is enabled.
	 *
	 * @param ddmTrackingCodes the ddm tracking codes
	 */
	public void cacheResult(java.util.List<DDMTrackingCode> ddmTrackingCodes);

	/**
	 * Creates a new ddm tracking code with the primary key. Does not add the ddm tracking code to the database.
	 *
	 * @param formInstanceRecordId the primary key for the new ddm tracking code
	 * @return the new ddm tracking code
	 */
	public DDMTrackingCode create(long formInstanceRecordId);

	/**
	 * Removes the ddm tracking code with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param formInstanceRecordId the primary key of the ddm tracking code
	 * @return the ddm tracking code that was removed
	 * @throws NoSuchTrackingCodeException if a ddm tracking code with the primary key could not be found
	 */
	public DDMTrackingCode remove(long formInstanceRecordId)
		throws NoSuchTrackingCodeException;

	public DDMTrackingCode updateImpl(DDMTrackingCode ddmTrackingCode);

	/**
	 * Returns the ddm tracking code with the primary key or throws a <code>NoSuchTrackingCodeException</code> if it could not be found.
	 *
	 * @param formInstanceRecordId the primary key of the ddm tracking code
	 * @return the ddm tracking code
	 * @throws NoSuchTrackingCodeException if a ddm tracking code with the primary key could not be found
	 */
	public DDMTrackingCode findByPrimaryKey(long formInstanceRecordId)
		throws NoSuchTrackingCodeException;

	/**
	 * Returns the ddm tracking code with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param formInstanceRecordId the primary key of the ddm tracking code
	 * @return the ddm tracking code, or <code>null</code> if a ddm tracking code with the primary key could not be found
	 */
	public DDMTrackingCode fetchByPrimaryKey(long formInstanceRecordId);

	/**
	 * Returns all the ddm tracking codes.
	 *
	 * @return the ddm tracking codes
	 */
	public java.util.List<DDMTrackingCode> findAll();

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
	public java.util.List<DDMTrackingCode> findAll(int start, int end);

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
	public java.util.List<DDMTrackingCode> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMTrackingCode>
			orderByComparator);

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
	public java.util.List<DDMTrackingCode> findAll(
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<DDMTrackingCode>
			orderByComparator,
		boolean useFinderCache);

	/**
	 * Removes all the ddm tracking codes from the database.
	 */
	public void removeAll();

	/**
	 * Returns the number of ddm tracking codes.
	 *
	 * @return the number of ddm tracking codes
	 */
	public int countAll();

}