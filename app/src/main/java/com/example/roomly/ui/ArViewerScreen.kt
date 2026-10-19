package com.example.roomly.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.roomly.data.Furniture
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberOnGestureListener
import java.util.concurrent.atomic.AtomicReference

private data class FurnitureBox(
    val x: Float, val y: Float, val z: Float,
    val width: Float, val height: Float, val depth: Float, val color: Int,
)

/** Native SceneView demo geometry with dimensions authored in meters. */
private fun furnitureBoxes(item: Furniture): List<FurnitureBox> {
    fun box(x: Float, y: Float, z: Float, w: Float, h: Float, d: Float, c: Int) =
        FurnitureBox(x, y, z, w, h, d, c)
    return when (item.id) {
        "oak-chair" -> {
            val wood = android.graphics.Color.rgb(132, 83, 46)
            listOf(
                box(0f, .455f, 0f, .46f, .07f, .50f, android.graphics.Color.rgb(185, 143, 91)),
                box(-.195f, .22f, -.215f, .035f, .44f, .035f, wood),
                box(.195f, .22f, -.215f, .035f, .44f, .035f, wood),
                box(-.195f, .22f, .215f, .035f, .44f, .035f, wood),
                box(.195f, .22f, .215f, .035f, .44f, .035f, wood),
                box(0f, .645f, -.2225f, .46f, .31f, .055f, wood),
            )
        }
        "sofa" -> {
            val frame = android.graphics.Color.rgb(22, 69, 61)
            listOf(
                box(0f, .255f, .05f, 1.80f, .21f, .78f, frame),
                box(0f, .405f, .09f, 1.72f, .18f, .72f, android.graphics.Color.rgb(37, 111, 96)),
                box(0f, .655f, -.365f, 1.95f, .33f, .19f, frame),
                box(-.96f, .39f, 0f, .18f, .62f, .92f, frame),
                box(.96f, .39f, 0f, .18f, .62f, .92f, frame),
                box(-.86f, .05f, -.34f, .08f, .10f, .08f, android.graphics.Color.DKGRAY),
                box(.86f, .05f, -.34f, .08f, .10f, .08f, android.graphics.Color.DKGRAY),
                box(-.86f, .05f, .34f, .08f, .10f, .08f, android.graphics.Color.DKGRAY),
                box(.86f, .05f, .34f, .08f, .10f, .08f, android.graphics.Color.DKGRAY),
            )
        }
        "armchair" -> {
            val fabric = android.graphics.Color.rgb(179, 137, 101)
            val wood = android.graphics.Color.rgb(91, 62, 40)
            listOf(
                box(0f, .33f, 0f, .76f, .22f, .70f, fabric),
                box(0f, .65f, -.30f, .76f, .46f, .18f, fabric),
                box(-.36f, .47f, .02f, .10f, .40f, .72f, fabric), box(.36f, .47f, .02f, .10f, .40f, .72f, fabric),
                box(-.32f, .08f, -.28f, .06f, .16f, .06f, wood), box(.32f, .08f, -.28f, .06f, .16f, .06f, wood),
                box(-.32f, .08f, .28f, .06f, .16f, .06f, wood), box(.32f, .08f, .28f, .06f, .16f, .06f, wood),
            )
        }
        "bookshelf", "cabinet" -> {
            val wood = android.graphics.Color.rgb(145, 103, 62)
            val w = item.modelDimensionsMeters.width
            val h = item.modelDimensionsMeters.height
            val d = item.modelDimensionsMeters.depth
            val side = .035f.coerceAtMost(w / 10f)
            val shelfDepth = d.coerceAtLeast(.20f)
            buildList {
                add(box(-w / 2 + side / 2, h / 2, 0f, side, h, shelfDepth, wood))
                add(box(w / 2 - side / 2, h / 2, 0f, side, h, shelfDepth, wood))
                add(box(0f, .025f, 0f, w, .05f, shelfDepth, wood))
                add(box(0f, h - .025f, 0f, w, .05f, shelfDepth, wood))
                if (item.id == "bookshelf") for (i in 1..4) add(box(0f, h * i / 5f, 0f, w, .035f, shelfDepth, wood))
                else add(box(0f, h * .60f, 0f, w - side * 2, .025f, shelfDepth, wood))
            }
        }
        "bed" -> {
            val wood = android.graphics.Color.rgb(145, 101, 59)
            listOf(
                box(0f, .27f, 0f, 1.54f, .22f, 1.92f, wood),
                box(0f, .41f, 0f, 1.58f, .16f, 1.96f, android.graphics.Color.rgb(230, 224, 211)),
                box(0f, .67f, -.91f, 1.60f, .52f, .12f, wood),
                box(-.70f, .59f, .62f, .54f, .18f, .40f, android.graphics.Color.WHITE),
                box(.70f, .59f, .62f, .54f, .18f, .40f, android.graphics.Color.WHITE),
            )
        }
        "floor-lamp" -> {
            val metal = android.graphics.Color.rgb(57, 59, 62)
            listOf(
                box(0f, .025f, 0f, .36f, .05f, .36f, metal),
                box(0f, .78f, 0f, .035f, 1.45f, .035f, metal),
                box(0f, 1.49f, 0f, .40f, .28f, .40f, android.graphics.Color.rgb(241, 215, 157)),
            )
        }
        "dining-table" -> {
            val wood = android.graphics.Color.rgb(151, 105, 60)
            listOf(
                box(0f, .72f, 0f, 1.60f, .06f, .90f, wood),
                box(-.68f, .36f, -.34f, .07f, .72f, .07f, wood), box(.68f, .36f, -.34f, .07f, .72f, .07f, wood),
                box(-.68f, .36f, .34f, .07f, .72f, .07f, wood), box(.68f, .36f, .34f, .07f, .72f, .07f, wood),
            )
        }
        "side-table" -> {
            val wood = android.graphics.Color.rgb(151, 105, 60)
            listOf(box(0f, .50f, 0f, .45f, .04f, .45f, wood), box(-.16f, .25f, 0f, .04f, .48f, .04f, wood), box(.16f, .25f, 0f, .04f, .48f, .04f, wood), box(0f, .25f, .16f, .04f, .48f, .04f, wood), box(0f, .25f, -.16f, .04f, .48f, .04f, wood))
        }
        "bar-stool" -> {
            val wood = android.graphics.Color.rgb(132, 83, 46)
            listOf(box(0f, .71f, 0f, .42f, .08f, .42f, wood), box(-.16f, .35f, -.16f, .035f, .70f, .035f, wood), box(.16f, .35f, -.16f, .035f, .70f, .035f, wood), box(-.16f, .35f, .16f, .035f, .70f, .035f, wood), box(.16f, .35f, .16f, .035f, .70f, .035f, wood))
        }
        else -> {
            val legs = android.graphics.Color.rgb(83, 53, 31)
            listOf(
                box(0f, .39f, 0f, .80f, .06f, .80f, android.graphics.Color.rgb(161, 108, 59)),
                box(-.29f, .19f, -.29f, .055f, .38f, .055f, legs),
                box(.29f, .19f, -.29f, .055f, .38f, .055f, legs),
                box(-.29f, .19f, .29f, .055f, .38f, .055f, legs),
                box(.29f, .19f, .29f, .055f, .38f, .055f, legs),
            )
        }
    }
}

