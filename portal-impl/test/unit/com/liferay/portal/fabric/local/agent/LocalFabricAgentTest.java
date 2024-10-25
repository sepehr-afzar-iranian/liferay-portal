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

package com.liferay.portal.fabric.local.agent;

import com.liferay.petra.concurrent.DefaultNoticeableFuture;
import com.liferay.petra.process.ProcessCallable;
import com.liferay.petra.process.ProcessException;
import com.liferay.portal.fabric.agent.FabricAgent;
import com.liferay.portal.fabric.status.LocalFabricStatus;
import com.liferay.portal.fabric.worker.FabricWorker;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.CodeCoverageAssertor;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class LocalFabricAgentTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			CodeCoverageAssertor.INSTANCE, LiferayUnitTestRule.INSTANCE);

	@Test
	public void testConstructor() {
		FabricAgent fabricAgent = new LocalFabricAgent(
			new EmbeddedProcessExecutor());

		Assert.assertSame(
			LocalFabricStatus.INSTANCE, fabricAgent.getFabricStatus());

		Collection<? extends FabricWorker<?>> fabricWorkers =
			fabricAgent.getFabricWorkers();

		Assert.assertTrue(fabricWorkers.toString(), fabricWorkers.isEmpty());

		try {
			fabricWorkers.clear();

			Assert.fail();
		}
		catch (UnsupportedOperationException unsupportedOperationException) {
		}
	}

	@Test
	public void testExecute() throws Exception {
		FabricAgent fabricAgent = new LocalFabricAgent(
			new EmbeddedProcessExecutor());

		Collection<? extends FabricWorker<?>> fabricWorkers =
			fabricAgent.getFabricWorkers();

		Assert.assertTrue(fabricWorkers.toString(), fabricWorkers.isEmpty());

		final String result = "Test result";

		FabricWorker<String> fabricWorker = fabricAgent.execute(
			null,
			new ProcessCallable<String>() {

				@Override
				public String call() {
					return result;
				}

			});

		Assert.assertEquals(fabricWorkers.toString(), 1, fabricWorkers.size());
		Assert.assertTrue(
			fabricWorkers.toString(), fabricWorkers.contains(fabricWorker));

		DefaultNoticeableFuture<String> defaultNoticeableFuture =
			(DefaultNoticeableFuture<String>)
				fabricWorker.getProcessNoticeableFuture();

		defaultNoticeableFuture.run();

		Assert.assertEquals(result, defaultNoticeableFuture.get());

		Assert.assertTrue(fabricWorkers.toString(), fabricWorkers.isEmpty());

		final ProcessException processException = new ProcessException(
			"Test exception");

		fabricWorker = fabricAgent.execute(
			null,
			new ProcessCallable<String>() {

				@Override
				public String call() throws ProcessException {
					throw processException;
				}

			});

		Assert.assertEquals(fabricWorkers.toString(), 1, fabricWorkers.size());
		Assert.assertTrue(
			fabricWorkers.toString(), fabricWorkers.contains(fabricWorker));

		defaultNoticeableFuture =
			(DefaultNoticeableFuture<String>)
				fabricWorker.getProcessNoticeableFuture();

		defaultNoticeableFuture.run();

		try {
			defaultNoticeableFuture.get();

			Assert.fail();
		}
		catch (ExecutionException executionException) {
			Assert.assertSame(processException, executionException.getCause());
		}

		Assert.assertTrue(fabricWorkers.toString(), fabricWorkers.isEmpty());
	}

}