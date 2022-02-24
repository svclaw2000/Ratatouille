package com.kdjj.domain.usecase

import com.kdjj.domain.model.RecipeType
import com.kdjj.domain.model.request.EmptyRequest
import com.kdjj.domain.repository.RecipeTypeRepository
import javax.inject.Inject

internal class FetchRecipeTypeListUseCase @Inject constructor(
	private val recipeTypeRepository: RecipeTypeRepository
) : ResultUseCase<EmptyRequest, @JvmSuppressWildcards List<RecipeType>> {
	
	override suspend fun invoke(request: EmptyRequest): Result<List<RecipeType>> {
		return recipeTypeRepository.fetchRecipeTypeList()
	}
}
