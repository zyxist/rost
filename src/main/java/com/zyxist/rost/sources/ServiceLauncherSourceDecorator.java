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

package com.zyxist.rost.sources;

import com.zyxist.rost.Rost;
import com.zyxist.rost.ServiceLauncher;
import com.zyxist.rost.ServiceLauncherDecorator;
import com.zyxist.rost.logic.metadata.ServiceDescription;
import org.jspecify.annotations.NonNull;

/// Decorates the [com.zyxist.rost.sources.ServiceLauncherSource] with new functionality
/// before the launch of services. The interface operates on the level of decorating the
/// entire sources. There is also an [ServiceLauncherSourceDecorator.Abstract] implementation
/// to reduce some boilerplate code.
/// 
/// @see Rost#withDecorator(ServiceLauncherSourceDecorator) 
public interface ServiceLauncherSourceDecorator {
    @NonNull ServiceLauncherSource decorate(@NonNull ServiceLauncherSource decorated);

    /// Provides the default source decoration logic and lets you just focus on creating
    /// the [ServiceLauncherDecorator] via
    /// [ServiceLauncherSourceDecorator.Abstract#decorateService(ServiceDescription)] method.
    abstract class Abstract implements ServiceLauncherSourceDecorator {
        @Override
        public @NonNull ServiceLauncherSource decorate(@NonNull ServiceLauncherSource decorated) {
            return () -> decorated
                .provideServiceDescriptions()
                .map(it -> it.decorateWith(decorateService(it)));
        }

        /// Decorate a single service provided by [ServiceLauncherSource].
        ///
        /// @param serviceDescription The parsed description of the service (includes the actual [ServiceLauncher])
        /// @return Decorator for the service launcher represented by the [ServiceDescription]
        protected abstract @NonNull ServiceLauncherDecorator decorateService(@NonNull ServiceDescription serviceDescription);
    }
}
