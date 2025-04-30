package ru.pkstudio.localhomeworkandtaskmanager.main.presentation.addHomework

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.pkstudio.localhomeworkandtaskmanager.R
import ru.pkstudio.localhomeworkandtaskmanager.ui.theme.LocalHomeworkAndTaskManagerTheme

@Composable
fun ImageCard(
    modifier: Modifier = Modifier,
    bitmap: Bitmap,
    onDeleteClick: () -> Unit
) {

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = Color.Transparent
    ) {
       Box(
           modifier = Modifier
               .fillMaxSize()
               .background(MaterialTheme.colorScheme.surface),
           contentAlignment = Alignment.TopEnd
       ) {
            Box(
                modifier = Modifier
                    .padding(8.dp),
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                )
            }
           IconButton(
               modifier = Modifier
                   .padding(4.dp)
                   .size(24.dp),
               colors = IconButtonDefaults.iconButtonColors().copy(
                   containerColor = MaterialTheme.colorScheme.onSecondaryContainer
               ),
               onClick = {
                    onDeleteClick()
               }
           ) {
               Icon(
                   imageVector = Icons.Default.Close,
                   contentDescription = "remove"
               )
           }
        }
    }
}

@Preview
@Composable
private fun ImageCardPreview() {
    LocalHomeworkAndTaskManagerTheme {
        ImageCard(
            modifier = Modifier.size(100.dp),
            bitmap = BitmapFactory.decodeResource(
                Resources.getSystem(),
                R.drawable.logo4
            ),
            onDeleteClick = {}
        )
    }
}