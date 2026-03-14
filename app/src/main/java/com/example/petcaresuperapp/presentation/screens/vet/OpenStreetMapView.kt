package com.example.petcaresuperapp.presentation.screens.vet

import android.preference.PreferenceManager
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.petcaresuperapp.domain.models.Doctor
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OpenStreetMapView(
    doctors: List<Doctor>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Remember the MapView to avoid re-initializing it on every recomposition
    val mapView = remember {
        Configuration.getInstance().load(
            context,
            PreferenceManager.getDefaultSharedPreferences(context)
        )
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            val mapController = controller
            mapController.setZoom(13.0)
            val startPoint = GeoPoint(20.247856, 85.801154)
            mapController.setCenter(startPoint)
        }
    }

    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    
    // Manage MapView lifecycle
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            when (event) {
                androidx.lifecycle.Lifecycle.Event.ON_RESUME -> mapView.onResume()
                androidx.lifecycle.Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                androidx.lifecycle.Lifecycle.Event.ON_DESTROY -> mapView.onDetach()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        factory = {
            mapView
        },
        update = { mv ->
            // Clear existing overlays to avoid duplicates if doctors list changes
            mv.overlays.clear()
            
            if (doctors.isNotEmpty()) {
                val icon = androidx.core.content.ContextCompat.getDrawable(
                    context, 
                    org.osmdroid.library.R.drawable.ic_menu_mylocation
                ) ?: androidx.core.content.ContextCompat.getDrawable(
                    context, 
                    org.osmdroid.library.R.drawable.marker_default
                )

                var minLat = Double.MAX_VALUE
                var maxLat = -Double.MAX_VALUE
                var minLon = Double.MAX_VALUE
                var maxLon = -Double.MAX_VALUE

                doctors.forEach { doctor ->
                    val marker = Marker(mv)
                    val position = GeoPoint(doctor.latitude, doctor.longitude)
                    
                    if (position.latitude < minLat) minLat = position.latitude
                    if (position.latitude > maxLat) maxLat = position.latitude
                    if (position.longitude < minLon) minLon = position.longitude
                    if (position.longitude > maxLon) maxLon = position.longitude

                    marker.position = position
                    marker.title = doctor.clinicName
                    marker.snippet = doctor.name
                    if (icon != null) {
                        marker.icon = icon
                    }
                    
                    marker.setOnMarkerClickListener { _, _ ->
                        navController.navigate("vet_detail/${doctor.uid}")
                        true
                    }
                    
                    mv.overlays.add(marker)
                }
                
                // Safely center the map instead of zoomToBoundingBox to prevent ANR loops during layout pass
                val centerLat = (minLat + maxLat) / 2.0
                val centerLon = (minLon + maxLon) / 2.0
                mv.controller.setCenter(GeoPoint(centerLat, centerLon))
                mv.controller.setZoom(13.0)
            }
            
            mv.invalidate()
        }
    )
}
