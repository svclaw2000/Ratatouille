package com.kdjj.presentation.model

import com.kdjj.domain.model.RecipeState
import com.kdjj.domain.model.request.ValidateRecipeFlowRequest
import com.kdjj.domain.model.request.ValidateRecipeStepFlowRequest
import com.kdjj.domain.model.response.ValidateRecipeFlowResponse
import com.kdjj.domain.model.response.ValidateRecipeStepFlowResponse
import com.kdjj.domain.usecase.UseCase
import com.kdjj.presentation.common.FILE_PATH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

internal sealed class RecipeEditorDto {

    data class RecipeMetaDto(
        val imgHashFlow: MutableStateFlow<String?>,
        val titleFlow: MutableStateFlow<String>,
        val typeIntFlow: MutableStateFlow<Int>,
        val stuffFlow: MutableStateFlow<String>,
        val recipeId: String,
        val authorId: String,

        val titleValidFlow: StateFlow<Boolean>,
        val stuffValidFlow: StateFlow<Boolean>,

        val viewCount: Int = 0,
        val isFavorite: Boolean = false,
        val state: RecipeState
    ) : RecipeEditorDto() {

        val imgPathFlow = imgHashFlow.map { hash -> hash?.let { "${FILE_PATH}/${hash}.png" } }

        companion object {

            fun create(
                validateUseCase: UseCase<ValidateRecipeFlowRequest, ValidateRecipeFlowResponse>,
                scope: CoroutineScope
            ): RecipeMetaDto {
                val titleFlow = MutableStateFlow("")
                val stuffFlow = MutableStateFlow("")

                val response = validateUseCase(ValidateRecipeFlowRequest(
                    titleFlow = titleFlow,
                    stuffFlow = stuffFlow
                ))

                return RecipeMetaDto(
                    titleFlow = titleFlow,
                    typeIntFlow = MutableStateFlow(0),
                    stuffFlow = stuffFlow,
                    imgHashFlow = MutableStateFlow(""),

                    titleValidFlow = response.titleFlow.stateIn(
                        scope = scope,
                        started = SharingStarted.Lazily,
                        initialValue = false
                    ),
                    stuffValidFlow = response.stuffFlow.stateIn(
                        scope = scope,
                        started = SharingStarted.Lazily,
                        initialValue = false
                    ),

                    recipeId = "",
                    authorId = "",
                    state = RecipeState.CREATE
                )
            }
        }
    }

    data class RecipeStepDto(
        val nameFlow: MutableStateFlow<String>,
        val imgHashFlow: MutableStateFlow<String?>,
        val typeIntFlow: MutableStateFlow<Int>,
        val descriptionFlow: MutableStateFlow<String>,
        val minutesFlow: MutableStateFlow<Int>,
        val secondsFlow: MutableStateFlow<Int>,

        val nameValidFlow: StateFlow<Boolean>,
        val descriptionValidFlow: StateFlow<Boolean>,

        val stepId: String
    ) : RecipeEditorDto() {

        val imgPathFlow = imgHashFlow.map { hash -> hash?.let { "${FILE_PATH}/${hash}.png" } }

        companion object {

            fun create(
                validateUseCase: UseCase<ValidateRecipeStepFlowRequest, ValidateRecipeStepFlowResponse>,
                scope: CoroutineScope
            ) : RecipeStepDto {
                val nameFlow = MutableStateFlow("")
                val descriptionFlow = MutableStateFlow("")

                val response = validateUseCase(ValidateRecipeStepFlowRequest(
                    nameFlow = nameFlow,
                    descriptionFlow = descriptionFlow
                ))

                return RecipeStepDto(
                    nameFlow = nameFlow,
                    imgHashFlow = MutableStateFlow(""),
                    typeIntFlow = MutableStateFlow(0),
                    descriptionFlow = descriptionFlow,
                    minutesFlow = MutableStateFlow(0),
                    secondsFlow = MutableStateFlow(0),

                    nameValidFlow = response.nameFlow.stateIn(
                        scope = scope,
                        started = SharingStarted.Lazily,
                        initialValue = false
                    ),
                    descriptionValidFlow = response.descriptionFlow.stateIn(
                        scope = scope,
                        started = SharingStarted.Lazily,
                        initialValue = false
                    ),

                    stepId = ""
                )
            }
        }
    }

    object PlusButton : RecipeEditorDto()
}