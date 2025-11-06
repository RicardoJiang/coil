@file:OptIn(ExperimentalForeignApi::class)

package coil3.decode

import coil3.ImageLoader
import coil3.asImage
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import okio.use
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ColorType
import org.jetbrains.skia.ImageInfo
import platform.multimedia.Image_ErrorCode
import platform.ohos.image.IMAGE_DYNAMIC_RANGE_AUTO
import platform.ohos.image.IMAGE_SUCCESS
import platform.ohos.image.OH_DecodingOptions_Create
import platform.ohos.image.OH_DecodingOptions_Release
import platform.ohos.image.OH_DecodingOptions_SetDesiredDynamicRange
import platform.ohos.image.OH_ImageSourceInfo_Create
import platform.ohos.image.OH_ImageSourceInfo_GetHeight
import platform.ohos.image.OH_ImageSourceInfo_GetWidth
import platform.ohos.image.OH_ImageSourceInfo_Release
import platform.ohos.image.OH_ImageSourceNative_CreateFromData
import platform.ohos.image.OH_ImageSourceNative_CreatePixelmap
import platform.ohos.image.OH_ImageSourceNative_GetImageInfo
import platform.ohos.image.OH_ImageSourceNative_Release
import platform.ohos.image.OH_PixelmapImageInfo_Create
import platform.ohos.image.OH_PixelmapImageInfo_GetHeight
import platform.ohos.image.OH_PixelmapImageInfo_GetWidth
import platform.ohos.image.OH_PixelmapImageInfo_Release
import platform.ohos.image.OH_PixelmapNative_GetImageInfo
import platform.ohos.image.OH_PixelmapNative_ReadPixels
import platform.ohos.image.OH_PixelmapNative_Release

/**
 * 鸿蒙平台专用的图片解码器，使用原生 Image Framework API。
 *
 * 该解码器使用 HarmonyOS 原生 API 来解码图片，相比基于 Skia 的解码方式，
 * 提供了更好的平台集成和性能表现。
 *
 * 解码流程：
 * 1. 从字节数组创建 ImageSource（图片源）
 * 2. 获取原始图片尺寸
 * 3. 将 ImageSource 解码为 PixelMap（像素映射）
 * 4. 获取 PixelMap 的尺寸（可能经过采样）
 * 5. 读取像素数据到字节数组
 * 6. 将像素数据转换为 Skia Bitmap
 * 7. 返回解码结果
 */
