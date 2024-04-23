package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import android.app.Activity
import androidx.activity.compose.setContent
import android.content.Context
import android.net.Uri
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

class Middle_action : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uriString = intent.getStringExtra("cropped_image_uri")


                    val croppedImageUri = uriString?.let { Uri.parse(it) }

                    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

                    GAlS { updatedImageUri ->
                        selectedImageUri = updatedImageUri
                    }

                    Row (modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom)
                    {

                        Button(modifier = Modifier
                            .background(Color.Transparent)
                            .padding(16.dp),
                            colors = ButtonDefaults.buttonColors(Color.Yellow),
                            onClick = {

                            // Send both URIs to another activity
                            val intent = Intent(this@Middle_action, stickerAndImage::class.java).apply {
                                putExtra("crop_image_uri", croppedImageUri.toString())
                                putExtra("selected_image_uri", selectedImageUri.toString())
                            }
                            startActivity(intent)

                        })
                        {
                            Text(text = "Press to save without background image\nOr press Camera Icon above for background")
                        }


                    }


                    //Greeting2("Android")
                }
            }
        }
    }



    @Composable
    fun GAlS(onImageUriChanged: (Uri?) -> Unit) {
        //val context = LocalContext.current
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }
        var imageUri by remember { mutableStateOf<Uri?>(null) }

        val galleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri ->
                uri?.let {
                    imageUri = it

                    onImageUriChanged(it) // Invoke the callback with the updated imageUri
                }
            }
        )





        if (imageUri != null) {
            if (Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
            } else {
                val source = ImageDecoder.createSource(this.contentResolver, imageUri!!)
                bitmap = ImageDecoder.decodeBitmap(source)
            }
        }


        Column(        ////selection icon
            horizontalAlignment = Alignment.CenterHorizontally,
            //verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(text = "Press Icon to Pick \nBackground Image:")

                Image(
                    painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(RectangleShape)
                        .background(Color.Gray)
                        .size(50.dp)
                        .padding(vertical = 10.dp)
                        .clickable {

                            galleryLauncher.launch("image/*")
                        }

                )

            }


        }

        Column( ////image
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 100.dp)
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap?.asImageBitmap()!!,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .clip(RectangleShape)
                        .background(Color.Blue)
                        .width(300.dp)
                        .height(500.dp)
                        .padding(bottom = 32.dp)


                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.baseline_person_24),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(RectangleShape)
                        .background(Color.Blue)
                        .width(300.dp)
                        .height(700.dp)
                        .padding(bottom = 32.dp)

                )

            }
        }
    }
}