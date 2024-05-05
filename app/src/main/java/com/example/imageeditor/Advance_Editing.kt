package com.example.imageeditor

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.geometry.Offset
import android.net.Uri
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.material3.*
import androidx.compose.material3.Slider
//noinspection UsingMaterialAndMaterial3Libraries
//import androidx.compose.material3.icons.materialicons.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.geometry.Size
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.imageeditor.ui.theme.ImageEditorTheme
import java.io.File
import java.io.FileOutputStream
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import android.graphics.Paint
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp


import androidx.lifecycle.ViewModel
import kotlin.math.roundToInt


class AdvanceEditing : ComponentActivity() {
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
            ImageEditorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),

                ) {
                    advanceeditingScreen(this, bitmap)
                }
            }
        }

    }
}
@Composable
fun advanceeditingScreen(context: Context, bitmap: Bitmap) {
    val contextForSending by remember { mutableStateOf(context) }
    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showImage by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.back17), // Replace R.drawable.background_image with your image resource
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .fillMaxSize(),
            //.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Advance Editing",
                color = Color(android.graphics.Color.parseColor("#D0D0D0")),
                modifier = Modifier.padding(top = 8.dp, bottom = 15.dp),
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                fontFamily = FontFamily(Font(R.font.sansserif))

            )

            originalBitmap = bitmap
            val imageToShow = bitmap.asImageBitmap()
            //original image
            if (showImage) {
                Image(
                    bitmap = imageToShow,
                    contentDescription = null,
                    modifier = Modifier
                        .height(580.dp)
                        .absolutePadding(bottom = 10.dp)
                        .fillMaxSize()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally // Align items to the center horizontally
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    var penClicked by remember { mutableStateOf(false) }
                    var cloneClicked by remember { mutableStateOf(false) }

                    if (penClicked) {
                        showImage = false // Hide the image
                        DrawingScreen(contextForSending, imageToShow)
                    } else if (cloneClicked) {
                        showImage = false // Hide the image
                        CloneTool(imageToShow)
                    } else {
                        Column(
                            verticalArrangement = Arrangement.Bottom,
                        ) {
                            Row {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .absolutePadding(top = 2.dp, left = 7.dp)
                                        .width(90.dp)
                                        .height(95.dp)
                                        .clickable {
                                            penClicked = true
                                            cloneClicked = false
                                            showImage = false // Hide the image
                                        }
                                        .background(
                                            Color(android.graphics.Color.parseColor("#281340")),
                                            shape = RoundedCornerShape(15.dp)
                                        )
                                )
                                {
                                    Image(
                                        painter = painterResource(R.drawable.pen_tool),
                                        contentDescription = "Pen",
                                        modifier = Modifier
                                            .absolutePadding(top = 2.dp, bottom = 5.dp)
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                    )

                                    Text(
                                        text = "Pen Tool",
                                        color = Color.White,
                                        modifier = Modifier.padding(2.dp),
                                        textAlign = TextAlign.Center,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(Font(R.font.sansserif))
                                    )
                                }

                                Spacer(modifier = Modifier.width(50.dp)) // Add space between the buttons

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .absolutePadding(top = 2.dp, left = 7.dp)
                                        .width(90.dp)
                                        .height(95.dp)
                                        .clickable {
                                            cloneClicked = true
                                            penClicked = false
                                            showImage = false // Hide the image
                                        }
                                        .background(
                                            Color(android.graphics.Color.parseColor("#281340")),
                                            shape = RoundedCornerShape(15.dp)
                                        )
                                )
                                {
                                    Image(
                                        painter = painterResource(R.drawable.clone_tool),
                                        contentDescription = "Pen",
                                        modifier = Modifier
                                            .absolutePadding(top = 2.dp, bottom = 5.dp)
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                    )

                                    Text(
                                        text = "Clone Tool",
                                        color = Color.White,
                                        modifier = Modifier.padding(2.dp),
                                        textAlign = TextAlign.Center,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily(Font(R.font.sansserif))
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Confirmationbuttons(originalBitmap!!, contextForSending)

        }
    }
}

@Composable
fun pentoolicon (isClicked: Boolean, onClicked: () -> Unit)
{
    Button(
        onClick= {
            onClicked()
        },
        modifier = Modifier.background(Color.Black), // Set background color here
        colors = ButtonDefaults.textButtonColors(
            //backgroundColor = Color.Red, // Change button color here
            contentColor = Color.Yellow
        )// Change text color here
    )
    {
        Text(text = "Pen tool",fontSize=20.sp,)
    }
}

@Composable
fun DrawingScreen(contextForSending: Context,bitmap: ImageBitmap) {
    val lines = remember {
        mutableStateListOf<Line>()
    }

    var useropacity by remember { mutableFloatStateOf(0.5f) }
    var usersize by remember { mutableStateOf(0.5f) }


    var red by remember { mutableStateOf(0f) }
    var green by remember { mutableStateOf(0f) }
    var blue by remember { mutableStateOf(0f) }


    var sliderVisible by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableStateOf(0.0f) }

    var copiedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val edited:editedimage=viewModel()

    Column(

        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Row{
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .height(480.dp)
                        .absolutePadding(bottom = 10.dp),

                ) {
                    Image(
                        bitmap = bitmap ,
                        contentDescription = null,
                        modifier = Modifier
                            .height(480.dp)
                            .fillMaxSize()
                    )
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(450.dp)
                            .pointerInput(true) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val canvasSize = size
                                    // Calculate the start and end points of the line
                                    val start = change.position - dragAmount
                                    val end = change.position

                                    // Adjust the start and end points to stay within the bounds of the image
                                    val adjustedStart = adjustToBounds(
                                        start,
                                        Size(
                                            canvasSize.width.toFloat(),
                                            canvasSize.height.toFloat()
                                        )
                                    )
                                    val adjustedEnd = adjustToBounds(
                                        end,
                                        Size(
                                            canvasSize.width.toFloat(),
                                            canvasSize.height.toFloat()
                                        )
                                    )
                                    //val adjustedStart = adjustToBounds(start, Size(bitmap.width.toFloat(), bitmap.height.toFloat()))
                                    //val adjustedEnd = adjustToBounds(end, Size(bitmap.width.toFloat(), bitmap.height.toFloat()))


                                    // Create a line with adjusted points
                                    val line = Line(
                                        start = adjustedStart,
                                        end = adjustedEnd,
                                        color = Color(
                                            red = red,
                                            green = green,
                                            blue = blue
                                        ),
                                        strokeWidth = usersize.dp, // Default stroke width
                                        opacity = useropacity
                                    )

                                    // Add the line to the list of lines
                                    lines.add(line)
                                    val offScreenBitmap = Bitmap.createBitmap(
                                        bitmap.width,
                                        bitmap.height,
                                        Bitmap.Config.ARGB_8888
                                    )
                                    val canvas = android.graphics.Canvas(offScreenBitmap)
                                    canvas.drawBitmap(bitmap.asAndroidBitmap(), 0f, 0f, null)
                                    lines.forEach { line ->
                                        canvas.drawLine(
                                            line.start.x,
                                            line.start.y,
                                            line.end.x,
                                            line.end.y,
                                            Paint().apply {
                                                color = line.color.toArgb()
                                                strokeWidth = line.strokeWidth.toPx()
                                                alpha = (line.opacity * 255).toInt()
                                                isAntiAlias = true
                                            }
                                        )
                                    }
                                    copiedBitmap =
                                        offScreenBitmap.copy(Bitmap.Config.ARGB_8888, true)
                                    edited.imageBitmap = copiedBitmap

                                }
                            }
                    ) {
                        // Draw all the lines on the canvas
                        lines.forEach { line ->
                            drawLine(
                                color = line.color.copy(alpha = line.opacity),
                                start = line.start,
                                end = line.end,
                                strokeWidth = line.strokeWidth.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Row {
            if(sliderVisible) {

                if(sliderValue == 0.5f) {
                    Slider(
                        value = useropacity,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.White
                        ),
                        onValueChange = { useropacity = it },
                        modifier = Modifier
                            .width(400.dp)
                            .height(20.dp),
                        valueRange = 0.0f..1.0f
                    )

                }
                else{
                    useropacity = sliderValue
                }

            }
        }
        Row {
            Text(
                text = " SIZE:   ",
                color = Color(android.graphics.Color.parseColor("#D0D0D0")),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontFamily = FontFamily(Font(R.font.sansserif))
            )

            Slider(
                value = usersize,
                onValueChange = { usersize = it },
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.Gray
                ),
                modifier = Modifier
                    .width(270.dp)
                    .height(25.dp),
                valueRange = 1.0f..10.0f
            )
        }
        Row {

            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                ) {


                    Column(
                        modifier = Modifier.padding(5.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {

                        Row {
                            Text(
                                text = "COLOR",
                                color = Color(android.graphics.Color.parseColor("#D0D0D0")),
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp,
                                fontFamily = FontFamily(Font(R.font.sansserif)),
                                modifier = Modifier
                                    .absolutePadding(left = 50.dp, right = 25.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .height(25.dp)
                                    .width(200.dp)
                                    .padding(2.dp)
                                    .background(Color(red = red, green = green, blue = blue))
                                    .border(width = 2.dp, color = Color.Gray)
                            )
                        }

                        Slider(
                            colors = SliderDefaults.colors(
                                thumbColor = Color(android.graphics.Color.parseColor("#990000")),
                                activeTrackColor = Color(android.graphics.Color.parseColor("#990000")),
                                inactiveTrackColor = Color.DarkGray
                            ),
                            value = red,
                            onValueChange = { red = it },
                            modifier = Modifier
                                .width(350.dp)
                                .height(20.dp)
                                .absolutePadding(left = 20.dp),
                            valueRange = 0f..1f

                        )
                        Slider(
                            colors = SliderDefaults.colors(
                                thumbColor = Color(android.graphics.Color.parseColor("#009900")),
                                activeTrackColor = Color(android.graphics.Color.parseColor("#009900")),
                                inactiveTrackColor = Color.DarkGray
                            ),
                            value = green,
                            onValueChange = { green = it },
                            modifier = Modifier
                                .width(350.dp)
                                .height(20.dp)
                                .absolutePadding(left = 20.dp),
                            valueRange = 0f..1f
                        )
                        Slider(
                            colors = SliderDefaults.colors(
                                thumbColor = Color(android.graphics.Color.parseColor("#000099")),
                                activeTrackColor = Color(android.graphics.Color.parseColor("#000099")),
                                inactiveTrackColor = Color.DarkGray
                            ),
                            value = blue,
                            onValueChange = { blue = it },
                            modifier = Modifier
                                .width(350.dp)
                                .height(20.dp)
                                .absolutePadding(left = 20.dp),
                            valueRange = 0f..1f
                        )
                    }
                }
                //Confirmationbuttons()
            }
        }
        Row {

            IconButton(
                onClick = {
                    sliderVisible = true // Show slider
                    sliderValue = 0.5f
                },
                modifier = Modifier
                    .padding(15.dp)
                    .height(20.dp)
                    .width(60.dp)
                    .background(Color(android.graphics.Color.parseColor(button_color)), shape = RoundedCornerShape(15.dp)),
            )
            {
                Text(
                    text = "PEN",
                    color = Color(android.graphics.Color.parseColor("#D0D0D0")),
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp,
                    fontFamily = FontFamily(Font(R.font.sansserif)),
                )
            }

            Spacer(modifier = Modifier.width(5.dp))

            IconButton(
                onClick = {
                    sliderVisible = true
                    sliderValue = 0.2f
                },
                modifier = Modifier
                    .padding(15.dp)
                    .height(20.dp)
                    .width(110.dp)
                    .background(Color(android.graphics.Color.parseColor(button_color)), shape = RoundedCornerShape(15.dp))
            ) {
                Text(
                    text = "HIGHLIGHTER",
                    color = Color(android.graphics.Color.parseColor("#D0D0D0")),
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp,
                    fontFamily = FontFamily(Font(R.font.sansserif))
                )
            }

            Spacer(modifier = Modifier.width(5.dp))

            IconButton(
                onClick = {
                    sliderVisible = true
                    sliderValue = 1.0f
                },
                modifier = Modifier
                    .padding(15.dp)
                    .height(20.dp)
                    .width(90.dp)
                    .background(Color(android.graphics.Color.parseColor(button_color)), shape = RoundedCornerShape(15.dp))
            ) {
                Text(
                    text = "MARKER",
                    color = Color(android.graphics.Color.parseColor("#D0D0D0")),
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp,
                    fontFamily = FontFamily(Font(R.font.sansserif))
                )
            }
        }
    }

}


@Composable
fun clonetoolicon(isClicked: Boolean, onClicked: () -> Unit) {
    Button(
        onClick = {
            onClicked()
        },
        modifier = Modifier.background(Color.Black), // Set background color here
        colors = ButtonDefaults.textButtonColors(
            //backgroundColor = Color.Red, // Change button color here
            contentColor = Color.Yellow
        )// Change text color here
    ) {
        Text(
            text = "Clone tool",
            fontSize = 20.sp,
        )
    }
}



@Composable
fun CloneTool(bitmap: ImageBitmap)
{
    var boxPosition by remember { mutableStateOf(Offset(0f, 0f)) }
    Box {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp),
            contentScale = ContentScale.FillWidth
        )
        Box(
            modifier = Modifier
                .size(width = 25.dp, height = 25.dp)
                .offset(boxPosition.x.dp, boxPosition.y.dp)
                .background(Color.Blue)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        boxPosition = offset
                    }
                }
        )
    }
}

