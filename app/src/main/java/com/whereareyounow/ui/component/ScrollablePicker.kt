package com.whereareyounow.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationResult
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun NumberPicker(
    state: MutableState<Long>,
    modifier: Modifier = Modifier,
    range: LongRange,
    onStateChanged: (Int) -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val numbersColumnHeight = 70.dp
    val halvedNumbersColumnHeight = numbersColumnHeight / 2
    val halvedNumbersColumnHeightPx = with(LocalDensity.current) { halvedNumbersColumnHeight.toPx() }
    fun animatedStateValue(offset: Float): Long = state.value - (offset / halvedNumbersColumnHeightPx).toLong()

    // 드래그에 의해 변경되는 y축 offset
    val animatedOffset = remember { Animatable(0f) }.apply {
        val offsetRange = remember(state.value, range) {
            val value = state.value
            val first = -(range.last - value) * halvedNumbersColumnHeightPx
            val last = -(range.first - value) * halvedNumbersColumnHeightPx
            first..last
        }
        updateBounds(offsetRange.start, offsetRange.endInclusive)
    }

    val coercedAnimatedOffset = animatedOffset.value % halvedNumbersColumnHeightPx

    // 실시간으로 변경되는 index
    val animatedStateValue = animatedStateValue(animatedOffset.value)

    Column(
        modifier = modifier
            .wrapContentSize()
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState { deltaY ->
                    coroutineScope.launch {
                        animatedOffset.snapTo(animatedOffset.value + deltaY)
                    }
                },
                onDragStopped = { velocity ->
                    coroutineScope.launch {
                        val endValue = animatedOffset.fling(
                            initialVelocity = velocity,
                            animationSpec = exponentialDecay(frictionMultiplier = 20f),
                            adjustTarget = { target ->
                                val coercedTarget = target % halvedNumbersColumnHeightPx
                                val coercedAnchors = listOf(-halvedNumbersColumnHeightPx, 0f, halvedNumbersColumnHeightPx)
                                val coercedPoint = coercedAnchors.minByOrNull { abs(it - coercedTarget) }!!
                                val base = halvedNumbersColumnHeightPx * (target / halvedNumbersColumnHeightPx).toInt()
                                coercedPoint + base
                            }
                        ).endState.value

                        state.value = animatedStateValue(endValue)
//                        onStateChanged(state.value)
                        // 애니메이션이 끝난 후 0으로 초기화
                        animatedOffset.snapTo(0f)
                    }
                }
            ),
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .offset { IntOffset(x = 0, y = coercedAnimatedOffset.roundToInt()) }
        ) {
            val labelModifier = Modifier.align(Alignment.Center)
            Label(
                text = if ((animatedStateValue - 2) in range) (animatedStateValue - 2).toString() else "",
                modifier = labelModifier
                    .offset(y = -(halvedNumbersColumnHeight * 2))
                    .alpha((coercedAnimatedOffset / halvedNumbersColumnHeightPx) / 4)
            )
            Label(
                text = if ((animatedStateValue - 1) in range) (animatedStateValue - 1).toString() else "",
                modifier = labelModifier
                    .offset(y = -halvedNumbersColumnHeight)
                    .alpha((coercedAnimatedOffset / halvedNumbersColumnHeightPx) / 4 * 3 + 0.25f)
            )
            Label(
                text = animatedStateValue.toString(),
                modifier = labelModifier
                    .alpha((1 - abs(coercedAnimatedOffset) / halvedNumbersColumnHeightPx) / 4 * 3 + 0.25f)
            )
            Label(
                text = if ((animatedStateValue + 1) in range) (animatedStateValue + 1).toString() else "",
                modifier = labelModifier
                    .offset(y = halvedNumbersColumnHeight)
                    .alpha((-coercedAnimatedOffset / halvedNumbersColumnHeightPx) / 4 * 3 + 0.25f)
            )
            Label(
                text = if ((animatedStateValue + 2) in range) (animatedStateValue + 2).toString() else "",
                modifier = labelModifier
                    .offset(y = halvedNumbersColumnHeight * 2)
                    .alpha((-coercedAnimatedOffset / halvedNumbersColumnHeightPx) / 4)
            )
        }
    }
}

@Composable
private fun Label(text: String, modifier: Modifier) {
    Text(
        text = text,
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(onLongPress = {
                // FIXME: Empty to disable text selection
            })
        },
        fontSize = 30.sp,
    )
}

private suspend fun Animatable<Float, AnimationVector1D>.fling(
    initialVelocity: Float,
    animationSpec: DecayAnimationSpec<Float>,
    adjustTarget: ((Float) -> Float)?,
    block: (Animatable<Float, AnimationVector1D>.() -> Unit)? = null,
): AnimationResult<Float, AnimationVector1D> {
    val targetValue = animationSpec.calculateTargetValue(value, initialVelocity)
    val adjustedTarget = adjustTarget?.invoke(targetValue)

    return if (adjustedTarget != null) {
        animateTo(
            targetValue = adjustedTarget,
            initialVelocity = initialVelocity,
            block = block
        )
    } else {
        animateDecay(
            initialVelocity = initialVelocity,
            animationSpec = animationSpec,
            block = block,
        )
    }
}