class OhosImageDecoder(
    private val source: ImageSource,
    private val options: Options,
) : Decoder {

    override suspend fun decode(): DecodeResult = memScoped {
        // memScoped 创建一个作用域，用于管理 C 内存分配
        // 当作用域结束时，所有通过 alloc 分配的内存会自动释放

        // 1. 读取图片的字节数据
        val bytes = source.source().use { it.readByteArray() }

        // 2. 创建 ImageSource（图片源对象）
        val imageSource = createImageSource(bytes)
        try {
            // 3. 获取原始图片的宽高
            val (originalWidth, originalHeight) = getImageDimensions(imageSource)

            // 4. 将 ImageSource 解码为 PixelMap
            val pixelMap = decodeToPixelMap(imageSource)

            try {
                // 5. 获取解码后 PixelMap 的宽高
                val (pixelMapWidth, pixelMapHeight) = getPixelMapDimensions(pixelMap)

                // 6. 判断是否进行了采样（downsampling）
                // 如果解码后的尺寸小于原始尺寸，说明进行了采样以节省内存
                val isSampled = pixelMapWidth < originalWidth || pixelMapHeight < originalHeight

                // 7. 读取像素数据到字节数组
                val pixelData = readPixelData(pixelMap, pixelMapWidth, pixelMapHeight)

                // 8. 将像素数据转换为 Skia Bitmap 对象
                val bitmap = createSkiaBitmap(pixelData, pixelMapWidth, pixelMapHeight)

                // 9. 返回解码结果
                DecodeResult(
                    image = bitmap.asImage(),
                    isSampled = isSampled,
                )
            } finally {
                // 释放 PixelMap 资源
                OH_PixelmapNative_Release(pixelMap)
            }
        } finally {
            // 释放 ImageSource 资源
            OH_ImageSourceNative_Release(imageSource)
        }
    }

    /**
     * 从字节数组创建 ImageSource。
     *
     * ImageSource 是 HarmonyOS 图片框架的核心对象，代表一个图片源。
     * 它可以从多种来源创建：字节数组、文件、URI 等。
     *
     * @param bytes 图片的字节数据
     * @return ImageSource 的 C 指针
     */
    private fun MemScope.createImageSource(
        bytes: ByteArray
    ): CPointer<cnames.structs.OH_ImageSourceNative> {
        // 分配一个指针变量，用于接收创建的 ImageSource
        val imageSourcePtr = alloc<kotlinx.cinterop.CPointerVar<cnames.structs.OH_ImageSourceNative>>()

        // usePinned 固定字节数组在内存中的位置，防止 GC 移动它
        // 这样才能安全地将指针传递给 C API
        bytes.usePinned { pinnedBytes ->
            OH_ImageSourceNative_CreateFromData(
                pinnedBytes.addressOf(0).reinterpret(), // 获取字节数组首地址并转换为 uint8_t*
                bytes.size.toULong(),                    // 字节数组的大小
                imageSourcePtr.ptr                       // 接收结果的指针地址
            ).checkSuccess("创建 ImageSource 失败")
        }

        // 返回创建的 ImageSource 指针，如果为 null 则抛出异常
        return imageSourcePtr.value ?: error("ImageSource 指针为 null")
    }

    /**
     * 获取图片的原始尺寸。
     *
     * 在解码图片之前，先获取图片的原始宽高信息。
     * 这对于判断是否需要采样、以及后续判断是否发生了采样都很重要。
     *
     * @param imageSource ImageSource 指针
     * @return 图片的宽度和高度（像素）
     */
    private fun MemScope.getImageDimensions(
        imageSource: CPointer<cnames.structs.OH_ImageSourceNative>
    ): Pair<Int, Int> {
        // 分配 ImageSourceInfo 指针变量
        val imageInfoPtr = alloc<kotlinx.cinterop.CPointerVar<cnames.structs.OH_ImageSource_Info>>()
        OH_ImageSourceInfo_Create(imageInfoPtr.ptr)

        val imageInfo = imageInfoPtr.value ?: error("创建 ImageSourceInfo 失败")
        try {
            // 获取第 0 帧的图片信息（对于非动图，只有一帧）
            OH_ImageSourceNative_GetImageInfo(imageSource, 0, imageInfo)

            // 分配宽度和高度变量
            val width = alloc<kotlinx.cinterop.UIntVar>()
            val height = alloc<kotlinx.cinterop.UIntVar>()

            // 读取宽度和高度
            OH_ImageSourceInfo_GetWidth(imageInfo, width.ptr)
            OH_ImageSourceInfo_GetHeight(imageInfo, height.ptr)

            return width.value.toInt() to height.value.toInt()
        } finally {
            // 释放 ImageSourceInfo 资源
            OH_ImageSourceInfo_Release(imageInfo)
        }
    }

    /**
     * 将 ImageSource 解码为 PixelMap。
     *
     * PixelMap 是 HarmonyOS 中的位图对象，包含了解码后的像素数据。
     * 可以通过 DecodingOptions 来控制解码参数，如动态范围、采样率等。
     *
     * @param imageSource ImageSource 指针
     * @return PixelMap 的 C 指针
     */
    private fun MemScope.decodeToPixelMap(
        imageSource: CPointer<cnames.structs.OH_ImageSourceNative>
    ): CPointer<cnames.structs.OH_PixelmapNative> {
        // 创建解码选项
        val decodingOptsPtr = alloc<kotlinx.cinterop.CPointerVar<cnames.structs.OH_DecodingOptions>>()
        OH_DecodingOptions_Create(decodingOptsPtr.ptr)

        val decodingOpts = decodingOptsPtr.value
        decodingOpts?.let {
            // 设置动态范围为自动（AUTO）
            // 这样系统会根据图片内容和设备能力自动选择 SDR 或 HDR
            OH_DecodingOptions_SetDesiredDynamicRange(it, IMAGE_DYNAMIC_RANGE_AUTO.toInt())
        }

        try {
            // 分配 PixelMap 指针变量
            val pixelMapPtr = alloc<kotlinx.cinterop.CPointerVar<cnames.structs.OH_PixelmapNative>>()

            // 执行解码操作
            OH_ImageSourceNative_CreatePixelmap(
                imageSource,     // 图片源
                decodingOpts,    // 解码选项
                pixelMapPtr.ptr  // 接收结果的指针地址
            ).checkSuccess("创建 PixelMap 失败")

            return pixelMapPtr.value ?: error("PixelMap 指针为 null")
        } finally {
            // 释放解码选项资源
            decodingOpts?.let { OH_DecodingOptions_Release(it) }
        }
    }

    /**
     * 获取 PixelMap 的尺寸。
     *
     * PixelMap 的尺寸可能与原始图片不同，因为解码过程中可能进行了采样。
     * 通过对比原始尺寸和 PixelMap 尺寸，可以判断是否发生了采样。
     *
     * @param pixelMap PixelMap 指针
     * @return PixelMap 的宽度和高度（像素）
     */
    private fun MemScope.getPixelMapDimensions(
        pixelMap: CPointer<cnames.structs.OH_PixelmapNative>
    ): Pair<Int, Int> {
        // 分配 PixelmapImageInfo 指针变量
        val pixelmapInfoPtr = alloc<kotlinx.cinterop.CPointerVar<cnames.structs.OH_Pixelmap_ImageInfo>>()
        OH_PixelmapImageInfo_Create(pixelmapInfoPtr.ptr)

        val pixelmapInfo = pixelmapInfoPtr.value ?: error("创建 PixelmapImageInfo 失败")
        try {
            // 获取 PixelMap 的信息
            OH_PixelmapNative_GetImageInfo(pixelMap, pixelmapInfo)

            // 分配宽度和高度变量
            val width = alloc<kotlinx.cinterop.UIntVar>()
            val height = alloc<kotlinx.cinterop.UIntVar>()

            // 读取宽度和高度
            OH_PixelmapImageInfo_GetWidth(pixelmapInfo, width.ptr)
            OH_PixelmapImageInfo_GetHeight(pixelmapInfo, height.ptr)

            return width.value.toInt() to height.value.toInt()
        } finally {
            // 释放 PixelmapImageInfo 资源
            OH_PixelmapImageInfo_Release(pixelmapInfo)
        }
    }

    /**
     * 从 PixelMap 读取像素数据。
     *
     * 将 PixelMap 中的像素数据复制到一个字节数组中。
     * 像素格式为 RGBA，每个像素占 4 个字节（R, G, B, A 各 1 字节）。
     *
     * @param pixelMap PixelMap 指针
     * @param width PixelMap 的宽度
     * @param height PixelMap 的高度
     * @return 包含像素数据的字节数组
     */
    private fun MemScope.readPixelData(
        pixelMap: CPointer<cnames.structs.OH_PixelmapNative>,
        width: Int,
        height: Int
    ): ByteArray {
        // 分配 buffer size 变量
        val bufferSize = alloc<kotlinx.cinterop.ULongVar>()
        // RGBA 格式：每个像素 4 字节
        bufferSize.value = (width * height * 4).toULong()

        // 创建字节数组来接收像素数据
        val pixelBuffer = ByteArray(bufferSize.value.toInt())

        // usePinned 固定字节数组在内存中的位置
        pixelBuffer.usePinned { pinnedBuffer ->
            OH_PixelmapNative_ReadPixels(
                pixelMap,                             // PixelMap 对象
                pinnedBuffer.addressOf(0).reinterpret(), // 目标缓冲区地址
                bufferSize.ptr                         // 缓冲区大小（输入/输出参数）
            ).checkSuccess("读取像素数据失败")
        }

        return pixelBuffer
    }

    /**
     * 从像素数据创建 Skia Bitmap。
     *
     * Coil 内部使用 Skia 作为图像表示格式，因此需要将 HarmonyOS 的
     * PixelMap 数据转换为 Skia Bitmap。
     *
     * @param pixelData 像素数据字节数组
     * @param width 图片宽度
     * @param height 图片高度
     * @return Skia Bitmap 对象
     */
    private fun createSkiaBitmap(
        pixelData: ByteArray,
        width: Int,
        height: Int
    ): Bitmap {
        // 创建空的 Bitmap 对象
        val bitmap = Bitmap()

        // 定义图片信息：宽度、高度、颜色格式、透明度类型
        val imageInfo = ImageInfo(
            width = width,
            height = height,
            colorType = ColorType.RGBA_8888,         // RGBA 格式，每个通道 8 位
            alphaType = ColorAlphaType.UNPREMUL      // 非预乘 Alpha
        )

        // 为 Bitmap 分配内存
        bitmap.allocPixels(imageInfo)
        // 将像素数据安装到 Bitmap 中
        bitmap.installPixels(pixelData)
        // 设置为不可变，优化内存使用
        bitmap.setImmutable()

        return bitmap
    }

    /**
     * 扩展函数：检查 Image_ErrorCode 并在失败时抛出异常。
     *
     * HarmonyOS Image Framework API 使用错误码来表示操作结果。
     * IMAGE_SUCCESS (0) 表示成功，其他值表示各种错误。
     *
     * @param message 错误消息
     * @throws IllegalStateException 当错误码不是 IMAGE_SUCCESS 时
     */
    private fun Image_ErrorCode.checkSuccess(message: String) {
        if (this != IMAGE_SUCCESS) {
            error("$message (错误码: $this)")
        }
    }

    /**
     * 解码器工厂类。
     *
     * Coil 通过工厂模式来创建解码器实例。
     * 在组件注册时注册工厂，在需要解码时通过工厂创建解码器。
     */
    class Factory : Decoder.Factory {
        override fun create(
            result: SourceFetchResult,
            options: Options,
            imageLoader: ImageLoader,
        ): Decoder = OhosImageDecoder(result.source, options)
    }
}
