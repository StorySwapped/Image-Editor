package com.example.imageeditor

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream


class FilterManagement : ComponentActivity() {
    public var tickConfirmation by mutableStateOf(false)
    public var crossConfirmation by mutableStateOf(false)
    companion object {
        const val IMAGE_URI_EXTRA = "imageUri"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the image URI from the intent extras
        val imageUriString = intent.getStringExtra(IMAGE_URI_EXTRA)
        val imageUri = Uri.parse(imageUriString)
        val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))

        setContent {
            if (imageUriString != null) {
                MyComposeScreen(this,bitmap)

            }
        }
        fun sendToMain(bitmap: Bitmap?) {
            val intent = Intent().apply {
                if (bitmap != null) {
                    val file = File(cacheDir, "image_next.jpg")
                    bitmap.writeBitmap(file)
                    putExtra("image", file.toUri().toString())
                } else {
                    putExtra("image", "") // Pass an empty string if no filter applied
                }
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

    }
}



@Composable
fun MyComposeScreen(context: Context,bitmap: Bitmap) {
    var selectedFilter by remember { mutableStateOf(Filter.None) }
    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val contextForSending by remember { mutableStateOf(context) }
    var tickConfirmation by remember { mutableStateOf(false) }
    var crossConfirmation by remember { mutableStateOf(false) }
    var removeFilterConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.Black)
            .padding(8.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "FILTERS",
            color = androidx.compose.ui.graphics.Color.White,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(R.font.sansserif)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        // Load the original bitmap
//        val imageResId = R.drawable.image
//        val bitmap = BitmapFactory.decodeResource(LocalContext.current.resources, imageResId)
        originalBitmap = bitmap

        // Apply the selected filter to the original bitmap
        val filteredBitmap = when (selectedFilter) {
            Filter.None -> originalBitmap
            Filter.Vignette -> applyVignetteFilter(originalBitmap!!)
            Filter.Sepia -> applySepiaFilter(originalBitmap!!)
            Filter.Saturate -> applyColorMatrixFilter(originalBitmap!!, saturateFilter)
            Filter.Rotate -> applyColorMatrixFilter(originalBitmap!!, rotateFilter)
            Filter.Posterize -> applyColorMatrixFilter(originalBitmap!!, posterizeFilter)
            Filter.Negative -> applyColorMatrixFilter(bitmap, negativeFilter)
            Filter.Tint -> applyColorMatrixFilter(bitmap, tintFilter)
            Filter.Temperature -> applyColorMatrixFilter(bitmap, temperatureFilter)
            Filter.Sharpen -> applyColorMatrixFilter(bitmap, sharpenFilter)

        }

        val imageToShow = filteredBitmap?.asImageBitmap() ?: bitmap.asImageBitmap()

        //original image
        Image(
            bitmap = imageToShow,
            contentDescription = null,
            modifier = Modifier
                .height(500.dp)
                .padding(top = 8.dp)
                .fillMaxSize()
        )

        // Buttons Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            IconButton(
                onClick = {
                    removeFilterConfirmation=true

                },
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Icon(

                    painter = painterResource(id = R.drawable.icon1),
                    contentDescription = "Remove Filter",
                    tint = androidx.compose.ui.graphics.Color.White // Change the color here
                )
            }
            IconButton(
                onClick = { selectedFilter = Filter.Sepia },
                modifier = Modifier.padding(end = 10.dp)

            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon2),
                    contentDescription = "Auto Apply Filter",
                    tint = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }


        // Horizontal Scrollable Row for Filters
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 7.dp)
        ) {
            FilterItem("None", Filter.None,originalBitmap!!,isSelected=selectedFilter==Filter.None) { selectedFilter = it }
            FilterItem("Vignette", Filter.Vignette, originalBitmap!!,isSelected=selectedFilter==Filter.Vignette) { selectedFilter = it }
            FilterItem("Sepia", Filter.Sepia, originalBitmap!!,isSelected=selectedFilter==Filter.Sepia) { selectedFilter = it }
            FilterItem("Saturate", Filter.Saturate, originalBitmap!!,isSelected=selectedFilter==Filter.Saturate) { selectedFilter = it }
            FilterItem("Rotate", Filter.Rotate, originalBitmap!!,isSelected=selectedFilter==Filter.Rotate) { selectedFilter = it }
            FilterItem("Posterize", Filter.Posterize, originalBitmap!!,isSelected=selectedFilter==Filter.Posterize) { selectedFilter = it }
            FilterItem("Negative", Filter.Negative,originalBitmap!!,isSelected=selectedFilter==Filter.Negative) { selectedFilter = it }
            FilterItem("Tint", Filter.Tint, originalBitmap!!,isSelected=selectedFilter==Filter.Tint) { selectedFilter = it }
            FilterItem("Temperature", Filter.Temperature, originalBitmap!!,isSelected=selectedFilter==Filter.Temperature) { selectedFilter = it }
            FilterItem("Sharpen", Filter.Sharpen, originalBitmap!!,isSelected=selectedFilter==Filter.Sharpen) { selectedFilter = it }
        }
        // Bottom Row for Icons
        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            IconButton(
                onClick = {
                    crossConfirmation=true
                 //   sendToMain(contextForSending, originalBitmap)
                },
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon3),
                    contentDescription = "Your Icon Left",
                    tint = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.size(22.dp)// Change the color to white here
                )
            }

            IconButton(
                onClick = {
                    tickConfirmation=true;},
              //      sendToMain(contextForSending, filteredBitmap)
                    //      },
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon4),
                    contentDescription = "Your Icon Right",
                    tint = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.size(22.dp)// Change the color to white here// Change the color to white here
                )
            }

            if(tickConfirmation){
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
                                sendToMain(contextForSending,filteredBitmap)
                                tickConfirmation = false
                            },colors = ButtonDefaults.buttonColors(
                                containerColor = androidx.compose.ui.graphics.Color.Black
                            )
                        ) {
                            Text(text = "Yes")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { tickConfirmation = false },colors = ButtonDefaults.buttonColors(
                                containerColor = androidx.compose.ui.graphics.Color.Black
                            )
                        ) {
                            Text(text = "No")
                        }
                    }
                )
            }

            if(crossConfirmation){
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
                                sendToMain(contextForSending, originalBitmap)
                                crossConfirmation = false
                            },colors = ButtonDefaults.buttonColors(
                                containerColor = androidx.compose.ui.graphics.Color.Black
                            )

                        ) {
                            Text(text = "Yes")
                        }
                    },
                    dismissButton = {


                        Button(
                            onClick = { crossConfirmation = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = androidx.compose.ui.graphics.Color.Black
                            )
                        ) {

                            Text(
                                text = "No")

                        }

                    }



                )
            }

            if(removeFilterConfirmation){
                AlertDialog(
                    onDismissRequest = { removeFilterConfirmation= false },
                    title = {
                        Text(text = "Confirmation")
                    },
                    text = {
                        Text(text = "Are you sure you want to Remove the Filter?")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                selectedFilter = Filter.None
                                removeFilterConfirmation = false
                            },colors = ButtonDefaults.buttonColors(
                                containerColor = androidx.compose.ui.graphics.Color.Black
                            )

                        ) {
                            Text(text = "Yes")
                        }
                    },
                    dismissButton = {


                        Button(
                            onClick = { removeFilterConfirmation = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = androidx.compose.ui.graphics.Color.Black
                            )
                        ) {

                            Text(
                                text = "No")

                        }

                    }



                )

            }

        }

    }
}



