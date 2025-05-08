package ru.pkstudio.localhomeworkandtaskmanager.core.components.kanban

import android.content.ClipData
import android.util.Log
import android.view.DragEvent
import android.view.View
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <M, A> KanbanBoard(
    modifier: Modifier = Modifier,
    items: List<KanbanItem<M, A>>,
    header: @Composable (M) -> Unit,
    footer: @Composable (M) -> Unit,
    columnFiller: @Composable (A) -> Unit,
    onColumnFillerClicked: (rowIndex: Int, columnIndex: Int) -> Unit,
    borderWidth: Dp = 1.dp,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    borderColorFromItem: ((M) -> Color)? = null,
    borderShape: Shape = RoundedCornerShape(8.dp),
    columnWidth: Dp = 200.dp,
    columnBackgroundColor: Color = Color.White,
    spaceBetweenColumns: Dp = 10.dp,
    spaceBetweenHeaderAndItems: Dp = 8.dp,
    spaceBetweenFooterAndItems: Dp = 8.dp,
    onStartDragAndDrop: (oldRowId: Int, oldColumnId: Int) -> Unit,
    onEndDragAndDrop: (oldRowId: Int, oldColumnId: Int, newRowId: Int) -> Unit,
    itemInDeleteBtn: (oldRowId: Int, oldColumnId: Int) -> Unit,
    isItemDrag: (Boolean) -> Unit
) {
    var moveItemEvent by remember {
        mutableStateOf(false)
    }
    var shimmerBoxSize by remember {
        mutableStateOf(IntSize.Zero)
    }
    var deleteBoxWidth by remember {
        mutableIntStateOf(0)
    }
    var deleteBoxHeight by remember {
        mutableIntStateOf(0)
    }
    var deleteBoxGlobalCoordinates by remember {
        mutableStateOf(Offset.Zero)
    }
    var isInDeleteBtn by remember {
        mutableStateOf(false)
    }
    val deleteBtnColor by animateColorAsState(
        targetValue =
            if (!isInDeleteBtn) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.errorContainer
    )

    var kanbanHeight by remember {
        mutableIntStateOf(0)
    }
    var isDrag by remember {
        mutableStateOf(false)
    }
    val view = LocalView.current
    val localDensity = LocalDensity.current
    val lazyListState = rememberLazyListState()
    val scrollZoneSize = 100.dp
    val scrollSpeed = 20f
    val coroutineScope = rememberCoroutineScope()

    val savedItemsWithIndices = remember(key1 = items) {
        val newListItems: SnapshotStateList<KanbanUtilRowItem<M, A>> = mutableStateListOf()

        items.forEach { kanbanItem ->
            var rowId: Long
            do {
                rowId = Random.nextLong()
            } while (newListItems.any { item -> item.id == rowId })

            val columnItems = mutableListOf<Item<A>>()
            kanbanItem.columnItems.forEach { columnItem ->
                var columnId: Long
                do {
                    columnId = Random.nextLong()
                } while (columnItems.any { item -> item.id == columnId })
                columnItems.add(
                    Item(
                        id = columnId,
                        item = columnItem
                    )
                )
            }

            val newKanbanItem = KanbanUtilRowItem(
                id = rowId,
                rowItem = kanbanItem.rowItem,
                items = columnItems
            )
            newListItems.add(newKanbanItem)
        }
        newListItems
    }
    var oldColumnId by rememberSaveable {
        mutableIntStateOf(-1)
    }
    var oldRowId by rememberSaveable {
        mutableIntStateOf(-1)
    }
    var targetColumnId by rememberSaveable {
        mutableIntStateOf(-1)
    }
    var columnWidthSource by rememberSaveable {
        mutableIntStateOf(0)
    }
    var fingerPosition by remember {
        mutableStateOf(Offset.Zero)
    }
    var isScrolling by remember {
        mutableStateOf(false)
    }


    fun moveItem(oldRowId: Int, oldColumnId: Int, newRowId: Int) {
        if (
            newRowId in savedItemsWithIndices.indices
            && oldRowId in savedItemsWithIndices.indices
            && oldColumnId in savedItemsWithIndices[oldRowId].items.indices
            && oldRowId != newRowId
        ) {
            onEndDragAndDrop(oldRowId, oldColumnId, newRowId)
        }
    }
    LaunchedEffect(key1 = moveItemEvent) {
        if (moveItemEvent) {
            moveItem(
                oldRowId = oldRowId,
                oldColumnId = oldColumnId,
                newRowId = targetColumnId
            )
        }
    }


    // Устанавливаем слушатель DragEvent
    DisposableEffect(Unit) {
        var scrollJob: Job? = null // Для управления единственной корутиной
        val dragListener = View.OnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    isDrag = true
                    isItemDrag(true)
                }

                DragEvent.ACTION_DRAG_LOCATION -> {
                    fingerPosition = Offset(event.x, event.y)
                    if (
                        fingerPosition.x in (deleteBoxGlobalCoordinates.x..deleteBoxGlobalCoordinates.x + deleteBoxWidth)
                        && fingerPosition.y in (deleteBoxGlobalCoordinates.y..deleteBoxGlobalCoordinates.y + deleteBoxHeight)
                    ) {
                        if (!isInDeleteBtn) {
                            isInDeleteBtn = true
                        }
                    } else {
                        if (isInDeleteBtn) {
                            isInDeleteBtn = false
                        }
                    }
                    val xPos = fingerPosition.x
                    val viewWidth = view.width.toFloat()

                    // Проверяем, нужно ли начать или продолжить прокрутку
                    if (!isScrolling && (
                                (xPos < scrollZoneSize.value && lazyListState.canScrollBackward) ||
                                        (xPos > viewWidth - scrollZoneSize.value && lazyListState.canScrollForward)
                                )
                    ) {
                        isScrolling = true
                        scrollJob?.cancel() // Отменяем предыдущую корутину, если она была
                        scrollJob = coroutineScope.launch {
                            while (isActive) {
                                val currentX = fingerPosition.x
                                when {
                                    currentX < scrollZoneSize.value &&
                                            lazyListState.canScrollBackward -> {
                                        lazyListState.scrollBy(-scrollSpeed)
                                    }

                                    currentX > viewWidth - scrollZoneSize.value &&
                                            lazyListState.canScrollForward -> {
                                        lazyListState.scrollBy(scrollSpeed)
                                    }

                                    else -> {
                                        break // Выходим из цикла если вышли из зоны
                                    }
                                }
                                delay(16) // ~60fps
                            }
                            isScrolling = false
                        }
                    }
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    isDrag = false
                    isItemDrag(false)
                    isScrolling = false
                    if (scrollJob != null && scrollJob!!.isActive) {
                        scrollJob!!.cancel()
                    }
                    val screenFullWidthDp = with(localDensity) { view.width.toDp() }
                    val columnWidthDp = with(localDensity) { columnWidthSource.toDp() }
                    val firstVisibleItemIndex = lazyListState.firstVisibleItemIndex
                    val firstVisibleItemOffset =
                        with(localDensity) { lazyListState.firstVisibleItemScrollOffset.toDp() }
                    val positionHorizontalDp = with(localDensity) { fingerPosition.x.toDp() }
                    val numberAllRowsAtScreen = (screenFullWidthDp / columnWidthDp).roundToInt()

                    val targetedColumnId = if (numberAllRowsAtScreen == 2) {
                        when {
                            firstVisibleItemOffset == 0.dp
                                    && positionHorizontalDp <= columnWidthDp -> firstVisibleItemIndex

                            firstVisibleItemOffset > 0.dp
                                    && positionHorizontalDp <= columnWidthDp - firstVisibleItemOffset -> firstVisibleItemIndex

                            else -> firstVisibleItemIndex + 1
                        }
                    } else {
                        when {
                            firstVisibleItemOffset == 0.dp -> {
                                if (positionHorizontalDp <= columnWidthDp) {
                                    firstVisibleItemIndex
                                } else {
                                    (firstVisibleItemIndex + (positionHorizontalDp / (columnWidthDp + spaceBetweenColumns))).toInt()
                                }
                            }

                            firstVisibleItemOffset > 0.dp -> {
                                if (positionHorizontalDp <= columnWidthDp - firstVisibleItemOffset) {
                                    firstVisibleItemIndex
                                } else {
                                    val offset =
                                        positionHorizontalDp - (columnWidthDp - firstVisibleItemOffset + spaceBetweenColumns)
                                    if (offset <= 0.dp) {
                                        firstVisibleItemIndex
                                    } else {
                                        (firstVisibleItemIndex + 1 + (positionHorizontalDp / (columnWidthDp + spaceBetweenColumns))).toInt()
                                    }
                                }
                            }

                            else -> {
                                firstVisibleItemIndex
                            }
                        }
                    }
                    targetColumnId = targetedColumnId
                    coroutineScope.launch {
                        moveItemEvent = true
                        delay(100)
                        moveItemEvent = false
                    }
                    if (isInDeleteBtn) {
                        itemInDeleteBtn(oldRowId, oldColumnId)
                    }
                }
            }
            true // Указываем, что событие обработано
        }
        view.setOnDragListener(dragListener)
        onDispose {
            scrollJob?.cancel()
            view.setOnDragListener(null) // Очищаем слушатель при выходе
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { layoutCoordinates ->
                kanbanHeight = layoutCoordinates.size.height
            },
    ) {
        LazyRow(
            state = lazyListState,
            contentPadding = PaddingValues(spaceBetweenColumns),
            horizontalArrangement = Arrangement.spacedBy(spaceBetweenColumns)
        ) {
            itemsIndexed(
                items = savedItemsWithIndices,
                key = { _, item ->
                    item.id
                }
            ) { rowIndex, rowItem ->
                Column(
                    modifier = Modifier
                        .width(if (columnWidth == 0.dp) Dp.Infinity else columnWidth)
                        .border(
                            width = borderWidth,
                            color = borderColorFromItem?.invoke(
                                rowItem.rowItem
                            ) ?: borderColor,
                            shape = borderShape
                        )
                        .background(columnBackgroundColor)
                        .onSizeChanged { intSize ->
                            columnWidthSource = intSize.width
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    header(rowItem.rowItem)
                    Spacer(modifier = Modifier.height(spaceBetweenHeaderAndItems))
                    LazyColumn(
                        modifier = Modifier
                            .heightIn(min = 300.dp)
                    ) {
                        itemsIndexed(
                            items = rowItem.items,
                            key = { _, item ->
                                item.id
                            }
                        ) { columnIndex, columnItem ->
                            Box(
                                modifier = Modifier
                                    .background(Color.Transparent)
                                    .dragAndDropSource {
                                        detectTapGestures(
                                            onLongPress = {
                                                startTransfer(
                                                    transferData = DragAndDropTransferData(
                                                        clipData = ClipData
                                                            .newPlainText(
                                                                "label",
                                                                columnIndex.toString()
                                                            )
                                                    )
                                                )
                                                oldRowId = rowIndex
                                                oldColumnId = columnIndex
                                                onStartDragAndDrop(
                                                    rowIndex,
                                                    columnIndex
                                                )
                                            },
                                            onTap = {
                                                onColumnFillerClicked(
                                                    rowIndex,
                                                    columnIndex
                                                )
                                            }
                                        )
                                    }
                                    .onGloballyPositioned { layoutCoordinates ->
                                        shimmerBoxSize = layoutCoordinates.size
                                    }

                            ) {
                                columnFiller(columnItem.item)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(spaceBetweenFooterAndItems))
                    footer(rowItem.rowItem)
                }
            }
        }
        AnimatedVisibility(isDrag) {
            FloatingActionButton(
                modifier = Modifier
                    .size(70.dp)
                    .offset {
                        IntOffset(
                            x = ((view.width / 2) - (deleteBoxWidth / 2)),
                            y = kanbanHeight - deleteBoxHeight - 40
                        )
                    }

                    .onGloballyPositioned { layoutCoordinates ->
                        deleteBoxWidth = layoutCoordinates.size.width
                        deleteBoxHeight = layoutCoordinates.size.height
                        deleteBoxGlobalCoordinates = layoutCoordinates.positionInRoot()
                        Log.d("zxczcxzcxzz", "KanbanBoard: ${layoutCoordinates.positionInRoot()}")

                    },
                onClick = {

                },
                containerColor = deleteBtnColor
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentDescription = ""
                )
            }
        }
    }
}

