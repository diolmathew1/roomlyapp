package com.example.roomly.data

interface FurnitureRepository {
    fun all(): List<Furniture>
    fun byId(id: String): Furniture?
}
object DemoFurnitureRepository : FurnitureRepository {
    private val products = listOf(
        Furniture("oak-chair", "Oak dining chair", "€189", "https://images.unsplash.com/photo-1503602642458-232111445657?w=900", "models/oak-chair.glb", DimensionsMeters(.46f,.80f,.50f), DimensionsCm(46,80,50)),
        Furniture("sofa", "Lounge sofa", "€1,290", "https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=900", "models/lounge-sofa.glb", DimensionsMeters(2.10f,.82f,.92f), DimensionsCm(210,82,92)),
        Furniture("table", "Coffee table", "€349", "https://images.unsplash.com/photo-1499933374294-4584851497cc?w=900", "models/coffee-table.glb", DimensionsMeters(.80f,.42f,.80f), DimensionsCm(80,42,80)),
        Furniture("armchair", "Lounge armchair", "€549", "https://images.unsplash.com/photo-1567538340887-4f0cb14a6c1b?w=900", "models/lounge-armchair.glb", DimensionsMeters(.82f,.86f,.84f), DimensionsCm(82,86,84)),
        Furniture("dining-table", "Oak dining table", "€799", "https://images.unsplash.com/photo-1449247709967-d4461a6a6103?w=900", "models/oak-dining-table.glb", DimensionsMeters(1.60f,.75f,.90f), DimensionsCm(160,75,90)),
        Furniture("bookshelf", "Open oak bookcase", "€429", "https://images.unsplash.com/photo-1594620302200-9a762244a156?w=900", "models/oak-bookcase.glb", DimensionsMeters(.90f,1.80f,.32f), DimensionsCm(90,180,32)),
        Furniture("floor-lamp", "Arc floor lamp", "€219", "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=900", "models/arc-floor-lamp.glb", DimensionsMeters(.42f,1.65f,.42f), DimensionsCm(42,165,42)),
        Furniture("side-table", "Round side table", "€129", "https://images.unsplash.com/photo-1499933374294-4584851497cc?w=900", "models/round-side-table.glb", DimensionsMeters(.45f,.52f,.45f), DimensionsCm(45,52,45)),
        Furniture("bed", "Calm oak bed", "€999", "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=900", "models/calm-oak-bed.glb", DimensionsMeters(1.60f,.95f,2.00f), DimensionsCm(160,95,200)),
        Furniture("cabinet", "Compact storage cabinet", "€289", "https://images.unsplash.com/photo-1595428774223-ef52624120d2?w=900", "models/storage-cabinet.glb", DimensionsMeters(.80f,.90f,.40f), DimensionsCm(80,90,40)),
        Furniture("bar-stool", "Minimal bar stool", "€149", "https://images.unsplash.com/photo-1503602642458-232111445657?w=900", "models/minimal-bar-stool.glb", DimensionsMeters(.42f,.75f,.42f), DimensionsCm(42,75,42)),
    )
    override fun all() = products
    override fun byId(id: String) = products.firstOrNull { it.id == id }
}
