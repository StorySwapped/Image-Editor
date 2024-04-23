package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.net.toUri
import androidx.compose.ui.unit.sp
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.File
import java.net.URL
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font

class MainActivity2 : ComponentActivity() {

    private var displayed by mutableStateOf<Bitmap?>(null)
    private var original by mutableStateOf<Bitmap?>(null)
    private var initial by mutableStateOf<Bitmap?>(null)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageUriString = intent.getStringExtra("imageUri")
        val imageUri = Uri.parse(imageUriString)
        setContent {
            Layout()
        }

        getImage(imageUri)
    }

    @Composable
    fun Layout() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Background Color Changer",
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp,
                    fontFamily = FontFamily(Font(R.font.sansserif))
                )
            }

            Box(
                modifier = Modifier.weight(1f),
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

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 16.dp)
                ) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.wrapContentSize()
                    ) {
                        eachButton(color = android.graphics.Color.RED) { applyColor(it) }
                        eachButton(color = android.graphics.Color.GREEN) { applyColor(it) }
                        eachButton(color = android.graphics.Color.BLUE) {applyColor(it) }
                        eachButton(color = android.graphics.Color.YELLOW) { applyColor(it) }
                        eachButton(color = android.graphics.Color.WHITE) { applyColor(it) }
                        eachButton(color = android.graphics.Color.BLACK) { applyColor(it) }
                        eachButton(color = android.graphics.Color.CYAN) {applyColor(it) }
                        eachButton(color = android.graphics.Color.DKGRAY) { applyColor(it) }
                        eachButton(color = android.graphics.Color.GRAY) { applyColor(it)}
                        eachButton(color = android.graphics.Color.LTGRAY) {applyColor(it) }
                        eachButton(color = android.graphics.Color.MAGENTA) { applyColor(it) }
                        eachButton(color = android.graphics.Color.parseColor("#D11799")) { applyColor(it)}
                        eachButton(color = android.graphics.Color.parseColor("#999B84")) {applyColor(it) }
                        eachButton(color = android.graphics.Color.parseColor("#A8ABE0")) { applyColor(it) }
                        eachButton(color = android.graphics.Color.parseColor("#BADA55")) { applyColor(it)}
                        eachButton(color = android.graphics.Color.parseColor("#F6546A")) {applyColor(it) }
                        eachButton(color = android.graphics.Color.parseColor("#5D9FA0")) { applyColor(it) }

                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable {
                            sendtoMain(initial)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    CrossIcon()
                }

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable {
                            sendtoMain(displayed)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    TickIcon()
                }
            }
        }
    }


    @Composable
    fun eachButton(color: Int, onClick: (Int) -> Unit) {
        Box(modifier = Modifier.padding(horizontal = 4.dp).size(50.dp).background(color = Color(color)).clickable { onClick(color) }, contentAlignment = Alignment.Center) {
        }
    }



    private fun applyColor(color: Int) {
        original?.let {
            changeBackground(it,color)
        }
    }


    private fun changeBackground(bitmap: Bitmap, color: Int){
        val X: Float
        val Y: Float

        val newBackground = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        newBackground.eraseColor(color)

        val foreRatio= bitmap.width.toFloat() / bitmap.height.toFloat()
        val backRatio = newBackground.width.toFloat() / newBackground.height.toFloat()

        if (foreRatio > backRatio) {
            X = bitmap.width.toFloat() / newBackground.width.toFloat()
            Y = X
        } else {
            Y = bitmap.height.toFloat() / newBackground.height.toFloat()
            X = Y
        }

        val transX = (bitmap.width - (newBackground.width * X)) / 2
        val transY = (bitmap.height - (newBackground.height * Y)) / 2
        val mat = android.graphics.Matrix()
        mat.postScale(X, Y)
        mat.postTranslate(transX, transY)
        val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(newBitmap)
        canvas.drawBitmap(newBackground, 0f, 0f, null)
        canvas.drawBitmap(bitmap, mat, null)

        displayed= newBitmap
    }
    private fun sendtoMain(bitmap: Bitmap?){
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

    private fun getImage(imageUri: Uri) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val inputStream = contentResolver.openInputStream(imageUri)
                val bitmap = BitmapFactory.decodeStream(inputStream)

                withContext(Dispatchers.Main) {
                    original = bitmap
                    displayed = bitmap
                    initial = bitmap
                    println("Calling detectBackground()")
                    detectBackground()

                }

                inputStream?.close()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun detectBackground() {
        println("detectBackground function called")
        original?.let { bitmap ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val temp = File.createTempFile("temp", ".jpeg", cacheDir)
                    bitmap.writeBitmap(temp)
                    val removed = removeBackgroundAPI(temp)
                    val newBitmap = BitmapFactory.decodeByteArray(removed, 0, removed.size)
                    withContext(Dispatchers.Main) {
                        original = newBitmap
                    }
                    temp.delete()
                } catch (error: Exception) {
                    error.printStackTrace()
                    println("Error in detectBackground: ${error.message}")
                }
            }
        }
    }




    private fun Bitmap.writeBitmap(file: File) {
        val output= file.outputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 100, output)
        output.flush()
        output.close()
    }


    private suspend fun removeBackgroundAPI(file: File): ByteArray {
        val apiKey = "2NzQeeVoTGujyqH4wr6dh5Ay"
        val client = OkHttpClient()
        val body = MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("image_file", "image.jpg", RequestBody.create("image/jpeg".toMediaType(), file)).build()
        val request = Request.Builder().url("https://api.remove.bg/v1.0/removebg").addHeader("X-Api-Key", apiKey).post(body).build()
        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception("Failure in removal: ${response.message}")
        }
        val type = response.header("Content-Type")
        if (type?.startsWith("application/json") == true) {
            val json = JSONObject(response.body?.string() ?: "")
            val image = json.getJSONObject("data").getString("result")
            return URL(image).readBytes()
        } else if (type?.startsWith("image/png") == true || type?.startsWith("image/jpeg")==true)  {
            return response.body?.bytes() ?: throw Exception("Empty")
        } else {
            throw Exception("Unexpected response type: $type")

        }

    }

}
@Composable
fun CrossIcon() {
    Canvas(modifier = Modifier.size(24.dp)) {
        drawLine(
            color = Color.Black,
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height),
            strokeWidth = 3f
        )
        drawLine(
            color = Color.Black,
            start = Offset(0f, size.height),
            end = Offset(size.width, 0f),
            strokeWidth = 3f
        )
    }
}
@Composable
fun TickIcon(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Tick",
            tint = Color.Black
        )
    }
}