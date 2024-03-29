package com.kdjj.presentation.viewmodel.home.my

import androidx.lifecycle.*
import com.kdjj.domain.model.Recipe
import com.kdjj.domain.model.request.EmptyRequest
import com.kdjj.domain.model.request.FetchMyFavoriteRecipeListRequest
import com.kdjj.domain.model.request.FetchMyLatestRecipeListRequest
import com.kdjj.domain.model.request.FetchMyTitleRecipeListRequest
import com.kdjj.domain.usecase.FlowUseCase
import com.kdjj.domain.usecase.ResultUseCase
import com.kdjj.presentation.common.Event
import com.kdjj.presentation.model.MyRecipeItem
import com.kdjj.presentation.model.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MyRecipeViewModel @Inject constructor(
    private val latestRecipeUseCase: ResultUseCase<FetchMyLatestRecipeListRequest, List<Recipe>>,
    private val favoriteRecipeUseCase: ResultUseCase<FetchMyFavoriteRecipeListRequest, List<Recipe>>,
    private val titleRecipeUseCase: ResultUseCase<FetchMyTitleRecipeListRequest, List<Recipe>>,
    private val getRecipeUpdateFlowUseCase: FlowUseCase<EmptyRequest, Unit>,
    private val deleteUselessImageFileUseCase: ResultUseCase<EmptyRequest, Unit>
) : ViewModel() {

    private val _liveSortType = MutableLiveData<SortType>()
    val liveSortType: LiveData<SortType> get() = _liveSortType

    private val _liveRecipeItemSelected = MutableLiveData<MyRecipeItem.MyRecipe?>()
    val liveRecipeItemSelected: LiveData<MyRecipeItem.MyRecipe?> get() = _liveRecipeItemSelected

    private val _liveLoading = MutableLiveData(false)
    val liveLoading: LiveData<Boolean> get() = _liveLoading

    private val _liveRecipeList = MutableLiveData(listOf<MyRecipeItem.MyRecipe>())
    private val _liveFetching = MutableLiveData(false)
    val liveRecipeItemList: LiveData<List<MyRecipeItem>> = MediatorLiveData<List<MyRecipeItem>>().apply {
        addSource(_liveRecipeList) { recipeList ->
            value = if (_liveFetching.value == true) {
                listOf(MyRecipeItem.PlusButton) + recipeList + MyRecipeItem.Progress
            } else {
                listOf(MyRecipeItem.PlusButton) + recipeList
            }
        }

        addSource(_liveFetching) { isFetching ->
            _liveRecipeList.value?.let { recipeList ->
                value = if (isFetching) {
                    listOf(MyRecipeItem.PlusButton) + recipeList + MyRecipeItem.Progress
                } else {
                    listOf(MyRecipeItem.PlusButton) + recipeList
                }
            }
        }
    }

    private var job: Job? = null

    private val _eventMyRecipe = MutableLiveData<Event<MyRecipeEvent>>()
    val eventMyRecipe: LiveData<Event<MyRecipeEvent>> get() = _eventMyRecipe

    val clickFlow = MutableSharedFlow<ButtonClick>(extraBufferCapacity = 1)

    sealed class MyRecipeEvent {
        object DataLoadFailed : MyRecipeEvent()
    }

    sealed class ButtonClick {
        object AddRecipeHasPressed : ButtonClick()
        class DoubleClicked(val item: MyRecipeItem.MyRecipe) : ButtonClick()
        object SearchIconClicked : ButtonClick()
    }

    init {
        setSortType(SortType.SORT_BY_TIME)

        viewModelScope.launch {
            deleteUselessImageFileUseCase(EmptyRequest)
        }

        viewModelScope.launch {
            getRecipeUpdateFlowUseCase(EmptyRequest)
                .collect {
                    refreshRecipeList()
                }
        }
    }

    fun setSortType(sortType: SortType) {
        if (_liveSortType.value != sortType) {
            _liveSortType.value = sortType
            fetchRecipeList(0)
        }
    }

    fun refreshRecipeList() {
        fetchRecipeList(0)
    }

    fun fetchRecipeList(page: Int) {
        if (_liveFetching.value == true && page > 0) return

        if (page == 0) {
            job?.cancel()
            _liveLoading.value = true
        } else {
            _liveFetching.value = true
        }

        job = viewModelScope.launch {
            when (_liveSortType.value) {
                SortType.SORT_BY_TIME ->
                    latestRecipeUseCase(FetchMyLatestRecipeListRequest(page))
                SortType.SORT_BY_FAVORITE ->
                    favoriteRecipeUseCase(FetchMyFavoriteRecipeListRequest(page))
                SortType.SORT_BY_NAME ->
                    titleRecipeUseCase(FetchMyTitleRecipeListRequest(page))
                else -> return@launch
            }.onSuccess { fetchedRecipeList ->
                val myRecipeList = fetchedRecipeList.map { MyRecipeItem.MyRecipe(it) }
                if (page == 0) {
                    _liveRecipeList.value = myRecipeList
                } else {
                    _liveRecipeList.value = _liveRecipeList.value?.plus(myRecipeList)
                }
            }.onFailure {
                if (it !is CancellationException) {
                    _eventMyRecipe.value = Event(MyRecipeEvent.DataLoadFailed)
                    it.printStackTrace()
                }
            }
            _liveFetching.value = false
            _liveLoading.value = false
        }
    }

    fun recipeItemSelected(selectedRecipe: MyRecipeItem.MyRecipe) {
        if (_liveRecipeItemSelected.value != selectedRecipe) {
            _liveRecipeItemSelected.value = selectedRecipe
        } else {
            clickFlow.tryEmit(ButtonClick.DoubleClicked(selectedRecipe))
        }
    }

    fun moveToRecipeEditorActivity() {
        clickFlow.tryEmit(ButtonClick.AddRecipeHasPressed)
    }

    fun moveToRecipeSearchFragment() {
        clickFlow.tryEmit(ButtonClick.SearchIconClicked)
    }
}
