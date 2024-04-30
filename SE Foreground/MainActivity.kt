package com.example.fgchange

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.File
import java.net.URL
import java.util.concurrent.TimeUnit
import androidx.core.net.toUri
import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.toArgb
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.rememberScrollState


class MainActivity : ComponentActivity() {
    private var image: Uri? = null
    private var displayed by mutableStateOf<Bitmap?>(null)
    private var original by mutableStateOf<Bitmap?>(null)
    private var initial by mutableStateOf<Bitmap?>(null)
    private var selectedColor by mutableStateOf(Color.Black)
    private var inimg: Bitmap? = null
    private var finimg: Bitmap? = null
    private val colors = listOf(
        Color.Red, Color.Green, Color.Blue, Color.Magenta, Color.Yellow,
        Color.Cyan, Color.White, Color.Gray, Color.DarkGray, Color(0xFFA52A2A),
        Color(0xFF008B8B), Color(0xFFB8860B), Color(0xFF8B4513), Color(0xFF556B2F),
        Color(0xFF4682B4), Color(0xFFDC143C),  Color(0xFF800080),
        Color(0xFF2F4F4F), Color(0xFF8B0000), Color(0xFF2E8B57), Color(0xFFD2691E)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Layout()
        }
    }

    @Composable
    fun Layout() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(0.dp))



                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .background(Color.Black),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Foreground Color Changer",
                        color = Color(android.graphics.Color.parseColor("#F9C706")),
                        modifier = Modifier.padding(top = 8.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 25.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onUploadImageClick() }
                ) {
                    Text(text = "Upload Image", fontSize = 18.sp)
                }

                Box(
                    modifier = Modifier
                        .size(520.dp)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    displayed?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }


                Spacer(modifier = Modifier.height(0.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                            .clickable { displayed = initial },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_none),
                            contentDescription = "Remove Background",
                            modifier = Modifier.size(22.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }


                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .horizontalScroll(rememberScrollState())
                        .background(Color.Black),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom

                ) {
                    Box(
                        modifier = Modifier
                            .size(0.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                            .clickable { displayed = initial },
                        contentAlignment = Alignment.Center
                    )
                    {

                    }


                    eachButton(color = android.graphics.Color.RED) { applyColor(it) }
                    eachButton(color = android.graphics.Color.GREEN) { applyColor(it) }
                    eachButton(color = android.graphics.Color.BLUE) {applyColor(it) }
                    eachButton(color = android.graphics.Color.YELLOW) { applyColor(it) }
                    eachButton(color = android.graphics.Color.WHITE) { applyColor(it) }
                    eachButton(color = android.graphics.Color.CYAN) {applyColor(it) }
                    eachButton(color = android.graphics.Color.DKGRAY) { applyColor(it) }
                    eachButton(color = android.graphics.Color.GRAY) {applyColor(it) }
                    eachButton(color = android.graphics.Color.LTGRAY) {applyColor(it) }
                    eachButton(color = android.graphics.Color.MAGENTA) { applyColor(it) }
                    eachButton(color = android.graphics.Color.parseColor("#D11799")) { applyColor(it)}
                    eachButton(color = android.graphics.Color.parseColor("#999B84")) {applyColor(it) }
                    eachButton(color = android.graphics.Color.parseColor("#A8ABE0")) { applyColor(it) }
                    eachButton(color = android.graphics.Color.parseColor("#BADA55")) { applyColor(it)}
                    eachButton(color = android.graphics.Color.parseColor("#F6546A")) {applyColor(it) }
                    eachButton(color = android.graphics.Color.parseColor("#5D9FA0")) { applyColor(it) }
                }

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
                                selectedColor = Color.Black
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
                                saveImage()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Save Image",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }



    @Composable
    fun eachButton(color: Int, onClick: (Color) -> Unit) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .padding(4.dp)
                .background(color = Color(color), shape = RoundedCornerShape(8.dp))
                .clickable { onClick(Color(color)) },
            contentAlignment = Alignment.Center
        ) {

        }
    }


    @Composable
    fun CrossIcon() {
        Canvas(modifier = Modifier.size(24.dp)) {
            drawLine(
                color = Color.White,
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height),
                strokeWidth = 2f
            )
            drawLine(
                color = Color.White,
                start = Offset(0f, size.height),
                end = Offset(size.width, 0f),
                strokeWidth = 2f
            )
        }
    }

    @Composable
    fun TickIcon() {
        Canvas(modifier = Modifier.size(24.dp)) {
            drawArc(
                color = Color.White,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = 2f),
                topLeft = Offset(0f, 0f)
            )
        }
    }

    @Composable
    fun showBoxTick() {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.Black)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Green)
            )
        }
    }

    @Composable
    fun showBoxCross() {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.Black)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.Red)
            )
        }
    }

    private fun onUploadImageClick() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, 1)
    }


    private fun applyColor(color: Color) {
        finimg?.let { bitmap ->
            changeForegroundColor(bitmap, color)
            selectedColor = color
            val scaleX = original!!.width.toFloat() / finimg!!.width.toFloat()
            val scaleY = original!!.height.toFloat() / finimg!!.height.toFloat()
            if (scaleX < 1 || scaleY < 1) {
                val scale = minOf(scaleX, scaleY)
                finimg = Bitmap.createScaledBitmap(finimg!!, (finimg!!.width * scale).toInt(), (finimg!!.height * scale).toInt(), true)
            } else {
                val scaleX = original!!.width.toFloat() / finimg!!.width.toFloat()
                val scaleY = original!!.height.toFloat() / finimg!!.height.toFloat()
                finimg = Bitmap.createScaledBitmap(finimg!!, (finimg!!.width * scaleX).toInt(), (finimg!!.height * scaleY).toInt(), true)
            }
            combineImages()
        }
    }

    private fun changeForegroundColor(bitmap: Bitmap, color: Color) {
        val newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val width = newBitmap.width
        val height = newBitmap.height
        val pixels = IntArray(width * height)
        newBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            if (isForegroundPixel(pixels[i])) {
                pixels[i] = color.toArgb()
            }
        }

        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)

        finimg = newBitmap
    }

    private fun isForegroundPixel(pixel: Int): Boolean {
        val alpha = android.graphics.Color.alpha(pixel)
        val red = android.graphics.Color.red(pixel)
        val green = android.graphics.Color.green(pixel)
        val blue = android.graphics.Color.blue(pixel)
        return alpha > 0 && (red > 128 || green > 128 || blue > 128)
    }


    /*private fun changeImageColor(bitmap: Bitmap, color: Color) {
        val newBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val width = newBitmap.width
        val height = newBitmap.height
        val pixels = IntArray(width * height)
        newBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val alpha = android.graphics.Color.alpha(pixels[i])
            if (alpha > 0) { // Check if the pixel is not fully transparent
                pixels[i] = color.toArgb() or (alpha shl 24)
            }
        }

        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        displayed = newBitmap
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK && data!= null) {
            image = data.data
            getImage()
        }
    }

    private fun getImage() {
        image?.let { uri ->
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            displayed = bitmap
            original = bitmap
            initial = bitmap
            inimg=bitmap
            detectBackground()
        }
    }

    private fun detectBackground() {
        original?.let { bitmap ->
            val temp = File.createTempFile("temp", ".jpeg", cacheDir)
            bitmap.writeBitmap(temp)

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val removed = removeBackgroundAPI(temp)
                    val newBitmap = BitmapFactory.decodeByteArray(removed, 0, removed.size)
                    withContext(Dispatchers.Main) {
                        finimg=newBitmap
                        displayed = newBitmap
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    temp.delete()
                }
            }
        }
    }

    private fun Bitmap.writeBitmap(file: File) {
        val output = file.outputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 100, output)
        output.flush()
        output.close()
    }

    private suspend fun removeBackgroundAPI(file: File): ByteArray {
        val apiKey = "Vopq52Tjc9tc5yCuzTXxARqQ"
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
        val body = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("image_file", "image.jpg", RequestBody.create("image/jpeg".toMediaType(), file)).build()
        val request = Request.Builder().url("https://api.remove.bg/v1.0/removebg").addHeader("X-Api-Key", apiKey).post(body).build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception("Failure in removal: ${response.message}")
        }
        val type = response.header("Content-Type")


        if (type?.startsWith("application/json") == true) {
            val json = JSONObject(response.body?.string()?: "")
            val image = json.getJSONObject("data").getString("result")
            return URL(image).readBytes()
        } else if (type?.startsWith("image/png") == true || type?.startsWith("image/jpeg")==true)  {
            return response.body?.bytes()?: throw Exception("Empty")
        } else {
            throw Exception("Unexpected response type: $type")
        }
    }

    private fun saveImage() {
        displayed?.let { bitmap ->
            val file = File(cacheDir, "image_next.jpg")
            bitmap.writeBitmap(file)
            val intent = Intent().apply {
                putExtra("image", file.toUri().toString())
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
    private fun combineImages() {
        inimg?.let { inBitmap ->
            finimg?.let { finBitmap ->
                val combinedBitmap = Bitmap.createBitmap(inBitmap.width, inBitmap.height, Bitmap.Config.ARGB_8888)
                val canvas = android.graphics.Canvas(combinedBitmap)
                canvas.drawBitmap(inBitmap, 0f, 0f, null) // draw background from inimg

                val left = (inBitmap.width - finBitmap.width) / 2f
                val top = (inBitmap.height - finBitmap.height) / 2f
                canvas.drawBitmap(finBitmap, left, top, null) // draw foreground from finimg with scaling and centering
                displayed = combinedBitmap
            }
        }
    }

}


