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

#ifndef INTERFACES_KITS_NATIVE_INCLUDE_IMAGE_IMAGE_SOURCE_NATIVE_H_
#define INTERFACES_KITS_NATIVE_INCLUDE_IMAGE_IMAGE_SOURCE_NATIVE_H_
#include "image_common.h"
#include "pixelmap_native.h"

#ifdef __cplusplus
extern "C" {
#endif

/**
 * @brief Defines an image source object.
 */
struct OH_ImageSourceNative;
typedef struct OH_ImageSourceNative OH_ImageSourceNative;

/**
 * @brief Defines image source information.
 */
struct OH_ImageSource_Info;
typedef struct OH_ImageSource_Info OH_ImageSource_Info;

/**
 * @brief Defines the decoding options.
 */
struct OH_DecodingOptions;
typedef struct OH_DecodingOptions OH_DecodingOptions;

/**
 * @brief Enumerates decoding dynamic range.
 */
typedef enum {
    IMAGE_DYNAMIC_RANGE_AUTO = 0,
    IMAGE_DYNAMIC_RANGE_SDR = 1,
    IMAGE_DYNAMIC_RANGE_HDR = 2,
} IMAGE_DYNAMIC_RANGE;

/**
 * @brief Gets the list of image formats supported for decoding.
 */
Image_ErrorCode OH_ImageSourceNative_GetSupportedFormats(Image_MimeType **mimeType, size_t *size);

/**
 * @brief Creates an ImageSource from URI.
 */
Image_ErrorCode OH_ImageSourceNative_CreateFromUri(char *uri, size_t uriSize, OH_ImageSourceNative **res);

/**
 * @brief Creates an ImageSource from file descriptor.
 */
Image_ErrorCode OH_ImageSourceNative_CreateFromFd(int32_t fd, OH_ImageSourceNative **res);

/**
 * @brief Creates an ImageSource from data buffer.
 */
Image_ErrorCode OH_ImageSourceNative_CreateFromData(uint8_t *data, size_t dataSize, OH_ImageSourceNative **res);

/**
 * @brief Creates image source information.
 */
Image_ErrorCode OH_ImageSourceInfo_Create(OH_ImageSource_Info **info);

/**
 * @brief Gets width from image source info.
 */
Image_ErrorCode OH_ImageSourceInfo_GetWidth(OH_ImageSource_Info *info, uint32_t *width);

/**
 * @brief Gets height from image source info.
 */
Image_ErrorCode OH_ImageSourceInfo_GetHeight(OH_ImageSource_Info *info, uint32_t *height);

/**
 * @brief Gets HDR info from image source.
 */
Image_ErrorCode OH_ImageSourceInfo_GetDynamicRange(OH_ImageSource_Info *info, bool *isHdr);

/**
 * @brief Releases image source info.
 */
Image_ErrorCode OH_ImageSourceInfo_Release(OH_ImageSource_Info *info);

/**
 * @brief Obtains image source information by index.
 */
Image_ErrorCode OH_ImageSourceNative_GetImageInfo(OH_ImageSourceNative *source, int32_t index,
    OH_ImageSource_Info *info);

/**
 * @brief Gets image property value.
 */
Image_ErrorCode OH_ImageSourceNative_GetImageProperty(OH_ImageSourceNative *source, Image_String *key,
    Image_String *value);

/**
 * @brief Modifies image property value.
 */
Image_ErrorCode OH_ImageSourceNative_ModifyImageProperty(OH_ImageSourceNative *source, Image_String *key,
    Image_String *value);

/**
 * @brief Creates decoding options.
 */
Image_ErrorCode OH_DecodingOptions_Create(OH_DecodingOptions **options);

/**
 * @brief Sets desired dynamic range for decoding.
 */
Image_ErrorCode OH_DecodingOptions_SetDesiredDynamicRange(OH_DecodingOptions *options,
    int32_t desiredDynamicRange);

/**
 * @brief Releases decoding options.
 */
Image_ErrorCode OH_DecodingOptions_Release(OH_DecodingOptions *options);

/**
 * @brief Creates a PixelMap from ImageSource.
 */
Image_ErrorCode OH_ImageSourceNative_CreatePixelmap(OH_ImageSourceNative *source, OH_DecodingOptions *options,
    OH_PixelmapNative **pixelmap);

/**
 * @brief Gets frame count from ImageSource.
 */
Image_ErrorCode OH_ImageSourceNative_GetFrameCount(OH_ImageSourceNative *source, uint32_t *frameCount);

/**
 * @brief Creates multiple PixelMaps from ImageSource.
 */
Image_ErrorCode OH_ImageSourceNative_CreatePixelmapList(OH_ImageSourceNative *source, OH_DecodingOptions *options,
    OH_PixelmapNative *resVecPixMap[], size_t size);

/**
 * @brief Gets delay time list for animation frames.
 */
Image_ErrorCode OH_ImageSourceNative_GetDelayTimeList(OH_ImageSourceNative *source, int32_t *delayTimeList, size_t size);

/**
 * @brief Releases ImageSource.
 */
Image_ErrorCode OH_ImageSourceNative_Release(OH_ImageSourceNative *source);

#ifdef __cplusplus
};
#endif

#endif // INTERFACES_KITS_NATIVE_INCLUDE_IMAGE_IMAGE_SOURCE_NATIVE_H_
