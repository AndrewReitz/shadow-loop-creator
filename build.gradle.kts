plugins {
    id("org.jetbrains.kotlin.js") version "1.4.10"
}

group = "cash.andrew"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-js")
    implementation("com.github.ajalt.clikt:clikt:3.0.1")
}

kotlin {
    js {
        nodejs()
        binaries.executable()
    }
}