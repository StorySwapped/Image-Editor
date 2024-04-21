package com.example.imageeditor

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.WhitePoint
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.imageeditor.ui.theme.ImageEditorTheme

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
    var selectedOption by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Header Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = { selectedOption = "Save" },
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
                onClick = { selectedOption = "Cancel" },
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

        // Image Display Section
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            IconButton(
                onClick = {
                    ImageView()

                },
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.Center) // Centering the button in the Box
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_photo), // Ensure this is the correct ID for your drawable resource
                    contentDescription = "Add Image",
                    tint = Color.White,
                    modifier = Modifier.size(75.dp)
                )
            }
        }

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
                .horizontalScroll(scrollState, true)
                ,
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
}

fun ImageView() {
    TODO("Not yet implemented")
}

@Composable
fun ImageUploadSection() {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the image Uri here
            val imageUri = result.data?.data
            // Use this Uri to display the image or store it
        }
    }

    Box(
        modifier = Modifier
            .padding(24.dp)
            .background(Color.Black)
    ) {
        // IconButton for adding an image
        IconButton(
            onClick = {
                // Create an intent to pick an image
                val pickImageIntent = Intent(Intent.ACTION_PICK).apply {
                    type = "image/*"
                }
                imagePickerLauncher.launch(pickImageIntent)
            },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add_photo),
                contentDescription = "Add Image"
            )
        }
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
