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

package com.liferay.oauth2.provider.token.endpoint.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth2.provider.internal.test.TestAuthorizationGrant;
import com.liferay.oauth2.provider.internal.test.TestJWTAssertionAuthorizationGrant;
import com.liferay.oauth2.provider.internal.test.util.JWTAssertionUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.BundleActivator;

/**
 * @author Arthur Chan
 */
@RunWith(Arquillian.class)
public class JWTAssertAuthorizationGrantTest
	extends BaseAuthorizationGrantTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testGrantWithCorrectAudience() throws Exception {
		User user = UserTestUtil.getAdminUser(PortalUtil.getDefaultCompanyId());

		TestJWTAssertionAuthorizationGrant testJWTAssertionAuthorizationGrant =
			new TestJWTAssertionAuthorizationGrant(
				TEST_CLIENT_ID_1, null, user.getUuid(), getTokenWebTarget());

		Assert.assertTrue(
			Validator.isNotNull(
				getAccessToken(
					testJWTAssertionAuthorizationGrant,
					testClientAuthentications.get(TEST_CLIENT_ID_1))));
	}

	@Test
	public void testGrantWithWrongAudience() throws Exception {
		User user = UserTestUtil.getAdminUser(PortalUtil.getDefaultCompanyId());

		TestJWTAssertionAuthorizationGrant testJWTAssertionAuthorizationGrant =
			new TestJWTAssertionAuthorizationGrant(
				TEST_CLIENT_ID_1, null, user.getUuid(),
				getJsonWebTarget("wrongPath"));

		Assert.assertTrue(
			Validator.isNull(
				getAccessToken(
					testJWTAssertionAuthorizationGrant,
					testClientAuthentications.get(TEST_CLIENT_ID_1))));
	}

	@Override
	protected TestAuthorizationGrant getAuthorizationGrant(String clientId) {
		User user = null;

		try {
			user = UserTestUtil.getAdminUser(PortalUtil.getDefaultCompanyId());

			return new TestJWTAssertionAuthorizationGrant(
				TEST_CLIENT_ID_1, null, user.getUuid(), getTokenWebTarget());
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Override
	protected BundleActivator getBundleActivator() {
		return new JWTBearerGrantTestPreparatorBundleActivator();
	}

	private static class JWTBearerGrantTestPreparatorBundleActivator
		extends BaseAuthorizationGrantTestCase.TestPreparatorBundleActivator {

		@Override
		protected void prepareTest() throws Exception {
			createFactoryConfiguration(
				"com.liferay.oauth2.provider.rest.internal.configuration." +
					"OAuth2InAssertionConfiguration",
				HashMapDictionaryBuilder.<String, Object>put(
					"oauth2.in.assertion.issuer", TEST_CLIENT_ID_1
				).put(
					"oauth2.in.assertion.signature.json.web.key.set",
					JWTAssertionUtil.JWKS
				).put(
					"oauth2.in.assertion.user.auth.type", "UUID"
				).build());

			super.prepareTest();
		}

	}

}