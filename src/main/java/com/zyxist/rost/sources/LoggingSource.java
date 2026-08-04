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

import com.zyxist.rost.ServiceLauncherDecorator;
import com.zyxist.rost.ServiceLauncher;
import com.zyxist.rost.logic.metadata.ServiceDescription;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.stream.Stream;

/// Decorates the [ServiceLauncher] instances returned by another [ServiceLauncherSource]
/// with the logging functionality using the slf4j API facade. The calls to
/// [ServiceLauncher#start()] and [ServiceLauncher#stop()] will be logged.
public class LoggingSource implements ServiceLauncherSource {
	private final Logger logger;
	private final ServiceLauncherSource decorated;

	public LoggingSource(@NonNull ServiceLauncherSource decorated, @NonNull Logger logger) {
		this.decorated = Objects.requireNonNull(decorated);
		this.logger = Objects.requireNonNull(logger);
	}

	public LoggingSource(@NonNull ServiceLauncherSource decorated) {
		this(decorated, LoggerFactory.getLogger(LoggingSource.class));
	}

	@Override
	public @NonNull Stream<ServiceDescription> provideServiceDescriptions() {
		return this
			.decorated
			.provideServiceDescriptions()
			.map(description -> description
				.decorateWith(new LoggingLauncherDecorator(description, logger))
			);
	}

	private record LoggingLauncherDecorator(
		@NonNull ServiceDescription decorated,
		@NonNull Logger logger
	) implements ServiceLauncherDecorator {
		@Override
		public @NonNull ServiceLauncher getDecoratedLauncher() {
			return decorated.getLauncher();
		}

		@Override
		public void start() throws Exception {
			logger.atInfo().addArgument(decorated.getName()).log("Service '{}': starting");
			decorated.getLauncher().start();
			logger.atInfo().addArgument(decorated.getName()).log("Service '{}': started");
		}

		@Override
		public void stop() throws Exception {
			logger.atInfo().addArgument(decorated.getName()).log("Service '{}': stopping");
			decorated.getLauncher().start();
			logger.atInfo().addArgument(decorated.getName()).log("Service '{}': stopped");
		}
	}
}
