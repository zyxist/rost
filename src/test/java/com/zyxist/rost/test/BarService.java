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
package com.zyxist.rost.test;

import com.zyxist.rost.api.ProvidesService;
import com.zyxist.rost.api.RequiresServices;

import java.util.function.Supplier;

public interface BarService {
	@RequiresServices({FooService.class})
	@ProvidesService(BarService.class)
	class Launcher extends AbstractTestLauncher<Launcher> {
		private boolean started = false;
		private boolean stopped = false;

		public Launcher() {
			super(null, null);
		}

		public Launcher(Supplier<Exception> onStartErr, Supplier<Exception> onStopErr) {
			super(onStartErr, onStopErr);
		}
	}

	@RequiresServices({GooService.class})
	@ProvidesService(BarService.class)
	class AlternativeLauncher extends AbstractTestLauncher<Launcher> {
		private boolean started = false;
		private boolean stopped = false;

		public AlternativeLauncher() {
			super(null, null);
		}

		public AlternativeLauncher(Supplier<Exception> onStartErr, Supplier<Exception> onStopErr) {
			super(onStartErr, onStopErr);
		}
	}
}
