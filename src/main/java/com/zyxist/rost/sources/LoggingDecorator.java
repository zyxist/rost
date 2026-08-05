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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/// Adds logging with Slf4j facade related to the execution of [ServiceLauncher]
/// methods. To use this decorator, you need to explicitly add Slf4j dependency
/// to your project.
/// 
/// @see Rost#withDecorator(ServiceLauncherSourceDecorator)
public class LoggingDecorator extends ServiceLauncherSourceDecorator.Abstract {
    private final Logger logger;

    public LoggingDecorator(@NonNull Logger logger) {
        this.logger = Objects.requireNonNull(logger);
    }

    public LoggingDecorator() {
        this(LoggerFactory.getLogger(LoggingDecorator.class));
    }

    public Logger getLogger() {
        return logger;
    }

    @Override
    protected @NonNull ServiceLauncherDecorator decorateService(@NonNull ServiceDescription serviceDescription) {
        return new LoggingServiceLauncher(serviceDescription);
    }

    public class LoggingServiceLauncher extends ServiceLauncherDecorator.Abstract {
        LoggingServiceLauncher(@NonNull ServiceDescription launcher) {
            super(launcher);
        }

        @Override
        public void start() throws Exception {
            logger.atInfo().addArgument(decorated.getName()).log("Service '{}': starting");
            decorated.getLauncher().start();
            logger.atInfo().addArgument(decorated.getName()).log("Service '{}': started");
        }

        @Override
        public void stop() throws Exception {
            logger.atInfo().addArgument(decorated.getName()).log("Service '{}': stopping");
            decorated.getLauncher().start();
            logger.atInfo().addArgument(decorated.getName()).log("Service '{}': stopped");
        }
    }
}
