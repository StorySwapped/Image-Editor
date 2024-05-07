package com.example.imageeditor

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.imageeditor.ui.theme.ImageEditorTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.graphics.Color.parseColor
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign


class LandingScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ImageEditorTheme {
                Surface(
                    color = Color(parseColor("#653355")),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    ImageEditorScreen()
                }
            }
        }
    }
}
var button_color = "#351D4A"

@Composable
fun ImageEditorScreen() {
    val viewModel: ImageViewModel = viewModel()

    Box {
        Image(
            painter = painterResource(id = R.drawable.back17), // Replace R.drawable.background_image with your image resource
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxHeight(1f)
        ) {
            HeaderSection(viewModel)
            ImagePreviewSection(viewModel)
            EditingOptions(viewModel)
        }
    }
}

@Composable
fun HeaderSection(viewModel: ImageViewModel) {
    var save by remember { mutableStateOf(false) }
    var cancel by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box()
    {
        Row(
            modifier = Modifier
                .absolutePadding(bottom = 25.dp, left = 30.dp, right = 30.dp, top = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = { save = true },
                modifier = Modifier
                    .height(30.dp)
                    .width(80.dp)
                    .background(Color(parseColor(button_color)), shape = RoundedCornerShape(15.dp))


            ) {
                Text(
                    text = "Save",
                    style = TextStyle(fontSize = 13.sp),
                    color = Color(parseColor("#dddddd")),
                    fontFamily = FontFamily(Font(R.font.sansserif))

                )
            }

            TextButton(
                onClick = { cancel = true },
                modifier = Modifier
                    .width(80.dp)
                    .height(30.dp)
                    .background(Color(parseColor(button_color)), shape = RoundedCornerShape(15.dp))
            ) {
                Text(
                    text = "Cancel",
                    style = TextStyle(fontSize = 13.sp),
                    color = Color(parseColor("#dddddd")),
                    fontFamily = FontFamily(Font(R.font.sansserif))
                )
            }

            if (save) {
                ConfirmationDialog("Are you sure you are done editing?", onConfirm = {
                    viewModel.saveImageToGallery(context)
                    viewModel.clearCurrentImage()
                    save = false
                }, onDismiss = {
                    save = false
                })
            }

            if (cancel) {
                ConfirmationDialog("Are you sure you want to discard all changes?", onConfirm = {
                    viewModel.clearCurrentImage()
                    cancel = false
                }, onDismiss = {
                    cancel = false
                })
            }
        }
    }
}

@Composable
fun ImagePreviewSection(viewModel: ImageViewModel) {
    val context = LocalContext.current
    val imageBitmap by viewModel.imageBitmap

    Log.d("ImageEditor", "Composing ImagePreviewSection with URI: ${viewModel.imageUri}")

    LaunchedEffect(key1 = viewModel.imageUri) {
        viewModel.imageUri?.let {
            viewModel.loadImage(it, context)
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val newUri = result.data?.data
            newUri?.let {
                viewModel.imageUri = it
                viewModel.loadImage(newUri, context)
            }
        }
    }

    Box(
        modifier = Modifier
            .absolutePadding(top = 20.dp, bottom = 20.dp)
            .size(580.dp),
        contentAlignment = Alignment.Center
    ){
        imageBitmap?.let {
            Image(
                painter = BitmapPainter(it),
                contentDescription = "Displayed Image",
                modifier = Modifier
                    .fillMaxSize()
            )
        } ?: IconButton(
            onClick = {
                val pickImageIntent = Intent(Intent.ACTION_PICK).apply {
                    type = "image/*"
                }
                imagePickerLauncher.launch(pickImageIntent)
            },
            modifier = Modifier.size(60.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add_photo),
                contentDescription = "Add Image",
                tint = Color(parseColor("#BBBBBB")),
                modifier = Modifier.size(50.dp)
            )
        }

    }
}

object Icons {
    val basicEditing = R.drawable.basic_editing
    val cropping = R.drawable.crop
    val advancedEditing = R.drawable.advanced_editing
    val backgroundColorChanger = R.drawable.background_color_changer
    val filter = R.drawable.filter
    val foregroundColorChanger = R.drawable.foreground_color_changer
}

object Activities {
    val basicEditingClass = BasicEditing::class.java
    val selectionAndCroppingClass = SelectionaAndcropping::class.java
    val advancedEditingClass = AdvanceEditing::class.java
    val changeBackgroundClass = ChangeBackground::class.java
    val filterManagementClass = FilterManagement::class.java
    val changeForegroundClass = ChangeForeground::class.java
}


