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
import com.zyxist.rost.meta.ServiceDescription;
import com.zyxist.rost.test.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.zyxist.rost.test.Duperele.stableSet;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DAGComposerTest {
	private ServiceComposer composer;
	private ServiceDescription fooService;
	private ServiceDescription barService;
	private ServiceDescription barService2;
	private ServiceDescription joeService;
	private ServiceDescription mooService;
	private ServiceDescription gooService;
	private ServiceDescription kooHook;

	@BeforeEach
	public void prepare() {
		composer = new DAGServiceComposer();
		fooService = new ServiceDescription(new FooService.Launcher());
		barService = new ServiceDescription(new BarService.Launcher());
		barService2 = new ServiceDescription(new BarService.AlternativeLauncher());
		joeService = new ServiceDescription(new JoeService.Launcher());
		mooService = new ServiceDescription(new MooService.Launcher());
		gooService = new ServiceDescription(new GooService.Launcher());
		kooHook = new ServiceDescription(new KooHook());
	}


	@Test
	public void shouldAcceptEmptyServiceSet() {
		// Given
		Set<ServiceDescription> services = Set.of();

		// When
		List<ServiceDescription> result = composer.compose(services);

		// Then
		assertEquals(0, result.size());
	}

	@Test
	public void shouldAcceptSingleService() {
		checkComposer(
			stableSet(fooService),
			List.of(fooService)
		);
	}

	@Test
	public void shouldHandleSimpleDependency() {
		checkComposer(
			stableSet(fooService, barService),
			List.of(fooService, barService)
		);
	}

	@Test
	public void shouldHandleSimpleDependencyReverseOrder() {
		checkComposer(
			stableSet(barService, fooService),
			List.of(fooService, barService)
		);
	}

	@Test
	public void shouldStableSortTreeWithCommonRoot() {
		checkComposer(
			stableSet(barService, joeService, fooService),
			List.of(fooService, barService, joeService)
		);
	}

	@Test
	public void shouldStableSortTreeWithCommonRootReverseOrder() {
		checkComposer(
			stableSet(joeService, barService, fooService),
			List.of(fooService, joeService, barService)
		);
	}

	@Test
	public void shouldHandleLifecycleHooks() {
		checkComposer(
			stableSet(kooHook, fooService, barService, gooService),
			List.of(fooService, gooService, barService, kooHook)
		);
	}

	@Test
	public void shouldHandleLifecycleHooksDifferentOrder() {
		checkComposer(
			stableSet(fooService, barService, kooHook, gooService),
			List.of(fooService, gooService, barService, kooHook)
		);
	}

	@Test
	public void shouldHandleMultipleLaunchersProvidingTheSameService() {
		checkComposer(
			stableSet(fooService, barService, barService2, gooService, mooService),
			List.of(fooService, gooService, barService, barService2, mooService)
		);
	}

	private void checkComposer(Set<ServiceDescription> input, List<ServiceDescription> expectedOutput) {
		// When
		List<ServiceDescription> result = composer.compose(input);

		// Then
		assertEquals(expectedOutput.size(), result.size());
		for (int i = 0; i < expectedOutput.size(); i++) {
			assertEquals(expectedOutput.get(i), result.get(i), "At position #"+i);
		}
	}
}
