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
import com.zyxist.rost.meta.ServiceDescription;
import com.zyxist.rost.sources.ComposingSource;
import com.zyxist.rost.sources.SimpleSource;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Entry point to Rost with factory methods for some default configurations.
 */
public final class Rost {
	private Rost() {}

	/**
	 * @return A default service executor
	 */
	public static ServiceExecutor executor() {
		return new StandardServiceExecutor();
	}

	/**
	 * @return A default service composer
	 */
	public static ServiceComposer composer() {
		return new DAGServiceComposer();
	}

	/**
	 * Starts the given set of services, and then executes the given piece of code.
	 * The services are started by the standard executor, and the default composer
	 * uses the {@link com.zyxist.rost.api.ProvidesService} and {@link com.zyxist.rost.api.RequiresServices}
	 * annotations to compute the launching order.
	 *
	 * @param serviceLaunchers Service launchers to start and stop
	 * @param serviceAwareCode Code to execute, when all services are started.
	 */
	public static void execute(Set<ServiceLauncher> serviceLaunchers, Runnable serviceAwareCode) {
		ServiceExecutor executor = executor();
		executor.execute(compose(serviceLaunchers), serviceAwareCode);
	}

	/**
	 * Creates a service source for {@link ServiceExecutor#execute(Supplier, Runnable)} method from
	 * the given set of service launchers. The returned service source applies a default composer
	 * to compute the launching order, using {@link com.zyxist.rost.api.ProvidesService} and
	 * {@link com.zyxist.rost.api.RequiresServices} annotations.
	 *
	 * @param serviceLaunchers Service launchers to wrap into a service source
	 * @return Service source with a composer
	 */
	public static Supplier<Stream<ServiceDescription>> compose(Set<ServiceLauncher> serviceLaunchers) {
		return new ComposingSource(composer(), new SimpleSource(serviceLaunchers));
	}

	/**
	 * Creates a service source for {@link ServiceExecutor#execute(Supplier, Runnable)} method from
	 * the given set of service launchers. The returned service source applies a custom service
	 * composer to compute the launching order.
	 *
	 * @param composer Service composer, used for computing the launching order.
	 * @param serviceLaunchers Service launchers to wrap into a service source
	 * @return Service source with a composer
	 */
	public static Supplier<Stream<ServiceDescription>> compose(ServiceComposer composer, Set<ServiceLauncher> serviceLaunchers) {
		return new ComposingSource(composer, new SimpleSource(serviceLaunchers));
	}

	/**
	 * Creates a service source for {@link ServiceExecutor#execute(Supplier, Runnable)} method from
	 * the given set of service launchers. The returned service source applies a custom service
	 * composer to compute the launching order.
	 *
	 * @param composer Service composer, used for computing the launching order.
	 * @param serviceLaunchers Service launchers to wrap into a service source
	 * @return Service source with a composer
	 */
	public static Supplier<Stream<ServiceDescription>> compose(ServiceComposer composer, ServiceLauncher ... serviceLaunchers) {
		return new ComposingSource(composer, new SimpleSource(List.of(serviceLaunchers)));
	}

	/**
	 * Creates a service source for {@link ServiceExecutor#execute(Supplier, Runnable)} method
	 * from the given set of services. The returned service does not have a composer, so it will
	 * not compute the launching order.
	 *
	 * @param serviceLaunchers Service launchers to wrap into a service source
	 * @return Simple service source created from the given launchers, without composition
	 */
	public static Supplier<Stream<ServiceDescription>> services(ServiceLauncher ... serviceLaunchers) {
		return new SimpleSource(List.of(serviceLaunchers));
	}
}
