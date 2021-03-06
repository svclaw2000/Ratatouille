package com.kdjj.remote.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RemoteModule {

    private const val MAX_DELAY_TIME = 5000L

    @Provides
    @Singleton
    fun provideFireStore(): FirebaseFirestore {
        val settings = firestoreSettings {
            isPersistenceEnabled = false
        }
        val fireStore = Firebase.firestore
        fireStore.firestoreSettings = settings
        return fireStore
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): StorageReference {
        val fireStorage = Firebase.storage.apply {
            maxDownloadRetryTimeMillis = MAX_DELAY_TIME
            maxOperationRetryTimeMillis = MAX_DELAY_TIME
            maxUploadRetryTimeMillis = MAX_DELAY_TIME
        }
        return fireStorage.reference
    }
}
