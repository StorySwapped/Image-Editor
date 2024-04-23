import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import com.example.userinterface.R

@Composable
fun ObjectColorChangeModule() {
    var selectedColor by remember { mutableStateOf(Color.Black) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.sample_image),
            contentDescription = "Sample Image",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.Fit
        )

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

        Button(
            onClick = { /* TODO */ },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Select Object", fontSize = 20.sp)
        }
    }
}

@Composable
fun ColorPalette(selectedColor: Color, onColorSelected: (Color) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val colors = listOf(
            Color.Red, Color.Green, Color.Blue, Color.Magenta, Color.Yellow,
            Color.Cyan, Color.White, Color.Gray, Color.DarkGray, Color(0xFFA52A2A),
            Color(0xFF008B8B), Color(0xFFB8860B), Color(0xFF8B4513), Color(0xFF556B2F),
            Color(0xFF4682B4), Color(0xFFDC143C),  Color(0xFF800080),
            Color(0xFF2F4F4F), Color(0xFF8B0000), Color(0xFF2E8B57), Color(0xFFD2691E)
        )

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
                        onClick = { onColorSelected(color) }
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

@Preview(showBackground = true)
@Composable
fun PreviewObjectColorChangeModule() {
    ObjectColorChangeModule()
}
