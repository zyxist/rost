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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Used for resolving the dependencies between the startable services ({@link ServiceLauncher})
 * to ensure they start in correct order. The annotation shall be put on a class that implements
 * some service. Specifies the interfaces of services (as defined by {@link ProvidesService} annotation)
 * that shall be started BEFORE this service.
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface RequiresServices {
    Class<?>[] value();
}