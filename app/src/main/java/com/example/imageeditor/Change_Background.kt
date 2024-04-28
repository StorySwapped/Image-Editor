package com.example.imageeditor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextField
import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
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
import androidx.core.content.FileProvider
import java.io.FileOutputStream

class ChangeBackground : ComponentActivity() {

    private var displayed by mutableStateOf<Bitmap?>(null)
    private var original by mutableStateOf<Bitmap?>(null)
    private var tickConfirmation by mutableStateOf(false)
    private var crossConfirmation by mutableStateOf(false)
    private var value by mutableStateOf("")
    private var error by mutableStateOf(false)
    private var hex_popup by mutableStateOf(false)

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
        var selectedColor by remember { mutableStateOf(android.graphics.Color.TRANSPARENT) }

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

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .background(Color.Black),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Background Color Changer",
                        color = Color(android.graphics.Color.parseColor("#F9C706")),
                        modifier = Modifier.padding(top = 8.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 25.sp,
                        fontFamily = FontFamily(Font(R.font.sansserif))
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

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

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                            .clickable { displayed = initial },
                        contentAlignment = Alignment.Center
                    )
                    {
                        Image(
                            painter = painterResource(id = R.drawable.undo1),
                            contentDescription = "Remove Background",
                            modifier = Modifier.size(22.dp),
                            contentScale = ContentScale.Fit
                        )
                    }


                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                            .clickable {
                                hex_popup = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.undo1),
                            contentDescription = "Add Value",
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
                            .padding(horizontal = 4.dp)
                            .size(75.dp)
                            .background(color = Color.LightGray, shape = RoundedCornerShape(8.dp))
                            .clickable { onUploadImageClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.undo1),
                            contentDescription = "Upload Image",
                            modifier = Modifier.size(36.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    eachButton(color = android.graphics.Color.RED,isSelected = selectedColor == android.graphics.Color.RED)  { selectedColor = it
                        applyColor(it) }
                    eachButton(color = android.graphics.Color.GREEN,isSelected = selectedColor == android.graphics.Color.GREEN) { selectedColor = it
                        applyColor(it) }
                    eachButton(color = android.graphics.Color.BLUE,isSelected = selectedColor == android.graphics.Color.BLUE) {selectedColor = it
                        applyColor(it) }
                    eachButton(color = android.graphics.Color.YELLOW,isSelected = selectedColor == android.graphics.Color.YELLOW) {selectedColor = it
                        applyColor(it) }
                    eachButton(color = android.graphics.Color.WHITE,isSelected = selectedColor == android.graphics.Color.WHITE) { selectedColor = it
                        applyColor(it) }
                    eachButton(color = android.graphics.Color.BLACK,isSelected = selectedColor == android.graphics.Color.BLACK) { selectedColor = it
                        applyColor(it) }
                    eachButton(color = android.graphics.Color.CYAN,isSelected = selectedColor == android.graphics.Color.CYAN) {selectedColor = it
                        applyColor(it) }
                    eachButton(color = android.graphics.Color.DKGRAY,isSelected = selectedColor == android.graphics.Color.DKGRAY) { selectedColor = it
                        applyColor(it) }
                    eachButton(color = android.graphics.Color.GRAY,isSelected = selectedColor == android.graphics.Color.GRAY) {selectedColor = it
                        applyColor(it)}
                    eachButton(color = android.graphics.Color.LTGRAY,isSelected = selectedColor == android.graphics.Color.LTGRAY) {selectedColor = it
                        applyColor(it) }
                    eachButton(color = android.graphics.Color.MAGENTA,isSelected = selectedColor == android.graphics.Color.MAGENTA) { selectedColor = it
                        applyColor(it) }
                    eachButton(color = android.graphics.Color.parseColor("#D11799"),isSelected = selectedColor == android.graphics.Color.parseColor("#D11799")) {selectedColor = it
                        applyColor(it)}
                    eachButton(color = android.graphics.Color.parseColor("#999B84"),isSelected = selectedColor == android.graphics.Color.parseColor("#999B84")) {selectedColor = it
                        applyColor(it) }
                    eachButton(color = android.graphics.Color.parseColor("#A8ABE0"),isSelected = selectedColor == android.graphics.Color.parseColor("#A8ABE0")) { selectedColor = it
                        applyColor(it) }
                    eachButton(color = android.graphics.Color.parseColor("#BADA55"),isSelected = selectedColor == android.graphics.Color.parseColor("#BADA55")) { selectedColor = it
                        applyColor(it)}
                    eachButton(color = android.graphics.Color.parseColor("#F6546A"),isSelected = selectedColor == android.graphics.Color.parseColor("#F6546A")) {selectedColor = it
                        applyColor(it) }
                    eachButton(color = android.graphics.Color.parseColor("#5D9FA0"),isSelected = selectedColor == android.graphics.Color.parseColor("#5D9FA0")) { selectedColor = it
                        applyColor(it) }
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

                if (hex_popup) {
                    AlertDialog(
                        onDismissRequest = {
                            hex_popup = false
                            value = ""
                            error = false
                        },
                        title = {
                            Text(text = "Enter Hex Value")
                        },
                        text = {
                            Column {
                                TextField(
                                    value = value,
                                    onValueChange = { value = it },
                                    label = { Text("Hex Value") }
                                )
                                if (error) {
                                    Text(
                                        text = "Incorrect value",
                                        color = Color.Red,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        },
                        confirmButton = {

                            Button(   onClick = {
                                if (value.length == 7 && value[0] == '#' && value.substring(1).all { it in '0'..'9' || it in 'A'..'F' || it in 'a'..'f' }) {
                                    val color = android.graphics.Color.parseColor(value)
                                    applyColor(color)
                                    hex_popup = false
                                } else {
                                    hex_popup = true
                                    error=true
                                }
                            },colors = ButtonDefaults.buttonColors(
                                containerColor = androidx.compose.ui.graphics.Color.Black
                            )

                            ) {
                                Text("Apply")
                            }
                        }
                    )
                }
            }
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
                        sendtoMain(displayed)
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

    fun onUploadImageClick() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, 1)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = data.data
            if (imageUri != null) {
                changeBackground2(this,imageUri)
            }
        }
    }

