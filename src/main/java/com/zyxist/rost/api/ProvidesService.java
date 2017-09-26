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

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Target;

/**
 * The annotation shall be put on a service launcher. It informs that the launcher manages
 * the lifecycle of the given service, represented by some interface.
 * <p>
 * <p>This information is used for resolving the dependencies between the startable services
 * ({@link ServiceLauncher}) to ensure they start in correct order.
 * <p>
 * <p>If the service launcher does not manage the lifecycle of any particular service, but
 * just performs some action, it shall be annotated with {@link LifecycleHook}. The
 * annotations {@link LifecycleHook} and {@link ProvidesService} cannot be used together.
 *
 * @see LifecycleHook
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface ProvidesService {
	/**
	 * @return Service interface, whose lifecycle is managed.
	 */
	Class<?> value();
}