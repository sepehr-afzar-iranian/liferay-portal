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

package com.liferay.dynamic.data.mapping.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.exception.NoSuchTrackingCodeException;
import com.liferay.dynamic.data.mapping.model.DDMTrackingCode;
import com.liferay.dynamic.data.mapping.service.DDMTrackingCodeLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.persistence.DDMTrackingCodePersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMTrackingCodeUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @generated
 */
@RunWith(Arquillian.class)
public class DDMTrackingCodePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.dynamic.data.mapping.service"));

	@Before
	public void setUp() {
		_persistence = DDMTrackingCodeUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DDMTrackingCode> iterator = _ddmTrackingCodes.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMTrackingCode ddmTrackingCode = _persistence.create(pk);

		Assert.assertNotNull(ddmTrackingCode);

		Assert.assertEquals(ddmTrackingCode.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DDMTrackingCode newDDMTrackingCode = addDDMTrackingCode();

		_persistence.remove(newDDMTrackingCode);

		DDMTrackingCode existingDDMTrackingCode =
			_persistence.fetchByPrimaryKey(newDDMTrackingCode.getPrimaryKey());

		Assert.assertNull(existingDDMTrackingCode);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDDMTrackingCode();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMTrackingCode newDDMTrackingCode = _persistence.create(pk);

		newDDMTrackingCode.setMvccVersion(RandomTestUtil.nextLong());

		newDDMTrackingCode.setCtCollectionId(RandomTestUtil.nextLong());

		newDDMTrackingCode.setCompanyId(RandomTestUtil.nextLong());

		newDDMTrackingCode.setTrackingCode(RandomTestUtil.randomString());

		_ddmTrackingCodes.add(_persistence.update(newDDMTrackingCode));

		DDMTrackingCode existingDDMTrackingCode = _persistence.findByPrimaryKey(
			newDDMTrackingCode.getPrimaryKey());

		Assert.assertEquals(
			existingDDMTrackingCode.getMvccVersion(),
			newDDMTrackingCode.getMvccVersion());
		Assert.assertEquals(
			existingDDMTrackingCode.getCtCollectionId(),
			newDDMTrackingCode.getCtCollectionId());
		Assert.assertEquals(
			existingDDMTrackingCode.getCompanyId(),
			newDDMTrackingCode.getCompanyId());
		Assert.assertEquals(
			existingDDMTrackingCode.getFormInstanceRecordId(),
			newDDMTrackingCode.getFormInstanceRecordId());
		Assert.assertEquals(
			existingDDMTrackingCode.getTrackingCode(),
			newDDMTrackingCode.getTrackingCode());
	}

	@Test
	public void testCountByTrackingCode() throws Exception {
		_persistence.countByTrackingCode("");

		_persistence.countByTrackingCode("null");

		_persistence.countByTrackingCode((String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DDMTrackingCode newDDMTrackingCode = addDDMTrackingCode();

		DDMTrackingCode existingDDMTrackingCode = _persistence.findByPrimaryKey(
			newDDMTrackingCode.getPrimaryKey());

		Assert.assertEquals(existingDDMTrackingCode, newDDMTrackingCode);
	}

	@Test(expected = NoSuchTrackingCodeException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DDMTrackingCode> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DDMTrackingCode", "mvccVersion", true, "ctCollectionId", true,
			"companyId", true, "formInstanceRecordId", true, "trackingCode",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DDMTrackingCode newDDMTrackingCode = addDDMTrackingCode();

		DDMTrackingCode existingDDMTrackingCode =
			_persistence.fetchByPrimaryKey(newDDMTrackingCode.getPrimaryKey());

		Assert.assertEquals(existingDDMTrackingCode, newDDMTrackingCode);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMTrackingCode missingDDMTrackingCode = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingDDMTrackingCode);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DDMTrackingCode newDDMTrackingCode1 = addDDMTrackingCode();
		DDMTrackingCode newDDMTrackingCode2 = addDDMTrackingCode();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMTrackingCode1.getPrimaryKey());
		primaryKeys.add(newDDMTrackingCode2.getPrimaryKey());

		Map<Serializable, DDMTrackingCode> ddmTrackingCodes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ddmTrackingCodes.size());
		Assert.assertEquals(
			newDDMTrackingCode1,
			ddmTrackingCodes.get(newDDMTrackingCode1.getPrimaryKey()));
		Assert.assertEquals(
			newDDMTrackingCode2,
			ddmTrackingCodes.get(newDDMTrackingCode2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DDMTrackingCode> ddmTrackingCodes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmTrackingCodes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DDMTrackingCode newDDMTrackingCode = addDDMTrackingCode();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMTrackingCode.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DDMTrackingCode> ddmTrackingCodes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmTrackingCodes.size());
		Assert.assertEquals(
			newDDMTrackingCode,
			ddmTrackingCodes.get(newDDMTrackingCode.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DDMTrackingCode> ddmTrackingCodes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmTrackingCodes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DDMTrackingCode newDDMTrackingCode = addDDMTrackingCode();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMTrackingCode.getPrimaryKey());

		Map<Serializable, DDMTrackingCode> ddmTrackingCodes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmTrackingCodes.size());
		Assert.assertEquals(
			newDDMTrackingCode,
			ddmTrackingCodes.get(newDDMTrackingCode.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			DDMTrackingCodeLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<DDMTrackingCode>() {

				@Override
				public void performAction(DDMTrackingCode ddmTrackingCode) {
					Assert.assertNotNull(ddmTrackingCode);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		DDMTrackingCode newDDMTrackingCode = addDDMTrackingCode();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DDMTrackingCode.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"formInstanceRecordId",
				newDDMTrackingCode.getFormInstanceRecordId()));

		List<DDMTrackingCode> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		DDMTrackingCode existingDDMTrackingCode = result.get(0);

		Assert.assertEquals(existingDDMTrackingCode, newDDMTrackingCode);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DDMTrackingCode.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"formInstanceRecordId", RandomTestUtil.nextLong()));

		List<DDMTrackingCode> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		DDMTrackingCode newDDMTrackingCode = addDDMTrackingCode();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DDMTrackingCode.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("formInstanceRecordId"));

		Object newFormInstanceRecordId =
			newDDMTrackingCode.getFormInstanceRecordId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"formInstanceRecordId",
				new Object[] {newFormInstanceRecordId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingFormInstanceRecordId = result.get(0);

		Assert.assertEquals(
			existingFormInstanceRecordId, newFormInstanceRecordId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DDMTrackingCode.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("formInstanceRecordId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"formInstanceRecordId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DDMTrackingCode newDDMTrackingCode = addDDMTrackingCode();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newDDMTrackingCode.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromDatabase()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(true);
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromSession()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(false);
	}

	private void _testResetOriginalValuesWithDynamicQuery(boolean clearSession)
		throws Exception {

		DDMTrackingCode newDDMTrackingCode = addDDMTrackingCode();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DDMTrackingCode.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"formInstanceRecordId",
				newDDMTrackingCode.getFormInstanceRecordId()));

		List<DDMTrackingCode> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(DDMTrackingCode ddmTrackingCode) {
		Assert.assertEquals(
			ddmTrackingCode.getTrackingCode(),
			ReflectionTestUtil.invoke(
				ddmTrackingCode, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "trackingCode"));
	}

	protected DDMTrackingCode addDDMTrackingCode() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMTrackingCode ddmTrackingCode = _persistence.create(pk);

		ddmTrackingCode.setMvccVersion(RandomTestUtil.nextLong());

		ddmTrackingCode.setCtCollectionId(RandomTestUtil.nextLong());

		ddmTrackingCode.setCompanyId(RandomTestUtil.nextLong());

		ddmTrackingCode.setTrackingCode(RandomTestUtil.randomString());

		_ddmTrackingCodes.add(_persistence.update(ddmTrackingCode));

		return ddmTrackingCode;
	}

	private List<DDMTrackingCode> _ddmTrackingCodes =
		new ArrayList<DDMTrackingCode>();
	private DDMTrackingCodePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}