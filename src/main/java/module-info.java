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
module com.zyxist.rost {
	requires static org.slf4j;
	requires org.jspecify;

	exports com.zyxist.rost;
	exports com.zyxist.rost.exception;
	exports com.zyxist.rost.logic;
	exports com.zyxist.rost.logic.metadata;
	exports com.zyxist.rost.sources;
	exports com.zyxist.rost.annotation;
}
