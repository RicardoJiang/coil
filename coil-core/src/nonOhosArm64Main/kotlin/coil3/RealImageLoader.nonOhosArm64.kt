package coil3

internal actual fun ComponentRegistry.Builder.addOhosComponents(
    options: RealImageLoader.Options,
): ComponentRegistry.Builder {
    // Non-Ohos platforms don't need Ohos components
    return this
}