/*
@Composable
fun CloneTool(bitmap: ImageBitmap) {
    var selectedArea: ImageBitmap? by remember { mutableStateOf(null) }
    var clickPosition: Offset? by remember { mutableStateOf(null) }
    var confirm by remember { mutableStateOf(false) }
    Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .border(4.dp, Color.Black)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            clickPosition = offset
                            val touchX = offset.x.toInt()
                            val touchY = offset.y.toInt()

                            // Calculate the selected area immediately after the user clicks
                            val startX = touchX - 10
                            val startY = touchY - 10
                            val endX = touchX + 10
                            val endY = touchY + 10

                            val width = 20f
                            val height = 20f
                            selectedArea = Bitmap.createBitmap(
                                bitmap.asAndroidBitmap(),
                                startX.toInt(),
                                startY.toInt(),
                                width.toInt(),
                                height.toInt()
                            ).asImageBitmap()
                        }
                    }
            ) {
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(450.dp),
                    contentScale = ContentScale.FillWidth
                )

                if(confirm!=false) {
                    // Draw the selected area if it exists
                    selectedArea?.let { area ->
                        Image(
                            bitmap = area,
                            contentDescription = null,
                            modifier = Modifier
                                .size(20.dp)
                                .offset {
                                    IntOffset(
                                        (clickPosition!!.x).toInt(),
                                        (clickPosition!!.y).toInt()
                                    )
                                }
                                .background(Color.Transparent.copy(alpha = 0.5f))
                        )
                    }
                }
            }
        Button(
            onClick = {
                      confirm=true
            },
            modifier = Modifier.wrapContentSize(Alignment.Center)
        ) {
            Text("Confirm")
        }
    }
}
*/


