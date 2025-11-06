/*
 * Copyright (C) 2023 Huawei Device Co., Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef INTERFACES_KITS_NATIVE_INCLUDE_IMAGE_PIXELMAP_NATIVE_H_
#define INTERFACES_KITS_NATIVE_INCLUDE_IMAGE_PIXELMAP_NATIVE_H_

#include <stdbool.h>
#include "image_common.h"

#ifdef __cplusplus
extern "C" {
#endif

/**
 * @brief Defines a PixelMap native object.
 */
struct OH_PixelmapNative;
typedef struct OH_PixelmapNative OH_PixelmapNative;

/**
 * @brief Defines the pixel map information.
 */
struct OH_Pixelmap_ImageInfo;
typedef struct OH_Pixelmap_ImageInfo OH_Pixelmap_ImageInfo;

/**
 * @brief Creates image info struct.
 */
Image_ErrorCode OH_PixelmapImageInfo_Create(OH_Pixelmap_ImageInfo **info);

/**
 * @brief Gets width from pixelmap image info.
 */
Image_ErrorCode OH_PixelmapImageInfo_GetWidth(OH_Pixelmap_ImageInfo *info, uint32_t *width);

/**
 * @brief Gets height from pixelmap image info.
 */
Image_ErrorCode OH_PixelmapImageInfo_GetHeight(OH_Pixelmap_ImageInfo *info, uint32_t *height);

/**
 * @brief Gets HDR info from pixelmap image info.
 */
Image_ErrorCode OH_PixelmapImageInfo_GetDynamicRange(OH_Pixelmap_ImageInfo *info, bool *isHdr);

/**
 * @brief Releases pixelmap image info.
 */
Image_ErrorCode OH_PixelmapImageInfo_Release(OH_Pixelmap_ImageInfo *info);

/**
 * @brief Gets image info from PixelMap.
 */
Image_ErrorCode OH_PixelmapNative_GetImageInfo(OH_PixelmapNative *pixelmap, OH_Pixelmap_ImageInfo *imageInfo);

/**
 * @brief Reads pixel data from PixelMap.
 */
Image_ErrorCode OH_PixelmapNative_ReadPixels(OH_PixelmapNative *pixelmap, uint8_t *destination, size_t *bufferSize);

/**
 * @brief Releases a PixelMap object.
 */
Image_ErrorCode OH_PixelmapNative_Release(OH_PixelmapNative *pixelmap);

#ifdef __cplusplus
};
#endif

#endif // INTERFACES_KITS_NATIVE_INCLUDE_IMAGE_PIXELMAP_NATIVE_H_
