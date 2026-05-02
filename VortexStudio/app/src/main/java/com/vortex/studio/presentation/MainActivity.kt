
package com.vortex.studio.presentation

import android.os.Bundle
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.vortex.studio.presentation.theme.VortexStudioTheme
import java.text.DecimalFormat

// Data class to hold properties of an object in the scene
data class SceneObject(
    val id: String,
    var positionX: Float = 0f,
    var positionY: Float = 0f,
    var scale: Float = 1f,
    var rotation: Float = 0f
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VortexStudioTheme {
                EditorScreen()
            }
        }
    }

    /**
     * A native method that is implemented by the 'beezyvortex-native' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String
    external fun initVulkan(surface: Surface)
    external fun cleanupVulkan()

    companion object {
        // Used to load the 'beezyvortex-native' library on application startup.
        init {
            System.loadLibrary("beezyvortex-native")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen() {
    // State for the currently selected object. For now, we'll create one default object.
    var selectedObject by remember { mutableStateOf(SceneObject(id = "rect_1")) }
    val activity = LocalContext.current as MainActivity

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(activity.stringFromJNI()) }, // Display text from C++
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        },
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Viewport(
                    modifier = Modifier.weight(0.7f),
                    onSurfaceCreated = { surface -> activity.initVulkan(surface) },
                    onSurfaceDestroyed = { activity.cleanupVulkan() }
                )
                TimelinePanel(modifier = Modifier.weight(0.3f))
            }
            InspectorPanel(
                modifier = Modifier.fillMaxHeight().width(280.dp),
                sceneObject = selectedObject,
                onObjectUpdated = { selectedObject = it } // Update state when properties change
            )
        }
    }
}

@Composable
fun Viewport(
    modifier: Modifier = Modifier, 
    onSurfaceCreated: (Surface) -> Unit,
    onSurfaceDestroyed: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Use AndroidView to embed a SurfaceView for native rendering
        AndroidView(
            factory = { context ->
                SurfaceView(context).apply {
                    holder.addCallback(object : SurfaceHolder.Callback {
                        override fun surfaceCreated(holder: SurfaceHolder) {
                            onSurfaceCreated(holder.surface)
                        }

                        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                            // TODO: Handle surface resize
                        }

                        override fun surfaceDestroyed(holder: SurfaceHolder) {
                            onSurfaceDestroyed()
                        }
                    })
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun TimelinePanel(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Timeline Panel",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun InspectorPanel(
    modifier: Modifier = Modifier,
    sceneObject: SceneObject,
    onObjectUpdated: (SceneObject) -> Unit
) {
    val floatFormatter = remember { DecimalFormat("#.0#") }

    Column(
        modifier = modifier
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
    ) {
        Text("Inspector", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))

        // Position X Slider
        PropertySlider(
            label = "Position X",
            value = sceneObject.positionX,
            range = -500f..500f,
            onValueChange = { onObjectUpdated(sceneObject.copy(positionX = it)) },
            formatter = floatFormatter
        )
        
        // Position Y Slider
        PropertySlider(
            label = "Position Y",
            value = sceneObject.positionY,
            range = -500f..500f,
            onValueChange = { onObjectUpdated(sceneObject.copy(positionY = it)) },
            formatter = floatFormatter
        )

        // Scale Slider
        PropertySlider(
            label = "Scale",
            value = sceneObject.scale,
            range = 0.1f..5f,
            onValueChange = { onObjectUpdated(sceneObject.copy(scale = it)) },
            formatter = floatFormatter
        )

        // Rotation Slider
        PropertySlider(
            label = "Rotation",
            value = sceneObject.rotation,
            range = 0f..360f,
            onValueChange = { onObjectUpdated(sceneObject.copy(rotation = it)) },
            formatter = floatFormatter
        )
    }
}

@Composable
fun PropertySlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    formatter: DecimalFormat
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = formatter.format(value), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range
        )
    }
}


@Preview(showBackground = true, widthDp = 1280, heightDp = 800)
@Composable
fun EditorScreenPreview() {
    VortexStudioTheme {
        EditorScreen()
    }
}