/**
 * Adjusts the given point to stay within the specified bounds.
 *
 * @param point The point to adjust.
 * @param bounds The bounds to constrain the point to.
 * @return The adjusted point.
 */

// Ensure the start and end points stay within the image bounds
private fun adjustToBounds(point: Offset, imageBounds: Size): Offset {
    val x = point.x.coerceIn(0f, imageBounds.width)
    val y = point.y.coerceIn(0f, imageBounds.height)
    return Offset(x, y)
}




data class Line(
    val start: Offset,
    val end: Offset,
    val color: Color,
    val strokeWidth: Dp,
    val opacity: Float
)



private fun createBitmapFromSelection(
    imageBitmap: ImageBitmap,
    start: Offset?,
    end: Offset?
): Bitmap {
    if (start == null || end == null) return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

    val startX = min(start.x, end.x).coerceAtLeast(0f).toInt()
    val startY = min(start.y, end.y).coerceAtLeast(0f).toInt()
    val endX = max(start.x, end.x).coerceAtMost(imageBitmap.width.toFloat()).toInt()
    val endY = max(start.y, end.y).coerceAtMost(imageBitmap.height.toFloat()).toInt()

    val width = endX - startX
    val height = endY - startY

    val androidBitmap = Bitmap.createBitmap(
        imageBitmap.width,
        imageBitmap.height,
        Bitmap.Config.ARGB_8888
    )
    val canvas = android.graphics.Canvas(androidBitmap)
    canvas.drawBitmap(imageBitmap.asAndroidBitmap(), -startX.toFloat(), -startY.toFloat(), null)
    return Bitmap.createBitmap(androidBitmap, startX, startY, width, height)
}





