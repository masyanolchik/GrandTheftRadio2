package com.masyanolchik.grandtheftradio2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
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
        bottomNav.setOnItemSelectedListener { menuItem ->
            navController.currentDestination?.let {
                val navOptions = NavOptions.Builder()
                    .setEnterAnim(androidx.navigation.ui.R.anim.nav_default_enter_anim)
                    .setExitAnim(androidx.navigation.ui.R.anim.nav_default_exit_anim)
                    .build()
                toolbar.setTitle(R.string.bottom_nav_title_import)
                    when(menuItem.itemId) {
                        R.id.fav_dest_menu -> {
                            if(it.id != R.id.fav_dest)  {
                                toolbar.setTitle(R.string.bottom_nav_title_favorites)
                                navController.navigate(R.id.fav_dest, null , navOptions)
                            }
                        }
                        R.id.twod_dest_menu -> {
                            if (!toolbar.title.equals(getString(R.string.bottom_nav_title_2d))) {
                                toolbar.setTitle(R.string.bottom_nav_title_2d)
                                navController.navigate(R.id.stations_dest, bundleOf("eraName" to "2D"), navOptions)
                            }
                        }
                        R.id.threed_dest_menu -> {
                            if(!toolbar.title.equals(getString(R.string.bottom_nav_title_3d))) {
                                toolbar.setTitle(R.string.bottom_nav_title_3d)
                                navController.navigate(R.id.stations_dest, bundleOf("eraName" to "3D"), navOptions)
                            }
                        }
                        R.id.hd_dest_menu -> {
                            if(!toolbar.title.equals(getString(R.string.bottom_nav_title_hd))) {
                                toolbar.setTitle(R.string.bottom_nav_title_hd)
                                navController.navigate(R.id.stations_dest, bundleOf("eraName" to "HD"), navOptions)
                            }
                        }
                        R.id.import_dest_menu -> {
                            if(it.id != R.id.import_dest) {
                                navController.navigate(R.id.import_dest, null, navOptions)
                            }
                        }
                        else -> throw IllegalStateException("Unknown destination")
                }
            }
            return@setOnItemSelectedListener true
        }
    }
}