@Composable
fun ArViewerScreen(item: Furniture, onClose: () -> Unit) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val boxes = remember(item.id) { furnitureBoxes(item) }
    val materials = remember(item.id, materialLoader) {
        boxes.map { materialLoader.createColorInstance(it.color) }
    }
    val photoPainter = rememberAsyncImagePainter(item.imageUrl)
    val photoBitmap = remember(photoPainter.state) {
        (photoPainter.state as? AsyncImagePainter.State.Success)
            ?.result?.drawable?.toBitmap()
    }
    val latestFrame = remember { AtomicReference<Frame?>(null) }
    var anchor by remember { mutableStateOf<Anchor?>(null) }
    var floorDetected by remember { mutableStateOf(false) }

    DisposableEffect(Unit) { onDispose { anchor?.detach() } }

    Box(Modifier.fillMaxSize()) {
        ARSceneView(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            materialLoader = materialLoader,
            planeRenderer = true,
            sessionConfiguration = { _, config ->
                config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            },
            onSessionUpdated = { session, frame ->
                latestFrame.set(frame)
                floorDetected = frame.camera.trackingState == TrackingState.TRACKING &&
                    session.getAllTrackables(Plane::class.java).any {
                        it.trackingState == TrackingState.TRACKING && it.type == Plane.Type.HORIZONTAL_UPWARD_FACING
                    }
            },
            onGestureListener = rememberOnGestureListener(
                onSingleTapConfirmed = { event, _ ->
                    val hit = latestFrame.get()
                        ?.takeIf { it.camera.trackingState == TrackingState.TRACKING }
                        ?.hitTest(event)
                        ?.firstOrNull { result ->
                            val plane = result.trackable as? Plane
                            plane != null && plane.type == Plane.Type.HORIZONTAL_UPWARD_FACING &&
                                plane.trackingState == TrackingState.TRACKING && plane.isPoseInPolygon(result.hitPose)
                        }
                    hit?.createAnchor()?.let { next -> anchor?.detach(); anchor = next }
                }
            ),
        ) {
            val placed = anchor
            if (placed != null) {
                AnchorNode(anchor = placed) {
                    boxes.forEachIndexed { index, part ->
                        CubeNode(
                            size = Position(part.width, part.height, part.depth),
                            center = Position(0f),
                            materialInstance = materials[index],
                            position = Position(part.x, part.y, part.z),
                        )
                    }
                    photoBitmap?.let { bitmap ->
                        val panelWidth = .45f
                        val aspect = bitmap.width.toFloat() / bitmap.height.coerceAtLeast(1)
                        val panelHeight = panelWidth / aspect
                        ImageNode(
                            bitmap = bitmap,
                            size = Position(panelWidth, panelHeight, 0f),
                            position = Position(
                                item.modelDimensionsMeters.width / 2f + panelWidth / 2f + .12f,
                                panelHeight / 2f,
                                0f,
                            ),
                            rotation = Rotation(x = 90f),
                        )
                    }
                }
            }
        }
        Column(Modifier.align(Alignment.TopCenter).fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onClose) { Text("Close") }
                Text(item.title, style = MaterialTheme.typography.titleMedium)
            }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        if (anchor == null) {
                            if (floorDetected) "Floor found — tap the floor to place the item. Tap again to move it."
                            else "Slowly move your phone to scan the floor."
                        } else "${item.dimensions} · Tap another floor spot to move",
                    )
                }
            }
        }
    }
}
