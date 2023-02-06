plugins {
    id("java")
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("application")
}

apply(plugin = "org.openjfx.javafxplugin")

group = "demo"
version = "0.0.6"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

javafx {
    version = "19.0.2.1"
    modules = listOf("javafx.controls")
}

dependencies {
    implementation("commons-cli:commons-cli:1.5.0")

    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("ch.qos.logback:logback-classic:1.4.5")

    testImplementation("org.mockito:mockito-core:4.+")
    testImplementation("net.jqwik:jqwik:1.+")
    testImplementation("org.assertj:assertj-assertions-generator:2.+")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform() {
        includeEngines.add("jqwik")
    }
}
application {
    mainClass.set("io.github.keymaster65.timecharts.application.JavaFxMain")
}