fun sendToMain(context: Context, bitmap: Bitmap?) {
    bitmap?.let {
        val file = File(context.cacheDir, "image_next.jpg")
        it.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
        val intent = Intent().apply {
            putExtra("imageUri", file.toUri().toString())
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        (context as? Activity)?.apply {
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}

@Composable
fun FilterItem(filterName: String, filter: Filter, originalBitmap: Bitmap,isSelected: Boolean,  onSelected: (Filter) -> Unit) {
    // Apply the filter to the original bitmap to get the preview image
    val previewBitmap = remember(filter) {
        applyFilter(originalBitmap, filter)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(150.dp)
            .padding(3.dp)
            .clickable { onSelected(filter) }
            .border(
                width = 3.dp,
                color = if (isSelected) androidx.compose.ui.graphics.Color.Yellow else androidx.compose.ui.graphics.Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        // filter bar images
        Image(
            bitmap = previewBitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Text(
            text = filterName,
            color = androidx.compose.ui.graphics.Color.White,
            modifier = Modifier.padding(4.dp),
            textAlign = TextAlign.Center
        )
    }
}

// Function to get Filter enum from filter name
private fun getFilterForName(filterName: String): Filter {
    return when (filterName) {
        "None" -> Filter.None
        "Vignette" -> Filter.Vignette
        else -> Filter.None // Default to None for unknown filters
    }
}

enum class Filter {
    None,
    Vignette,
    Sepia,
    Saturate,
    Rotate,
    Posterize,
    Negative,
    Tint,
    Temperature,
    Sharpen,
}

fun applyFilter(bitmap: Bitmap, filter: Filter): Bitmap {
    return when (filter) {
        Filter.None -> bitmap // No filter
        Filter.Vignette -> applyVintageFilter(bitmap)
        Filter.Sepia -> applySepiaFilter(bitmap)
        Filter.Saturate -> applyColorMatrixFilter(bitmap, saturateFilter)
        Filter.Rotate -> applyColorMatrixFilter(bitmap, rotateFilter)
        Filter.Posterize -> applyColorMatrixFilter(bitmap, posterizeFilter)
        Filter.Negative -> applyColorMatrixFilter(bitmap, negativeFilter)
        Filter.Tint -> applyColorMatrixFilter(bitmap, tintFilter)
        Filter.Temperature -> applyColorMatrixFilter(bitmap, temperatureFilter)
        Filter.Sharpen -> applyColorMatrixFilter(bitmap, sharpenFilter)
    }
}
private val negativeFilter: ColorMatrix
    get() {
        val colorMatrix = ColorMatrix()
        colorMatrix.set(
            floatArrayOf(
                -1f, 0f, 0f, 0f, 255f,  // Red
                0f, -1f, 0f, 0f, 255f,  // Green
                0f, 0f, -1f, 0f, 255f,  // Blue
                0f, 0f, 0f, 1f, 0f // Alpha
            )
        )
        return colorMatrix
    }
private fun Bitmap.writeBitmap(file: File) {
    val output= file.outputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, output)
    output.flush()
    output.close()
}
private val tintFilter: ColorMatrix
    get() {
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f) // To make the image grayscale
        colorMatrix.postConcat(
            ColorMatrix(
                floatArrayOf(
                    2f, 0f, 0f, 0f, 0f,  // Red tint
                    0f, 1f, 0f, 0f, 0f,  // Green tint
                    0f, 0f, 2f, 0f, 0f,  // Blue tint
                    0f, 0f, 0f, 1f, 0f // Alpha
                )
            )
        )
        return colorMatrix
    }

private val temperatureFilter: ColorMatrix
    get() {
        val colorMatrix = ColorMatrix()
        colorMatrix.set(
            floatArrayOf(
                2f, 0f, 0f, 0f, 0f,  // Red
                0f, 1f, 0f, 0f, 0f,  // Green
                0f, 0f, 0.5f, 0f, 0f,  // Blue
                0f, 0f, 0f, 1f, 0f // Alpha
            )
        )
        return colorMatrix
    }

private val sharpenFilter: ColorMatrix
    get() {
        val colorMatrix = ColorMatrix()
        colorMatrix.set(
            floatArrayOf(
                0f, -1f, 0f, 0f, 0f,  // Red
                -1f, 5f, -1f, 0f, 0f,  // Green
                0f, -1f, 0f, 0f, 0f,  // Blue
                0f, 0f, 0f, 1f, 0f // Alpha
            )
        )
        return colorMatrix
    }


private fun applyColorMatrixFilter(inputBitmap: Bitmap, colorMatrix: ColorMatrix): Bitmap {
    val outputBitmap = Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, inputBitmap.config)

    val paint = Paint().apply {
        colorFilter = ColorMatrixColorFilter(colorMatrix)
    }

    val canvas = Canvas(outputBitmap)
    canvas.drawBitmap(inputBitmap, 0f, 0f, paint)

    return outputBitmap
}

private val saturateFilter: ColorMatrix
    get() {
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(2f) // Increase saturation
        return colorMatrix
    }

private val rotateFilter: ColorMatrix
    get() {
        val colorMatrix = ColorMatrix()
        colorMatrix.setRotate(0, 180f) // Rotate hue by 180 degrees
        return colorMatrix
    }

private val posterizeFilter: ColorMatrix
    get() {
        val colorMatrix = ColorMatrix()
        colorMatrix.set(
            floatArrayOf(
                1f, 0f, 0f, 0f, 0f,  // Red
                0f, 1f, 0f, 0f, 0f,  // Green
                0f, 0f, 1f, 0f, 0f,  // Blue
                0f, 0f, 0f, 20f, 0f // Alpha, increase to get more steps between colors
            )
        )
        return colorMatrix
    }

private fun applySepiaFilter(inputBitmap: Bitmap): Bitmap {
    val outputBitmap = Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, inputBitmap.config)

    val sepiaMatrix = ColorMatrix().apply {
        set(
            floatArrayOf(
                0.393f, 0.769f, 0.189f, 0f, 0f,  // Red
                0.349f, 0.686f, 0.168f, 0f, 0f,  // Green
                0.272f, 0.534f, 0.131f, 0f, 0f,  // Blue
                0f, 0f, 0f, 1f, 0f // Alpha
            )
        )
    }

    val paint = Paint().apply {
        colorFilter = ColorMatrixColorFilter(sepiaMatrix)
    }

    val canvas = Canvas(outputBitmap)
    canvas.drawBitmap(inputBitmap, 0f, 0f, paint)

    return outputBitmap
}

// Apply Vignette effect
private fun applyVintageFilter(inputBitmap: Bitmap): Bitmap {
    val outputBitmap = Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, inputBitmap.config)

    val colorMatrix = ColorMatrix()
    colorMatrix.setSaturation(0f) // Remove saturation
    colorMatrix.set(
        floatArrayOf(
            1.2f, 0f, 0f, 0f, 0f, // Red multiplier
            0f, 0.8f, 0f, 0f, 0f, // Green multiplier
            0f, 0f, 0.7f, 0f, 0f, // Blue multiplier
            0f, 0f, 0f, 1f, 0f    // Alpha multiplier
        )
    )

    val paint = Paint().apply {
        colorFilter = ColorMatrixColorFilter(colorMatrix)
    }

    val canvas = Canvas(outputBitmap)
    canvas.drawBitmap(inputBitmap, 0f, 0f, paint)

    return outputBitmap
}

