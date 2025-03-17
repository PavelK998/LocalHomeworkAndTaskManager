package ru.pakarpichev.homeworktool.core.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeableCard(
    modifier: Modifier = Modifier,
    isRevealed: Boolean,
    onExpanded: () -> Unit = {},
    onCollapsed: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit,
    content: @Composable () -> Unit
) {
    var actionsRowWidth by remember {
        mutableFloatStateOf(0f)
    }
    val offset = remember {
        Animatable(0f)
    }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = isRevealed) {
        if (isRevealed) {
            coroutineScope.launch {
                offset.animateTo(actionsRowWidth)
            }
        } else {
            coroutineScope.launch {
                offset.animateTo(0f)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .onSizeChanged { size ->
                    actionsRowWidth = size.width.toFloat()
                },

            verticalAlignment = Alignment.CenterVertically
        ) {
            actions()
        }
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(offset.value.roundToInt(), 0)
                }
                .pointerInput(actionsRowWidth) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            coroutineScope.launch {
                                val newOffset = (offset.value + dragAmount)
                                    .coerceIn(0f, actionsRowWidth)
                                offset.snapTo(newOffset)
                            }
                        },
                        onDragEnd = {
                            when {
                                offset.value >= actionsRowWidth / 2f -> {
                                    onExpanded()
                                    coroutineScope.launch {
                                        offset.animateTo(actionsRowWidth)
                                    }
                                }

                                else -> {
                                    onCollapsed()
                                    coroutineScope.launch {
                                        offset.animateTo(0f)
                                    }
                                }
                            }
                        }
                    )
                }
        ){
            content()
        }
    }
}

