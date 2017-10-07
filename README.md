[![Build Status](https://travis-ci.org/zyxist/rost.svg?branch=master)](https://travis-ci.org/zyxist/rost)

Rost: lifecycle made simple
============================

Rost is a simple Java library that provides a lifecycle implementation to your
services.

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
Rost.execute(launchers, () -> System.out.println("System started"));
```

And the services start... and stop.

Using Rost
----------

Rost requires Java 9+, because it natively uses the new Java module system.

Using with Gradle:

```
dependencies {
    compile group('com.zyxist.rost'), name('rost'), version: 'x.y.z'
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

Advanced configuration
----------------------

Let's get back to the quick-start example. Rost provides a convenient API that wraps
a couple of the most common use cases and helps get you start. If the defaults do
not suit you, you can compose everything on your own.

The code below shows the same quick-start example in the verbose form:

```java
Set<ServiceLauncher> launchers = Set.of(
    new FooLauncher(),
    new BarLauncher(),
    new JoeLauncher()
);

ServiceExecutor executor = new StandardServiceExecutor();
ServiceComposer composer = Rost.composer();
Rost.execute(
    new ComposingSource(composer, new SimpleSource(launchers)),
    () -> System.out.println("System started")
);
```

Some concepts:

 * *executor* - responsible for calling `start()` and `stop()`
 * *composer* - puts the services in the proper order, defined by annotations,
 * *service source* - provides the services to the executor. The sources can be decorated
    to provide additional behaviors (e.g. logging). You can also see that the composer
    itself is attached as a decorator, too!
    
Using with Dependency Injection containers
------------------------------------------

Rost can be easily used with Dependency Injection containers. All the popular containers
(most notably Dagger and Guice) support some sort of a *multibindings* feature, where you
can register multiple implementations of a single interface.

Register your implementations of `ServiceLauncher` interface in the DI container, inject
the result set somewhere and simply pass it to Rost:

```java
public class Bootstrap {
    private final Set<ServiceLauncher> services;
    private final ServiceExecutor executor;
	
    @Inject
    public Bootstrap(Set<ServiceLauncher> services, ServiceExecutor executor) {
        this.services = Objects.requireNonNull(services);
        this.executor = Objects.requireNonNull(executor);
    }
	
    public void runApplication() {
        executor.execute(Rost.compose(services), () -> {
            // some custom code
        });
    }
}
```

Authors and license
-------------------

Rost has been written by Tomasz Jędrzejewski. The project is available under the terms
of Apache License 2.0.