// Apply the vignette filter to a bitmap
fun applyVignetteFilter(originalBitmap: Bitmap): Bitmap {
    val vignetteArray = floatArrayOf(
        0.5f, 0f, 0f, 0f, 0f,
        0f, 0.5f, 0f, 0f, 0f,
        0f, 0f, 0.5f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )
    val colorMatrix = ColorMatrix()
    colorMatrix.set(vignetteArray)

    val filteredBitmap = Bitmap.createBitmap(
        originalBitmap.width,
        originalBitmap.height,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(filteredBitmap)
    val paint = Paint()
    paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
    canvas.drawBitmap(originalBitmap, 0f, 0f, paint)

    return filteredBitmap
}

private fun autoApplyFilter(inputBitmap: Bitmap): Bitmap {
    val brightness = calculateBrightness(inputBitmap)
    val contrast = calculateContrast(inputBitmap)
    val filteredBitmap: Bitmap

    if (brightness < 100 && contrast > 50) {
        filteredBitmap = increaseSaturation(inputBitmap)
    } else if (brightness >= 100 && contrast <= 50) {
        filteredBitmap = applyVintageFilter(inputBitmap)
    } else if (brightness >= 120 && contrast > 40) {
        filteredBitmap = applyFilmFilter(inputBitmap)
    } else {
        filteredBitmap = applyGrayscaleFilter(inputBitmap)
    }

    return filteredBitmap
}
// Helper method to show a toast message
private fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun calculateBrightness(bitmap: Bitmap): Double {
    val totalPixelCount = bitmap.width * bitmap.height
    var totalBrightness = 0
    for (y in 0 until bitmap.height) {
        for (x in 0 until bitmap.width) {
            val pixel = bitmap.getPixel(x, y)
            val brightness =
                (0.3 * Color.red(pixel) + 0.59 * Color.green(pixel) + 0.11 * Color.blue(pixel)).toInt()
            totalBrightness = totalBrightness +  brightness
        }
    }
    return totalBrightness.toDouble() / totalPixelCount
}
fun calculateContrast(bitmap: Bitmap): Double {
    val totalPixelCount = bitmap.width * bitmap.height
    var contrastSum = 0.0
    val meanGrayValue = calculateBrightness(bitmap)
    for (y in 0 until bitmap.height) {
        for (x in 0 until bitmap.width) {
            val pixel = bitmap.getPixel(x, y)
            val brightness =
                (0.3 * Color.red(pixel) + 0.59 * Color.green(pixel) + 0.11 * Color.blue(pixel)).toInt()
            contrastSum += Math.pow(brightness - meanGrayValue, 2.0)
        }
    }
    return Math.sqrt(contrastSum / totalPixelCount)
}

fun applyGrayscaleFilter(inputBitmap: Bitmap): Bitmap {
    val pixel = 0xFFAABBCC.toInt() // Example color value (0xAABBCC)
    val alpha = pixel shr 24 and 0xFF
    val red = pixel shr 16 and 0xFF
    val green = pixel shr 8 and 0xFF
    val blue = pixel and 0xFF

// Now you have the alpha, red, green, and blue components as separate integers

    val outputBitmap =
        Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, inputBitmap.config)
    for (y in 0 until inputBitmap.height) {
        for (x in 0 until inputBitmap.width) {
            val pixel = inputBitmap.getPixel(x, y).toInt();
            val red = (0.3 * Color.red(pixel).toDouble()).toInt()
            val green = (0.59 * Color.green(pixel).toDouble()).toInt()
            val blue = (0.11 * Color.blue(pixel).toDouble()).toInt()

            val gray = red + green + blue
            outputBitmap.setPixel(x, y, Color.rgb(gray, gray, gray))
        }
    }
    return outputBitmap
}

