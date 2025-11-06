package coil3

import coil3.decode.OhosImageDecoder

internal actual fun ComponentRegistry.Builder.addOhosComponents(
    options: RealImageLoader.Options,
): ComponentRegistry.Builder {
    return this
        // Decoders - Use Ohos-specific decoder
        .add(OhosImageDecoder.Factory())
}
