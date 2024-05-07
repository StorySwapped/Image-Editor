package com.example.imageeditor

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
import com.example.imageeditor.ui.theme.ImageEditorTheme
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class SC_Middle_action : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            setContent {
                ImageEditorTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        MainScreen()
                    }
                }

            }
        }
    }

    @Composable
    fun MainScreen() {
        Image(
            painter = painterResource(id = R.drawable.back17),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        val uriString = intent.getStringExtra("cropped_image_uri")
        val croppedImageUri = uriString?.let { Uri.parse(it) }
        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

        GAlS { updatedImageUri ->
            selectedImageUri = updatedImageUri
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        )
        {

            Button(modifier = Modifier
                .padding(16.dp)
                .background(
                    Color(android.graphics.Color.parseColor("#281340")),
                    shape = RoundedCornerShape(5.dp)
                ),
                onClick = {

                    // Send both URIs to another activity
                    val intent =
                        Intent(this@SC_Middle_action, SC_stickerAndImage::class.java).apply {
                            putExtra("crop_image_uri", croppedImageUri.toString())
                            putExtra("selected_image_uri", selectedImageUri.toString())
                        }
                    startActivity(intent)

                })

            {
                Text(
                    text = "Sticker Image",
                    color = Color(android.graphics.Color.parseColor("#D0D0D0")),
                    modifier = Modifier.padding(top = 2.dp, bottom = 2.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    //backgroundcolor = #281340
                    //shape = RoundedCornerShape(15.dp)
                    //fontFamily = FontFamily(Font(R.font.sansserif))
                )
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
        Image(
            painter = painterResource(id = R.drawable.back17), // Replace R.drawable.background_image with your image resource
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )


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

                Text(
                    text = "Pick Background Image ",
                    color = Color(android.graphics.Color.parseColor("#D0D0D0")),
                    modifier = Modifier.padding(top = 8.dp, bottom = 15.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    //fontFamily = FontFamily(Font(R.font.sansserif))
                )

                Image(
                    painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                    contentDescription = null,
                    modifier = Modifier
                        .clip(RectangleShape)
                        .background(Color.Gray)
                        .size(50.dp)
                        .padding(vertical = 5.dp)
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
                        .background(Color.Black)
                        .width(300.dp)
                        .height(500.dp)
                        .padding(bottom = 32.dp)


                )
            }
        }
    }
}
