package com.example.imageeditor

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.util.LinkedList
import java.util.Queue
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode

class SC_polygoSelection : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val byteArray = intent?.getByteArrayExtra("image")
            val bitmap = byteArray?.let { BitmapFactory.decodeByteArray(it, 0, 150000) }

            if (bitmap != null) {

                val imageBitmap = bitmap.asImageBitmap()
                //Polygon Crop
                PolygonCropWrapper(imageBitmap = imageBitmap)
            }
        }
    }


    private fun returnToMain(bitmap: Bitmap?) {
        bitmap?.let {
            val file = File(cacheDir, "image_next.jpg")
            it.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))
            val intent = Intent().apply {
                putExtra("imageUri", file.toUri().toString())
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }


    @Composable
    fun PolygonCropWrapper(imageBitmap: ImageBitmap) {
        val scope = rememberCoroutineScope()
        val points = remember { mutableStateListOf<Offset>() }
        val bitmapPoints = remember { mutableStateListOf<Offset>() }
        var croppedImageBitmap = remember { mutableStateOf(imageBitmap) }
        var flag = true  // Editing mode flag

        Column(modifier = Modifier.fillMaxSize().background(Color.DarkGray)) {
            Row(
                modifier = Modifier.fillMaxHeight(0.1f).fillMaxWidth().background(Color.Black).padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        returnToMain(polygonCrop(imageBitmap, bitmapPoints).asAndroidBitmap())
                    }) {
                    Text("Save")
                }

                Button(onClick = {
                    scope.launch {
                        croppedImageBitmap.value = polygonCrop(imageBitmap, bitmapPoints)
                        flag = false
                    }
                }) {
                    Text("Crop")
                }
            }

            Box(
                modifier = Modifier.weight(1f).fillMaxSize().background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = croppedImageBitmap.value,
                    contentDescription = "Cropped Image",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.matchParentSize()
                )

                Canvas(modifier = Modifier.matchParentSize().pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // Ensure proper scaling between touch points and bitmap coordinates
                        val scaleX = imageBitmap.width.toFloat() / size.width
                        val scaleY = imageBitmap.height.toFloat() / size.height
                        val adjustedOffset = Offset(offset.x * scaleX, offset.y * scaleY)
                        points.add(offset)
                        if (flag) bitmapPoints.add(adjustedOffset)
                        Log.d("DebugPoints", "Screen: $offset, Bitmap: $adjustedOffset")
                    }
                }) {
                    if (points.isNotEmpty()) {
                        drawPath(
                            path = Path().apply {
                                moveTo(points.first().x, points.first().y)
                                points.forEach { lineTo(it.x, it.y) }
                                close()
                            },
                            color = Color.Red,
                            style = Stroke(width = 5f)
                        )
                    }
                }
            }
        }
    }

    private fun polygonCrop (
        imageBitmap: ImageBitmap,
        points: SnapshotStateList<Offset>
    ): ImageBitmap {
        if (points.size < 3)
            return imageBitmap

        var androidBitmap: Bitmap =
            imageBitmap.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, true)

        var tempList = mutableListOf<Pair<Int, Int>>()
        for (i in 0..<points.size) {
            var xSt: Float = points[i].x
            var ySt: Float = points[i].y
            var xEnd: Float
            var yEnd: Float

            if (i == points.size - 1) {
                xEnd = points[0].x
                yEnd = points[0].y
            } else {
                xEnd = points[i + 1].x
                yEnd = points[i + 1].y
            }

            tempList = (tempList + bresenhamLine(
                xSt.toInt(),
                ySt.toInt(),
                xEnd.toInt(),
                yEnd.toInt()
            )).toMutableList()
        }

        for (point in points) {
            androidBitmap.setPixel(
                point.x.toInt(),
                point.y.toInt(),
                android.graphics.Color.TRANSPARENT
            )
        }

        for (point in tempList) {
            androidBitmap.setPixel(point.first, point.second, android.graphics.Color.TRANSPARENT)
        }

        var minX = tempList[0].first
        var minY = tempList[0].second
        var maxX = tempList[0].first
        var maxY = tempList[0].second

        for (point in tempList) {
            if (point.first < minX) {
                minX = point.first
            }
            if (point.second < minY) {
                minY = point.second
            }
            if (point.first > maxX) {
                maxX = point.first
            }
            if (point.second > maxY) {
                maxY = point.second
            }
        }


        println("Corner points are: ${points.toList()}")
        println("Intermediate points are: ${tempList.toList()}")
        println("Start ${tempList[0]} and End ${tempList[tempList.size - 1]}")

        var croppedBitmap =
            Bitmap.createBitmap(androidBitmap, minX, minY, (maxX - minX + 1), (maxY - minY + 1))

        //Flood outside pixels
        croppedBitmap = addBorder(croppedBitmap, 1)
        val newImageBitmap: Bitmap = floodFill(croppedBitmap, 0, 0)

        return newImageBitmap.asImageBitmap()
    }

    fun bresenhamLine(x0: Int, y0: Int, x1: Int, y1: Int): List<Pair<Int, Int>> {
        val points = mutableListOf<Pair<Int, Int>>()
        var x = x0
        var y = y0
        val dx = kotlin.math.abs(x1 - x0)
        val dy = kotlin.math.abs(y1 - y0)
        val sx = if (x0 < x1) 1 else -1
        val sy = if (y0 < y1) 1 else -1
        var err = (if (dx > dy) dx else -dy) / 2
        var e2: Int

        while (true) {
            points.add(Pair(x, y))

            if (x == x1 && y == y1) break

            e2 = err
            if (e2 > -dx) {
                err -= dy
                x += sx
            }
            if (e2 < dy) {
                err += dx
                y += sy
            }
        }

        return points
    }

    fun floodFill(bitmap: Bitmap, x: Int, y: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)


        val queue: Queue<Pair<Int, Int>> = LinkedList()
        queue.add(Pair(x, y))

        while (queue.isNotEmpty()) {
            val pair = queue.remove()
            val px = pair.first
            val py = pair.second

            if (pixels[py * width + px] != android.graphics.Color.TRANSPARENT) {
                /*if(pairs.contains(Pair(px, py/width)))
            {
                println("Hello")
            }*/

                pixels[py * width + px] = android.graphics.Color.TRANSPARENT

                //println("Painting ${px},${py}")
                if (px + 1 < width) queue.add(Pair(px + 1, py))
                if (px - 1 >= 0) queue.add(Pair(px - 1, py))
                if (py + 1 < height) queue.add(Pair(px, py + 1))
                if (py - 1 >= 0) queue.add(Pair(px, py - 1))
            }
        }

        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }

    private fun addBorder(bmp: Bitmap, borderSize: Int): Bitmap {
        val bmpWithBorder = Bitmap.createBitmap(
            bmp.getWidth() + borderSize * 2,
            bmp.getHeight() + borderSize * 2,
            bmp.getConfig()
        )
        val canvas = android.graphics.Canvas(bmpWithBorder)
        canvas.drawColor(android.graphics.Color.YELLOW)
        canvas.drawBitmap(bmp, borderSize.toFloat(), borderSize.toFloat(), null)
        return bmpWithBorder
    }

}
