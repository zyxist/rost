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
package com.zyxist.rost.sources;

import com.zyxist.rost.api.ServiceComposer;
import com.zyxist.rost.api.ServiceSource;
import com.zyxist.rost.meta.ServiceDescription;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Decorates another service source, and runs the given service composer on it.
 */
public class ComposingSource implements ServiceSource {
	private final ServiceComposer composer;
	private final Supplier<Stream<ServiceDescription>> decoratedSource;

	public ComposingSource(ServiceComposer composer, Supplier<Stream<ServiceDescription>> decoratedSource) {
		this.composer = Objects.requireNonNull(composer);
		this.decoratedSource = Objects.requireNonNull(decoratedSource);
	}

	@Override
	public Stream<ServiceDescription> get() {
		return composer.compose(decoratedSource.get().collect(Collectors.toCollection(() -> new LinkedHashSet<>()))).stream();
	}
}
