@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

package coil3

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyBuilder
import org.jetbrains.kotlin.gradle.plugin.KotlinHierarchyTemplate
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

private val hierarchyTemplate = KotlinHierarchyTemplate {
    withSourceSetTree(
        KotlinSourceSetTree.main,
        //KotlinSourceSetTree.test,
    )

    common {
        withCompilations { true }

        groupNonAndroid()
        groupJsCommon()
        groupNonJsCommon()
        groupJvmCommon()
        groupNonJvmCommon()
        groupNative()
        groupNonNative()
        groupNonApple()
        groupNonOhosArm64()
    }
}

private fun KotlinHierarchyBuilder.groupNonAndroid() {
    group("nonAndroid") {
        withJvm()
        groupJsCommon()
        groupNative()
    }
}

private fun KotlinHierarchyBuilder.groupJsCommon() {
    group("jsCommon") {
        withJs()
        withWasmJs()
    }
}

private fun KotlinHierarchyBuilder.groupNonJsCommon() {
    group("nonJsCommon") {
        groupJvmCommon()
        groupNative()
    }
}

private fun KotlinHierarchyBuilder.groupJvmCommon() {
    group("jvmCommon") {
        withAndroidTarget()
        withJvm()
    }
}

private fun KotlinHierarchyBuilder.groupNonJvmCommon() {
    group("nonJvmCommon") {
        groupJsCommon()
        groupNative()
    }
}

private fun KotlinHierarchyBuilder.groupNative() {
    group("native") {
        withNative()
        groupApple()
        groupOhosArm64()
    }
}

private fun KotlinHierarchyBuilder.groupApple() {
    group("apple") {
        withApple()

        group("ios") {
            withIos()
        }

        group("macos") {
            withMacos()
        }
    }
}

private fun KotlinHierarchyBuilder.groupOhosArm64() {
    group("ohosArm64") {
        withOhosArm64()
    }
}

private fun KotlinHierarchyBuilder.groupNonApple() {
    group("nonApple") {
        groupNonNative()
        groupOhosArm64()
    }
}

private fun KotlinHierarchyBuilder.groupNonNative() {
    group("nonNative") {
        groupJsCommon()
        groupJvmCommon()
    }
}

private fun KotlinHierarchyBuilder.groupNonOhosArm64() {
    group("nonOhosArm64") {
        groupNonNative()
        groupApple()
    }
}

fun KotlinMultiplatformExtension.applyCoilHierarchyTemplate() {
    applyHierarchyTemplate(hierarchyTemplate)
}
