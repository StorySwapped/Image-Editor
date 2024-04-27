package com.example.imageeditor

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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


class BasicEditing : ComponentActivity() {
    private var initial by mutableStateOf<Bitmap?>(null)
    private var displayed by mutableStateOf<Bitmap?>(null)
    private var original by mutableStateOf<Bitmap?>(null)

    private var tickConfirmation by mutableStateOf(false)
    private var crossConfirmation by mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageUriString = intent.getStringExtra("imageUri")
        val imageUri = Uri.parse(imageUriString)
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
                    original = bitmap
                    displayed = bitmap
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
            .fillMaxHeight(1f)
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
                .padding(top = 12.dp)
                .background(Color.Black),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Basic Editing",
                color = Color(android.graphics.Color.parseColor("#F9C706")),
                modifier = Modifier.padding(top = 8.dp),
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
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }

    @Composable
    fun EditingOptions(){

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
                        sendtoMain(displayed)
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
}



