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
import android.graphics.Color.parseColor
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.core.graphics.translationMatrix
import coil.compose.rememberImagePainter


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
    val context = LocalContext.current
    val viewModel: ImageViewModel = viewModel()

    Box() {
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
    val imageBitmap = viewModel.imageBitmap.value

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

    LaunchedEffect(key1 = viewModel.imageUri) {
        viewModel.imageUri?.let {
            Log.d("ImageEditor", "Loading new image for URI: $it")
            viewModel.loadImage(it, context)
        }
    }
    Box() {
        Box(
            modifier = Modifier
                .size(570.dp),
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
                    tint = Color(parseColor("#BBBBBB")),
                    modifier = Modifier.size(50.dp)
                )
            }
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
            .absolutePadding(top = 15.dp, left = 25.dp, right = 300.dp, bottom = 5.dp),

        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        IconButton(
            onClick = { viewModel.undo(context) },
            modifier = Modifier
                .size(30.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.undo),
                contentDescription = "Undo",
                tint = Color(parseColor("#dddddd")),
                modifier = Modifier.size(25.dp)  // Set the size of the Icon
            )
        }

        IconButton(
            onClick = { viewModel.redo(context) },
            modifier = Modifier
                .size(30.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.redo),
                contentDescription = "Redo",
                tint = Color(parseColor("#dddddd")),
                modifier = Modifier.size(25.dp)
            )
        }
    }

    // Editing Options Section
    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .fillMaxWidth()
            .absolutePadding(top = 5.dp, bottom = 2.dp, left = 2.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
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
                .size(65.dp)
                .padding(5.dp)
                .background(Color(parseColor(button_color)), shape = RoundedCornerShape(15.dp))
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.basic_editing),
                contentDescription = "Basic Editing",
                tint = Color(parseColor("#dddddd")),
                modifier = Modifier
                    .size(40.dp)  // Set the size of the Icon
            )
        }

        IconButton(
            onClick = {
                if (viewModel.imageUri == null) {
                error = true
            } else {
                val intent = Intent(context, SelectionaAndcropping::class.java).apply {
                    putExtra("imageUri", viewModel.imageUri.toString())
                }

                Launcher.launch(intent)
            }
                      },
            modifier = Modifier
                .size(65.dp)
                .padding(5.dp)
                .background(Color(parseColor(button_color)), shape = RoundedCornerShape(15.dp))
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.crop),
                contentDescription = "Cropping and Selection",
                tint = Color(parseColor("#dddddd")),
                modifier = Modifier
                    .size(40.dp)
            )
        }

        IconButton(
            onClick = {
                if (viewModel.imageUri == null){
                    error = true
                }
                else {
                    val intent = Intent(context, AdvanceEditing::class.java).apply {
                        putExtra("imageUri", viewModel.imageUri.toString())
                    }
                    Launcher.launch(intent)
                }
            },
            modifier = Modifier
                .size(65.dp)
                .padding(5.dp)
                .background(Color(parseColor(button_color)), shape = RoundedCornerShape(15.dp))
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.advanced_editing),
                contentDescription = "Advanced Editing",
                tint = Color(parseColor("#dddddd")),
                modifier = Modifier.size(45.dp)
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
            modifier = Modifier
                .size(65.dp)
                .padding(5.dp)
                .background(Color(parseColor(button_color)), shape = RoundedCornerShape(15.dp))
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.background_color_changer),
                contentDescription = "Background Color Changer",
                tint = Color(parseColor("#dddddd")),
                modifier = Modifier.size(40.dp)
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
            modifier = Modifier
                .size(65.dp)
                .padding(5.dp)
                .background(Color(parseColor(button_color)), shape = RoundedCornerShape(15.dp))
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.filter),
                contentDescription = "Filters",
                tint = Color(parseColor("#dddddd")),
                modifier = Modifier.size(40.dp)
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
                .size(65.dp)
                .padding(5.dp)
                .background(Color(parseColor(button_color)), shape = RoundedCornerShape(15.dp))
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.foreground_color_changer),
                contentDescription = "Foreground Color Changer",
                tint = Color(parseColor("#dddddd")),
                modifier = Modifier.size(40.dp)
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
            .fillMaxWidth()
            .absolutePadding(top = 5.dp, left = 2.dp, right = 2.dp),
    ) {
        Text(
            text = "    BASIC EDITING   ",
            style = TextStyle(fontSize = 9.sp), // Sets the font size to 24sp
            color = Color(parseColor("#dddddd")),
            fontFamily = FontFamily(Font(R.font.sansserif))
        )

        Text(
            text = "     CROP & SELECT   ",
            style = TextStyle(fontSize = 9.sp),
            color = Color(parseColor("#dddddd")),
            fontFamily = FontFamily(Font(R.font.sansserif))
        )

        Text(
            text = "  ADVANCE EDITING    ",
            style = TextStyle(fontSize = 9.sp),
            color = Color(parseColor("#dddddd")),
            fontFamily = FontFamily(Font(R.font.sansserif))
        )

        Text(
            text = "   BACKGROUND   ",
            style = TextStyle(fontSize = 9.sp),
            color = Color(parseColor("#dddddd")),
            fontFamily = FontFamily(Font(R.font.sansserif))
        )

        Text(
            text = "      APPLY FILTERS    ",
            style = TextStyle(fontSize = 9.sp),
            color = Color(parseColor("#dddddd")),
            fontFamily = FontFamily(Font(R.font.sansserif))
        )

        Text(
            text = "     FOREGROUND   ",
            style = TextStyle(fontSize = 9.sp),
            color = Color(parseColor("#dddddd")),
            fontFamily = FontFamily(Font(R.font.sansserif))
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

    private val history = mutableListOf<Uri>()
    private val future = mutableListOf<Uri>()

    // Load image and manage state history correctly
    fun loadImage(uri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)?.let { bitmap ->
                    withContext(Dispatchers.Main) {
                        _imageBitmap.value = bitmap.asImageBitmap()
                        setCurrentImage(uri)
                    }
                }
            }
        }
    }

    // Implement undo logic
    fun undo(context: Context) {
        if (history.isNotEmpty()) {
            future.add(0, imageUri!!)  // Push current to future
            imageUri = history.removeLastOrNull()
            imageUri?.let { loadImage(it, context) }
        }
    }

    fun redo(context: Context) {
        if (future.isNotEmpty()) {
            history.add(imageUri!!)  // Add current to history
            imageUri = future.removeFirstOrNull()
            imageUri?.let { loadImage(it, context) }
        }
    }

    private fun setCurrentImage(uri: Uri) {
        if (imageUri != null && imageUri != uri) {
            history.add(imageUri!!)
        }
        imageUri = uri
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
        history.clear()
        future.clear()
    }
}

@Preview(showBackground = true)
@Composable
fun ImageEditorPreview() {
    ImageEditorTheme {
        ImageEditorScreen()
    }
}
