plugins {
    `java-library`
}

group = "com.zyxist.rost"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

dependencies {
    api(libs.slf4j.api)
    api(libs.jspecify)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.mockito)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.launcher)
    testRuntimeOnly(libs.logback)
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8";
    options.memberLevel = JavadocMemberLevel.PUBLIC
    (options as CoreJavadocOptions).addBooleanOption("Xdoclint:all,-missing", true)
}

tasks.test {
    useJUnitPlatform()
}
