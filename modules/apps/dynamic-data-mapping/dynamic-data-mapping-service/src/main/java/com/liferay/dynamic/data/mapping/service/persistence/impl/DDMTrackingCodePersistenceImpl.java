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

package com.liferay.dynamic.data.mapping.service.persistence.impl;

import com.liferay.dynamic.data.mapping.exception.NoSuchTrackingCodeException;
import com.liferay.dynamic.data.mapping.model.DDMTrackingCode;
import com.liferay.dynamic.data.mapping.model.DDMTrackingCodeTable;
import com.liferay.dynamic.data.mapping.model.impl.DDMTrackingCodeImpl;
import com.liferay.dynamic.data.mapping.model.impl.DDMTrackingCodeModelImpl;
import com.liferay.dynamic.data.mapping.service.persistence.DDMTrackingCodePersistence;
import com.liferay.dynamic.data.mapping.service.persistence.impl.constants.DDMPersistenceConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTColumnResolutionType;
import com.liferay.portal.kernel.configuration.Configuration;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.Query;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.service.persistence.change.tracking.helper.CTPersistenceHelper;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.io.Serializable;

import java.lang.reflect.InvocationHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * The persistence implementation for the ddm tracking code service.
 *
 * <p>
 * Caching information and settings can be found in <code>portal.properties</code>
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
@Component(service = DDMTrackingCodePersistence.class)
public class DDMTrackingCodePersistenceImpl
	extends BasePersistenceImpl<DDMTrackingCode>
	implements DDMTrackingCodePersistence {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use <code>DDMTrackingCodeUtil</code> to access the ddm tracking code persistence. Modify <code>service.xml</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static final String FINDER_CLASS_NAME_ENTITY =
		DDMTrackingCodeImpl.class.getName();

	public static final String FINDER_CLASS_NAME_LIST_WITH_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List1";

	public static final String FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION =
		FINDER_CLASS_NAME_ENTITY + ".List2";

	private FinderPath _finderPathWithPaginationFindAll;
	private FinderPath _finderPathWithoutPaginationFindAll;
	private FinderPath _finderPathCountAll;
	private FinderPath _finderPathFetchByTrackingCode;
	private FinderPath _finderPathCountByTrackingCode;

	/**
	 * Returns the ddm tracking code where trackingCode = &#63; or throws a <code>NoSuchTrackingCodeException</code> if it could not be found.
	 *
	 * @param trackingCode the tracking code
	 * @return the matching ddm tracking code
	 * @throws NoSuchTrackingCodeException if a matching ddm tracking code could not be found
	 */
	@Override
	public DDMTrackingCode findByTrackingCode(String trackingCode)
		throws NoSuchTrackingCodeException {

		DDMTrackingCode ddmTrackingCode = fetchByTrackingCode(trackingCode);

		if (ddmTrackingCode == null) {
			StringBundler sb = new StringBundler(4);

			sb.append(_NO_SUCH_ENTITY_WITH_KEY);

			sb.append("trackingCode=");
			sb.append(trackingCode);

			sb.append("}");

			if (_log.isDebugEnabled()) {
				_log.debug(sb.toString());
			}

			throw new NoSuchTrackingCodeException(sb.toString());
		}

		return ddmTrackingCode;
	}

	/**
	 * Returns the ddm tracking code where trackingCode = &#63; or returns <code>null</code> if it could not be found. Uses the finder cache.
	 *
	 * @param trackingCode the tracking code
	 * @return the matching ddm tracking code, or <code>null</code> if a matching ddm tracking code could not be found
	 */
	@Override
	public DDMTrackingCode fetchByTrackingCode(String trackingCode) {
		return fetchByTrackingCode(trackingCode, true);
	}

	/**
	 * Returns the ddm tracking code where trackingCode = &#63; or returns <code>null</code> if it could not be found, optionally using the finder cache.
	 *
	 * @param trackingCode the tracking code
	 * @param useFinderCache whether to use the finder cache
	 * @return the matching ddm tracking code, or <code>null</code> if a matching ddm tracking code could not be found
	 */
	@Override
	public DDMTrackingCode fetchByTrackingCode(
		String trackingCode, boolean useFinderCache) {

		trackingCode = Objects.toString(trackingCode, "");

		boolean productionMode = ctPersistenceHelper.isProductionMode(
			DDMTrackingCode.class);

		Object[] finderArgs = null;

		if (useFinderCache && productionMode) {
			finderArgs = new Object[] {trackingCode};
		}

		Object result = null;

		if (useFinderCache && productionMode) {
			result = finderCache.getResult(
				_finderPathFetchByTrackingCode, finderArgs, this);
		}

		if (result instanceof DDMTrackingCode) {
			DDMTrackingCode ddmTrackingCode = (DDMTrackingCode)result;

			if (!Objects.equals(
					trackingCode, ddmTrackingCode.getTrackingCode())) {

				result = null;
			}
		}

		if (result == null) {
			StringBundler sb = new StringBundler(3);

			sb.append(_SQL_SELECT_DDMTRACKINGCODE_WHERE);

			boolean bindTrackingCode = false;

			if (trackingCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_TRACKINGCODE_TRACKINGCODE_3);
			}
			else {
				bindTrackingCode = true;

				sb.append(_FINDER_COLUMN_TRACKINGCODE_TRACKINGCODE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindTrackingCode) {
					queryPos.add(trackingCode);
				}

				List<DDMTrackingCode> list = query.list();

				if (list.isEmpty()) {
					if (useFinderCache && productionMode) {
						finderCache.putResult(
							_finderPathFetchByTrackingCode, finderArgs, list);
					}
				}
				else {
					DDMTrackingCode ddmTrackingCode = list.get(0);

					result = ddmTrackingCode;

					cacheResult(ddmTrackingCode);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		if (result instanceof List<?>) {
			return null;
		}
		else {
			return (DDMTrackingCode)result;
		}
	}

	/**
	 * Removes the ddm tracking code where trackingCode = &#63; from the database.
	 *
	 * @param trackingCode the tracking code
	 * @return the ddm tracking code that was removed
	 */
	@Override
	public DDMTrackingCode removeByTrackingCode(String trackingCode)
		throws NoSuchTrackingCodeException {

		DDMTrackingCode ddmTrackingCode = findByTrackingCode(trackingCode);

		return remove(ddmTrackingCode);
	}

	/**
	 * Returns the number of ddm tracking codes where trackingCode = &#63;.
	 *
	 * @param trackingCode the tracking code
	 * @return the number of matching ddm tracking codes
	 */
	@Override
	public int countByTrackingCode(String trackingCode) {
		trackingCode = Objects.toString(trackingCode, "");

		boolean productionMode = ctPersistenceHelper.isProductionMode(
			DDMTrackingCode.class);

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		Long count = null;

		if (productionMode) {
			finderPath = _finderPathCountByTrackingCode;

			finderArgs = new Object[] {trackingCode};

			count = (Long)finderCache.getResult(finderPath, finderArgs, this);
		}

		if (count == null) {
			StringBundler sb = new StringBundler(2);

			sb.append(_SQL_COUNT_DDMTRACKINGCODE_WHERE);

			boolean bindTrackingCode = false;

			if (trackingCode.isEmpty()) {
				sb.append(_FINDER_COLUMN_TRACKINGCODE_TRACKINGCODE_3);
			}
			else {
				bindTrackingCode = true;

				sb.append(_FINDER_COLUMN_TRACKINGCODE_TRACKINGCODE_2);
			}

			String sql = sb.toString();

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				QueryPos queryPos = QueryPos.getInstance(query);

				if (bindTrackingCode) {
					queryPos.add(trackingCode);
				}

				count = (Long)query.uniqueResult();

				if (productionMode) {
					finderCache.putResult(finderPath, finderArgs, count);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	private static final String _FINDER_COLUMN_TRACKINGCODE_TRACKINGCODE_2 =
		"ddmTrackingCode.trackingCode = ?";

	private static final String _FINDER_COLUMN_TRACKINGCODE_TRACKINGCODE_3 =
		"(ddmTrackingCode.trackingCode IS NULL OR ddmTrackingCode.trackingCode = '')";

	public DDMTrackingCodePersistenceImpl() {
		setModelClass(DDMTrackingCode.class);

		setModelImplClass(DDMTrackingCodeImpl.class);
		setModelPKClass(long.class);

		setTable(DDMTrackingCodeTable.INSTANCE);
	}

	/**
	 * Caches the ddm tracking code in the entity cache if it is enabled.
	 *
	 * @param ddmTrackingCode the ddm tracking code
	 */
	@Override
	public void cacheResult(DDMTrackingCode ddmTrackingCode) {
		if (ddmTrackingCode.getCtCollectionId() != 0) {
			return;
		}

		entityCache.putResult(
			DDMTrackingCodeImpl.class, ddmTrackingCode.getPrimaryKey(),
			ddmTrackingCode);

		finderCache.putResult(
			_finderPathFetchByTrackingCode,
			new Object[] {ddmTrackingCode.getTrackingCode()}, ddmTrackingCode);
	}

	/**
	 * Caches the ddm tracking codes in the entity cache if it is enabled.
	 *
	 * @param ddmTrackingCodes the ddm tracking codes
	 */
	@Override
	public void cacheResult(List<DDMTrackingCode> ddmTrackingCodes) {
		for (DDMTrackingCode ddmTrackingCode : ddmTrackingCodes) {
			if (ddmTrackingCode.getCtCollectionId() != 0) {
				continue;
			}

			if (entityCache.getResult(
					DDMTrackingCodeImpl.class,
					ddmTrackingCode.getPrimaryKey()) == null) {

				cacheResult(ddmTrackingCode);
			}
		}
	}

	/**
	 * Clears the cache for all ddm tracking codes.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache() {
		entityCache.clearCache(DDMTrackingCodeImpl.class);

		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);
	}

	/**
	 * Clears the cache for the ddm tracking code.
	 *
	 * <p>
	 * The <code>EntityCache</code> and <code>FinderCache</code> are both cleared by this method.
	 * </p>
	 */
	@Override
	public void clearCache(DDMTrackingCode ddmTrackingCode) {
		entityCache.removeResult(DDMTrackingCodeImpl.class, ddmTrackingCode);
	}

	@Override
	public void clearCache(List<DDMTrackingCode> ddmTrackingCodes) {
		for (DDMTrackingCode ddmTrackingCode : ddmTrackingCodes) {
			entityCache.removeResult(
				DDMTrackingCodeImpl.class, ddmTrackingCode);
		}
	}

	@Override
	public void clearCache(Set<Serializable> primaryKeys) {
		finderCache.clearCache(FINDER_CLASS_NAME_ENTITY);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITH_PAGINATION);
		finderCache.clearCache(FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION);

		for (Serializable primaryKey : primaryKeys) {
			entityCache.removeResult(DDMTrackingCodeImpl.class, primaryKey);
		}
	}

	protected void cacheUniqueFindersCache(
		DDMTrackingCodeModelImpl ddmTrackingCodeModelImpl) {

		Object[] args = new Object[] {
			ddmTrackingCodeModelImpl.getTrackingCode()
		};

		finderCache.putResult(
			_finderPathCountByTrackingCode, args, Long.valueOf(1), false);
		finderCache.putResult(
			_finderPathFetchByTrackingCode, args, ddmTrackingCodeModelImpl,
			false);
	}

	/**
	 * Creates a new ddm tracking code with the primary key. Does not add the ddm tracking code to the database.
	 *
	 * @param formInstanceRecordId the primary key for the new ddm tracking code
	 * @return the new ddm tracking code
	 */
	@Override
	public DDMTrackingCode create(long formInstanceRecordId) {
		DDMTrackingCode ddmTrackingCode = new DDMTrackingCodeImpl();

		ddmTrackingCode.setNew(true);
		ddmTrackingCode.setPrimaryKey(formInstanceRecordId);

		return ddmTrackingCode;
	}

	/**
	 * Removes the ddm tracking code with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param formInstanceRecordId the primary key of the ddm tracking code
	 * @return the ddm tracking code that was removed
	 * @throws NoSuchTrackingCodeException if a ddm tracking code with the primary key could not be found
	 */
	@Override
	public DDMTrackingCode remove(long formInstanceRecordId)
		throws NoSuchTrackingCodeException {

		return remove((Serializable)formInstanceRecordId);
	}

	/**
	 * Removes the ddm tracking code with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param primaryKey the primary key of the ddm tracking code
	 * @return the ddm tracking code that was removed
	 * @throws NoSuchTrackingCodeException if a ddm tracking code with the primary key could not be found
	 */
	@Override
	public DDMTrackingCode remove(Serializable primaryKey)
		throws NoSuchTrackingCodeException {

		Session session = null;

		try {
			session = openSession();

			DDMTrackingCode ddmTrackingCode = (DDMTrackingCode)session.get(
				DDMTrackingCodeImpl.class, primaryKey);

			if (ddmTrackingCode == null) {
				if (_log.isDebugEnabled()) {
					_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
				}

				throw new NoSuchTrackingCodeException(
					_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			return remove(ddmTrackingCode);
		}
		catch (NoSuchTrackingCodeException noSuchEntityException) {
			throw noSuchEntityException;
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}
	}

	@Override
	protected DDMTrackingCode removeImpl(DDMTrackingCode ddmTrackingCode) {
		Session session = null;

		try {
			session = openSession();

			if (!session.contains(ddmTrackingCode)) {
				ddmTrackingCode = (DDMTrackingCode)session.get(
					DDMTrackingCodeImpl.class,
					ddmTrackingCode.getPrimaryKeyObj());
			}

			if ((ddmTrackingCode != null) &&
				ctPersistenceHelper.isRemove(ddmTrackingCode)) {

				session.delete(ddmTrackingCode);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddmTrackingCode != null) {
			clearCache(ddmTrackingCode);
		}

		return ddmTrackingCode;
	}

	@Override
	public DDMTrackingCode updateImpl(DDMTrackingCode ddmTrackingCode) {
		boolean isNew = ddmTrackingCode.isNew();

		if (!(ddmTrackingCode instanceof DDMTrackingCodeModelImpl)) {
			InvocationHandler invocationHandler = null;

			if (ProxyUtil.isProxyClass(ddmTrackingCode.getClass())) {
				invocationHandler = ProxyUtil.getInvocationHandler(
					ddmTrackingCode);

				throw new IllegalArgumentException(
					"Implement ModelWrapper in ddmTrackingCode proxy " +
						invocationHandler.getClass());
			}

			throw new IllegalArgumentException(
				"Implement ModelWrapper in custom DDMTrackingCode implementation " +
					ddmTrackingCode.getClass());
		}

		DDMTrackingCodeModelImpl ddmTrackingCodeModelImpl =
			(DDMTrackingCodeModelImpl)ddmTrackingCode;

		Session session = null;

		try {
			session = openSession();

			if (ctPersistenceHelper.isInsert(ddmTrackingCode)) {
				if (!isNew) {
					session.evict(
						DDMTrackingCodeImpl.class,
						ddmTrackingCode.getPrimaryKeyObj());
				}

				session.save(ddmTrackingCode);
			}
			else {
				ddmTrackingCode = (DDMTrackingCode)session.merge(
					ddmTrackingCode);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		if (ddmTrackingCode.getCtCollectionId() != 0) {
			if (isNew) {
				ddmTrackingCode.setNew(false);
			}

			ddmTrackingCode.resetOriginalValues();

			return ddmTrackingCode;
		}

		entityCache.putResult(
			DDMTrackingCodeImpl.class, ddmTrackingCodeModelImpl, false, true);

		cacheUniqueFindersCache(ddmTrackingCodeModelImpl);

		if (isNew) {
			ddmTrackingCode.setNew(false);
		}

		ddmTrackingCode.resetOriginalValues();

		return ddmTrackingCode;
	}

	/**
	 * Returns the ddm tracking code with the primary key or throws a <code>com.liferay.portal.kernel.exception.NoSuchModelException</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the ddm tracking code
	 * @return the ddm tracking code
	 * @throws NoSuchTrackingCodeException if a ddm tracking code with the primary key could not be found
	 */
	@Override
	public DDMTrackingCode findByPrimaryKey(Serializable primaryKey)
		throws NoSuchTrackingCodeException {

		DDMTrackingCode ddmTrackingCode = fetchByPrimaryKey(primaryKey);

		if (ddmTrackingCode == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
			}

			throw new NoSuchTrackingCodeException(
				_NO_SUCH_ENTITY_WITH_PRIMARY_KEY + primaryKey);
		}

		return ddmTrackingCode;
	}

	/**
	 * Returns the ddm tracking code with the primary key or throws a <code>NoSuchTrackingCodeException</code> if it could not be found.
	 *
	 * @param formInstanceRecordId the primary key of the ddm tracking code
	 * @return the ddm tracking code
	 * @throws NoSuchTrackingCodeException if a ddm tracking code with the primary key could not be found
	 */
	@Override
	public DDMTrackingCode findByPrimaryKey(long formInstanceRecordId)
		throws NoSuchTrackingCodeException {

		return findByPrimaryKey((Serializable)formInstanceRecordId);
	}

	/**
	 * Returns the ddm tracking code with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param primaryKey the primary key of the ddm tracking code
	 * @return the ddm tracking code, or <code>null</code> if a ddm tracking code with the primary key could not be found
	 */
	@Override
	public DDMTrackingCode fetchByPrimaryKey(Serializable primaryKey) {
		if (ctPersistenceHelper.isProductionMode(DDMTrackingCode.class)) {
			return super.fetchByPrimaryKey(primaryKey);
		}

		DDMTrackingCode ddmTrackingCode = null;

		Session session = null;

		try {
			session = openSession();

			ddmTrackingCode = (DDMTrackingCode)session.get(
				DDMTrackingCodeImpl.class, primaryKey);

			if (ddmTrackingCode != null) {
				cacheResult(ddmTrackingCode);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return ddmTrackingCode;
	}

	/**
	 * Returns the ddm tracking code with the primary key or returns <code>null</code> if it could not be found.
	 *
	 * @param formInstanceRecordId the primary key of the ddm tracking code
	 * @return the ddm tracking code, or <code>null</code> if a ddm tracking code with the primary key could not be found
	 */
	@Override
	public DDMTrackingCode fetchByPrimaryKey(long formInstanceRecordId) {
		return fetchByPrimaryKey((Serializable)formInstanceRecordId);
	}

	@Override
	public Map<Serializable, DDMTrackingCode> fetchByPrimaryKeys(
		Set<Serializable> primaryKeys) {

		if (ctPersistenceHelper.isProductionMode(DDMTrackingCode.class)) {
			return super.fetchByPrimaryKeys(primaryKeys);
		}

		if (primaryKeys.isEmpty()) {
			return Collections.emptyMap();
		}

		Map<Serializable, DDMTrackingCode> map =
			new HashMap<Serializable, DDMTrackingCode>();

		if (primaryKeys.size() == 1) {
			Iterator<Serializable> iterator = primaryKeys.iterator();

			Serializable primaryKey = iterator.next();

			DDMTrackingCode ddmTrackingCode = fetchByPrimaryKey(primaryKey);

			if (ddmTrackingCode != null) {
				map.put(primaryKey, ddmTrackingCode);
			}

			return map;
		}

		StringBundler sb = new StringBundler((primaryKeys.size() * 2) + 1);

		sb.append(getSelectSQL());
		sb.append(" WHERE ");
		sb.append(getPKDBName());
		sb.append(" IN (");

		for (Serializable primaryKey : primaryKeys) {
			sb.append((long)primaryKey);

			sb.append(",");
		}

		sb.setIndex(sb.index() - 1);

		sb.append(")");

		String sql = sb.toString();

		Session session = null;

		try {
			session = openSession();

			Query query = session.createQuery(sql);

			for (DDMTrackingCode ddmTrackingCode :
					(List<DDMTrackingCode>)query.list()) {

				map.put(ddmTrackingCode.getPrimaryKeyObj(), ddmTrackingCode);

				cacheResult(ddmTrackingCode);
			}
		}
		catch (Exception exception) {
			throw processException(exception);
		}
		finally {
			closeSession(session);
		}

		return map;
	}

	/**
	 * Returns all the ddm tracking codes.
	 *
	 * @return the ddm tracking codes
	 */
	@Override
	public List<DDMTrackingCode> findAll() {
		return findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);
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
	@Override
	public List<DDMTrackingCode> findAll(int start, int end) {
		return findAll(start, end, null);
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
	@Override
	public List<DDMTrackingCode> findAll(
		int start, int end,
		OrderByComparator<DDMTrackingCode> orderByComparator) {

		return findAll(start, end, orderByComparator, true);
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
	@Override
	public List<DDMTrackingCode> findAll(
		int start, int end,
		OrderByComparator<DDMTrackingCode> orderByComparator,
		boolean useFinderCache) {

		boolean productionMode = ctPersistenceHelper.isProductionMode(
			DDMTrackingCode.class);

		FinderPath finderPath = null;
		Object[] finderArgs = null;

		if ((start == QueryUtil.ALL_POS) && (end == QueryUtil.ALL_POS) &&
			(orderByComparator == null)) {

			if (useFinderCache && productionMode) {
				finderPath = _finderPathWithoutPaginationFindAll;
				finderArgs = FINDER_ARGS_EMPTY;
			}
		}
		else if (useFinderCache && productionMode) {
			finderPath = _finderPathWithPaginationFindAll;
			finderArgs = new Object[] {start, end, orderByComparator};
		}

		List<DDMTrackingCode> list = null;

		if (useFinderCache && productionMode) {
			list = (List<DDMTrackingCode>)finderCache.getResult(
				finderPath, finderArgs, this);
		}

		if (list == null) {
			StringBundler sb = null;
			String sql = null;

			if (orderByComparator != null) {
				sb = new StringBundler(
					2 + (orderByComparator.getOrderByFields().length * 2));

				sb.append(_SQL_SELECT_DDMTRACKINGCODE);

				appendOrderByComparator(
					sb, _ORDER_BY_ENTITY_ALIAS, orderByComparator);

				sql = sb.toString();
			}
			else {
				sql = _SQL_SELECT_DDMTRACKINGCODE;

				sql = sql.concat(DDMTrackingCodeModelImpl.ORDER_BY_JPQL);
			}

			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(sql);

				list = (List<DDMTrackingCode>)QueryUtil.list(
					query, getDialect(), start, end);

				cacheResult(list);

				if (useFinderCache && productionMode) {
					finderCache.putResult(finderPath, finderArgs, list);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return list;
	}

	/**
	 * Removes all the ddm tracking codes from the database.
	 *
	 */
	@Override
	public void removeAll() {
		for (DDMTrackingCode ddmTrackingCode : findAll()) {
			remove(ddmTrackingCode);
		}
	}

	/**
	 * Returns the number of ddm tracking codes.
	 *
	 * @return the number of ddm tracking codes
	 */
	@Override
	public int countAll() {
		boolean productionMode = ctPersistenceHelper.isProductionMode(
			DDMTrackingCode.class);

		Long count = null;

		if (productionMode) {
			count = (Long)finderCache.getResult(
				_finderPathCountAll, FINDER_ARGS_EMPTY, this);
		}

		if (count == null) {
			Session session = null;

			try {
				session = openSession();

				Query query = session.createQuery(_SQL_COUNT_DDMTRACKINGCODE);

				count = (Long)query.uniqueResult();

				if (productionMode) {
					finderCache.putResult(
						_finderPathCountAll, FINDER_ARGS_EMPTY, count);
				}
			}
			catch (Exception exception) {
				throw processException(exception);
			}
			finally {
				closeSession(session);
			}
		}

		return count.intValue();
	}

	@Override
	protected EntityCache getEntityCache() {
		return entityCache;
	}

	@Override
	protected String getPKDBName() {
		return "formInstanceRecordId";
	}

	@Override
	protected String getSelectSQL() {
		return _SQL_SELECT_DDMTRACKINGCODE;
	}

	@Override
	public Set<String> getCTColumnNames(
		CTColumnResolutionType ctColumnResolutionType) {

		return _ctColumnNamesMap.get(ctColumnResolutionType);
	}

	@Override
	public List<String> getMappingTableNames() {
		return _mappingTableNames;
	}

	@Override
	public Map<String, Integer> getTableColumnsMap() {
		return DDMTrackingCodeModelImpl.TABLE_COLUMNS_MAP;
	}

	@Override
	public String getTableName() {
		return "DDMTrackingCode";
	}

	@Override
	public List<String[]> getUniqueIndexColumnNames() {
		return _uniqueIndexColumnNames;
	}

	private static final Map<CTColumnResolutionType, Set<String>>
		_ctColumnNamesMap = new EnumMap<CTColumnResolutionType, Set<String>>(
			CTColumnResolutionType.class);
	private static final List<String> _mappingTableNames =
		new ArrayList<String>();
	private static final List<String[]> _uniqueIndexColumnNames =
		new ArrayList<String[]>();

	static {
		Set<String> ctControlColumnNames = new HashSet<String>();
		Set<String> ctIgnoreColumnNames = new HashSet<String>();
		Set<String> ctMergeColumnNames = new HashSet<String>();
		Set<String> ctStrictColumnNames = new HashSet<String>();

		ctControlColumnNames.add("mvccVersion");
		ctControlColumnNames.add("ctCollectionId");
		ctStrictColumnNames.add("trackingCode");

		_ctColumnNamesMap.put(
			CTColumnResolutionType.CONTROL, ctControlColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.IGNORE, ctIgnoreColumnNames);
		_ctColumnNamesMap.put(CTColumnResolutionType.MERGE, ctMergeColumnNames);
		_ctColumnNamesMap.put(
			CTColumnResolutionType.PK,
			Collections.singleton("formInstanceRecordId"));
		_ctColumnNamesMap.put(
			CTColumnResolutionType.STRICT, ctStrictColumnNames);

		_uniqueIndexColumnNames.add(new String[] {"trackingCode"});
	}

	/**
	 * Initializes the ddm tracking code persistence.
	 */
	@Activate
	public void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_argumentsResolverServiceRegistration = _bundleContext.registerService(
			ArgumentsResolver.class,
			new DDMTrackingCodeModelArgumentsResolver(),
			MapUtil.singletonDictionary(
				"model.class.name", DDMTrackingCode.class.getName()));

		_finderPathWithPaginationFindAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITH_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathWithoutPaginationFindAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "findAll", new String[0],
			new String[0], true);

		_finderPathCountAll = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countAll",
			new String[0], new String[0], false);

		_finderPathFetchByTrackingCode = _createFinderPath(
			FINDER_CLASS_NAME_ENTITY, "fetchByTrackingCode",
			new String[] {String.class.getName()},
			new String[] {"trackingCode"}, true);

		_finderPathCountByTrackingCode = _createFinderPath(
			FINDER_CLASS_NAME_LIST_WITHOUT_PAGINATION, "countByTrackingCode",
			new String[] {String.class.getName()},
			new String[] {"trackingCode"}, false);
	}

	@Deactivate
	public void deactivate() {
		entityCache.removeCache(DDMTrackingCodeImpl.class.getName());

		_argumentsResolverServiceRegistration.unregister();

		for (ServiceRegistration<FinderPath> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.SERVICE_CONFIGURATION_FILTER,
		unbind = "-"
	)
	public void setConfiguration(Configuration configuration) {
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	@Override
	@Reference(
		target = DDMPersistenceConstants.ORIGIN_BUNDLE_SYMBOLIC_NAME_FILTER,
		unbind = "-"
	)
	public void setSessionFactory(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	private BundleContext _bundleContext;

	@Reference
	protected CTPersistenceHelper ctPersistenceHelper;

	@Reference
	protected EntityCache entityCache;

	@Reference
	protected FinderCache finderCache;

	private static final String _SQL_SELECT_DDMTRACKINGCODE =
		"SELECT ddmTrackingCode FROM DDMTrackingCode ddmTrackingCode";

	private static final String _SQL_SELECT_DDMTRACKINGCODE_WHERE =
		"SELECT ddmTrackingCode FROM DDMTrackingCode ddmTrackingCode WHERE ";

	private static final String _SQL_COUNT_DDMTRACKINGCODE =
		"SELECT COUNT(ddmTrackingCode) FROM DDMTrackingCode ddmTrackingCode";

	private static final String _SQL_COUNT_DDMTRACKINGCODE_WHERE =
		"SELECT COUNT(ddmTrackingCode) FROM DDMTrackingCode ddmTrackingCode WHERE ";

	private static final String _ORDER_BY_ENTITY_ALIAS = "ddmTrackingCode.";

	private static final String _NO_SUCH_ENTITY_WITH_PRIMARY_KEY =
		"No DDMTrackingCode exists with the primary key ";

	private static final String _NO_SUCH_ENTITY_WITH_KEY =
		"No DDMTrackingCode exists with the key {";

	private static final Log _log = LogFactoryUtil.getLog(
		DDMTrackingCodePersistenceImpl.class);

	private FinderPath _createFinderPath(
		String cacheName, String methodName, String[] params,
		String[] columnNames, boolean baseModelResult) {

		FinderPath finderPath = new FinderPath(
			cacheName, methodName, params, columnNames, baseModelResult);

		if (!cacheName.equals(FINDER_CLASS_NAME_LIST_WITH_PAGINATION)) {
			_serviceRegistrations.add(
				_bundleContext.registerService(
					FinderPath.class, finderPath,
					MapUtil.singletonDictionary("cache.name", cacheName)));
		}

		return finderPath;
	}

	private Set<ServiceRegistration<FinderPath>> _serviceRegistrations =
		new HashSet<>();
	private ServiceRegistration<ArgumentsResolver>
		_argumentsResolverServiceRegistration;

	private static class DDMTrackingCodeModelArgumentsResolver
		implements ArgumentsResolver {

		@Override
		public Object[] getArguments(
			FinderPath finderPath, BaseModel<?> baseModel, boolean checkColumn,
			boolean original) {

			String[] columnNames = finderPath.getColumnNames();

			if ((columnNames == null) || (columnNames.length == 0)) {
				if (baseModel.isNew()) {
					return FINDER_ARGS_EMPTY;
				}

				return null;
			}

			DDMTrackingCodeModelImpl ddmTrackingCodeModelImpl =
				(DDMTrackingCodeModelImpl)baseModel;

			long columnBitmask = ddmTrackingCodeModelImpl.getColumnBitmask();

			if (!checkColumn || (columnBitmask == 0)) {
				return _getValue(
					ddmTrackingCodeModelImpl, columnNames, original);
			}

			Long finderPathColumnBitmask = _finderPathColumnBitmasksCache.get(
				finderPath);

			if (finderPathColumnBitmask == null) {
				finderPathColumnBitmask = 0L;

				for (String columnName : columnNames) {
					finderPathColumnBitmask |=
						ddmTrackingCodeModelImpl.getColumnBitmask(columnName);
				}

				_finderPathColumnBitmasksCache.put(
					finderPath, finderPathColumnBitmask);
			}

			if ((columnBitmask & finderPathColumnBitmask) != 0) {
				return _getValue(
					ddmTrackingCodeModelImpl, columnNames, original);
			}

			return null;
		}

		private static Object[] _getValue(
			DDMTrackingCodeModelImpl ddmTrackingCodeModelImpl,
			String[] columnNames, boolean original) {

			Object[] arguments = new Object[columnNames.length];

			for (int i = 0; i < arguments.length; i++) {
				String columnName = columnNames[i];

				if (original) {
					arguments[i] =
						ddmTrackingCodeModelImpl.getColumnOriginalValue(
							columnName);
				}
				else {
					arguments[i] = ddmTrackingCodeModelImpl.getColumnValue(
						columnName);
				}
			}

			return arguments;
		}

		private static final Map<FinderPath, Long>
			_finderPathColumnBitmasksCache = new ConcurrentHashMap<>();

	}

}