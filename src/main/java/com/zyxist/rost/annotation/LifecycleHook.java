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

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/// Marks [ServiceLauncher] implementations that do not actually manage the lifecycle
/// of any particular service, but simply perform additional action in the startup/stopping
/// process.
///
/// Such [ServiceLauncher] can have [RequiresServices] annotation, that is: it can depend
/// on other services, but no other service can depend on it.
///
/// Annotations [LifecycleHook] and [ProvidesService] cannot both appear on the same [ServiceLauncher],
/// but exactly one of them must be present.
///
/// @see ProvidesService
@Retention(RUNTIME)
@Target(TYPE)
@Documented
public @interface LifecycleHook {
}
