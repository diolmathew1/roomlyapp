package com.example.roomly.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.roomly.data.Furniture

private val ShopBlue = Color(0xFF1769D2)
private val Ink = Color(0xFF182230)
private val Muted = Color(0xFF687385)
private val ShopYellow = Color(0xFFFFD84D)
private val catalogCategories = listOf("All", "Sofas", "Chairs", "Tables", "Storage", "Lighting", "Beds")

@Composable
fun CatalogScreen(products: List<Furniture>, onSelect: (Furniture) -> Unit) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    val filtered = products.filter { product ->
        val matchesSearch = query.isBlank() || product.title.contains(query, ignoreCase = true)
        val category = categoryFor(product.id)
        matchesSearch && (selectedCategory == "All" || category == selectedCategory)
    }

    Column(Modifier.fillMaxSize().background(Color(0xFFF4F6FA))) {
        Column(Modifier.fillMaxWidth().background(ShopBlue).padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("ROOMLY", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                    Text("Furniture that fits your space", color = Color.White.copy(alpha = .82f), style = MaterialTheme.typography.labelSmall)
                }
                Text("⌖", color = ShopBlue, modifier = Modifier.clip(CircleShape).background(ShopYellow).padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                placeholder = { Text("Search sofas, chairs, tables...") },
                leadingIcon = { Text("⌕", style = MaterialTheme.typography.headlineSmall, color = Muted) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent,
                ),
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        catalogCategories.forEach { category ->
                            val selected = category == selectedCategory
                            Surface(
                                modifier = Modifier.clickable { selectedCategory = category },
                                shape = RoundedCornerShape(20.dp),
                                color = if (selected) ShopBlue else Color.White,
                                shadowElevation = if (selected) 0.dp else 1.dp,
                            ) {
                                Text(category, Modifier.padding(horizontal = 15.dp, vertical = 9.dp), color = if (selected) Color.White else Ink, style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(ShopBlue).clickable { selectedCategory = "All" }.padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("A home refresh,\nmade easy", color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Text("See it in your room before you buy", color = Color.White.copy(alpha = .88f), modifier = Modifier.padding(top = 6.dp), style = MaterialTheme.typography.bodySmall)
                            Text("EXPLORE FURNITURE  →", color = ShopBlue, modifier = Modifier.padding(top = 12.dp).clip(RoundedCornerShape(6.dp)).background(ShopYellow).padding(horizontal = 10.dp, vertical = 7.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                        Text("⌂", color = ShopYellow, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold)
                    }
                    Row(Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 2.dp), verticalAlignment = Alignment.Bottom) {
                        Column(Modifier.weight(1f)) {
                            Text(if (selectedCategory == "All") "Shop by category" else "${selectedCategory} for your home", color = Ink, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("${filtered.size} pieces · tap a card to preview in AR", color = Muted, style = MaterialTheme.typography.bodySmall)
                        }
                        Text("SORT  ↕", color = ShopBlue, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
            if (filtered.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text("No furniture found. Try another search or category.", Modifier.padding(20.dp), color = Muted)
                }
            }
            items(filtered, key = { it.id }) { product ->
                ProductCard(product = product, onClick = { onSelect(product) })
            }
        }
    }
}

@Composable
private fun ProductCard(product: Furniture, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column {
            Box {
                AsyncImage(product.imageUrl, product.title, Modifier.fillMaxWidth().height(142.dp), contentScale = ContentScale.Crop)
                Text("AR READY", Modifier.align(Alignment.TopStart).padding(8.dp).clip(RoundedCornerShape(5.dp)).background(ShopYellow).padding(horizontal = 7.dp, vertical = 4.dp), color = Ink, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
            Column(Modifier.padding(horizontal = 11.dp, vertical = 10.dp)) {
                Text(product.title, color = Ink, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                Text("★ 4.8  ·  ${product.dimensions.width} × ${product.dimensions.height} × ${product.dimensions.depth} cm", color = Muted, modifier = Modifier.padding(top = 4.dp), style = MaterialTheme.typography.labelSmall, maxLines = 1)
                Text(product.price, color = Ink, modifier = Modifier.padding(top = 6.dp), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("VIEW IN YOUR ROOM  →", color = ShopBlue, modifier = Modifier.padding(top = 6.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private fun categoryFor(id: String): String = when (id) {
    "sofa", "armchair" -> "Sofas"
    "oak-chair", "bar-stool" -> "Chairs"
    "table", "dining-table", "side-table" -> "Tables"
    "bookshelf", "cabinet" -> "Storage"
    "floor-lamp" -> "Lighting"
    "bed" -> "Beds"
    else -> "All"
}
