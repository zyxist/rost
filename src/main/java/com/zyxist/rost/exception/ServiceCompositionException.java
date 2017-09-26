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
package com.zyxist.rost.exception;

import java.util.Objects;

/**
 * Notifies about issues with service composition process, such as missing
 * information. These issues shall be fixed by the programmer in order to
 * run the application.
 */
public class ServiceCompositionException extends IllegalArgumentException {
	private final Class<?> serviceLauncherClass;

	public ServiceCompositionException(String message, Class<?> serviceLauncherClass) {
		super(message);
		this.serviceLauncherClass = Objects.requireNonNull(serviceLauncherClass);
	}

	public Class<?> getServiceLauncherClass() {
		return serviceLauncherClass;
	}
}
