package com.example.roomly.ui

import androidx.lifecycle.ViewModel
import com.example.roomly.data.DemoFurnitureRepository
import com.example.roomly.data.Furniture
class CatalogViewModel : ViewModel() {
    val products: List<Furniture> = DemoFurnitureRepository.all()
}
