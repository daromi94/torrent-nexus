plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

group = "com.daromi.torrent.nexus"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.arrow.core)
    testImplementation(libs.kotlin.test)
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "$group.cli.MainKt"
}

tasks.test {
    useJUnitPlatform()
}
