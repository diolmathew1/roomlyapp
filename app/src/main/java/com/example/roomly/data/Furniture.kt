package com.example.roomly.data

data class DimensionsCm(val width: Int, val height: Int, val depth: Int) {
    fun meters() = DimensionsMeters(width / 100f, height / 100f, depth / 100f)
    override fun toString() = "$width × $height × $depth cm"
}
data class DimensionsMeters(val width: Float, val height: Float, val depth: Float)
data class Furniture(
    val id: String, val title: String, val price: String, val imageUrl: String,
    val modelAsset: String,
    /** Unscaled GLB AABB dimensions, authored/measured in meters. */
    val modelDimensionsMeters: DimensionsMeters,
    val dimensions: DimensionsCm,
)
