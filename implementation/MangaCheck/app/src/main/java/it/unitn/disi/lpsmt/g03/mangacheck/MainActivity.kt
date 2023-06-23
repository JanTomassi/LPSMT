package it.unitn.disi.lpsmt.g03.mangacheck

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import it.unitn.disi.lpsmt.g03.core.BarVisibility
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), BarVisibility {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)/*WindowCompat.setDecorFitsSystemWindows(window, false)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        // Configure the behavior of the hidden system bars.
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())*/

        val bottomNavigation = mBinding.navView
        val toolbar = mBinding.appBarMain.toolbar

        val navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment).navController

        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.library_nav, R.id.navigation_tracker))

        setSupportActionBar(toolbar)

        bottomNavigation.setupWithNavController(navController)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun hideBars() {
        supportActionBar?.hide()
        mBinding.navView.visibility = View.GONE
    }

    override fun hideNavBar() {
        mBinding.navView.visibility = View.GONE
    }

    override fun showBars() {
        supportActionBar?.show()
        mBinding.navView.visibility = View.VISIBLE
    }

    override fun showNavBar() {
        mBinding.navView.visibility = View.VISIBLE
    }
}