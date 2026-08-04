/*
 * Copyright (C) 2017, 2026 Tomasz "zyxist" Jędrzejewski
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
package com.zyxist.rost.sources;

import com.zyxist.rost.ServiceLauncher;
import com.zyxist.rost.logic.metadata.ServiceDescription;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

/// Uses a collection of [ServiceLauncher] as the source of services to run. You can build
/// the collection manually or get from your dependency injection container.
public class SimpleSource implements ServiceLauncherSource {
	private final Collection<ServiceLauncher> launchers;

	public SimpleSource(@NonNull Collection<ServiceLauncher> launchers) {
		this.launchers = Objects.requireNonNull(launchers);
	}

	@Override
	public @NonNull Stream<ServiceDescription> provideServiceDescriptions() {
		return launchers.stream()
			.map(ServiceDescription::new);
	}
}
