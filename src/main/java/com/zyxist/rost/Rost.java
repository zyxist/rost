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
package com.zyxist.rost;

import com.zyxist.rost.annotation.ProvidesService;
import com.zyxist.rost.annotation.RequiresServices;
import com.zyxist.rost.logic.DependencyResolutionComposer;
import com.zyxist.rost.logic.ServiceComposer;
import com.zyxist.rost.logic.ServiceExecutor;
import com.zyxist.rost.logic.StandardServiceExecutor;
import com.zyxist.rost.logic.metadata.ServiceDescription;
import com.zyxist.rost.sources.ServiceLauncherSource;
import com.zyxist.rost.sources.SimpleSource;
import org.jspecify.annotations.NonNull;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/// Rost provides a simple lifecycle management: starting a collection of services, executing
/// a custom code and stopping all services. The following components control the detailed behavior
/// of each of the steps:
///
///  - [ServiceComposer]: defines the starting and stopping order by resolving dependencies between services,
///  - [ServiceExecutor]: actual execution logic
///
/// The actual service can be any class that must be initialized in order to run the actual code of your
/// application. For each service, you create a corresponding [ServiceLauncher] implementation that
/// adds [ServiceLauncher#start()] and [ServiceLauncher#stop()] methods. You can also implement the interface
/// directly on your service class, but this is less preferred solution. To declare dependencies between
/// services, use [ProvidesService] and [RequiresServices] annotations placed on a [ServiceLauncher]
/// implementation.
public final class Rost {
	private final ServiceExecutor executor;
	private final ServiceComposer composer;

	private Rost(@NonNull ServiceComposer composer, @NonNull ServiceExecutor executor) {
		this.composer = Objects.requireNonNull(composer);
		this.executor = Objects.requireNonNull(executor);
	}

	public static Rost create() {
		return new Rost(new DependencyResolutionComposer(), new StandardServiceExecutor());
	}

	/// Creates a new immutable instance of [Rost] with the specified [ServiceComposer] implementation,
	/// responsible for determining the launch order.
	///
	/// @param composer Composer implementation to use
	/// @return New instance of [Rost]
	public Rost withComposer(@NonNull ServiceComposer composer) {
		return new Rost(Objects.requireNonNull(composer), this.executor);
	}

	/// Creates a new immutable instance of [Rost] with the specified [ServiceExecutor] implementation,
	/// responsible for executing the [ServiceLauncher] instances in the order defined by [ServiceComposer].
	///
	/// @param executor Executor implementation to use
	/// @return New instance of [Rost]
	public Rost withExecutor(@NonNull ServiceExecutor executor) {
		return new Rost(this.composer, Objects.requireNonNull(executor));
	}

	public @NonNull ServiceComposer getComposer() {
		return composer;
	}

	public @NonNull ServiceExecutor getExecutor() {
		return executor;
	}

	/// Launches the specified set of services
	public void launch(@NonNull Set<ServiceLauncher> serviceLaunchers, @NonNull Runnable serviceAwareCode) {
		launch(new SimpleSource(serviceLaunchers), serviceAwareCode);
	}

	/// Starts all services returned by the `source`, executes the `serviceAwareCode`, and then stops
	/// all services.
	///
	/// @param source Source for service launchers to execute
	/// @param serviceAwareCode The custom code to run, when all services start successfully
	public void launch(@NonNull ServiceLauncherSource source, @NonNull Runnable serviceAwareCode) {
		Objects.requireNonNull(source);
		Objects.requireNonNull(serviceAwareCode);
		executor.execute(
			composer.compose(
				source
					.provideServiceDescriptions()
					.collect(Collectors.toCollection(() -> new LinkedHashSet<>()))
			),
			serviceAwareCode
		);
	}
}
