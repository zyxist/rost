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
package com.zyxist.rost.logic;

import com.zyxist.rost.ServiceLauncher;
import com.zyxist.rost.internal.BasicErrorHandler;
import com.zyxist.rost.logic.impl.StandardServiceExecutor;
import com.zyxist.rost.logic.metadata.ServiceDescription;
import com.zyxist.rost.logic.metadata.ServiceFailure;

import java.util.List;
import java.util.function.Consumer;

/// Executes the [ServiceLauncher] instances. The implementations shall follow the rules below:
///
///  - Each service that successfully starts, **MUST** stop.
///  - Services shall start in the provided order.
///  - Services shall stop in the reverse order.
///  - After starting all services, run the `serviceAwareCode`
///  - Do not attempt to stop a service that failed to start.
///  - Exceptions thrown from [ServiceLauncher#stop()] **MUST NOT** prevent other services from stopping.
public interface ServiceExecutor {
	/// The execution logic.
	///
	/// @param services List of services in the startup order
	/// @param serviceAwareCode The custom code block to execute, when all services start successfully.
	void execute(List<ServiceDescription> services, Runnable serviceAwareCode);

	/// @return Default service executor with the default error handler
	static ServiceExecutor defaultExecutor() {
		return withErrorHandler(new BasicErrorHandler());
	}

	/// @param errorHandler Code for handling errors thrown from [ServiceLauncher].
	/// @return Default service executor with the provided error handler
	static ServiceExecutor withErrorHandler(Consumer<ServiceFailure> errorHandler) {
		return new StandardServiceExecutor(errorHandler);
	}
}
