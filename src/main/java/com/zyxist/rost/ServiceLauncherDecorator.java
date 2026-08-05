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
package com.zyxist.rost;

import com.zyxist.rost.logic.metadata.ServiceDescription;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/// Allows decorating the existing [ServiceLauncher] with extra functionality. Extend
/// [ServiceLauncherDecorator.Abstract] to reduce some boilerplate code.
public interface ServiceLauncherDecorator extends ServiceLauncher {
	@NonNull ServiceLauncher getDecoratedLauncher();

	abstract class Abstract implements ServiceLauncherDecorator {
		protected final ServiceDescription decorated;

		public Abstract(@NonNull ServiceDescription decorated) {
			this.decorated = Objects.requireNonNull(decorated);
		}

		@Override
		public @NonNull ServiceLauncher getDecoratedLauncher() {
			return decorated.getLauncher();
		}
	}
}
