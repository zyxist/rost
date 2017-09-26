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
package com.zyxist.rost.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CollectionUtils {

    public static <T> List<T> reverseList(List<T> list) {
		Objects.requireNonNull(list);
		List<T> reversed = new ArrayList<>(list.size());
		int revIdx = list.size() - 1;
		for (T item: list) {
			reversed.add(revIdx, item);
			revIdx--;
		}
        return reversed;
    }
}
