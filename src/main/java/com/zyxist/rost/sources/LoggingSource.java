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
package com.zyxist.rost.sources;

import com.zyxist.rost.api.DecoratingServiceLauncher;
import com.zyxist.rost.api.ServiceLauncher;
import com.zyxist.rost.api.ServiceSource;
import com.zyxist.rost.meta.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Decorates another service source, and provides a logging for the service launchers. The source
 * uses SLF4j as a logging backend (it must be loaded as a dependency and a module by the application).
 */
public class LoggingSource implements ServiceSource {
	private static final Logger LOG = LoggerFactory.getLogger(LoggingSource.class);
	private final Supplier<Stream<ServiceDescription>> decoratedSource;

	public LoggingSource(Supplier<Stream<ServiceDescription>> decoratedSource) {
		this.decoratedSource = decoratedSource;
	}

	@Override
	public Stream<ServiceDescription> get() {
		return this.decoratedSource.get().map(
			(description) -> description.decorateWith(new LoggingLauncher(description)));
	}

	private static class LoggingLauncher implements DecoratingServiceLauncher {
		private final ServiceDescription decorated;

		LoggingLauncher(ServiceDescription decorated) {
			this.decorated = Objects.requireNonNull(decorated);
		}

		@Override
		public ServiceLauncher getDecoratedLauncher() {
			return decorated.getLauncher();
		}

		@Override
		public void start() throws Exception {
			LOG.info("Service '"+decorated.getName()+"': starting");
			decorated.getLauncher().start();
			LOG.info("Service '"+decorated.getName()+"': started");
		}

		@Override
		public void stop() throws Exception {
			LOG.info("Service '"+decorated.getName()+"': stopping");
			decorated.getLauncher().start();
			LOG.info("Service '"+decorated.getName()+"': stopped");
		}
	}
}
