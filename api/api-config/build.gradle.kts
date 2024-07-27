plugins {
    kotlin("jvm") version "2.0.0"
}

group = "com.mattmx.ktgui"
version = "2.4.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}