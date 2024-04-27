package com.example.imageeditor

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.File
import java.net.URL
import java.util.concurrent.TimeUnit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.net.toUri
import androidx.compose.ui.graphics.toArgb

class ChangeForeground : ComponentActivity() {
    private var image: Uri? = null
    private var displayed by mutableStateOf<Bitmap?>(null)
    private var original by mutableStateOf<Bitmap?>(null)
    private var initial by mutableStateOf<Bitmap?>(null)
    private var selectedColor by mutableStateOf(Color.Black)
    private val colors = listOf(
        Color.Red, Color.Green, Color.Blue, Color.Magenta, Color.Yellow,
        Color.Cyan, Color.White, Color.Gray, Color.DarkGray, Color(0xFFA52A2A),
        Color(0xFF008B8B), Color(0xFFB8860B), Color(0xFF8B4513), Color(0xFF556B2F),
        Color(0xFF4682B4), Color(0xFFDC143C),  Color(0xFF800080),
        Color(0xFF2F4F4F), Color(0xFF8B0000), Color(0xFF2E8B57), Color(0xFFD2691E)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageUriString = intent.getStringExtra("imageUri")
        image = Uri.parse(imageUriString)
        setContent {
            ObjectColorChangeModule()
        }
    }

    @Composable
    fun ObjectColorChangeModule() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            displayed?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Fit
                )
            }

            Text(
                text = "Select The Colour You Want To Apply",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )

            ColorPalette(selectedColor) { color ->
                selectedColor = color
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = {
                            val galleryIntent =
                                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            startActivityForResult(galleryIntent, 1)
                        }
                    ) {
                        Text(text = "Upload Image", fontSize = 18.sp)
                    }

                    Button(
                        onClick = {
                            displayed?.let { bitmap ->
                                sendtoMain(bitmap)
                            }
                        }
                    ) {
                        Text(text = "Save Image", fontSize = 18.sp)
                    }
                }

                Button(
                    onClick = {
                        displayed?.let { bitmap ->
                            changeImageColor(bitmap, selectedColor)
                        }
                    }
                ) {
                    Text(text = "Apply Color", fontSize = 20.sp)
                }
            }


        }
    }

    @Composable
    fun ColorPalette(selectedColor: Color, onColorSelected: (Color) -> Unit) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val rows = colors.chunked(7)

            for (row in rows) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    for (color in row) {
                        ColorSquare(
                            color = color,
                            isSelected = color == selectedColor,
                            onClick = { onColorSelected(color)
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ColorSquare(color: Color, isSelected: Boolean, onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clickable(onClick = onClick)
                .background(color = color, shape = RoundedCornerShape(4.dp))
                .padding(4.dp)
                .then(
                    if (isSelected) Modifier.background(
                        color = Color.Gray,
                        shape = RoundedCornerShape(4.dp)
                    ) else Modifier
                )
        )
    }

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
        val apiKey = "MKUaHWmuAvjY9jHMP3GmRJU8"
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

    private fun changeImageColor(bitmap: Bitmap, color: Color) {
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
    }

    private fun sendtoMain(bitmap: Bitmap?) {
        bitmap?.let {
            val file = File(cacheDir, "image_next.jpg")
            it.writeBitmap(file)
            val intent = Intent().apply {
                putExtra("image", file.toUri().toString())
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}