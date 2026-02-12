package it.unitn.disi.lpsmt.g03.mangacheck

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigation = binding.navView
        val toolbar = binding.appBarMain.toolbar
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_library, R.id.navigation_tracker))

        setSupportActionBar(toolbar)

        bottomNavigation.setupWithNavController(navController)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        CoroutineScope(Dispatchers.IO).launch {
            // Log.v(MainActivity::class.simpleName, Test().getContinents().data?.Page?.media.toString())
        }
    }
}