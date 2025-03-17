package ru.pakarpichev.homeworktool.core.data.util

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun pickVisualMedia(modifier: Modifier = Modifier): Uri? {
    var pickedUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            pickedUri = uri
        }
    )
    launcher.launch(
        PickVisualMediaRequest(
            mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly
        )
    )
    return pickedUri
}