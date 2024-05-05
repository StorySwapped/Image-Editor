package com.example.imageeditor

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.imageeditor.ui.theme.ImageEditorTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.cos
import kotlin.math.sin


class BasicEditing : ComponentActivity() {
    private var initial by mutableStateOf<Bitmap?>(null)
    private var displayed by mutableStateOf<ImageBitmap?>(null)
    private var original by mutableStateOf<ImageBitmap?>(null)

    private var tickConfirmation by mutableStateOf(false)
    private var crossConfirmation by mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageUriString = intent.getStringExtra("imageUri")
        val imageUri = Uri.parse(imageUriString)

        val inputStream = contentResolver.openInputStream(imageUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        original = bitmap.asImageBitmap()
        displayed = bitmap.asImageBitmap()

        setContent {

            ImageEditorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black

                ) {
                    Layout()
                }
            }
            getImage(imageUri)
        }

    }

    private fun getImage(imageUri: Uri) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val inputStream = contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                withContext(Dispatchers.Main) {
                    original = bitmap.asImageBitmap()
                    displayed = bitmap.asImageBitmap()
                    initial = bitmap
                    println("Calling detectBackground()")
                }

                inputStream?.close()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    @Composable
    fun Layout() {
        Column(modifier = Modifier
            .fillMaxWidth()
        ) {
            Title()
            ImagePreview()
            EditingOptions()
            TickCross()
        }
    }
    @Composable
    fun Title()
    {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 8.dp)
                .background(Color.Black),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Basic Editing",
                color = Color(android.graphics.Color.parseColor("#F9C706")),
                textAlign = TextAlign.Center,
                fontSize = 25.sp,
                fontFamily = FontFamily(Font(R.font.sansserif))
            )
        }
    }

    @Composable
    fun ImagePreview(){
        Box(
            modifier = Modifier
                .size(520.dp)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            displayed?.let {
                Image(
                    bitmap = it,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }

    @Composable
    fun EditingOptions() {
        var brightness by remember { mutableStateOf(1f) }
        var contrast by remember { mutableStateOf(1f) }
        var hue by remember { mutableStateOf(0f) }
        var saturation by remember { mutableStateOf(1f) }
        var sharpness by remember { mutableStateOf(0f) }
        var shadows by remember { mutableStateOf(0f) }

        val originalBitmap = original ?: return

        LaunchedEffect(brightness, contrast, hue, saturation, sharpness, shadows) {
            displayed = applyEdits(
                originalBitmap.asAndroidBitmap(),
                brightness,
                contrast,
                hue,
                saturation,
                sharpness,
                shadows
            ).asImageBitmap()
        }

        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .absolutePadding(top = 15.dp, left = 16.dp, right = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.Absolute.Left

            ) {
                //Image(
                //    painter = painterResource(R.drawable.brightness),
                //    contentDescription = null,
                //    modifier = Modifier
                //        .size(70.dp),
                //)
                Text(
                    text = "Brightness:  ",
                    style = TextStyle(fontSize = 18.sp),
                    color = Color(android.graphics.Color.parseColor("#F9C706")),
                )
                EditingFeatureSlider(
                    value = brightness,
                    onValueChange = { brightness = it }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),

                ) {
                Text(
                    text = "Contrast:     ",
                    style = TextStyle(fontSize = 18.sp),
                    color = Color(android.graphics.Color.parseColor("#F9C706")),
                )
                EditingFeatureSlider(
                    value = contrast,
                    onValueChange = { contrast = it }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),

                ) {
                Text(
                    text = "Hue:              ",
                    style = TextStyle(fontSize = 18.sp),
                    color = Color(android.graphics.Color.parseColor("#F9C706")),
                )
                EditingFeatureSlider(
                    value = hue,
                    onValueChange = { hue = it }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),

                ) {
                Text(
                    text = "Saturation:  ",
                    style = TextStyle(fontSize = 18.sp),
                    color = Color(android.graphics.Color.parseColor("#F9C706")),
                )
                EditingFeatureSlider(
                    value = saturation,
                    onValueChange = { saturation = it }
                )
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),

                ) {
                Text(
                    text = "Sharpness:   ",
                    style = TextStyle(fontSize = 18.sp),
                    color = Color(android.graphics.Color.parseColor("#F9C706")),
                )
                EditingFeatureSlider(
                    value = sharpness,
                    onValueChange = { sharpness = it }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),

                ) {
                Text(
                    text = "Shadows:     ",
                    style = TextStyle(fontSize = 18.sp),
                    color = Color(android.graphics.Color.parseColor("#F9C706")),
                )
                EditingFeatureSlider(
                    value = shadows,
                    onValueChange = { shadows = it }
                )
            }
        }
    }



    @Composable
    fun TickCross(){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .clickable {
                        crossConfirmation = true
                    },
                contentAlignment = Alignment.Center
            ) {
                CrossIcon()

            }

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .clickable {
                        tickConfirmation = true
                    },
                contentAlignment = Alignment.Center
            ) {
                TickIcon()
            }
        }

        if(tickConfirmation){
            showBoxTick()
        }

        if(crossConfirmation){
            showBoxCross()
        }
    }

    @Composable
    fun showBoxTick() {
        AlertDialog(
            onDismissRequest = { tickConfirmation = false },
            title = {
                Text(text = "Confirmation")
            },
            text = {
                Text(text = "Are you sure you want to save changes?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        sendtoMain(displayed?.asAndroidBitmap())
                        tickConfirmation = false
                    },colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    )
                ) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = { tickConfirmation = false },colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    )
                ) {
                    Text(text = "No")
                }
            }
        )
    }

    @Composable
    fun showBoxCross() {
        AlertDialog(
            onDismissRequest = { crossConfirmation= false },
            title = {
                Text(text = "Confirmation")
            },
            text = {
                Text(text = "Are you sure you want to discard changes?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        sendtoMain(initial)
                        crossConfirmation = false
                    },colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    )

                ) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = { crossConfirmation = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    )
                ) {
                    Text( text = "No")
                }
            }
        )
    }


    @Preview(showBackground = true)
    @Composable
    fun ImageEditorPreview() {
        ImageEditorTheme {
            Layout()
        }
    }

    private fun sendtoMain(bitmap: Bitmap?) {
        bitmap?.let {
            val file = File(cacheDir, "image_next.jpg")
            it.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
            val intent = Intent().apply {
                putExtra("imageUri", file.toUri().toString())
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    @Composable
    fun EditingFeatureSlider(
        value: Float,
        onValueChange: (Float) -> Unit,
        range: ClosedFloatingPointRange<Float> = 0f..2f // Default range
    ) {
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = 1000,
        )

    }
    fun saveEditedImage(
        context: Context,
        brightness: Float,
        contrast: Float,
        hue: Float,
        saturation: Float,
        sharpness: Float,
        shadows: Float
    ) {
        val editedBitmap = displayed?.asAndroidBitmap() ?: return
        val editedImageUri = saveBitmap(context, editedBitmap)

        // You can use editedImageUri to do further operations like sharing or displaying the edited image
        // For example:
        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = editedImageUri
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(intent)

        // Once saved, you can navigate back to the previous screen if needed
        // For example:
        // (context as Activity).onBackPressed()
    }

    private fun saveBitmap(context: Context, bitmap: Bitmap): Uri {
        val savedImageUri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues()
        ) ?: throw RuntimeException("Failed to insert image into MediaStore")

        context.contentResolver.openOutputStream(savedImageUri)?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        } ?: throw RuntimeException("Failed to open output stream for image")

        return savedImageUri
    }

    private fun applyEdits(
        originalBitmap: Bitmap,
        brightness: Float,
        contrast: Float,
        hue: Float,
        saturation: Float,
        sharpness: Float,
        shadows: Float
    ): Bitmap {
        val editedBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)

        // Apply brightness adjustment
        editedBitmap.applyBrightness(brightness)

        // Apply contrast adjustment
        editedBitmap.applyContrast(contrast)

        // Apply hue adjustment
        editedBitmap.applyHue(hue)

        // Apply saturation
        editedBitmap.applySaturation(saturation)

        // Apply sharpen adjustment
        editedBitmap.applySharpness(sharpness)

        // Apply shadows adjustment
        editedBitmap.applyShadows(shadows)

        return editedBitmap
    }

    private fun Bitmap.applyBrightness(brightness: Float) {
        val pixels = IntArray(width * height)
        getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val color = pixels[i]
            val alpha = color shr 24 and 0xFF
            val red = (color shr 16 and 0xFF) * brightness
            val green = (color shr 8 and 0xFF) * brightness
            val blue = (color and 0xFF) * brightness

            // Ensure that color components stay within valid range [0, 255]
            val newRed = red.coerceIn(0f, 255f).toInt()
            val newGreen = green.coerceIn(0f, 255f).toInt()
            val newBlue = blue.coerceIn(0f, 255f).toInt()

            pixels[i] = (alpha shl 24) or (newRed shl 16) or (newGreen shl 8) or newBlue
        }

        setPixels(pixels, 0, width, 0, 0, width, height)
    }

    private fun Bitmap.applyContrast(contrast: Float) {
        val matrix = ColorMatrix()


        matrix.set(floatArrayOf(
            contrast, 0f, 0f, 0f, 0f,
            0f, contrast, 0f, 0f, 0f,
            0f, 0f, contrast, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))

        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(matrix)
        }

        val canvas = android.graphics.Canvas(this)
        canvas.drawBitmap(this, 0f, 0f, paint)
    }

    private fun Bitmap.applyHue(hue: Float) {
        val matrix = ColorMatrix()

        val cos = cos(hue.toDouble()).toFloat()
        val sin = sin(hue.toDouble()).toFloat()

        val lumR = 0.213f
        val lumG = 0.715f
        val lumB = 0.072f

        matrix.set(floatArrayOf(
            lumR + cos * (1 - lumR) + sin * (-lumR), lumG + cos * (-lumG) + sin * (-lumG), lumB + cos * (-lumB) + sin * (1 - lumB), 0f, 0f,
            lumR + cos * (-lumR) + sin * (0.143f), lumG + cos * (1 - lumG) + sin * (0.140f), lumB + cos * (-lumB) + sin * (-0.283f), 0f, 0f,
            lumR + cos * (-lumR) + sin * (-(1 - lumR)), lumG + cos * (-lumG) + sin * (lumG), lumB + cos * (1 - lumB) + sin * (lumB), 0f, 0f,
            0f, 0f, 0f, 1f, 0f,
            0f, 0f, 0f, 0f, 1f
        ))

        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(matrix)
        }

        val canvas = android.graphics.Canvas(this)
        canvas.drawBitmap(this, 0f, 0f, paint)
    }

    private fun Bitmap.applySaturation(saturation: Float) {
        val matrix = ColorMatrix()

        val lumR = 0.213f
        val lumG = 0.715f
        val lumB = 0.072f

        val sr = (1 - saturation) * lumR
        val sg = (1 - saturation) * lumG
        val sb = (1 - saturation) * lumB

        matrix.set(floatArrayOf(
            sr + saturation, sg, sb, 0f, 0f,
            sr, sg + saturation, sb, 0f, 0f,
            sr, sg, sb + saturation, 0f, 0f,
            0f, 0f, 0f, 1f, 0f,
            0f, 0f, 0f, 0f, 1f
        ))

        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(matrix)
        }

        val canvas = android.graphics.Canvas(this)
        canvas.drawBitmap(this, 0f, 0f, paint)
    }


    private fun Bitmap.applySharpness(sharpness: Float) {
        val editedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Apply sharpening effect
        val pixels = IntArray(width * height)
        getPixels(pixels, 0, width, 0, 0, width, height)

        val kernel = arrayOf(
            intArrayOf(-1, -1, -1),
            intArrayOf(-1, 9, -1),
            intArrayOf(-1, -1, -1)
        )

        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                var sumR = 0
                var sumG = 0
                var sumB = 0

                // Apply the convolution operation
                for (ky in -1..1) {
                    for (kx in -1..1) {
                        val pixel = pixels[(y + ky) * width + (x + kx)]
                        val weight = kernel[ky + 1][kx + 1]

                        sumR += android.graphics.Color.red(pixel) * weight
                        sumG += android.graphics.Color.green(pixel) * weight
                        sumB += android.graphics.Color.blue(pixel) * weight
                    }
                }

                // Ensure RGB values are within valid range
                val newR = (android.graphics.Color.red(pixels[y * width + x]) + sharpness * sumR).toInt().coerceIn(0, 255)
                val newG = (android.graphics.Color.green(pixels[y * width + x]) + sharpness * sumG).toInt().coerceIn(0, 255)
                val newB = (android.graphics.Color.blue(pixels[y * width + x]) + sharpness * sumB).toInt().coerceIn(0, 255)

                // Set the new pixel color
                pixels[y * width + x] = android.graphics.Color.rgb(newR, newG, newB)
            }
        }

        // Set the modified pixels to the edited bitmap
        setPixels(pixels, 0, width, 0, 0, width, height)

    }

    private fun Bitmap.applyShadows(shadows: Float) {
        val editedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Apply shadows adjustment
        val pixels = IntArray(width * height)
        getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val color = pixels[i]

            // Extract the RGB components
            val red = android.graphics.Color.red(color)
            val green = android.graphics.Color.green(color)
            val blue = android.graphics.Color.blue(color)

            // Calculate luminance (brightness)
            val luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255

            // Scale shadows parameter to range from 0 to 1
            val scaledShadows = shadows / 8

            // Adjust shadow intensity based on scaled shadows parameter and luminance
            val adjustedRed = (red * (1 - scaledShadows * luminance)).toInt().coerceIn(0, 255)
            val adjustedGreen = (green * (1 - scaledShadows * luminance)).toInt().coerceIn(0, 255)
            val adjustedBlue = (blue * (1 - scaledShadows * luminance)).toInt().coerceIn(0, 255)

            // Combine adjusted RGB components back into a color
            val adjustedColor = android.graphics.Color.argb(android.graphics.Color.alpha(color), adjustedRed, adjustedGreen, adjustedBlue)
            pixels[i] = adjustedColor
        }

        // Set the modified pixels to the edited bitmap
        setPixels(pixels, 0, width, 0, 0, width, height)

    }

}