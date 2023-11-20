package nau.android.taskify.ui.eisenhowerMatrix

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import nau.android.taskify.ui.model.Task


@Composable
fun DragTarget(
    modifier: Modifier = Modifier,
    dataToDrop: Task,
    eisenhowerMatrixQuadrant: EisenhowerMatrixQuadrant,
    content: @Composable (() -> Unit)
) {

    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    val currentState = LocalDragTargetInfo.current

    Box(modifier = modifier
        .onGloballyPositioned {
            currentPosition = it.localToWindow(
                Offset.Zero
            )
        }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(onDragStart = {
                //currentState.itemDropped = false
                currentState.dataToDrop = dataToDrop
                currentState.sourceQuadrant = eisenhowerMatrixQuadrant
                currentState.isDragging = true
                currentState.dragPosition = currentPosition + it
                currentState.draggableComposable = content
                currentState.itemDropped = false
            }, onDrag = { change, dragAmount ->
                change.consume()
                currentState.dragOffset += Offset(dragAmount.x, dragAmount.y)
            }, onDragEnd = {
                currentState.isDragging = false
                currentState.dragOffset = Offset.Zero
            }, onDragCancel = {
                currentState.dragOffset = Offset.Zero
                currentState.isDragging = false
            })
        },
        contentAlignment = Alignment.TopStart
    ) {
        if (currentState.dataToDrop != dataToDrop) {
            content()
        }
    }
}

@Composable
fun DraggableScreen(
    content: @Composable BoxScope.() -> Unit
) {
    val state = remember {
        DragTargetInfo()
    }

    CompositionLocalProvider(LocalDragTargetInfo provides state) {
        Box(modifier = Modifier.fillMaxSize()) {
            content()
            if (state.isDragging) {
                var targetSize by remember {
                    mutableStateOf(IntSize.Zero)
                }
                Box(modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .graphicsLayer {
                        val offset = (state.dragOffset + state.dragPosition)
                        alpha = if (targetSize == IntSize.Zero) 0f else .9f
                        translationX = offset.x.minus(targetSize.width / 2)
                        translationY = offset.y.minus(targetSize.height / 2)
                    }
                    .onGloballyPositioned {
                        targetSize = it.size
                    }) {
                    state.draggableComposable?.invoke()
                }
            }
        }
    }
}

internal class DragTargetInfo {
    var isDragging by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableComposable by mutableStateOf<(@Composable () -> Unit)?>(null)
    var dataToDrop by mutableStateOf<Any?>(null)
    var sourceQuadrant by mutableStateOf<EisenhowerMatrixQuadrant?>(null)
    var itemDropped by mutableStateOf(false)
}