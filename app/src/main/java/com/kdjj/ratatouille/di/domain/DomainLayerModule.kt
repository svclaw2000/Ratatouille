package com.kdjj.ratatouille.di.domain

import com.kdjj.domain.di.FlowUseCaseModule
import com.kdjj.domain.di.HashModule
import com.kdjj.domain.di.ResultUseCaseModule
import com.kdjj.domain.di.UseCaseModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module(
    includes = [
        FlowUseCaseModule::class,
        HashModule::class,
        ResultUseCaseModule::class,
        UseCaseModule::class
    ]
)
@InstallIn(SingletonComponent::class)
class DomainLayerModule