@Composable
fun SelectableImageView(
    onStartSelection: (Offset) -> Unit,
    onSelectionChange: (Offset) -> Unit,
    onSelectionEnd: (Offset, Offset) -> Unit,
    selectionStart: Offset?,
    selectionEnd: Offset?
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        onStartSelection(offset)
                    },
                    onDragEnd = {
                        selectionEnd?.let { end ->
                            selectionStart?.let { start ->
                                onSelectionEnd(start, end)
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        onSelectionChange(change.position)
                    }
                )
            }
    ) {
        selectionStart?.let { start ->
            selectionEnd?.let { end ->
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        color = Color.Blue.copy(alpha = 0.5f),
                        topLeft = Offset(x = min(start.x, end.x), y = min(start.y, end.y)),
                        size = Size(width = abs(start.x - end.x), height = abs(start.y - end.y))
                    )
                }
            }
        }
    }
}

@Composable
fun Confirmationbuttons(originalBitmap: Bitmap, contextForSending: Context) {
    var tickConfirmation by remember { mutableStateOf(false) }
    var crossConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Row {
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
            Spacer(modifier = Modifier.width(270.dp))
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

            if (tickConfirmation) {
                showBoxTick(contextForSending,originalBitmap,tickConfirmation) {
                    tickConfirmation = it
                }
            }

            if (crossConfirmation) {
                showBoxCross(contextForSending,originalBitmap) {
                    crossConfirmation = it
                }
            }
        }
    }
}

