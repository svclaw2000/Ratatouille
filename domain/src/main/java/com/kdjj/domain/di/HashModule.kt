package com.kdjj.domain.di

import com.kdjj.domain.hash.HashGenerator
import com.kdjj.domain.hash.SHA256HashGenerator
import dagger.Binds
import dagger.Module

@Module
abstract class HashModule {

    @Binds
    internal abstract fun bindHashGenerator(
        hashGenerator: SHA256HashGenerator
    ): HashGenerator
}