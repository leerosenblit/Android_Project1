package com.example.ex1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment() {

    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        val supportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync { map ->
            googleMap = map
        }

        return view
    }

    fun zoom(lat: Double, lon: Double) {
        if (!::googleMap.isInitialized) return

        val location = LatLng(lat, lon)

        googleMap.clear()
        googleMap.addMarker(MarkerOptions().position(location).title("Score Location"))

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }
}