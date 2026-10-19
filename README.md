# Roomly AR furniture viewer

An Android Studio project using Kotlin, Jetpack Compose, MVVM, Navigation Compose, and SceneView. A debug APK is built at `app/build/outputs/apk/debug/app-debug.apk`.

## Architecture

`CatalogScreen -> CatalogViewModel -> FurnitureRepository` owns catalog data. Navigation passes a stable furniture ID. `ArViewerScreen` loads that item's GLB, runs SceneView's ARCore session, detects horizontal planes, hit-tests taps, and displays one movable anchor. SceneView owns the camera/session lifecycle; Compose owns the catalog and overlay controls.

## Model scale contract

ARCore world coordinates use meters. The included demo GLBs are generated with bounding boxes that match their catalog dimensions in meters. `ModelNode(scaleToUnits = ...)` sets the model's longest dimension to the catalog's longest dimension and preserves its proportions, so each production GLB must be authored at the correct width/height/depth ratios and with measured dimensions. If an asset has the wrong proportions, normalize it in the content pipeline before shipping. Models are bottom-aligned to the detected floor. ARCore's tracking and device calibration still affect real-room measurement accuracy.

`tools/generate_demo_models.py` regenerates the simple demo geometry in `app/src/main/assets/models/`. Replace those block-style models with production GLBs when available, keeping each model's outer dimensions and ratios aligned with the catalog entry.

## Run on your phone

1. Open this folder in Android Studio and wait for Gradle sync.
2. On the Android phone, enable Developer options and USB debugging, connect it with a data cable, and accept the computer's debugging prompt.
3. Select the phone in Android Studio's device menu and press **Run**. Or use the included `gradlew.bat installDebug` from PowerShell.
4. Open **Roomly AR**, tap a furniture card, then slowly move the phone to scan the floor. Tap a detected floor area to place the item; tap another area to move it.

The project compiles against Android API 36, targets API 35, and has minSdk 24. The manifest marks ARCore as required, so the Play listing is limited to supported devices. For optional AR, make the camera AR feature optional, set `com.google.ar.core` to `optional`, and gate the AR action using `ArCoreApk.checkAvailabilityAsync`.

SceneView's `ARSceneView` handles runtime camera permission and the ARCore install/update flow. When using raw ARCore directly, call `ArCoreApk.getInstance().requestInstall(activity, userRequestedInstall)` before creating a Session, then retry after resume if it returns `INSTALL_REQUESTED`. Do not create a second Session alongside SceneView.

The standard Android emulator does not provide ARCore tracking; use a supported physical device.

## Sources

- `app/build.gradle.kts` — Compose, Navigation, Coil, SceneView AR dependencies
- `app/src/main/AndroidManifest.xml` — camera + ARCore declarations
- `data/Furniture.kt`, `data/FurnitureRepository.kt` — product model and mock repository
- `ui/CatalogViewModel.kt`, `ui/CatalogScreen.kt` — MVVM catalog and responsive card grid
- `ui/MainActivity.kt` — navigation host
- `ui/ArViewerScreen.kt` — plane detection and tap placement/moving

## References

- [SceneView AR module documentation](https://github.com/SceneView/sceneview/blob/main/arsceneview/Module.md)
- [SceneView node documentation](https://github.com/SceneView/sceneview/blob/main/docs/docs/nodes.md)
- [Google ARCore app setup](https://developers.google.com/ar/develop/java/enable-arcore)
