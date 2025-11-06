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

#ifndef INTERFACES_KITS_NATIVE_INCLUDE_IMAGE_IMAGE_COMMON_H_
#define INTERFACES_KITS_NATIVE_INCLUDE_IMAGE_IMAGE_COMMON_H_
#include <stdint.h>
#include <stddef.h>

#ifdef __cplusplus
extern "C" {
#endif

/**
 * @brief Defines the image size.
 */
struct Image_Size {
    uint32_t width;
    uint32_t height;
};

typedef struct Image_Size Image_Size;

/**
 * @brief Defines the region of the image source to decode.
 */
struct Image_Region {
    uint32_t x;
    uint32_t y;
    uint32_t width;
    uint32_t height;
};

typedef struct Image_Region Image_Region;

#ifdef __cplusplus
/**
 * @brief Defines the string type for image properties.
 */
struct Image_String {
    char *data = nullptr;
    size_t size = 0;
};
#else
struct Image_String {
    char *data;
    size_t size;
};
#endif

typedef struct Image_String Image_String;
typedef struct Image_String Image_MimeType;

/**
 * @brief Enumerates the return values.
 */
typedef enum {
    IMAGE_SUCCESS = 0,
    IMAGE_BAD_PARAMETER = 401,
    IMAGE_UNSUPPORTED_MIME_TYPE = 7600101,
    IMAGE_UNKNOWN_MIME_TYPE = 7600102,
    IMAGE_TOO_LARGE = 7600103,
    IMAGE_DMA_NOT_EXIST = 7600173,
    IMAGE_DMA_OPERATION_FAILED = 7600174,
    IMAGE_UNSUPPORTED_OPERATION = 7600201,
    IMAGE_UNSUPPORTED_METADATA = 7600202,
    IMAGE_UNSUPPORTED_CONVERSION = 7600203,
    IMAGE_INVALID_REGION = 7600204,
    IMAGE_UNSUPPORTED_MEMORY_FORMAT = 7600205,
    IMAGE_ALLOC_FAILED = 7600301,
    IMAGE_COPY_FAILED = 7600302,
    IMAGE_LOCK_UNLOCK_FAILED = 7600303,
    IMAGE_UNKNOWN_ERROR = 7600901,
    IMAGE_BAD_SOURCE = 7700101,
    IMAGE_SOURCE_UNSUPPORTED_MIME_TYPE = 7700102,
    IMAGE_SOURCE_TOO_LARGE = 7700103,
    IMAGE_SOURCE_UNSUPPORTED_ALLOCATOR_TYPE = 7700201,
    IMAGE_SOURCE_UNSUPPORTED_OPTIONS = 7700203,
    IMAGE_DECODE_FAILED = 7700301,
    IMAGE_SOURCE_ALLOC_FAILED = 7700302,
    IMAGE_ENCODE_FAILED = 7800301,
} Image_ErrorCode;

#ifdef __cplusplus
};
#endif

#endif // INTERFACES_KITS_NATIVE_INCLUDE_IMAGE_IMAGE_COMMON_H_