    @Composable
    fun eachButton(color: Int, isSelected: Boolean, onClick: (Int) -> Unit) {
        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .size(75.dp)
                .background(color = Color(color), shape = RoundedCornerShape(8.dp))
                .border(width = 5.dp, color = if (isSelected) Color.Yellow else Color.Transparent, shape = RoundedCornerShape(8.dp))
                .clickable { onClick(color) },
            contentAlignment = Alignment.Center
        ) {
        }
    }




    private fun applyColor(color: Int) {
        original?.let {
            changeBackground(it,color)
        }
    }

    private fun changeBackground2(context: Context, imageUri: Uri) {
        val inputStream = context.contentResolver.openInputStream(imageUri) ?: return
        val newBackground = BitmapFactory.decodeStream(inputStream) ?: return

        val originalBitmap = original ?: return
        val newBitmap = Bitmap.createBitmap(originalBitmap.width, originalBitmap.height, Bitmap.Config.ARGB_8888)

        val foreRatio = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()
        val backRatio = newBackground.width.toFloat() / newBackground.height.toFloat()

        val X: Float
        val Y: Float

        if (foreRatio > backRatio) {
            X = originalBitmap.width.toFloat() / newBackground.width.toFloat()
            Y = X
        }
        else {
            Y = originalBitmap.height.toFloat() / newBackground.height.toFloat()
            X = Y
        }
        val transX = (originalBitmap.width - (newBackground.width * X)) / 2
        val transY = (originalBitmap.height - (newBackground.height * Y)) / 2


        val matrix = Matrix()
        matrix.setScale(X, Y)
        matrix.postTranslate(transX, transY)

        val canvas = android.graphics.Canvas(newBitmap)
        canvas.drawBitmap(newBackground, matrix, null)
        canvas.drawBitmap(originalBitmap, 0f, 0f, null)

        displayed = newBitmap
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
        val mat = Matrix()
        mat.postScale(X, Y)
        mat.postTranslate(transX, transY)
        val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(newBitmap)
        canvas.drawBitmap(newBackground, 0f, 0f, null)
        canvas.drawBitmap(bitmap, mat, null)

        displayed= newBitmap
    }
    private fun sendtoMain(bitmap: Bitmap?) {
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


    private fun removeBackgroundAPI(file: File): ByteArray {
        val apiKey = "3Sx9cEMgxSnHjcKPboeehoh7"
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
    Canvas(modifier = Modifier.size(14.dp)) {
        drawLine(
            color = Color.White,
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height),
            strokeWidth = 3f
        )
        drawLine(
            color = Color.White,
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
            .size(25.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Tick",
            tint = Color.White
        )
    }
}