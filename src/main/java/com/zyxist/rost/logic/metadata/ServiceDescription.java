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
package com.zyxist.rost.logic.metadata;

import com.zyxist.rost.ServiceLauncherDecorator;
import com.zyxist.rost.annotation.LifecycleHook;
import com.zyxist.rost.annotation.ProvidesService;
import com.zyxist.rost.annotation.RequiresServices;
import com.zyxist.rost.ServiceLauncher;
import com.zyxist.rost.exception.ServiceCompositionException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/// Keeps additional metadata about the given [ServiceLauncher], decoded from
/// annotations. The instances of this class are immutable, however this does not
/// apply to the underlying [ServiceLauncher].
public class ServiceDescription {
	private final ServiceLauncher launcher;
	private final String name;
	private final Optional<Class<?>> providedService;
	private final Set<Class<?>> requiredServices;

	public ServiceDescription(ServiceLauncher launcher) throws ServiceCompositionException {
		this.launcher = Objects.requireNonNull(launcher);
		this.providedService = this.discoverService(launcher);
		this.requiredServices = this.discoverRequirements(launcher);
		this.name = this.discoverServiceName(this.providedService, launcher);
	}

	private ServiceDescription(ServiceDescription source, ServiceLauncherDecorator decorated) {
		this.launcher = Objects.requireNonNull(decorated);
		this.providedService = source.providedService;
		this.requiredServices = source.requiredServices;
		this.name = source.name;
	}

	private Optional<Class<?>> discoverService(ServiceLauncher launcher) throws ServiceCompositionException {
		ProvidesService provision = launcher.getClass().getAnnotation(ProvidesService.class);
		LifecycleHook action = launcher.getClass().getAnnotation(LifecycleHook.class);
		if (!(null == provision ^ null == action)) {
			throw new ServiceCompositionException("The service launcher '" + launcher.getClass().getCanonicalName() +
				"' must be annotated with exactly one of the annotations: @ProvidesService, @LifecycleHook", launcher.getClass());
		}
		return null != provision ? Optional.of(provision.value()) : Optional.empty();
	}

	private String discoverServiceName(Optional<Class<?>> providedService, ServiceLauncher launcher) {
		if (providedService.isPresent()) {
			return providedService.get().getSimpleName();
		}
		return launcher.getClass().getSimpleName();
	}

	private Set<Class<?>> discoverRequirements(ServiceLauncher launcher) {
		RequiresServices requirements = launcher.getClass().getAnnotation(RequiresServices.class);
		Set<Class<?>> serviceSet = new HashSet<>();
		if (null != requirements) {
			for (Class<?> svcClass : requirements.value()) {
				serviceSet.add(svcClass);
			}
		}
		return serviceSet;
	}

	public ServiceLauncher getLauncher() {
		return launcher;
	}

	public String getName() {
		return name;
	}

	public Optional<Class<?>> getProvidedService() {
		return providedService;
	}

	public boolean hasRequiredServices() {
		return !requiredServices.isEmpty();
	}

	public Set<Class<?>> getRequiredServices() {
		return requiredServices;
	}

	@Override
	public String toString() {
		return "Service '" + name + "'";
	}

	/// @return Copy of this [ServiceDescription], but with the original [ServiceLauncher] replaced by a decorator.
	public ServiceDescription decorateWith(ServiceLauncherDecorator launcher) {
		if (launcher.getDecoratedLauncher() != this.launcher) {
			throw new IllegalArgumentException("The launcher passed to ServiceDescription.decorateWith() does not decorate the original service launcher.");
		}
		return new ServiceDescription(this, launcher);
	}
}
