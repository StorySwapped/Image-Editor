package com.example.imageeditor

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
import com.example.imageeditor.ui.theme.ImageEditorTheme
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.core.content.ContextCompat.startActivity
import kotlin.math.roundToInt

class SC_stickerAndImage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageEditorTheme {
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

        val modifiedBitmap = remember { mutableStateOf<Bitmap?>(null) }

        val context = LocalContext.current
        Image(
            painter = painterResource(id = com.example.imageeditor.R.drawable.back17), // Replace R.drawable.background_image with your image resource
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )

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
                    .background(Color.Black)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(modifier = Modifier
                        .padding(16.dp)
                        .weight(1f),
                        onClick = {
                            modifiedBitmap.value = saveImageWithSticker(
                            context = context,
                            backgroundImageUri = backgroundImageUri,
                            stickerUri = stickerUri,
                            stickerOffset = stickerOffset,
                            stickerSize = stickerSize
                        )




                        },) {
                        Text(text = "Save Changes")
                        // Display the modified image

                    }



                    Button(modifier = Modifier
                        .padding(16.dp)
                        .weight(1f),
                        onClick = {

                            finish()

                        }) {

                        Text(
                            text = "Cancel Go Back",
                        )
                    }




                }



            }


            // Display the modified image
            modifiedBitmap.value?.let { bitmap ->
                ModifiedImage(bitmap)
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

    @Composable
    fun ModifiedImage(modifiedBitmap: Bitmap?) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (modifiedBitmap != null) {
                Image(
                    bitmap = modifiedBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Handle the case where modifiedBitmap is null
                // For example, you can display a placeholder image or some text
                Text(
                    text = "Image not available",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }


    fun saveImageWithSticker(
        context: Context,
        backgroundImageUri: Uri,
        stickerUri: Uri,
        stickerOffset: Pair<Float, Float>,
        stickerSize: Dp
    ): Bitmap? {
        return try {
            // Load background image bitmap from URI
            val backgroundImageBitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, backgroundImageUri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, backgroundImageUri)
                ImageDecoder.decodeBitmap(source)
            }

            // Load sticker bitmap from URI
            val stickerBitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, stickerUri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, stickerUri)
                ImageDecoder.decodeBitmap(source)
            }

            // Scale sticker bitmap
            val density = context.resources.displayMetrics.density
            val scaledStickerBitmap = Bitmap.createScaledBitmap(
                stickerBitmap,
                (stickerSize * density).toString().toInt(),
                (stickerSize * density).toString().toInt(),
                true
            )

            // Create a mutable copy of the background image bitmap
            val mutableBitmap = backgroundImageBitmap.copy(Bitmap.Config.ARGB_8888, true)

            // Draw sticker onto background image
            val canvas = Canvas(mutableBitmap)
            canvas.drawBitmap(scaledStickerBitmap, stickerOffset.first, stickerOffset.second, null)

            // Convert modified bitmap to ImageBitmap


            //val imageBitmap = mutableBitmap.asImageBitmap()

            // Return the resulting ImageBitmap
            mutableBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }
}