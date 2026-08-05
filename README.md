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
void main() {
    Set<ServiceLauncher> launchers = Set.of(
        new FooLauncher(),
        new BarLauncher(),
        new JoeLauncher()
    );
    Rost.create().launch(launchers, () -> System.out.println("App started"));
}
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

### Services and dependencies

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
 - Services that failed to start for whatever reason, shall not be stopped.
 - Exceptions thrown from `ServiceLauncher.stop()` **MUST NOT** block other services from stopping.

Optionally, you can also create a `ServiceLauncher` annotated with `@LifecycleHook` instead of
`@ProvidesService`. Lifecycle hooks are just additional actions that you need to execute during
the startup process. They do not manage any particular service, so nothing can depend on them,
but they *may* depend on other services.

### Composer and executor

 * `ServiceComposer` - responsible for resolving dependencies between services
 * `ServiceExecutor` - responsible for executing the services according to the contract,
   and in order provided by the composer.

The default implementations are good enough for the majority of use cases. However, you may replace them
with custom implementations:

```java
void main() {
    Rost.create()
        .withComposer(customComposer)
        .withExecutor(customExecutor)
        .launch(launchers, () -> System.out.println("App started"));
}
```

The typical use case is installing a custom exception handler in the executor:

```java
void main() {
    Consumer<ServiceFailure> errorHandler = createCustomErrorHandler();
    
    Rost.create()
        .withExecutor(new StandardServiceExecutor(errorHandler))
        .launch(launchers, () -> System.out.println("App started"));
}
```

### Service sources

`ServiceSource` is a class that provides a stream of `ServiceDescription` instances to the composer.
The default implementation is `SimpleServiceSource` that just returns the provided collection of
`ServiceLauncher` instances.

If you want to create a custom `ServiceSource`, use the constructor of `ServiceDescription` to find
and parse annotations placed on `ServiceLauncher` implementations:

```java
void main() {
    ServiceLauncher myServiceLauncher = new MyServiceLauncher();
    var serviceDescription = new ServiceDescription(myServiceLauncher);
    
    serviceDescription.getRequiredServices();
}
```

### Logging

To enable logging of the service execution process:

 1. Add `org.slf4j:slf4j-api` dependency to your project, and the logging backend of your choice
 2. Register `LoggingDecorator` in Rost.

Example:

```java
void main() {
    Rost.create()
        .withDecorator(new LoggingDecorator())
        .launch(launchers, () -> System.out.println("App started"));
}
```

By default, `LoggingDecorator` uses its own canonical class name as the logger name, but you can optionally
pass your own logger in the constructor.

### Custom decorators

You can inject custom behavior into the launch process with the decorator pattern. There are two types of
decorators that you typically use together:

 * `ServiceLauncherSourceDecorator` - for decorating `ServiceLauncherSource` instances
 * `ServiceLauncherDecorator` - for decorating `ServiceLauncher` instances

Both provide the `.Abstract` abstract implementations to reduce the boilerplate code. Below, you can find a basic
sample code to start with:

```java
public class MySourceDecorator extends ServiceLauncherSourceDecorator.Abstract {
   @Override
   protected @NonNull ServiceLauncherDecorator decorateService(@NonNull ServiceDescription serviceDescription) {
      return new MyServiceLauncherDecorator(serviceDescription);
   }
}

public class MyServiceLauncherDecorator extends ServiceLauncherDecorator.Abstract {
   public MyServiceLauncherDecorator(@NonNull ServiceDescription decorated) {
      super(decorated);
   }
   
   @Override
   public void start() {
      // do something
     decorated.start();
   }

   @Override
   public void start() {
      // do something
      decorated.start();
   }
}

void main() {
   Rost.create().withDecorator(new MySourceDecorator());
}
```

Authors and license
-------------------

Rost has been written by Tomasz Jędrzejewski. The project is available under the terms
of Apache License 2.0.