@Composable
fun EditingOptions(viewModel: ImageViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getStringExtra("imageUri")?.let {
                Log.d("LandingScreen", "Updating image with URI: $it")
                val newUri = Uri.parse(it)
                viewModel.imageUri = newUri
                viewModel.loadImage(newUri, context)
            }
        } else {
            Log.d("LandingScreen", "Result not OK or no data received")
        }
    }

    var error by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .fillMaxWidth()
            .absolutePadding(top = 5.dp, bottom = 2.dp, left = 2.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        if (editingIconButton(
                context,
                viewModel,
                launcher,
                Icons.basicEditing,
                "BASIC EDITING",
                Activities.basicEditingClass
        )) { error = true }

        if (editingIconButton(
                context,
                viewModel,
                launcher,
                Icons.filter,
                "FILTERS",
                Activities.filterManagementClass
            )) { error = true }

        if (editingIconButton(
                context,
                viewModel,
                launcher,
                Icons.advancedEditing,
                "ADVANCED EDITING",
                Activities.advancedEditingClass
            )) { error = true }

        if (editingIconButton(
                context,
                viewModel,
                launcher,
                Icons.foregroundColorChanger,
                "FOREGROUND",
                Activities.changeForegroundClass
            )) { error = true }

        if (editingIconButton(
                context,
                viewModel,
                launcher,
                Icons.cropping,
                "CROP & SELECT",
                Activities.selectionAndCroppingClass
            )) { error = true }

        if (editingIconButton(
                context,
                viewModel,
                launcher,
                Icons.backgroundColorChanger,
                "BACKGROUND",
                Activities.changeBackgroundClass
        )) { error = true }

    }

    if (error)
    {
        if (errorMessage ()){
            error = false
        }
    }
}

@Composable
fun editingIconButton(
    context: Context,
    viewModel: ImageViewModel,
    launcher: ActivityResultLauncher<Intent>,
    iconId: Int,
    content: String,
    activityClass: Class<*>,
): Boolean {
    var value by remember {mutableStateOf(false)}
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(65.dp)
            .height(95.dp)
            .background(Color.Black)
    )
    {
        IconButton(
            onClick = {
                if (viewModel.imageUri == null) {
                    value = true
                } else {
                    val intent = Intent(context, activityClass).apply {
                        putExtra("imageUri", viewModel.imageUri.toString())
                    }
                    launcher.launch(intent)
                }
            },
            modifier = Modifier
                .size(65.dp)
                .padding(5.dp)
                .background(Color(parseColor(button_color)), shape = RoundedCornerShape(15.dp))
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(iconId),
                contentDescription = content,
                tint = Color(parseColor("#dddddd")),
                modifier = Modifier
                    .size(40.dp)
            )
        }
        Text(
            text = content,
            style = TextStyle(fontSize = 9.sp),
            textAlign = TextAlign.Center,
            color = Color(parseColor("#dddddd")),
            fontFamily = FontFamily(Font(R.font.sansserif)),
            modifier = Modifier.absolutePadding(top = 5.dp)
        )
    }
    return value
}



@Composable
fun errorMessage(): Boolean {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Error") },
            text = { Text(text = "Image Not Uploaded") },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text(text = "OK")
                }
            }
        )
    }
    return !showDialog
}
@Composable
fun ConfirmationDialog(message: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmation") },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onConfirm) { Text("Yes") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("No") }
        }
    )
}

class ImageViewModel : ViewModel() {
    var imageUri by mutableStateOf<Uri?>(null)
    var imageBitmap = mutableStateOf<ImageBitmap?>(null)

    // Load image and manage state history correctly
    fun loadImage(uri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)?.let { bitmap ->
                    withContext(Dispatchers.Main) {
                        imageBitmap.value = bitmap.asImageBitmap()
                        imageUri = uri
                    }
                }
            }
        }
    }

    fun clearCurrentImage() {
        imageUri = null
        imageBitmap.value = null
    }

    fun saveImageToGallery(context: Context) {
        imageUri?.let { uri ->
            val contentResolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "Image_${System.currentTimeMillis()}.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }

            val url = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            url?.let {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    contentResolver.openInputStream(uri)?.copyTo(outputStream)
                }
            }
        }
    }

}