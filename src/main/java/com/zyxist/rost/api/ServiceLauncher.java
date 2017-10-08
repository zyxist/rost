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

/**
 * Service launcher implements a lifecycle for some service. It can be implemented directly by the
 * service implementation, but it can be also a distinct class. The execution of the lifecycle methods
 * is performed by the implementation of {@link ServiceExecutor} interface.
 *
 * <p>Every service launcher MUST be annotated either with {@link ProvidesService} or {@link LifecycleHook}.</p>
 *
 * <p>Service launchers CAN be annotated with {@link RequiresServices} to specify the services that are
 * expected to start earlier. The actual sorting of the services is performed by a {@link ServiceComposer}
 * implementation.</p>
 *
 * <p>Recommended practices for writing services:</p>
 *
 * <ul>
 *   <li>Each service should consist of a public interface and an implementation,</li>
 *   <li>The {@link ProvidesService} and {@link RequiresServices} shall refer to the service interfaces,
 * not implementations,</li>
 *   <li>Service interface can have multiple implementations (and be provided by multiple launchers),</li>
 *   <li>Rost only guarantees that the services specified {@link RequiresServices} will be started
 * earlier - no other assumptions about the ordering should be made.</li>
 *   <li>Services are stopped in the reverse order.</li>
 *   <li>If {@link #start()} has completed successfully, {@link #stop()} is guaranteed to be called.</li>
 * </ul>
 */
public interface ServiceLauncher {
	/**
	 * Initialization code for some service. If the method completes without an exception, {@link #stop()}
	 * is guaranteed to be called.
	 *
	 * <p>To report errors, any exception class can be used. Rost provides a generic exception that can
	 * be used for reporting lifecycle issues: {@link com.zyxist.rost.exception.ServiceException}.</p>
	 *
	 * @throws Exception Startup error (terminates the startup of remaining services).
	 */
	default void start() throws Exception {
	}

	/**
	 * Finalization code for some service. All services that have been started are guaranteed to stop,
	 * even in case of exceptions.
	 * <p>
	 * <p>To report errors, any exception class can be used. Rost provides a generic exception that can
	 * be used for reporting lifecycle issues: {@link com.zyxist.rost.exception.ServiceException}.</p>
	 *
	 * @throws Exception Stop error (does not terminate the stopping of remaining services).
	 */
	default void stop() throws Exception {
	}
}
