/*
 * Copyright (C) 2017 The Rost Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zyxist.rost;

import com.zyxist.rost.fixtures.BarService;
import com.zyxist.rost.fixtures.ExampleSourceDecorator;
import com.zyxist.rost.fixtures.FooService;
import com.zyxist.rost.fixtures.GooService;
import com.zyxist.rost.fixtures.HooService;
import com.zyxist.rost.fixtures.JoeService;
import com.zyxist.rost.fixtures.ServiceOrder;
import com.zyxist.rost.logic.DependencyResolutionComposer;
import com.zyxist.rost.logic.ServiceComposer;
import com.zyxist.rost.logic.ServiceExecutor;
import com.zyxist.rost.logic.StandardServiceExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.zyxist.rost.fixtures.Duperele.stableSet;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class RostTest {
	private Runnable runnableCode;
	private FooService.Launcher fooService;
	private BarService.Launcher barService;
	private JoeService.Launcher joeService;
	private GooService.Launcher gooService;
	private HooService.Launcher hooService;
	private ServiceOrder order;

	@BeforeEach
	void prepareMocks() {
		runnableCode = mock(Runnable.class);
		order = new ServiceOrder();
		fooService = new FooService.Launcher().ordered(order);
		barService = new BarService.Launcher().ordered(order);
		joeService = new JoeService.Launcher().ordered(order);
		gooService = new GooService.Launcher().ordered(order);
		hooService = new HooService.Launcher().ordered(order);
	}

	@Test
	void shouldWorkInQuickstartConfiguration() {
		// Given
		var rost = Rost.create();
		Set<ServiceLauncher> services = stableSet(hooService, joeService, gooService, barService, fooService);

		// When
		rost.launch(services, runnableCode);

		// Then
		assertAll("Services",
			() -> verify(runnableCode, times(1)).run(),
			() -> fooService.assertStartedAndStopped(),
			() -> barService.assertStartedAndStopped(),
			() -> joeService.assertStartedAndStopped(),
			() -> gooService.assertStartedAndStopped(),
			() -> hooService.assertStartedAndStopped()
		);
		assertAll("start/stop order",
			() -> order.assertStartOrder(gooService, fooService, hooService, joeService, barService),
			() -> order.assertStopOrder(barService, joeService, hooService, fooService, gooService)
		);
	}

	@Test
	void shouldReturnDefaultImplementationsWhenUnconfigured() {
		// given
		var rost = Rost.create();

		// when
		var composer = rost.getComposer();
		var executor = rost.getExecutor();

		// then
		assertInstanceOf(DependencyResolutionComposer.class, composer);
		assertInstanceOf(StandardServiceExecutor.class, executor);
	}

	@Test
	void shouldDecorateServiceLauncherSources() {
		// given
		var decorator = new ExampleSourceDecorator();
		var rost = Rost.create().withDecorator(decorator);
		Set<ServiceLauncher> services = stableSet(barService, fooService);

		// when
		rost.launch(services, runnableCode);

		// then
		assertAll(
			() -> decorator.assertStarted(fooService),
			() -> decorator.assertStopped(barService),
			() -> decorator.assertStarted(fooService),
			() -> decorator.assertStopped(barService)
		);
	}

	@Test
	void shouldAllowReplacingDefaultImplementations() {
		// given
		ServiceComposer customComposer = mock(ServiceComposer.class);
		ServiceExecutor customExecutor = mock(ServiceExecutor.class);

		// when
		var rost = Rost.create()
			.withComposer(customComposer)
			.withExecutor(customExecutor);

		// then
		assertSame(customComposer, rost.getComposer());
		assertSame(customExecutor, rost.getExecutor());
	}
}
