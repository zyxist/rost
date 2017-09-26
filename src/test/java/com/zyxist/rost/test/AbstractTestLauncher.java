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

import com.zyxist.rost.api.ServiceLauncher;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AbstractTestLauncher<T extends AbstractTestLauncher<T>> implements ServiceLauncher {
	private boolean started = false;
	private boolean stopped = false;
	private Supplier<Exception> onStartErr;
	private Supplier<Exception> onStopErr;
	private ServiceOrder orderRecorder;

	protected AbstractTestLauncher(Supplier<Exception> onStartErr, Supplier<Exception> onStopErr) {
		this.onStartErr = onStartErr;
		this.onStopErr = onStopErr;
	}

	public T ordered(ServiceOrder orderRecorder) {
		this.orderRecorder = orderRecorder;
		return (T) this;
	}

	@Override
	public void start() throws Exception {
		if (orderRecorder != null) {
			orderRecorder.recordStart(this);
		}
		if (onStartErr != null) {
			throw onStartErr.get();
		}
		started = true;
	}

	@Override
	public void stop() throws Exception {
		if (orderRecorder != null) {
			orderRecorder.recordStop(this);
		}
		if (onStopErr != null) {
			throw onStopErr.get();
		}
		stopped = true;
	}

	@Override
	public String toString() {
		Class<?> enclosing = this.getClass().getEnclosingClass();
		if (null != enclosing) {
			return enclosing.getSimpleName();
		}
		return this.getClass().getSimpleName();
	}

	public void assertStartedAndStopped() {
		assertAll(this.getClass().getSimpleName()+" launcher",
			() -> assertTrue(started, "started"),
			() -> assertTrue(stopped, "stopped")
		);
	}

	public void assertOnlyStarted() {
		assertAll(this.getClass().getSimpleName()+" launcher",
			() -> assertTrue(started, "started"),
			() -> assertFalse(stopped, "stopped")
		);
	}

	public void assertNotStarted() {
		assertAll(this.getClass().getSimpleName()+" launcher",
			() -> assertFalse(started, "started"),
			() -> assertFalse(stopped, "stopped")
		);
	}
}
