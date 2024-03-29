package com.kdjj.presentation.viewmodel.home.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kdjj.domain.model.Recipe
import com.kdjj.domain.model.exception.ApiException
import com.kdjj.domain.model.exception.NetworkException
import com.kdjj.domain.model.request.EmptyRequest
import com.kdjj.domain.model.request.FetchMySearchRecipeListRequest
import com.kdjj.domain.model.request.FetchOthersSearchRecipeListRequest
import com.kdjj.domain.usecase.FlowUseCase
import com.kdjj.domain.usecase.ResultUseCase
import com.kdjj.presentation.common.Event
import com.kdjj.presentation.mapper.toRecipeListDto
import com.kdjj.presentation.model.RecipeListDto
import com.kdjj.presentation.model.ResponseError
import com.kdjj.presentation.model.SearchTabState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    private val fetchMySearchUseCase: ResultUseCase<FetchMySearchRecipeListRequest, List<Recipe>>,
    private val fetchOthersSearchUseCase: ResultUseCase<FetchOthersSearchRecipeListRequest, List<Recipe>>,
    private val getRecipeUpdateFlowUseCase: FlowUseCase<EmptyRequest, Unit>
) : ViewModel() {
    private val _liveTabState = MutableLiveData(SearchTabState.OTHERS_RECIPE)
    val liveTabState: LiveData<SearchTabState> get() = _liveTabState

    private val _liveResultList = MutableLiveData<List<RecipeListDto>>(listOf())
    val liveResultList: LiveData<List<RecipeListDto>> get() = _liveResultList

    val liveKeyword = MutableStateFlow("")

    private var fetchingJob: Job? = null

    private val _liveLoading = MutableLiveData(false)
    val liveLoading: LiveData<Boolean> get() = _liveLoading

    private var isFetching = false
        set(value) {
            field = value
            _liveLoading.value = isFetching
        }

    private val _eventSearchRecipe = MutableLiveData<Event<SearchRecipeEvent>>()
    val eventSearchRecipe: LiveData<Event<SearchRecipeEvent>> get() = _eventSearchRecipe

    private val _liveNoResult = MutableLiveData(false)
    val liveNoResult: LiveData<Boolean> get() = _liveNoResult

    private var lastKeyword = ""
    private var lastTabState = _liveTabState.value ?: SearchTabState.OTHERS_RECIPE

    val clickFlow = MutableSharedFlow<ButtonClick>(extraBufferCapacity = 1)

    sealed class SearchRecipeEvent {
        class Exception(val error: ResponseError) : SearchRecipeEvent()
    }

    sealed class ButtonClick {
        class Summary(val item: RecipeListDto) : ButtonClick()
    }

    init {
        viewModelScope.launch {
            val updateFlow = getRecipeUpdateFlowUseCase(EmptyRequest)
            updateFlow.collect {
                if (liveTabState.value == SearchTabState.MY_RECIPE) {
                    updateSearchKeyword()
                }
            }
        }
    }

    fun setTabState(tabState: SearchTabState) {
        if (_liveTabState.value != tabState) {
            fetchingJob?.cancel()
            isFetching = false
            _liveTabState.value = tabState
        }
    }

    fun updateSearchKeyword() {
        if (lastKeyword == liveKeyword.value && lastTabState == liveTabState.value) {
            return
        }

        if (isFetching) {
            fetchingJob?.cancel()
        }
        isFetching = true

        _liveResultList.value = listOf()
        _liveNoResult.value = false
        if (liveKeyword.value.isBlank()) {
            isFetching = false
            lastKeyword = ""
            return
        }

        fetchingJob = viewModelScope.launch {
            when (liveTabState.value) {
                SearchTabState.OTHERS_RECIPE -> {
                    fetchOthersSearchUseCase(
                        FetchOthersSearchRecipeListRequest(
                            liveKeyword.value, true
                        )
                    )
                        .onSuccess {
                            if (it.isEmpty()) {
                                _liveNoResult.value = true
                            }
                            _liveResultList.value = it.map(Recipe::toRecipeListDto)
                            lastKeyword = liveKeyword.value
                            lastTabState = SearchTabState.OTHERS_RECIPE
                        }
                        .onFailure { t ->
                            setException(t)
                        }
                }
                SearchTabState.MY_RECIPE -> {
                    fetchMySearchUseCase(
                        FetchMySearchRecipeListRequest(
                            liveKeyword.value, 0
                        )
                    )
                        .onSuccess {
                            if (it.isEmpty()) {
                                _liveNoResult.value = true
                            }
                            _liveResultList.value = it.map(Recipe::toRecipeListDto)
                            lastKeyword = liveKeyword.value
                            lastTabState = SearchTabState.MY_RECIPE
                        }
                        .onFailure { t ->
                            setException(t)
                        }
                }

                null -> {
                    // no-op
                }
            }
            isFetching = false
        }
    }

    fun loadMoreRecipe() {
        if (isFetching) return
        isFetching = true

        if (!liveKeyword.value.isNotBlank()) {
            _liveResultList.value = listOf()
            isFetching = false
            return
        }

        fetchingJob = viewModelScope.launch {
            when (liveTabState.value) {
                SearchTabState.OTHERS_RECIPE -> {
                    fetchOthersSearchUseCase(
                        FetchOthersSearchRecipeListRequest(
                            liveKeyword.value,
                            false
                        )
                    )
                        .onSuccess { recipeList ->
                            _liveResultList.value = _liveResultList.value
                                ?.let { it + recipeList.map(Recipe::toRecipeListDto) }
                                ?: recipeList.map(Recipe::toRecipeListDto)
                        }
                        .onFailure { t ->
                            setException(t)
                        }
                }
                SearchTabState.MY_RECIPE -> {
                    fetchMySearchUseCase(
                        FetchMySearchRecipeListRequest(
                            liveKeyword.value,
                            _liveResultList.value?.size ?: 0
                        )
                    )
                        .onSuccess { recipeList ->
                            _liveResultList.value = _liveResultList.value
                                ?.let { it + recipeList.map(Recipe::toRecipeListDto) }
                                ?: recipeList.map(Recipe::toRecipeListDto)
                        }
                        .onFailure { t ->
                            setException(t)
                        }
                }
                null -> {
                    // no-op
                }
            }
            isFetching = false
        }
    }

    fun moveToSummary(recipeModel: RecipeListDto) {
        clickFlow.tryEmit(ButtonClick.Summary(recipeModel))
    }


    private fun setException(throwable: Throwable) {
        when (throwable) {
            is NetworkException -> {
                _eventSearchRecipe.value = Event(SearchRecipeEvent.Exception(ResponseError.NETWORK_CONNECTION))
            }
            is ApiException -> {
                _eventSearchRecipe.value = Event(SearchRecipeEvent.Exception(ResponseError.SERVER))
            }
            is CancellationException -> {
            }
            else -> _eventSearchRecipe.value = Event(SearchRecipeEvent.Exception(ResponseError.UNKNOWN))
        }
    }
}

