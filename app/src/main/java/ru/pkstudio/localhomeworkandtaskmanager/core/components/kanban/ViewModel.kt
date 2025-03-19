package ru.pakarpichev.homeworktool.core.presentation.components.kanban

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.pkstudio.localhomeworkandtaskmanager.core.components.kanban.KanbanTestInfo
import ru.pkstudio.localhomeworkandtaskmanager.core.components.kanban.StageFromServer
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor() : ViewModel() {

    data class TestState(
        val rowItems: List<String> = emptyList(),
        val list: List<KanbanTestInfo> = emptyList(),
        val stagesFromServer: List<StageFromServer> = emptyList()
    )

    private val _uiState = MutableStateFlow(TestState())
    val uiState = _uiState.asStateFlow()

    init {
        updateState()
    }


    private fun updateState() {
        val rowItems = listOf(
            "todo",
            "done",
            "doing"
        )
        viewModelScope.launch(Dispatchers.Default) {
            val stagesFromServer = mutableListOf<StageFromServer>()
            var indexOfStageName = 0
            val listList = listOf<List<String>>(
                listOf(
                    "сделка 1",
                    "сделка 2",
                    "сделка 3",
                    "сделка 4",
                ),
                listOf(
                    "сделка 5",
                    "сделка 6",
                    "сделка 7",
                    "сделка 8",
                ),
                listOf(
                    "сделка 9",
                    "сделка 10",
                    "сделка 11",
                    "сделка 12",
                ),
                listOf(
                    "сделка 13",
                    "сделка 14",
                    "сделка 15",
                    "сделка 16",
                ),
                listOf(
                    "сделка 17",
                    "сделка 18",
                    "сделка 19",
                    "сделка 20",
                ),
                listOf(
                    "сделка 21",
                    "сделка 22",
                    "сделка 23",
                    "сделка 24",
                )
            )

            (0..4).forEach { index ->
                stagesFromServer.add(
                    StageFromServer(
                        id = index,
                        name = rowItems[indexOfStageName],
                        deals = listList[index]
                    ),
                )
                indexOfStageName = if (indexOfStageName == 2) {
                    0
                } else {
                    indexOfStageName + 1
                }
            }
            _uiState.update {
                it.copy(
                    stagesFromServer = stagesFromServer.toMutableStateList()
                )
            }
//            var indexOfStageName = 0
//            val list = mutableListOf<KanbanTestInfo>()
//
//            (0..10).forEach { index ->
//                list.add(
//                    KanbanTestInfo(
//                        string = "test $index",
//                        stage = rowItems[indexOfStageName]
//                    ),
//                )
//                indexOfStageName = if (indexOfStageName == 2) {
//                    0
//                } else {
//                    indexOfStageName + 1
//                }
//            }
//            _uiState.update {
//                it.copy(
//                    rowItems = rowItems,
//                    list = list
//                )
//            }

        }
    }


    fun moveCard(oldRowId: Int, oldColumnId: Int, newRowId: Int) {
        val movedElement = _uiState.value.stagesFromServer[oldRowId].deals[oldColumnId]
        val items = _uiState.value.stagesFromServer.toMutableList()
        val oldItems = _uiState.value.stagesFromServer[oldRowId].deals.toMutableList()
        val newItems = _uiState.value.stagesFromServer[newRowId].deals.toMutableList()
        newItems.add(movedElement)
        oldItems.remove(movedElement)
        items[newRowId] = items[newRowId].copy(deals = newItems)
        items[oldRowId] = items[oldRowId].copy(deals = oldItems)
        _uiState.update {
            it.copy(
                stagesFromServer = items
            )
        }
    }
}