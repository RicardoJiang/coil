# Coil - 鸿蒙适配版

![Coil](logo.svg)

适配 **Android**、**iOS**、和 **鸿蒙 NEXT** 的图片平台。

基于 Coil 3.0.x 及 OvCompose，新增鸿蒙平台支持，使用原生 C API 实现最佳性能。

## 特性

- ✅ **多平台支持**：Android、iOS、鸿蒙 NEXT
- ✅ **鸿蒙原生解码**：使用 `OH_ImageSourceNative` 原生 API
- ✅ **高效下采样**：支持图片解码时自动下采样，大幅节省内存
- ✅ **智能缩放策略**：支持 `Precision.INEXACT` 模式，防止小图片上采样
- ✅ **Compose Multiplatform**：基于 OvCompose 完整支持 Compose 跨平台 UI
- ✅ **图片格式**：PNG、JPEG、WebP等

## 快速开始

### 1. 添加依赖

在 `build.gradle.kts` 中添加：

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.ricardojiang:coil-core:3.0.4-KBA-003")
            implementation("io.github.ricardojiang:coil-compose:3.0.4-KBA-003")
        }
    }
}
```

### 2. 使用 Compose 加载图片

```kotlin
import coil3.compose.AsyncImage

@Composable
fun MyImage() {
    AsyncImage(
        model = "https://example.com/image.jpg",
        contentDescription = "图片描述",
    )
}
```

### 3. 配置 ImageLoader
由于 Ktor 未适配鸿蒙，因此需要自定义 ImageLoader，具体可见项目：[https://github.com/RicardoJiang/now-in-kotlin](https://github.com/RicardoJiang/now-in-kotlin)

```kotlin
fun createImageLoader(context: PlatformContext): ImageLoader {
    return ImageLoader.Builder(context)
        .components {
            // 添加自定义的 KmpNetworkFetcher
            add(KmpNetworkFetcher.Factory())
        }
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context, 0.25) // 使用 25% 的可用内存
                .build()
        }
        .logger(DebugLogger()) // 开发时启用日志
        .build()
}
```

## License

    Copyright 2024 Coil Contributors

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
