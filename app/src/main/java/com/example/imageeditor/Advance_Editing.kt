package com.example.imageeditor


import android.graphics.Paint.Align
import androidx.compose.runtime.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.geometry.Offset
import android.view.MotionEvent
import android.graphics.Rect
import androidx.compose.foundation.clickable

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.transformable
import androidx.compose.material3.Button
import androidx.compose.material3.*
import androidx.compose.material3.Slider
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.icons.*
//import androidx.compose.material3.icons.materialicons.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.material3.Slider


import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Size



import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap


import androidx.compose.foundation.*

import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults


import androidx.compose.ui.res.imageResource
import com.example.imageeditor.ui.theme.ImageEditorTheme
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
class AdvanceEditing : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageUriString = intent.getStringExtra("imageUri")
        val imageUri = Uri.parse(imageUriString)
        setContent {
            //Box(modifier = Modifier.background(Color.Black))
            ImageEditorTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp) // Add padding to the bottom
                        .background(Color.Black),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally // Align items to the center horizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {

                        var penClicked by remember { mutableStateOf(false) }
                        var cloneClicked by remember { mutableStateOf(false) }

                        if(penClicked)
                        {
                            DrawingScreen()
                        }
                        else if(cloneClicked)
                        {
                            CloneTool()
                        }
                        else
                        {
                            Row {
                                pentoolicon(isClicked = penClicked)
                                {
                                    penClicked = true
                                    cloneClicked = false
                                }

                                Spacer(modifier = Modifier.width(16.dp)) // Add space between the buttons

                                clonetoolicon(isClicked = cloneClicked)
                                {
                                    cloneClicked = true
                                    penClicked = false
                                }
                            }
                        }
                    }
                }
            }
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
fun Clonetoool() {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    val selectedArea = remember { mutableStateOf<android.graphics.Rect?>(null) }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            // Handle tap to select area
                            if (bitmap.value != null) {
                                val scaleFactor = bitmap.value!!.width.toFloat() / size.width
                                val x = (offset.x * scaleFactor).toInt()
                                val y = (offset.y * scaleFactor).toInt()

                                // Define the size of the selected area
                                val selectedSize = 100

                                // Calculate the bounding box of the selected area
                                val left = max(0, x - selectedSize / 2)
                                val top = max(0, y - selectedSize / 2)
                                val right = min(bitmap.value!!.width, x + selectedSize / 2)
                                val bottom = min(bitmap.value!!.height, y + selectedSize / 2)

                                selectedArea.value = android.graphics.Rect(left, top, right, bottom)
                            }
                        }
                    }
            ) {
                // Draw the image
                imageUri?.let {
                    if (Build.VERSION.SDK_INT < 28) {
                        bitmap.value = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                    } else {
                        val source = ImageDecoder.createSource(context.contentResolver, it)
                        bitmap.value = ImageDecoder.decodeBitmap(source)
                    }

                    bitmap.value?.let { btm ->
                        Image(
                            bitmap = btm.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                        )

                        // Draw the selected area if it exists
                        selectedArea.value?.let { rect ->
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawRect(
                                    color = Color.Transparent,
                                    topLeft = Offset(rect.left.toFloat(), rect.top.toFloat()),
                                    size = Size(rect.width().toFloat(), rect.height().toFloat()),
                                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.background(Color.Black)
            ) {
                Text(text = "Pick Image", color = Color.Yellow)
            }
        }
    }
}





@Composable
fun DrawingScreen() {
    val lines = remember {
        mutableStateListOf<Line>()
    }

    var opacity by remember { mutableFloatStateOf(1.0f) } // Opacity value (from 0.0 to 1.0)

    var useropacity by remember { mutableFloatStateOf(0.5f) }
    var usersize by remember { mutableStateOf(0.5f) }


    var red by remember { mutableStateOf(0f) }
    var green by remember { mutableStateOf(0f) }
    var blue by remember { mutableStateOf(0f) }


    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    var sliderVisible by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableStateOf(0.0f) }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }



    Column(

        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        ){
            //Text(text = "koko")
        }

        Row{
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) {

                    imageUri?.let {
                        if (Build.VERSION.SDK_INT < 28) {
                            bitmap.value = MediaStore.Images
                                .Media.getBitmap(context.contentResolver, it)
                        } else {
                            val source = ImageDecoder.createSource(context.contentResolver, it)
                            bitmap.value = ImageDecoder.decodeBitmap(source)
                        }

                        bitmap.value?.let { btm ->

                            Box(){
                                Image(
                                    bitmap = btm.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()


                                )

                                Canvas(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .border(BorderStroke(1.dp, Color.Black))
                                        .pointerInput(true) {
                                            detectDragGestures { change, dragAmount ->
                                                change.consume()

                                                val canvasSize = size.toSize()

                                                // Calculate the start and end points of the line
                                                val start = change.position - dragAmount
                                                val end = change.position


                                                val adjustedStart = adjustToBounds(start, canvasSize)
                                                val adjustedEnd = adjustToBounds(end, canvasSize)

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

                                                lines.add(line)
                                            }
                                        }
                                )

                                {
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



                }

            }
        }

        Row{

            Button(
                onClick = {
                    launcher.launch("image/*")
                },
                modifier = Modifier.background(Color.Black)

            ) {
                Text(text = "Pick Image", color = Color.Yellow)

            }

        }


        Row(
            modifier = Modifier.fillMaxWidth()
        )
        {
            Button(
                onClick = {
                    sliderVisible = true // Show slider
                    sliderValue = 0.5f
                }
            )
            {
                Text(text = "Pen", color = Color.Yellow)
            }

            Spacer(modifier = Modifier.width(5.dp))

            Button(onClick = {
                sliderVisible = true
                sliderValue = 0.2f
            },
                modifier = Modifier.background(Color.Black)
            ) {
                Text(text = "Highlighter", color = Color.Yellow)
            }

            Spacer(modifier = Modifier.width(5.dp))

            Button(onClick = {
                sliderVisible = true
                sliderValue = 1.0f
            },
                modifier = Modifier.background(Color.Black)
            ) {
                Text(text = "Marker", color = Color.Yellow)
            }
        }



        Row(
            modifier = Modifier.fillMaxWidth()
        )
        {
            Text(text = "Opacity:", color = Color.White)
            if(sliderVisible) {

                if(sliderValue == 0.5f) {
                    Slider(
                        value = useropacity,
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
            Text(text = "Size:", color = Color.White)

            Slider(
                value = usersize,
                onValueChange = { usersize = it },
                modifier = Modifier
                    .width(400.dp)
                    .height(20.dp),
                valueRange = 1.0f..10.0f
            )
        }

        Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                ) {
            Column(
                modifier = Modifier.padding(5.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                Row {
                    Text(text = "Red:", color = Color.White)



                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .padding(2.dp)

                            .background(Color(red = red, green = green, blue = blue))
                    )
                }



                Slider(

                    value = red,
                    onValueChange = { red = it },
                    modifier = Modifier
                        .width(400.dp)
                        .height(20.dp),
                    valueRange = 0f..1f
                )
                Text(text = "Green:", color = Color.White)

                Slider(
                    value = green,
                    onValueChange = { green = it },
                    modifier = Modifier
                        .width(400.dp)
                        .height(20.dp),
                    valueRange = 0f..1f
                )
                Text(text = "Blue:", color = Color.White)

                Slider(
                    value = blue,
                    onValueChange = { blue = it },
                    modifier = Modifier
                        .width(400.dp)
                        .height(20.dp),
                    valueRange = 0f..1f
                )
            }
        }
    }



}



/**
 * Adjusts the given point to stay within the specified bounds.
 *
 * @param point The point to adjust.
 * @param bounds The bounds to constrain the point to.
 * @return The adjusted point.
 */

// Ensure the start and end points stay within the canvas bounds
private fun adjustToBounds(point: Offset, bounds: Size): Offset {
    val x = point.x.coerceIn(0f, bounds.width)
    val y = point.y.coerceIn(0f, bounds.height)
    return Offset(x, y)
}




data class Line(
    val start: Offset,
    val end: Offset,
    val color: Color,
    val strokeWidth: Dp,
    val opacity: Float
)




@Composable
fun CloneTool() {
    val imageBitmap = ImageBitmap.imageResource(id = R.drawable.undo1)
    var selectionStart by remember { mutableStateOf<Offset?>(null) }
    var selectionEnd by remember { mutableStateOf<Offset?>(null) }
    var brushBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val clonePositions = remember { mutableStateListOf<Offset>() }
    // State to handle confirmation dialog visibility
    var showConfirmationDialog by remember { mutableStateOf(false) }
    // Temporary bitmap to store the selected area before confirmation
    var tempBrushBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showSelectionButton by remember { mutableStateOf(true) }
    var isSelecting by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            bitmap = imageBitmap,
            contentDescription = "Editable Image",
            modifier = Modifier.size(450.dp)
        )

        SelectableImageView(
            onStartSelection = { offset -> selectionStart = offset },
            onSelectionChange = { offset -> selectionEnd = offset },
            onSelectionEnd = { start, end ->
                tempBrushBitmap = createBitmapFromSelection(imageBitmap, start, end)
                showConfirmationDialog = true
            },
            selectionStart = selectionStart,
            selectionEnd = selectionEnd
        )

        // Show confirmation dialog when the user has completed a selection
        if (showSelectionButton) {
            Button(onClick = {
                isSelecting = true
                showSelectionButton = false // Hide the button when selection starts
            },
                modifier = Modifier.background(Color.Black)
            ) {
                Text("Start Selection", color = Color.White)
            }
        }

        if (showConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                title = { Text("Confirm Selection", color = Color.White) },
                text = { Text("Do you want to use the selected area?", color = Color.White) },
                confirmButton = {
                    Button(onClick = {
                        brushBitmap = tempBrushBitmap // Confirm the temporary bitmap
                        tempBrushBitmap = null // Clear the temporary bitmap
                        showConfirmationDialog = false
                        isSelecting = false // Stop selection mode
                        showSelectionButton = true // Show the selection button again
                    },
                        modifier = Modifier.background(Color.Black)
                    ) {
                        Text("OK", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        tempBrushBitmap = null // Clear the temporary bitmap
                        showConfirmationDialog = false
                        isSelecting = false // Stop selection mode
                        showSelectionButton = true // Show the selection button again
                    },
                        modifier = Modifier.background(Color.Black)
                    ) {
                        Text("No", color = Color.White)
                    }
                }
            )
        }

        // Tap to place the brush bitmap onto the canvas
        Modifier.pointerInput(brushBitmap != null) {
            detectTapGestures { offset ->
                brushBitmap?.let {
                    clonePositions.add(offset)
                }
            }
        }
        if (!isSelecting && brushBitmap != null) {
            Modifier.pointerInput(true) {
                detectTapGestures { offset ->
                    clonePositions.add(offset)
                    showSelectionButton = true // Show the selection button again after pasting
                }
            }
        }

        // Draw the cloned areas on the image
        clonePositions.forEach { position ->
            brushBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Cloned Area",
                    modifier = Modifier
                        .offset { IntOffset(position.x.toInt() - bitmap.width / 2, position.y.toInt() - bitmap.height / 2) }
                )
            }
        }
    }
}


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