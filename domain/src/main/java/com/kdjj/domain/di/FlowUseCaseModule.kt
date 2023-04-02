package com.kdjj.domain.di

import com.kdjj.domain.model.request.EmptyRequest
import com.kdjj.domain.usecase.FlowUseCase
import com.kdjj.domain.usecase.GetRecipeUpdateFlowUseCase
import dagger.Binds
import dagger.Module

@Module
abstract class FlowUseCaseModule {

    @Binds
    internal abstract fun bindGetRecipeUpdateFlowUseCase(
        getRecipeUpdateFlowUseCase: GetRecipeUpdateFlowUseCase
    ): FlowUseCase<EmptyRequest, Unit>
}
