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

import com.zyxist.rost.meta.ServiceDescription;

import java.util.List;
import java.util.Set;

/**
 * The service composer is responsible for determining the proper startup order for all the services,
 * that satisfies all the dependencies. The dependencies between services are defined by {@link RequiresServices}
 * and {@link ProvidesService} annotations.
 *
 * <p>The service composer is launched by {@link com.zyxist.rost.sources.ComposingSource} service source.</p>
 */
public interface ServiceComposer {
	List<ServiceDescription> compose(Set<ServiceDescription> unorderedServices);
}
