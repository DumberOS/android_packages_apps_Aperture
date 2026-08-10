/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.aperture.ext

import android.app.RemoteAction
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.textclassifier.TextClassification
import android.view.textclassifier.TextClassifier
import com.google.zxing.client.result.URIParsedResult
import org.lineageos.aperture.R

private const val FDROID_LINK_HOST = "fdroid.link"
private const val FDROID_REPO_FINGERPRINT_PARAM = "fingerprint"
private const val SCHEME_FDROID_REPO = "fdroidrepo"
private const val SCHEME_FDROID_REPOS = "fdroidrepos"

private fun Uri.toFdroidRepoUriOrNull(): Uri? {
    return when (scheme?.lowercase()) {
        SCHEME_FDROID_REPO, SCHEME_FDROID_REPOS -> this
        "http", "https" -> when {
            host.equals(FDROID_LINK_HOST, ignoreCase = true) -> {
                fragment?.takeIf { it.isNotBlank() }?.let(Uri::parse)?.toFdroidRepoUriOrNull()
            }
            getQueryParameter(FDROID_REPO_FINGERPRINT_PARAM) != null -> {
                buildUpon()
                    .scheme(if (scheme.equals("https", ignoreCase = true)) {
                        SCHEME_FDROID_REPOS
                    } else {
                        SCHEME_FDROID_REPO
                    })
                    .fragment(null)
                    .build()
            }
            else -> null
        }
        else -> null
    }
}

fun URIParsedResult.createIntent(): Intent {
    val parsedUri = Uri.parse(uri)
    return Intent(Intent.ACTION_VIEW, parsedUri.toFdroidRepoUriOrNull() ?: parsedUri)
}

fun URIParsedResult.createTextClassification(context: Context) = TextClassification.Builder()
    .setText(uri)
    .setEntityType(TextClassifier.TYPE_URL, 1.0f)
    .apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            addAction(
                RemoteAction::class.build(
                    context,
                    R.drawable.ic_open_in_browser,
                    R.string.qr_uri_title,
                    R.string.qr_uri_content_description,
                    createIntent()
                )
            )
        }
    }
    .build()
