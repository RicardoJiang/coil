import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    `kotlin-dsl-base`
}

repositories {
    google()
    maven("https://mirrors.tencent.com/nexus/repository/maven-public/")
    mavenCentral()
}

dependencies {
    implementation(libs.gradlePlugin.android)
    implementation(libs.gradlePlugin.jetbrainsCompose)
    implementation(libs.gradlePlugin.composeCompiler)
    implementation(libs.gradlePlugin.kotlin)
    implementation(libs.gradlePlugin.mavenPublish)
}

// Target JVM 17.
tasks.withType<JavaCompile>().configureEach {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
}
tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions.jvmTarget = JvmTarget.JVM_17
}
