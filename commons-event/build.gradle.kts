plugins {
    `java-library`
}
description = "Events stuff"


version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-collections4:4.4")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
