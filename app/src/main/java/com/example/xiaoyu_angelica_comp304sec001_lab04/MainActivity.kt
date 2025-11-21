package com.example.xiaoyu_angelica_comp304sec001_lab04

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.xiaoyu_angelica_comp304sec001_lab04.repository.PlacesRepository
import com.example.xiaoyu_angelica_comp304sec001_lab04.ui.theme.OsakaTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.MaterialTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OsakaTheme {
                MainScreen { categoryId ->
                    val intent = Intent(this, XiaoyuActivity::class.java)
                    intent.putExtra("categoryId", categoryId)
                    startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun MainScreen(onCategoryClick: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background image (make sure R.drawable.background_main exists)
        Image(
            painter = painterResource(id = R.drawable.background_main),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // translucent overlay for legibility
        Box(modifier = Modifier
            .fillMaxSize()
            .background(color = androidx.compose.ui.graphics.Color(0x88FFFFFF))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // Title centered, bold, larger
            Text(
                text = "Osaka Explorer",
                style = androidx.compose.material3.MaterialTheme.typography.displayLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Category list — each card has an image + text
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                val context = LocalContext.current
                val categories = PlacesRepository.getCategories(context)
                categories.forEach { category ->
                    CategoryCard(
                        category = category,
                        imageName = when (category.lowercase()) {
                            "landmarks", "historic" -> "ic_landmarks"
                            "museums", "museum" -> "ic_museums"
                            "cafes", "cafe" -> "ic_cafes"
                            else -> "ic_touristic"
                        },
                        onClick = { onCategoryClick(category) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCard(category: String, imageName: String, onClick: () -> Unit) {
    // resolve image resource by name, fallback to placeholder
    val ctx = LocalContext.current
    val resId = remember(imageName) {
        val id = ctx.resources.getIdentifier(imageName, "drawable", ctx.packageName)
        if (id == 0) R.drawable.placeholder_place else id
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(98.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = resId),
                contentDescription = category,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(74.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = category,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}