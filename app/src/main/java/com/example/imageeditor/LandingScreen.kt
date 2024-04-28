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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.State
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.imageeditor.ui.theme.ImageEditorTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LandingScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ImageEditorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black

                ) {
                    ImageEditorScreen()
                }
            }
        }
    }
}


@Composable
fun ImageEditorScreen() {
    val viewModel: ImageViewModel = viewModel()
    Column(modifier = Modifier
        .fillMaxHeight(1f)
    ) {
        HeaderSection(viewModel)
        ImagePreviewSection(viewModel)
        EditingOptions(viewModel)
    }
}

@Composable
fun HeaderSection(viewModel: ImageViewModel) {
    var save by remember { mutableStateOf(false) }
    var cancel by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .absolutePadding(bottom = 30.dp, left = 30.dp, right = 30.dp, top = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(
            onClick = { save = true },
            modifier = Modifier
                .height(32.dp)
                .width(80.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(30.dp))
                .border(BorderStroke(2.dp, Color.Gray), shape = RoundedCornerShape(30.dp)),


            ) {
            Text(
                text = "Save",
                style = TextStyle(fontSize = 14.sp),
                color = Color.Black
            )
        }

        TextButton(
            onClick = { cancel = true },
            modifier = Modifier
                .width(80.dp)
                .height(32.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(50.dp))
                .border(BorderStroke(2.dp, Color.Gray), shape = RoundedCornerShape(50.dp)),
        ) {
            Text(
                text = "Cancel",
                style = TextStyle(fontSize = 14.sp),
                color = Color.Black
            )
        }

        if (save){
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

@Composable
fun ImagePreviewSection(viewModel: ImageViewModel) {
    val context = LocalContext.current
    val imageBitmap = viewModel.imageBitmap.value

    Log.d("ImageEditor", "Composing ImagePreviewSection with URI: ${viewModel.imageUri}")

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

    LaunchedEffect(key1 = viewModel.imageUri) {
        viewModel.imageUri?.let {
            Log.d("ImageEditor", "Loading new image for URI: $it")
            viewModel.loadImage(it, context)
        }
    }

    Box(
        modifier = Modifier
            .background(Color.Black)
            .size(530.dp),
        contentAlignment = Alignment.Center
    ) {
        imageBitmap?.let {
            Image(
                painter = BitmapPainter(it),
                contentDescription = "Displayed Image",
                modifier = Modifier.fillMaxSize()
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
                tint = Color.Gray,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}

@Composable
fun EditingOptions(viewModel: ImageViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val Launcher = rememberLauncherForActivityResult(
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
            .fillMaxWidth()
            .absolutePadding(top = 30.dp, left = 30.dp, right = 250.dp),

        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        IconButton(
            onClick = { selectedOption("Undo") },
            modifier = Modifier
                .size(30.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.undo),
                contentDescription = "Undo",
                tint = Color.Gray,
                modifier = Modifier.size(30.dp)  // Set the size of the Icon
            )
        }

        IconButton(
            onClick = { selectedOption("Redo") },
            modifier = Modifier
                .size(30.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.redo),
                contentDescription = "Redo",
                tint = Color.Gray,
                modifier = Modifier.size(30.dp)
            )
        }
    }

    // Editing Options Section
    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .fillMaxWidth()
    ) {

        IconButton(
            onClick = {
                if (viewModel.imageUri == null) {
                    error = true
                } else {
                    val intent = Intent(context, BasicEditing::class.java).apply {
                        putExtra("imageUri", viewModel.imageUri.toString())
                    }

                    Launcher.launch(intent)
                }
            },
            modifier = Modifier
                .size(100.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.basic_editing),
                contentDescription = "Basic Editing",
                tint = Color.Gray,
                modifier = Modifier
                    .size(60.dp)  // Set the size of the Icon
            )
        }

        IconButton(
            onClick = { selectedOption("Cropping and Selection") },
            modifier = Modifier
                .size(100.dp)

        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.crop),
                contentDescription = "Cropping and Selection",
                tint = Color.Gray,
                modifier = Modifier
                    .size(50.dp)
            )
        }

        IconButton(
            onClick = { selectedOption("Advanced Editing") },
            modifier = Modifier
                .size(100.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.advanced_editing),
                contentDescription = "Advanced Editing",
                tint = Color.Gray,
                modifier = Modifier.size(55.dp)
            )
        }

        IconButton(
            onClick = {
                if (viewModel.imageUri == null){
                    error = true
                }
                else {
                    val intent = Intent(context, ChangeBackground::class.java).apply {
                        putExtra("imageUri", viewModel.imageUri.toString())
                    }
                    Launcher.launch(intent)
                }
            },
            modifier = Modifier.size(100.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.background_color_changer),
                contentDescription = "Background Color Changer",
                tint = Color.Gray,
                modifier = Modifier.size(50.dp)
            )
        }


        IconButton(
            onClick = {
                if (viewModel.imageUri == null) {
                    error = true
                } else {
                    val intent = Intent(context, FilterManagement::class.java).apply {
                        putExtra("imageUri", viewModel.imageUri.toString())
                    }
                    Launcher.launch(intent)
                }
            },
            modifier = Modifier.size(100.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.filter),
                contentDescription = "Filters",
                tint = Color.Gray,
                modifier = Modifier.size(50.dp)
            )
        }

        IconButton(
            onClick = {
                if (viewModel.imageUri == null) {
                    error = true
                } else {
                    val intent = Intent(context, ChangeForeground::class.java).apply {
                        putExtra("imageUri", viewModel.imageUri.toString())
                    }
                    Launcher.launch(intent)
                }
            },
            modifier = Modifier
                .size(100.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.foreground_color_changer),
                contentDescription = "Foreground Color Changer",
                tint = Color.Gray,
                modifier = Modifier.size(50.dp)
            )
        }


    }

    if (error)
    {
        if (ErrorMessage ()){
            error = false
        }
    }

    Row(
        modifier = Modifier
            .horizontalScroll(scrollState, true)
            .fillMaxWidth(),
    ) {
        Text(
            text = "       Basic Editing       ",
            style = TextStyle(fontSize = 11.sp), // Sets the font size to 24sp
            color = Color.LightGray
        )

        Text(
            text = "        Crop & Select     ",
            style = TextStyle(fontSize = 11.sp),
            color = Color.LightGray
        )

        Text(
            text = "      Advance Editing    ",
            style = TextStyle(fontSize = 11.sp),
            color = Color.LightGray
        )

        Text(
            text = "    Background Color    ",
            style = TextStyle(fontSize = 11.sp),
            color = Color.LightGray
        )

        Text(
            text = "       Apply Filters     ",
            style = TextStyle(fontSize = 11.sp),
            color = Color.LightGray
        )

        Text(
            text = "      Foreground Color   ",
            style = TextStyle(fontSize = 11.sp),
            color = Color.LightGray
        )
    }
}


@Composable
fun ErrorMessage(): Boolean {
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
    private var _imageBitmap = mutableStateOf<ImageBitmap?>(null)
    val imageBitmap: State<ImageBitmap?> = _imageBitmap

    fun loadImage(uri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = inputStream?.use { BitmapFactory.decodeStream(it) }
            withContext(Dispatchers.Main) {
                if (bitmap != null) {
                    imageUri = uri
                    _imageBitmap.value = bitmap.asImageBitmap()
                }
            }
        }
    }


    fun updateImage(resultUri: String?, context: Context) {
        if (resultUri != null) {
            val uri = Uri.parse(resultUri)
            Log.d("ImageEditor", "Updating image URI to: $uri")
            imageUri = uri
            clearCurrentImage()
            loadImage(uri, context)
        }
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

    fun clearCurrentImage() {
        imageUri = null
        _imageBitmap.value = null
    }
}

fun selectedOption(s: String) {

}



@Preview(showBackground = true)
@Composable
fun ImageEditorPreview() {
    ImageEditorTheme {
        ImageEditorScreen()
    }
}
