/*
 * SPDX-FileCopyrightText: 2022 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package androidx.camera.view

import androidx.camera.core.ImageCapture

fun CameraController.onPinchToZoom(pinchToZoomScale: Float) = onPinchToZoom(pinchToZoomScale)

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
