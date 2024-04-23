package com.example.myapplication

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.canhub.cropper.CropImage.CancelledResult.uriContent
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) {result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
            imageUri?.let {


                val intent = Intent(context, Middle_action::class.java)
                intent.putExtra("cropped_image_uri", it.toString())

                startActivity(context,intent,null)

            }
        }
        else {
            val exception = result.error
        }
    }

    if (imageUri != null) {
        if (Build.VERSION.SDK_INT < 28) {
            bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        }
        else {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri!!)
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

            Text(text = "Press Icon to Select:")

            Image(
            painter = painterResource(id = R.drawable.baseline_photo_camera_24),
            contentDescription = null,
            modifier = Modifier
                .clip(RectangleShape)
                .background(Color.Gray)
                .size(50.dp)
                .padding(vertical = 10.dp)
                .clickable {
                    val cropOption = CropImageContractOptions(uriContent, CropImageOptions())
                    imageCropLauncher.launch(cropOption)


                })
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


            )
        }
        else {
            Image(
                painter = painterResource(id = R.drawable.baseline_person_24),
                contentDescription = null,
                modifier = Modifier
                    .clip(RectangleShape)
                    .background(Color.Blue)
                    .width(300.dp)
                    .height(700.dp)

            )

        }
    }


}



