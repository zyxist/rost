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
package com.zyxist.rost.gems;

import com.zyxist.rost.api.ServiceComposer;
import com.zyxist.rost.api.ServiceExecutor;
import com.zyxist.rost.api.ServiceLauncher;
import com.zyxist.rost.test.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.zyxist.rost.test.Duperele.stableSet;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

public class RostTest {
	private Runnable runnableCode;
	private FooService.Launcher fooService;
	private BarService.Launcher barService;
	private JoeService.Launcher joeService;
	private GooService.Launcher gooService;
	private HooService.Launcher hooService;
	private ServiceOrder order;

	public RostTest() {
	}


	@BeforeEach
	public void prepareMocks() {
		runnableCode = mock(Runnable.class);
		order = new ServiceOrder();
		fooService = new FooService.Launcher().ordered(order);
		barService = new BarService.Launcher().ordered(order);
		joeService = new JoeService.Launcher().ordered(order);
		gooService = new GooService.Launcher().ordered(order);
		hooService = new HooService.Launcher().ordered(order);
	}

	@Test
	public void shouldWorkInQuickstartConfiguration() {
		// Given
		ServiceExecutor executor = Rost.executor();
		ServiceComposer composer = Rost.composer();
		Set<ServiceLauncher> services = stableSet(hooService, joeService, gooService, barService, fooService);

		// When
		executor.execute(Rost.compose(composer, services), runnableCode);

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


}
