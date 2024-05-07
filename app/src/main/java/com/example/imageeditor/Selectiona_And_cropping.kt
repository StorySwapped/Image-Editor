package com.example.imageeditor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.example.imageeditor.ui.theme.ImageEditorTheme
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class SelectionaAndcropping : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uriString = intent.getStringExtra("imageUri")

        val imageUr = uriString?.toUri()
        setContent {
            ImageEditorTheme {
                // A surface container using the 'background' color from the theme

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (imageUr != null) {
                        HomeScreen(imageUr)
                    }
                }
            }

        }
    }


    @Composable
    fun HomeScreen(imageUr: Uri) {


        var tickConfirmation by remember { mutableStateOf<Boolean>(false) }
        var crossConfirmation by remember { mutableStateOf(false) }
        val context = LocalContext.current
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }
        var imageUri by remember { mutableStateOf<Uri?>(imageUr) }


        val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                imageUri = result.uriContent
                imageUri?.let {


                    val intent = Intent(context, SC_Middle_action::class.java)
                    intent.putExtra("cropped_image_uri", it.toString())

                    startActivity(context, intent, null)

                }
            } else {
                val exception = result.error
            }
        }

        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getStringExtra("imageUri")?.let {
                    Log.d("LandingScreen", "Updating image with URI: $it")
                    val newUri = Uri.parse(it)
                    imageUri = newUri
                }
            }
        }

        if (imageUri != null) {
            if (Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, imageUri!!)
                bitmap = ImageDecoder.decodeBitmap(source)
            }
        }
        val initial by remember { mutableStateOf<Bitmap?>(bitmap) }

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
        )
        {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "CROP & SELECT",
                    color = Color(android.graphics.Color.parseColor("#D0D0D0")),
                    modifier = Modifier.padding(top = 8.dp, bottom = 15.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    fontFamily = FontFamily(Font(R.font.sansserif))
                )
            }


            /////end
            Column( ////image
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(570.dp)
                    .absolutePadding(top = 5.dp, bottom = 25.dp)


            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap?.asImageBitmap()!!,
                        contentDescription = null,
                        modifier = Modifier
                            .height(570.dp)
                    )
                } else {
                    Image(
                        painter = rememberAsyncImagePainter(imageUr),
                        contentDescription = null,
                        modifier = Modifier
                            .background(Color.Black)
                            .width(300.dp)
                            .height(700.dp)

                    )

                }
            }
            Row {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .absolutePadding(top = 2.dp, bottom = 1.dp)
                        .width(70.dp)
                        .height(75.dp)
                        .background(
                            Color(android.graphics.Color.parseColor("#281340")),
                            shape = RoundedCornerShape(15.dp)
                        )
                )
                {
                    Image(
                        painter = painterResource(id = R.drawable.crop),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(RectangleShape)
                            .background(Color.Gray, shape = RoundedCornerShape(15.dp))
                            .size(50.dp)
                            .padding(vertical = 10.dp)
                            .clickable {
                                val cropOption =
                                    CropImageContractOptions(imageUr, CropImageOptions())
                                imageCropLauncher.launch(cropOption)
                            }
                    )
                    Text(
                        text = "Basic Crop",
                        style = TextStyle(fontSize = 9.sp),
                        textAlign = TextAlign.Center,
                        color = Color(android.graphics.Color.parseColor("#dddddd")),
                        fontFamily = FontFamily(Font(R.font.sansserif)),
                        modifier = Modifier.absolutePadding(top = 5.dp)
                    )

                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .absolutePadding(top = 2.dp, left = 20.dp, bottom = 1.dp)
                        .width(70.dp)
                        .height(75.dp)
                        .background(
                            Color(android.graphics.Color.parseColor("#281340")),
                            shape = RoundedCornerShape(15.dp)
                        )
                )
                {
                    Image(
                        painter = painterResource(id = R.drawable.select),
                        contentDescription = null,
                        modifier = Modifier
                            .clip(RectangleShape)
                            .background(Color.Gray, shape = RoundedCornerShape(15.dp))
                            .size(50.dp)
                            .padding(vertical = 5.dp)
                            .clickable {
                                val stream = ByteArrayOutputStream()
                                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                                val byteArray = stream.toByteArray()

                                val intent = Intent(context, SC_polygoSelection::class.java).apply {
                                    putExtra("image", byteArray)
                                }
                                context.startActivity(intent)
                            }
                    )
                    Text(
                        text = "Polygon Crop",
                        style = TextStyle(fontSize = 9.sp),
                        textAlign = TextAlign.Center,
                        color = Color(android.graphics.Color.parseColor("#dddddd")),
                        fontFamily = FontFamily(Font(R.font.sansserif)),
                        modifier = Modifier.absolutePadding(top = 5.dp)
                    )

                }

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .absolutePadding(left = 10.dp, right = 10.dp),
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
        }

        fun sendtoMain(bitmap: Bitmap?) {
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
        fun ShowBoxTick1() {
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
                            sendtoMain(bitmap)
                            tickConfirmation = false
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        )
                    ) {
                        Text(text = "Yes")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { tickConfirmation = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        )
                    ) {
                        Text(text = "No")
                    }
                }
            )
        }

        @Composable
        fun ShowBoxCross1() {
            AlertDialog(
                onDismissRequest = { crossConfirmation = false },
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
                        }, colors = ButtonDefaults.buttonColors(
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
                        Text(text = "No")
                    }
                }
            )
        }


    }


}






