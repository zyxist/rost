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
package com.zyxist.rost.annotation;

import com.zyxist.rost.ServiceLauncher;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Target;

/// Defines the type of the service that the given [ServiceLauncher] initializes. You can later use
/// this type in [RequiresServices] to declare a dependency on this service launcher.
///
/// Annotations [LifecycleHook] and [ProvidesService] cannot both appear on the same [ServiceLauncher],
/// but exactly one of them must be present.
///
/// @see LifecycleHook
@Retention(RUNTIME)
@Target(TYPE)
@Documented
public @interface ProvidesService {
	/**
	 * @return Service interface, whose lifecycle is managed.
	 */
	Class<?> value();
}