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
package com.zyxist.rost.logic;

import com.zyxist.rost.annotation.ProvidesService;
import com.zyxist.rost.annotation.RequiresServices;
import com.zyxist.rost.logic.metadata.ServiceDescription;

import java.util.List;
import java.util.Set;

/// Determines the execution order for services, using at least the information from [ProvidesService]
/// and [RequiresServices] annotations. Custom services may also include additional constraints.
public interface ServiceComposer {
	List<ServiceDescription> compose(Set<ServiceDescription> unorderedServices);
}
