/*
 * SPDX-FileCopyrightText: 2022 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package androidx.camera.view

import androidx.annotation.MainThread
import androidx.camera.core.ImageCapture
import java.util.concurrent.Executor

fun CameraController.onPinchToZoom(pinchToZoomScale: Float) = onPinchToZoom(pinchToZoomScale)

/** Takes one photo with an optional rotation override, preserving sensor rotation for later shots. */
@MainThread
fun CameraController.takePictureWithRotation(
    targetRotation: Int?,
    outputOptions: ImageCapture.OutputFileOptions,
    executor: Executor,
    callback: ImageCapture.OnImageSavedCallback
) {
    val imageCapture = mImageCapture
    val previousRotation = imageCapture.targetRotation
    try {
        targetRotation?.let { imageCapture.targetRotation = it }
        takePicture(outputOptions, executor, callback)
    } finally {
        // CameraX snapshots the rotation when takePicture is called on the main thread.
        if (targetRotation != null) {
            imageCapture.targetRotation = previousRotation
        }
    }
}

fun CameraController.setImageCaptureFlashType(flashType: Int) {
    val currentImageCapture = mImageCapture
    val replacement = ImageCapture.Builder
        .fromConfig(currentImageCapture.currentConfig)
        .setFlashType(flashType)
        .build()
    replacement.flashMode = currentImageCapture.flashMode
    replacement.screenFlash = currentImageCapture.screenFlash
    replacement.targetRotation = currentImageCapture.targetRotation
    mImageCapture = replacement
}
