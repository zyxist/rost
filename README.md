Rost: simple lifecycle management to your Java apps
============================

![build status](https://github.com/zyxist/rost/actions/workflows/build.yml/badge.svg)

Library for lifecycle management:

 - Start your services in order before executing the actual application code
 - Stop the services in the reverse order
 - Resolve dependencies between services to determine the startup order.

The library does not have any third-party dependencies, except for **JSpecify**
annotations for nullability. Its primary use case are standalone applications that
do not use any large framework, but need some form of lifecycle management.

*Note: the library is a work-in-progress; the stable version has not been released
yet.*

How it works?
-------------

You create some service launchers:

```java
@RequiresServices({Bar.class})
@ProvidesService(Foo.class)
public class FooLauncher implements ServiceLauncher {
    public void start() {
        System.out.println("I'm starting!");
    }

    public void stop() {
        System.out.println("I'm stopping!");
    }
}
```

You insert them into the Rost:

```java
Set<ServiceLauncher> launchers = Set.of(
    new FooLauncher(),
    new BarLauncher(),
    new JoeLauncher()
);
Rost.create().launch(launchers, () -> System.out.println("App started"));
```

And the services start... and stop.

Installation
------------

Using with Gradle:

```
dependencies {
    testImplementation("com.zyxist.rost:rost:0.1.0-SNAPSHOT")
}
```

Using in the module descriptors:

```
module com.example.mymodule {
   requires com.zyxist.rost;
}
```

By default, the project has no external dependencies. If you, however, use [SLF4j](http://slf4j.org),
Rost provides `LoggingSource` that adds logging information about started and stopped services.

Usage
-----

A service can be *any* class that you need to initialize before the first use, and that you *might*
want to stop at the end. To add the lifecycle, implement the corresponding `ServiceLauncher` class
(you can also implement it directly on the service class) and annotate it with the following annotations:

 - `@ProvidesService(MyService.class)` - specifies, what service this launcher handles
 - `@RequiresServices(ServiceA.class, ServiceB.class)` - declares dependencies that need to start earlier.

The execution of services follows the contract below:

 - Each service that successfully starts, **MUST** stop.
 - Services shall start in the provided order.
 - Services shall stop in the reverse order.
 - After starting all services, run the `serviceAwareCode`
 - Do not attempt to stop a service that failed to start.
 - Exceptions thrown from [ServiceLauncher#stop()] **MUST NOT** prevent other services from stopping.

Optionally, you can also create a `ServiceLauncher` annotated with `@LifecycleHook` instead of
`@ProvidesService`. Lifecycle hooks are just additional actions that you need to execute during
the startup process. They do not manage any particular service, so nothing can depend on them,
but they *may* depend on other services.

Authors and license
-------------------

Rost has been written by Tomasz Jędrzejewski. The project is available under the terms
of Apache License 2.0.
