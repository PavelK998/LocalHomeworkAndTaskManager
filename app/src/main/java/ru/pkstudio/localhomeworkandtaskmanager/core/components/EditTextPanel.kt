package ru.pkstudio.localhomeworkandtaskmanager.core.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatStrikethrough
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.RichTextState
import ru.pkstudio.localhomeworkandtaskmanager.core.util.Constants
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme
import kotlin.math.roundToInt

@Composable
fun EditTextPanel(
    modifier: Modifier = Modifier,
    textState: RichTextState,
    isExtraOptionsVisible: Boolean,
    onBoldBtnClick: () -> Unit,
    onItalicBtnClick: () -> Unit,
    onUnderlinedBtnClick: () -> Unit,
    onLineThroughBtnClick: () -> Unit,
    onFontSizeChange: (Int) -> Unit,
    onExtraOptionsBtnClick: () -> Unit,
) {
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExtraOptionsVisible) 180f else 0f
    )
    var isUnderlined by remember {
        mutableStateOf(false)
    }

    var isLineThrough by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(textState.currentSpanStyle) {
        val textDecoration = textState.currentSpanStyle.textDecoration
        textDecoration?.let { textDecorationResult ->
            val list = listOf(
                TextDecoration.LineThrough,
                TextDecoration.Underline
            )
            val countTextDecorations = list.count { textDecorationResult.contains(it) }
            if (countTextDecorations == 2) {
                isLineThrough = true
                isUnderlined = true
            } else {
                isLineThrough = textDecorationResult == TextDecoration.LineThrough
                isUnderlined = textDecorationResult == TextDecoration.Underline
            }
        }
        if (textDecoration == null) {
            isLineThrough = false
            isUnderlined = false
        }



    }


    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .heightIn(min = 48.dp)
    ) {
        AnimatedVisibility(isExtraOptionsVisible) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Slider(
                    modifier = Modifier
                        .weight(1f),

                    value = textState.currentSpanStyle.fontSize.value,
                    onValueChange = {
                        onFontSizeChange(it.roundToInt())
                    },
                    valueRange = Constants.MIN_FONT_VALUE..48f,
                    steps = 5
                )
                Text(
                    modifier = Modifier.weight(0.1f),
                    text = textState.currentSpanStyle.fontSize.value.roundToInt().toString(),
                    textAlign = TextAlign.End
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconToggleButton(
                colors = IconButtonDefaults.iconToggleButtonColors().copy(
                    checkedContentColor = MaterialTheme.colorScheme.onTertiary,
                    contentColor = MaterialTheme.colorScheme.tertiary
                ),
                checked = textState.currentSpanStyle.fontWeight == FontWeight.Bold,
                onCheckedChange = {
                    onBoldBtnClick()
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.FormatBold,
                    contentDescription = ""
                )
            }
            IconToggleButton(
                colors = IconButtonDefaults.iconToggleButtonColors().copy(
                    checkedContentColor = MaterialTheme.colorScheme.onTertiary,
                    contentColor = MaterialTheme.colorScheme.tertiary
                ),
                checked = textState.currentSpanStyle.fontStyle == FontStyle.Italic,
                onCheckedChange = {
                    onItalicBtnClick()
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.FormatItalic,
                    contentDescription = ""
                )
            }
            IconToggleButton(
                colors = IconButtonDefaults.iconToggleButtonColors().copy(
                    checkedContentColor = MaterialTheme.colorScheme.onTertiary,
                    contentColor = MaterialTheme.colorScheme.tertiary
                ),
                checked = isUnderlined,
                onCheckedChange = {
                    onUnderlinedBtnClick()
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.FormatUnderlined,
                    contentDescription = ""
                )
            }
            IconToggleButton(
                colors = IconButtonDefaults.iconToggleButtonColors().copy(
                    checkedContentColor = MaterialTheme.colorScheme.onTertiary,
                    contentColor = MaterialTheme.colorScheme.tertiary
                ),
                checked = isLineThrough,
                onCheckedChange = {
                    onLineThroughBtnClick()
                },
            ) {
                Icon(
                    imageVector = Icons.Filled.FormatStrikethrough,
                    contentDescription = ""
                )
            }
            Spacer(modifier = Modifier.weight(1f, fill = true))
            IconToggleButton(
                colors = IconButtonDefaults.iconToggleButtonColors().copy(
                    checkedContentColor = MaterialTheme.colorScheme.onTertiary,
                    contentColor = MaterialTheme.colorScheme.tertiary
                ),
                checked = isExtraOptionsVisible,
                onCheckedChange = {
                    onExtraOptionsBtnClick()
                },
            ) {
                Icon(
                    modifier = Modifier
                        .graphicsLayer {
                            rotationZ = arrowRotation
                        },
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = ""
                )
            }
        }
    }
}

@Preview
@Composable
private fun EditTextPanelPreview() {
    LocalHomeworkAndTaskManagerTheme {
        EditTextPanel(
            modifier = Modifier.fillMaxWidth(),
            textState = RichTextState(),
            isExtraOptionsVisible = true,
            onLineThroughBtnClick = {},
            onBoldBtnClick = {},
            onItalicBtnClick = {},
            onFontSizeChange = {},
            onExtraOptionsBtnClick = {},
            onUnderlinedBtnClick = {}
        )
    }
}