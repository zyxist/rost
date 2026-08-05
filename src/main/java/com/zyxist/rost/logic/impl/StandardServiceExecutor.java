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
package com.zyxist.rost.logic.impl;

import com.zyxist.rost.logic.ServiceExecutor;
import com.zyxist.rost.logic.metadata.ServiceDescription;
import com.zyxist.rost.logic.metadata.ServiceFailure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public final class StandardServiceExecutor implements ServiceExecutor {
	private final Consumer<ServiceFailure> errorHandler;

	public StandardServiceExecutor(Consumer<ServiceFailure> errorHandler) {
		this.errorHandler = Objects.requireNonNull(errorHandler);
	}

	@Override
	public void execute(List<ServiceDescription> services, Runnable serviceAwareCode) {
		List<ServiceDescription> orderedServices = new ArrayList<>();
		List<ServiceDescription> reverseOrderedServices = new ArrayList<>();
		for (var service: services) {
			orderedServices.add(service);
			reverseOrderedServices.addFirst(service);
		}
		Set<ServiceDescription> correctlyStarted = new HashSet<>();
		try {
			if (startServices(orderedServices, correctlyStarted)) {
				serviceAwareCode.run();
			}
		} finally {
			stopServices(reverseOrderedServices, correctlyStarted);
		}
	}

	private boolean startServices(List<ServiceDescription> services, Set<ServiceDescription> correctlyStarted) {
		for (ServiceDescription svc : services) {
			try {
				svc.getLauncher().start();
				correctlyStarted.add(svc);
			} catch (Exception exception) {
				this.errorHandler.accept(new ServiceFailure(svc, ServiceFailure.StartupPhase.START, exception));
				return false;
			}
		}
		return true;
	}

	private void stopServices(List<ServiceDescription> services, Set<ServiceDescription> correctlyStarted) {
		for (ServiceDescription svc : services) {
			if (correctlyStarted.contains(svc)) {
				try {
					svc.getLauncher().stop();
				} catch (Exception exception) {
					this.errorHandler.accept(new ServiceFailure(svc, ServiceFailure.StartupPhase.STOP, exception));
				}
			}
		}
	}
}
