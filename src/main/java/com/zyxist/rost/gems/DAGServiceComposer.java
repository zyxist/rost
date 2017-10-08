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

import com.zyxist.rost.api.ProvidesService;
import com.zyxist.rost.api.RequiresServices;
import com.zyxist.rost.api.ServiceComposer;
import com.zyxist.rost.meta.ServiceDescription;
import com.zyxist.rost.utils.Multimap;

import java.util.*;

/**
 * Implementation of Kahn's algorithm for topological sorting that produces the order the services
 * should be started in to satisfy their inter-dependencies. The annotations {@link ProvidesService}
 * and {@link RequiresServices} are used for constructing the service graph.
 */
public final class DAGServiceComposer implements ServiceComposer {

	@Override
	public List<ServiceDescription> compose(Set<ServiceDescription> unorderedServices) {
		Multimap<Class<?>, ServiceDescription> servicesReachableFrom = findServiceReachabilityGraph(unorderedServices);
		Deque<ServiceDescription> toProcess = findRoots(unorderedServices);
		List<ServiceDescription> output = new ArrayList<>(unorderedServices.size());

		while (!toProcess.isEmpty()) {
			ServiceDescription svc = toProcess.poll();
			output.add(svc);
			svc.getProvidedService().ifPresent(
				provided -> removeFromReachability(provided, servicesReachableFrom, toProcess)
			);
		}
		if (!servicesReachableFrom.isEmpty()) {
			throw new RuntimeException("Cycle detected in services!");
		}
		return output;
	}

	private void removeFromReachability(Class<?> providedSvc, Multimap<Class<?>, ServiceDescription> servicesReachableFrom, Deque<ServiceDescription> toProcess) {
		Iterator<ServiceDescription> reachableServices = servicesReachableFrom.get(providedSvc).iterator();
		while (reachableServices.hasNext()) {
			ServiceDescription reachable = reachableServices.next();
			reachableServices.remove();
			if (!servicesReachableFrom.containsValue(reachable)) {
				toProcess.add(reachable);
			}
		}
	}

	private Deque<ServiceDescription> findRoots(Set<ServiceDescription> unorderedServices) {
		Deque<ServiceDescription> output = new LinkedList<>();
		main:
		for (ServiceDescription svc : unorderedServices) {
			if (svc.hasRequiredServices()) {
				continue main;
			}
			output.add(svc);
		}
		return output;
	}

	private Multimap<Class<?>, ServiceDescription> findServiceReachabilityGraph(Set<ServiceDescription> unorderedServices) {
		Multimap<Class<?>, ServiceDescription> servicesReachableFrom = Multimap.create();
		for (ServiceDescription svc : unorderedServices) {
			svc.getRequiredServices().forEach(required -> servicesReachableFrom.put(required, svc));
		}
		return servicesReachableFrom;
	}
}
