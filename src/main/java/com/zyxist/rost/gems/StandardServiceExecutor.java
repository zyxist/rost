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
package com.zyxist.rost.gems;

import com.zyxist.rost.api.ServiceExecutor;
import com.zyxist.rost.meta.ServiceDescription;
import com.zyxist.rost.meta.ServiceFailure;
import com.zyxist.rost.utils.BasicErrorHandler;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class StandardServiceExecutor implements ServiceExecutor {
	private final Consumer<ServiceFailure> errorHandler;

	public StandardServiceExecutor() {
		this(new BasicErrorHandler());
	}

	public StandardServiceExecutor(Consumer<ServiceFailure> errorHandler) {
		this.errorHandler = Objects.requireNonNull(errorHandler);
	}

	@Override
	public void execute(Supplier<Stream<ServiceDescription>> serviceSource, Runnable serviceAwareCode) {
		List<ServiceDescription> orderedServices = new ArrayList<>();
		List<ServiceDescription> reverseOrderedServices = new ArrayList<>();
		serviceSource.get().forEachOrdered(svc -> {
			orderedServices.add(svc);
			reverseOrderedServices.add(0, svc);
		});
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
