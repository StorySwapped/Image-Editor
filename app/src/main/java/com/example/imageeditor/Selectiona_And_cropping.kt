package com.example.imageeditor

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.imageeditor.ui.theme.ImageEditorTheme
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class SelectionaAndcropping : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uriString = intent.getStringExtra("imageUri")

        val imageUri =  Uri.parse(uriString)
        setContent {
            ImageEditorTheme {
                // A surface container using the 'background' color from the theme

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    HomeScreen(imageUri)

                    //HomeScreen()

//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.Center,
//                        verticalAlignment = Alignment.Bottom
//                    )
//                    {
//
//                        Button(modifier = Modifier
//                            .padding(16.dp),
//                            onClick = {
//
//
//                                // Send both URIs to another activity
//                                val intent = Intent(
//                                    this@SelectionaAndcropping,
//                                    SC_polygoSelection::class.java
//                                ).apply {
//                                    putExtra("image_uri", imageUri.toString())
//
//
//                                    startActivity(intent)
//                                }
//
//
//                            })
//                        {
//                            Text(text = "Press for Polygon Selection")
//                        }
//
//
//                    }

                }
            }

        }
    }
}

