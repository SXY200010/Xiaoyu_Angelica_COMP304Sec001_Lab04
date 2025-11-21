package com.example.xiaoyu_angelica_comp304sec001_lab04

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.xiaoyu_angelica_comp304sec001_lab04.repository.PlacesRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CategoriesScreen(
                    categories = PlacesRepository.getCategories(),
                    onCategoryClick = { categoryId ->
                        val intent = Intent(this, XiaoyuActivity::class.java)
                        intent.putExtra("categoryId", categoryId)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun CategoriesScreen(categories: List<String>, onCategoryClick: (String) -> Unit) {
    Column(Modifier.padding(16.dp)) {
        Text("Categories", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        categories.forEach { cat ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onCategoryClick(cat) }
            ) {
                Text(cat, Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}