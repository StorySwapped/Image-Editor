package com.example.imageeditor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.imageeditor.ui.theme.ImageEditorTheme
import androidx.compose.ui.graphics.painter.Painter
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ImageEditorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black // Set the background color to black
                ) {
                    ImageEditorScreen()
                }
            }
        }
    }
}

@Composable
fun ImageEditorScreen() {
    val selectedOption by remember { mutableStateOf("") }
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            // Handle the returned Uri, e.g., show in the UI or store it
            // This could involve updating a ViewModel or UI state
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        HeaderSection(selectedOption)
        ImageUploadSection(selectedOption)
        EditingOptions(selectedOption)
    }
}

@Composable
fun HeaderSection(selectedOption: String) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(
            onClick = {  },
            modifier = Modifier
                .width(100.dp)
                .height(60.dp)
        ) {
            Text(
                text = "Save",
                style = TextStyle(fontSize = 20.sp), // Sets the font size to 24sp
                color = Color.White
            )
        }

        TextButton(
            onClick = {  },
            modifier = Modifier
                .width(100.dp)
                .height(60.dp)
        ) {
            Text(
                text = "Cancel",
                style = TextStyle(fontSize = 20.sp), // Sets the font size to 24sp
                color = Color.White
            )
        }
    }
}

@Composable
fun ImageUploadSection(selectedOption: String) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            // Assuming image data is returned as Uri in data
            imageUri = result.data?.data
        }
    }
    val imageModel = viewModel<ImageViewModel>()

    Box(
        modifier = Modifier
            .background(Color.Black)
            .size(400.dp),
        contentAlignment = Alignment.Center
    ) {
        // Display the selected image or show the button if no image is selected
        imageUri?.let { uri ->
            Image(

                painter = rememberAsyncImagePainter(uri, imageModel, context),
                contentDescription = "Displayed Image",
            )
        } ?: run {
            IconButton(
                onClick = {
                    // Create an intent to pick an image
                    val pickImageIntent = Intent(Intent.ACTION_PICK).apply {
                        type = "image/*"
                    }
                    imagePickerLauncher.launch(pickImageIntent)
                },
                modifier = Modifier.size(70.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_photo),
                    contentDescription = "Add Image",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun EditingOptions(selectedOption: String) {

    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),

        horizontalArrangement = Arrangement.Absolute.SpaceAround
    ) {

        IconButton(
            onClick = { selectedOption("Undo") },
            modifier = Modifier.size(30.dp)  // Set the size of the IconButton
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.undo),
                contentDescription = "Undo",
                tint = Color.White,
                modifier = Modifier.size(30.dp)  // Set the size of the Icon
            )
        }

        IconButton(
            onClick = { selectedOption("Redo") },
            modifier = Modifier.size(30.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.redo),
                contentDescription = "Redo",
                tint = Color.White,
                modifier = Modifier.size(30.dp)
            )
        }
    }

    // Editing Options Section
    Row(
        modifier = Modifier
            .padding(10.dp)
            .horizontalScroll(scrollState)
    ) {

        IconButton(
            onClick = { selectedOption("Basic Editing") },
            modifier = Modifier.size(100.dp)  // Set the size of the IconButton
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.basic_editing),
                contentDescription = "Basic Editing",
                tint = Color.White,
                modifier = Modifier.size(70.dp)  // Set the size of the Icon
            )
        }

        IconButton(
            onClick = { selectedOption("Advanced Editing") },
            modifier = Modifier.size(100.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.advanced_editing),
                contentDescription = "Advanced Editing",
                tint = Color.White,
                modifier = Modifier.size(70.dp)
            )
        }

        IconButton(
            onClick = { selectedOption("Cropping and Selection") },
            modifier = Modifier.size(100.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.crop),
                contentDescription = "Cropping and Selection",
                tint = Color.White,
                modifier = Modifier
                    .size(70.dp)
            )
        }

        IconButton(
            onClick = { selectedOption("Background Color Changer") },
            modifier = Modifier
                .size(100.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.background_color_changer),
                contentDescription = "Background Color Changer",
                tint = Color.White,
                modifier = Modifier.size(70.dp)
            )
        }

        IconButton(
            onClick = { selectedOption("Foreground Color Changer") },
            modifier = Modifier.size(100.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.foreground_color_changer),
                contentDescription = "Foreground Color Changer",
                tint = Color.White,
                modifier = Modifier.size(70.dp)
            )
        }

        IconButton(
            onClick = { selectedOption("Filters") },
            modifier = Modifier.size(100.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.filter),
                contentDescription = "Filters",
                tint = Color.White,
                modifier = Modifier.size(70.dp)
            )
        }
    }

    Row(
        modifier = Modifier
            .horizontalScroll(scrollState, true),
    ) {
        Text(
            text = "      Basic Editing      ",
            style = TextStyle(fontSize = 13.sp), // Sets the font size to 24sp
            color = Color.White
        )

        Text(
            text = " Advance Editing   ",
            style = TextStyle(fontSize = 13.sp),
            color = Color.White
        )

        Text(
            text = "   Crop & Select    ",
            style = TextStyle(fontSize = 13.sp),
            color = Color.White
        )

        Text(
            text = "    Background  ",
            style = TextStyle(fontSize = 13.sp),
            color = Color.White
        )

        Text(
            text = "   Foreground  ",
            style = TextStyle(fontSize = 13.sp),
            color = Color.White
        )

        Text(
            text = "    Filters   ",
            style = TextStyle(fontSize = 13.sp),
            color = Color.White
        )


    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .horizontalScroll(scrollState),
    ) {}

}

class ImageViewModel : ViewModel() {
    private var _imageBitmap = mutableStateOf<ImageBitmap?>(null)
    val imageBitmap: State<ImageBitmap?> = _imageBitmap

    fun loadImage(uri: Uri, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val bitmap = BitmapFactory.decodeStream(stream)
                withContext(Dispatchers.Main) {
                    _imageBitmap.value = bitmap?.asImageBitmap()
                }
            }
        }
    }
}


@Composable
fun rememberAsyncImagePainter(uri: Uri, viewModel: ImageViewModel, context: Context): Painter {
    viewModel.loadImage(uri, context)
    val imageBitmap by viewModel.imageBitmap
    return imageBitmap?.let { BitmapPainter(it) } ?: EmptyPainter
}

object EmptyPainter : Painter() {
    override val intrinsicSize: Size
        get() = Size.Unspecified

    override fun DrawScope.onDraw() {
        // Draw nothing if no image is loaded
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
