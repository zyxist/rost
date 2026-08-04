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

import com.zyxist.rost.annotation.LifecycleHook;
import com.zyxist.rost.annotation.ProvidesService;
import com.zyxist.rost.annotation.RequiresServices;
import com.zyxist.rost.logic.ServiceComposer;
import com.zyxist.rost.logic.ServiceExecutor;

/// Implementation of a lifecycle for a service. The recommended approach is to create a separate launcher
/// class for each of your services, but you can also choose to implement it directly in your service class.
/// Each implementation **MUST** have either [ProvidesService] or [LifecycleHook] annotation. Optionally,
/// it **MAY** use [RequiresServices] annotation to define services that need to start before the current
/// service.
///
/// The library uses annotations over methods in [ServiceLauncher] interface to make the dependencies visible
/// in Javadoc and to provide access to the information to static code analysis tools.
public interface ServiceLauncher {
	/// Start logic for the service (for example: initializing connections, etc.). Contract:
	///
	///  - If the method completes without throwing an exception, [#stop()] is guaranteed to be called.
	///  - Any services listed in [RequiresServices] will start BEFORE this service.
	///
	/// You should not make any other assumptions about the starting order.
	///
	/// @throws Exception Allows throwing any checked exception without the need to wrap it.
	default void start() throws Exception {
	}

	/// Finalization logic for the service (for example: closing the connections, etc.). Contract:
	///
	///  - The method will be called, if the corresponding [#start()] method completes without an exception.
	///  - The method will be called even if an earlier service throws an exception from its [#stop()] method.
	///  - Any services listed in [RequiresServices] will stop AFTER this service.
	///
	/// You should not make any other assumptions about the starting order.
	///
	/// @throws Exception Allows throwing any checked exception without the need to wrap it.
	default void stop() throws Exception {
	}
}
