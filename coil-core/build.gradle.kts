import coil3.addAllMultiplatformTargets
import coil3.androidLibrary

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("dev.drewhamilton.poko")
    id("androidx.baselineprofile")
}

addAllMultiplatformTargets(libs.versions.skiko)
androidLibrary(name = "coil3.core")

kotlin {
    targets.named<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>("ohosArm64") {
        val main by compilations.getting
        val image by main.cinterops.creating {
            defFile(file("src/ohosArm64Main/cinterop/image.def"))
            includeDirs(file("src/ohosArm64Main/cinterop/include"))
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(libs.coroutines.core)
                api(libs.kotlin.stdlib)
                api(libs.okio.core)
                api(libs.kotlinx.atomicfu)
            }
        }
        /*commonTest {
            dependencies {
                implementation(projects.internal.testUtils)
                implementation(libs.bundles.test.common)
            }
        }
        */
        named("nonAndroidMain") {
            dependencies {
                api(libs.skiko)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.annotation)
                implementation(libs.androidx.appcompat.resources)
                implementation(libs.androidx.core)
                implementation(libs.androidx.exifinterface)
                implementation(libs.androidx.profileinstaller)
                implementation(libs.coroutines.android)
                api(libs.androidx.lifecycle.runtime)
            }
        }
        /*androidUnitTest {
            dependencies {
                implementation(projects.internal.testUtils)
                implementation(libs.bundles.test.jvm)
            }
        }
        androidInstrumentedTest {
            dependencies {
                implementation(projects.internal.testUtils)
                implementation(libs.bundles.test.android)
            }
        }*/
    }
}

/*baselineProfile {
    mergeIntoMain = true
    saveInSrc = true
    baselineProfileOutputDir = "."
    filter {
        include("coil3.**")
        exclude("coil3.compose.**")
        exclude("coil3.gif.**")
        exclude("coil3.network.**")
        exclude("coil3.svg.**")
        exclude("coil3.video.**")
    }
}

dependencies {
    baselineProfile(projects.internal.benchmark)
}*/
