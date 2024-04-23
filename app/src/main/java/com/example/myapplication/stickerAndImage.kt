package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import android.app.Activity
import androidx.activity.compose.setContent
import android.content.Context
import android.net.Uri
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.io.FileOutputStream
import android.graphics.Canvas
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import androidx.core.content.ContextCompat.startActivity

class stickerAndImage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val croppedImageUriString = intent.getStringExtra("crop_image_uri")
                    val selectedImageUriString = intent.getStringExtra("selected_image_uri")

                    val croppedImageUri = Uri.parse(croppedImageUriString)
                    val selectedImageUri = Uri.parse(selectedImageUriString)

                    MyApp(backgroundImageUri = selectedImageUri, stickerUri = croppedImageUri)


                }
            }
        }
    }


    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        MyApplicationTheme {
            Greeting("Android")
        }
    }

    @Composable
    fun MyApp(backgroundImageUri: Uri, stickerUri: Uri) {
        StickerOverImage(backgroundImageUri = backgroundImageUri, stickerUri = stickerUri)
    }


    @Composable
    fun StickerOverImage(
        backgroundImageUri: Uri,
        stickerUri: Uri
    ) {
        var stickerOffset by remember { mutableStateOf(Pair(0f, 0f)) }
        var stickerSize by remember { mutableStateOf(100.dp) }

        val context = LocalContext.current

        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(8f)
            ) {
                ImageWithSticker(
                    backgroundImageRes = backgroundImageUri,
                    stickerRes = stickerUri,
                    stickerOffset = stickerOffset,
                    stickerSize = stickerSize,
                    onStickerOffsetChange = { newOffset ->
                        stickerOffset = newOffset
                    },
                    onStickerSizeChange = { newSize ->
                        stickerSize = newSize
                    }
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Blue)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(modifier = Modifier
                        .background(Color.Transparent)
                        .padding(16.dp)
                        .weight(1f),
                        colors = ButtonDefaults.buttonColors(Color.Yellow),
                        onClick = {
                            //saveImageWithSticker(context, backgroundImageUri, stickerUri, stickerOffset, stickerSize)


                        }) {
                        Text(text = "Save Changes")
                    }



                    Button(modifier = Modifier
                        .background(Color.Transparent)
                        .padding(16.dp)
                        .weight(1f),
                        colors = ButtonDefaults.buttonColors(Color.Yellow),
                        onClick = {

                            finish()

                        }) {

                        Text(
                            text = "Cancel Go Back",
                        )
                    }

                }

            }
        }


    }

    @Composable
    fun ImageWithSticker(
        backgroundImageRes: Uri,
        stickerRes: Uri,
        stickerOffset: Pair<Float, Float>,
        stickerSize: Dp,
        onStickerOffsetChange: (Pair<Float, Float>) -> Unit,
        onStickerSizeChange: (Dp) -> Unit
    ) {
        val stickerPainter: Painter = rememberAsyncImagePainter(stickerRes)
        val backgroundPainter = rememberAsyncImagePainter(backgroundImageRes)

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = backgroundPainter,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
            Sticker(
                stickerPainter = stickerPainter,
                initialstickerOffset = stickerOffset,
                stickerSize = stickerSize,
                onStickerOffsetChange = onStickerOffsetChange,
                onStickerSizeChange = onStickerSizeChange
            )
        }
    }

    @Composable
    fun Sticker(
        stickerPainter: Painter,
        initialstickerOffset: Pair<Float, Float>,
        stickerSize: Dp,
        onStickerOffsetChange: (Pair<Float, Float>) -> Unit,
        onStickerSizeChange: (Dp) -> Unit
    ) {
        var stickerOffset by remember { mutableStateOf(initialstickerOffset) }
        var stickerSize by remember { mutableStateOf(stickerSize) }

        Box(
            modifier = Modifier
                .offset(
                    x = stickerOffset.first.dp,
                    y = stickerOffset.second.dp
                )
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        val dragFactor =
                            0.2f //Adjust the drag sensitivity here (lower value = less sensitivity)
                        val newOffset = Pair(
                            stickerOffset.first + pan.x * dragFactor,
                            stickerOffset.second + pan.y * dragFactor
                        )
                        val newSize = (stickerSize * zoom).coerceIn(50.dp, 900.dp)

                        onStickerOffsetChange(newOffset)
                        stickerOffset = newOffset
                        onStickerSizeChange(newSize)
                        stickerSize = newSize
                    }
                }
        ) {
            Image(
                painter = stickerPainter,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(stickerSize)
            )
        }
    }
}