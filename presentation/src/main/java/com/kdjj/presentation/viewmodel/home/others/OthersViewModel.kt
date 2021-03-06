package com.kdjj.presentation.viewmodel.home.others

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kdjj.domain.model.Recipe
import com.kdjj.domain.model.exception.ApiException
import com.kdjj.domain.model.exception.NetworkException
import com.kdjj.domain.model.request.FetchOthersLatestRecipeListRequest
import com.kdjj.domain.model.request.FetchOthersPopularRecipeListRequest
import com.kdjj.domain.usecase.ResultUseCase
import com.kdjj.presentation.common.Event
import com.kdjj.presentation.mapper.toRecipeListDto
import com.kdjj.presentation.model.RecipeListDto
import com.kdjj.presentation.model.ResponseError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class OthersViewModel @Inject constructor(
    private val fetchOthersLatestRecipeListUseCase: ResultUseCase<FetchOthersLatestRecipeListRequest, List<Recipe>>,
    private val fetchOthersPopularRecipeListUseCase: ResultUseCase<FetchOthersPopularRecipeListRequest, List<Recipe>>,
) : ViewModel() {

    private var _liveSortType = MutableLiveData<OthersSortType>()
    val liveSortType: LiveData<OthersSortType> get() = _liveSortType

    private var _liveFetchLock = MutableLiveData(false)
    val liveFetchLock: LiveData<Boolean> get() = _liveFetchLock

    private var _liveRecipeList = MutableLiveData<List<RecipeListDto>>()
    val liveRecipeList: LiveData<List<RecipeListDto>> get() = _liveRecipeList

    private var fetchingJob: Job? = null

    val setFetchEnabled = Runnable {
        _liveFetchLock.value = false
    }

    val clickFlow = MutableSharedFlow<ButtonClick>(extraBufferCapacity = 1)

    private var _eventOthersRecipe = MutableLiveData<Event<OtherRecipeEvent>>()
    val eventOthersRecipe: LiveData<Event<OtherRecipeEvent>> get() = _eventOthersRecipe

    sealed class OtherRecipeEvent {
        class ShowSnackBar(val error: ResponseError) : OtherRecipeEvent()
    }

    sealed class ButtonClick {
        object SearchIconClicked : ButtonClick()
        class RecipeItemClicked(val item: RecipeListDto) : ButtonClick()
    }

    init {
        setChecked(OthersSortType.LATEST)
    }

    fun setChecked(othersSortType: OthersSortType) {
        if (_liveSortType.value != othersSortType) {
            _liveSortType.value = othersSortType
            initFetching()
            fetchNextRecipeListPage(true)
        }
    }

    private fun initFetching() {
        fetchingJob?.cancel()
        _liveFetchLock.value = false
        _liveRecipeList.value = listOf()
    }

    fun refreshList() {
        initFetching()
        fetchNextRecipeListPage(true)
    }

    fun fetchNextRecipeListPage(isFirstPage: Boolean) {
        if (liveFetchLock.value == true) return

        _liveSortType.value?.let {

            _liveFetchLock.value = true

            fetchingJob = viewModelScope.launch {
                if (it == OthersSortType.LATEST) {
                    fetchRemoteLatestRecipeList(isFirstPage)
                } else {
                    fetchRemotePopularRecipeList(isFirstPage)
                }
            }
        }
    }

    private suspend fun fetchRemoteLatestRecipeList(isFirstPage: Boolean) {
        onRecipeListFetched(
            fetchOthersLatestRecipeListUseCase(
                FetchOthersLatestRecipeListRequest(isFirstPage)
            )
        )
    }

    private suspend fun fetchRemotePopularRecipeList(isFirstPage: Boolean) {
        onRecipeListFetched(
            fetchOthersPopularRecipeListUseCase(
                FetchOthersPopularRecipeListRequest(isFirstPage)
            )
        )
    }

    private fun onRecipeListFetched(result: Result<List<Recipe>>) {
        result.onSuccess { list ->
            _liveRecipeList.value?.let {
                if (list.isEmpty()) {
                    _liveFetchLock.value = false
                    return
                }
                val othersRecipeModelList = list.map { recipe -> recipe.toRecipeListDto() }
                if (it.isEmpty()) _liveRecipeList.value = othersRecipeModelList
                else _liveRecipeList.value = it.plus(othersRecipeModelList)
            }
        }.onFailure {
            _liveFetchLock.value = false
            when (it) {
                is NetworkException -> {
                    _eventOthersRecipe.value = Event(OtherRecipeEvent.ShowSnackBar(ResponseError.NETWORK_CONNECTION))
                }
                is ApiException -> {
                    _eventOthersRecipe.value = Event(OtherRecipeEvent.ShowSnackBar(ResponseError.SERVER))
                }
                is CancellationException -> {
                }
                else -> _eventOthersRecipe.value = Event(OtherRecipeEvent.ShowSnackBar(ResponseError.UNKNOWN))

            }
        }
    }

    fun moveToRecipeSearchFragment() {
        clickFlow.tryEmit(ButtonClick.SearchIconClicked)
    }

    fun recipeItemClick(recipeModel: RecipeListDto) {
        clickFlow.tryEmit(ButtonClick.RecipeItemClicked(recipeModel))
    }

    enum class OthersSortType {
        POPULAR, LATEST
    }
}
