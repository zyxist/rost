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
package com.zyxist.rost.api;

import com.zyxist.rost.gems.Rost;
import com.zyxist.rost.meta.ServiceDescription;

import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Executes the service launchers, by calling {@link ServiceLauncher#start()} and {@link ServiceLauncher#stop()}
 * methods. When all the services are started, the executor shall execute the custom code represented by the
 * {@link Runnable} interface (see {@link #execute(Supplier, Runnable)} method), and then it shall call
 * {@link ServiceLauncher#stop()} methods.
 * <p>
 * <p>The following assumptions must be met by all implementations:</p>
 * <ul>
 * <li>Each service that successfully started, must be also stopped.</li>
 * <li>Services shall be started in the order returned by the service source.</li>
 * <li>Services shall be stopped in the reverse startup order.</li>
 * <li>Exceptions thrown by {@link ServiceLauncher#stop()} shall not terminate stopping of other remaining services.</li>
 * </ul>
 */
public interface ServiceExecutor {
	/**
	 * Starts all the services, executes the custom code, and then stops the services.
	 * To provide services to start, you must create a {@link ServiceSource}. The collection of
	 * built-in sources can be found in {@link com.zyxist.rost.sources} package, and there
	 * are convenient factory methods for them in {@link Rost} class.
	 *
	 * @param serviceSource    Provides services to the executor.
	 * @param serviceAwareCode Custom code to execute, when all the services are started.
	 */
	void execute(Supplier<Stream<ServiceDescription>> serviceSource, Runnable serviceAwareCode);
}
