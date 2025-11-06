package coil3

import kotlin.jvm.JvmField

actual abstract class PlatformContext private constructor() {
    companion object {
        val INSTANCE = object : PlatformContext() {}
    }
}