@Composable
fun showBoxTick(contextForSending: Context,originalBitmap: Bitmap, tickConfirmation: Boolean, onDismiss: (Boolean) -> Unit) {
    val edited:editedimage=viewModel()
    val currentImageBitmap: Bitmap? = edited.imageBitmap
    AlertDialog(
        onDismissRequest = { onDismiss(false) },
        title = { Text(text = "Confirmation") },
        text = { Text(text = "Are you sure you want to save changes?") },
        confirmButton = {
            Button(
                onClick = {
                    // Call sendToMain1 function here
                    if (currentImageBitmap != null) {
                        sendToMain1(contextForSending,currentImageBitmap)
                    }
                    else{
                        sendToMain1(contextForSending,originalBitmap)
                    }
                    onDismiss(false)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Text(text = "Yes")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss(false) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Text(text = "No")
            }
        }
    )
}

fun sendToMain1(context: Context,currentImageBitmap: Bitmap) {
    currentImageBitmap?.let {
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
fun showBoxCross(contextForSending: Context,originalBitmap: Bitmap,onDismiss: (Boolean) -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss(false) },
        title = {
            Text(text = "Confirmation")
        },
        text = {
            Text(text = "Are you sure you want to discard changes?")
        },
        confirmButton = {
            Button(
                onClick = {
                    sendToMain1(contextForSending,originalBitmap)
                    onDismiss(false)
                },
                colors = ButtonDefaults.buttonColors(
                    //backgroundColor = Color.Black
                )
            ) {
                Text(text = "Yes")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss(false) },
                colors = ButtonDefaults.buttonColors(
                    //backgroundColor = Color.Black
                )
            ) {
                Text(text = "No")
            }
        }
    )
}

class editedimage:ViewModel(){
    private var _imageBitmap by mutableStateOf<Bitmap?>(null)

    var imageBitmap: Bitmap?
        get() = _imageBitmap
        set(value) {
            _imageBitmap = value
        }
}