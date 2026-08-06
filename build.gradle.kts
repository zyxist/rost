import org.cyclonedx.model.Component.Type.LIBRARY
import org.cyclonedx.model.License
import org.cyclonedx.model.LicenseChoice

plugins {
    `java-library`
    alias(libs.plugins.cyclonedx)
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
    testImplementation(libs.logback)
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8";
    options.memberLevel = JavadocMemberLevel.PROTECTED
    (options as CoreJavadocOptions).addBooleanOption("Xdoclint:all,-missing", true)
}

tasks.cyclonedxBom {
    projectType = LIBRARY
    includeLicenseText = true
    includeBuildSystem = false
    licenseChoice = LicenseChoice().apply {
        addLicense(License().apply {
            name = "Apache-2.0"
            url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
        })
    }
}

tasks.cyclonedxDirectBom {
    skipConfigs = listOf(".*test.*")
}

tasks.test {
    useJUnitPlatform()
}
