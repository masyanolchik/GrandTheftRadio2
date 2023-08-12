package com.masyanolchik.grandtheftradio2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.IllegalStateException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return

        val navController = host.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.fav_dest -> {
                    toolbar.setTitle(R.string.bottom_nav_title_favorites)
                    navController.navigate(R.id.fav_dest)
                }
                R.id.twod_dest -> {
                    toolbar.setTitle(R.string.bottom_nav_title_2d)
                    navController.navigate(R.id.stations_dest, bundleOf("eraName" to "2D"))
                }
                R.id.threed_dest -> {
                    toolbar.setTitle(R.string.bottom_nav_title_3d)
                    navController.navigate(R.id.stations_dest, bundleOf("eraName" to "3D"))
                }
                R.id.hd_dest -> {
                    toolbar.setTitle(R.string.bottom_nav_title_hd)
                    navController.navigate(R.id.stations_dest, bundleOf("eraName" to "HD"))
                }
                R.id.import_dest -> {
                    toolbar.setTitle(R.string.bottom_nav_title_import)
                    navController.navigate(R.id.import_dest)
                }
                else -> throw IllegalStateException("Unknown destination")
            }
            return@setOnItemSelectedListener true
        }
    }
}