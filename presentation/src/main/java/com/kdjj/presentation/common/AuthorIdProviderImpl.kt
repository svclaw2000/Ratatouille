package com.kdjj.presentation.common

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.kdjj.domain.common.AuthorIdProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class AuthorIdProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AuthorIdProvider {

    @SuppressLint("HardwareIds")
    override fun getAuthorId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
}