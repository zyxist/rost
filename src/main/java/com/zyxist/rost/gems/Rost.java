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

public final class Rost {
	private Rost() {}

	public static ServiceExecutor executor() {
		return new StandardServiceExecutor();
	}

	public static ServiceComposer composer() {
		return new DAGServiceComposer();
	}

	public static void execute(Set<ServiceLauncher> launchers, Runnable serviceAwareCode) {
		ServiceExecutor executor = executor();
		executor.execute(compose(launchers), serviceAwareCode);
	}

	public static Supplier<Stream<ServiceDescription>> compose(Set<ServiceLauncher> launchers) {
		return new ComposingSource(composer(), new SimpleSource(launchers));
	}

	public static Supplier<Stream<ServiceDescription>> compose(ServiceComposer composer, Set<ServiceLauncher> launchers) {
		return new ComposingSource(composer, new SimpleSource(launchers));
	}

	public static Supplier<Stream<ServiceDescription>> compose(ServiceComposer composer, ServiceLauncher ... launchers) {
		return new ComposingSource(composer, new SimpleSource(List.of(launchers)));
	}

	public static Supplier<Stream<ServiceDescription>> services(ServiceLauncher ... launchers) {
		return new SimpleSource(List.of(launchers));
	}
}
