package com.example.imageeditor


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.imageeditor.ui.theme.ImageEditorTheme
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.canhub.cropper.CropImage.CancelledResult.bitmap
import com.canhub.cropper.CropImageContract
import java.io.ByteArrayOutputStream

class Polygon : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContent {
            ImageEditorTheme {
                // A surface container using the 'background' color from the theme

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uriString = intent.getStringExtra("imageUri")
                    var imageUri = Uri.parse(uriString)
                    val context = LocalContext.current
                    val bitmap: Bitmap? = if (Build.VERSION.SDK_INT < 28) {
                        MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    } else {
                        val source = ImageDecoder.createSource(context.contentResolver, imageUri!!)
                        ImageDecoder.decodeBitmap(source)
                    }
                    val polygonCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
                        if (result.isSuccessful) {
                            imageUri = result.uriContent
                        } else {
                            val exception = result.error
                        }
                    }
                    Column(        ////selection icon
                        horizontalAlignment = Alignment.CenterHorizontally,
                        //verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 20.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 30.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            Button(modifier = Modifier.defaultMinSize(), onClick = {
                                val stream = ByteArrayOutputStream()
                                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                                val byteArray = stream.toByteArray()

                                val intent = Intent(context
                                    , SC_polygoSelection::class.java)
                                intent.putExtra("image", byteArray)
                                startActivity(intent)

                                //val intent = Intent(context, SC_polygoSelection::class.java)
//                    intent.putExtra("cropped_image_uri", it)
                                //context.startActivity(intent)

                            }) {
                                Text("Polygon Crop")
                            }
                        }
                    }






                }
            }
        }
    }
}