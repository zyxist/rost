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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.zyxist.rost.Rost;
import com.zyxist.rost.fixtures.FooService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class LoggingDecoratorTest {
    @Test
    void shouldUseDefaultLoggerIfNotProvided() {
        // given
        var decorator = new LoggingDecorator();

        // when
        var logger = decorator.getLogger();

        // then
        assertEquals(LoggingDecorator.class.getCanonicalName(), logger.getName());
    }

    @Test
    void shouldLogServiceStartAndStop() {
        // given
        Logger logger = spy(LoggerFactory.getLogger("TEST"));
        var rost = Rost.create().withDecorator(new LoggingDecorator(logger));
        var service = new FooService.Launcher();

        // when
        rost.launch(Set.of(service), () -> { });

        // then
        verify(logger, times(4)).atInfo();
    }

    @Test
    void shouldLogProperMessages() {
        // given
        var capture = new LogCapture();
        var logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("TEST");
        logger.addAppender(capture);
        var rost = Rost.create().withDecorator(new LoggingDecorator(logger));
        var service = new FooService.Launcher();

        // when
        capture.start();
        rost.launch(Set.of(service), () -> logger.info("RUN"));
        capture.stop();

        // then
        capture.assertContainsInOrder(
            "Service 'FooService': starting",
            "Service 'FooService': started",
            "RUN",
            "Service 'FooService': stopping",
            "Service 'FooService': stopped"
        );
    }

    static class LogCapture extends AppenderBase<ILoggingEvent> {
        private final List<String> captured = new LinkedList<>();

        @Override
        protected void append(ILoggingEvent eventObject) {
            captured.add(eventObject.getFormattedMessage());
        }

        void assertContainsInOrder(String ... messages) {
            var iterator = captured.iterator();
            for (var message: messages) {
                var found = false;
                while (iterator.hasNext()) {
                    var actual = iterator.next();
                    if (actual.endsWith(message)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    fail("Message '" + message + "' not found or not in order in the captured log output");
                }
            }
        }
    }
}
