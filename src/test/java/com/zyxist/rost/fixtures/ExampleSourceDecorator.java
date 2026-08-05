package com.zyxist.rost.fixtures;

import com.zyxist.rost.ServiceLauncher;
import com.zyxist.rost.ServiceLauncherDecorator;
import com.zyxist.rost.sources.ServiceLauncherSource;
import com.zyxist.rost.sources.ServiceLauncherSourceDecorator;
import org.jspecify.annotations.NonNull;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExampleSourceDecorator implements ServiceLauncherSourceDecorator {
    private final List<ServiceLauncher> startedLaunchers = new LinkedList<>();
    private final List<ServiceLauncher> stoppedLaunchers = new LinkedList<>();

    @Override
    public @NonNull ServiceLauncherSource decorate(@NonNull ServiceLauncherSource decorated) {
        return () -> decorated
            .provideServiceDescriptions()
            .map(it -> it.decorateWith(new ExampleServiceLauncherDecorator(it.getLauncher())));
    }

    public void assertStarted(ServiceLauncher launcher) {
        assertTrue(
            startedLaunchers.contains(launcher),
            () -> "Service '" + launcher.getClass().getSimpleName() + "' was not decorated at start."
        );
    }

    public void assertStopped(ServiceLauncher launcher) {
        assertTrue(
            stoppedLaunchers.contains(launcher),
            () -> "Service '" + launcher.getClass().getSimpleName() + "' was not decorated at stop."
        );
    }

    class ExampleServiceLauncherDecorator implements ServiceLauncherDecorator {
        private final ServiceLauncher decorated;

        ExampleServiceLauncherDecorator(ServiceLauncher decorated) {
            this.decorated = decorated;
        }

        @Override
        public @NonNull ServiceLauncher getDecoratedLauncher() {
            return decorated;
        }

        @Override
        public void start() {
            startedLaunchers.add(decorated);
        }

        @Override
        public void stop() {
            stoppedLaunchers.add(decorated);
        }
    }
}