private fun increaseSaturation(inputBitmap: Bitmap): Bitmap {
    val outputBitmap =
        Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, inputBitmap.config)
    val saturationFactor = 1.5f
    for (y in 0 until inputBitmap.height) {
        for (x in 0 until inputBitmap.width) {
            val pixel = inputBitmap.getPixel(x, y)
            val hsv = FloatArray(3)
            Color.RGBToHSV(Color.red(pixel), Color.green(pixel), Color.blue(pixel), hsv)
            hsv[1] *= saturationFactor
            outputBitmap.setPixel(x, y, Color.HSVToColor(hsv))
        }
    }
    return outputBitmap
}

private fun applyFilmFilter(inputBitmap: Bitmap): Bitmap {
    val outputBitmap =
        Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, inputBitmap.config)
    val brightnessFactor = 0.8f
    val contrastFactor = 1.2f
    for (y in 0 until inputBitmap.height) {
        for (x in 0 until inputBitmap.width) {
            val pixel = inputBitmap.getPixel(x, y)
            val red = (Color.red(pixel) * brightnessFactor * contrastFactor).toInt()
            val green = (Color.green(pixel) * brightnessFactor * contrastFactor).toInt()
            val blue = (Color.blue(pixel) * brightnessFactor * contrastFactor).toInt()
            outputBitmap.setPixel(x, y, Color.rgb(red.coerceIn(0, 255), green.coerceIn(0, 255), blue.coerceIn(0, 255)))
        }
    }
    return outputBitmap
}
