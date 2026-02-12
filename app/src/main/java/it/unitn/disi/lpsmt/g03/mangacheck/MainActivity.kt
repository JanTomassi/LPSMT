package it.unitn.disi.lpsmt.g03.mangacheck

import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import it.unitn.disi.lpsmt.g03.core.CustomeActivity
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), CustomeActivity {

    private lateinit var mBinding: ActivityMainBinding
    private val model: MainActivityModel by viewModels()
    override val progressBarState: MutableLiveData<Int>
        get() = model.progressBarState
    val isDebug by lazy { applicationContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0 }

    class MainActivityModel : ViewModel() {
        val progressBarState: MutableLiveData<Int> by lazy { MutableLiveData<Int>(0) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isDebug) {
            Log.v(this::class.simpleName, "Is debugging on")
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build())
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())
        }

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val bottomNavigation = mBinding.navView
        val toolbar = mBinding.appBarMain.toolbar

        val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment).navController

        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.library_nav,
            R.id.navigation_tracker))

        setSupportActionBar(toolbar)

        bottomNavigation.setupWithNavController(navController)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        progressBarState.observe(this) {
            if (it in 1..999) {
                mBinding.progress.visibility = View.VISIBLE
                mBinding.progress.progress = it
            } else mBinding.progress.visibility = View.GONE
        }
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

    override var isFullscreen: Boolean = false
        set(value) {
            field = value
            if (value) {
                WindowCompat.setDecorFitsSystemWindows(window, true)
                val windowInsetsController = WindowCompat.getInsetsController(window,
                    window.decorView)
                // Configure the behavior of the hidden system bars.
                windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            } else {
                WindowCompat.setDecorFitsSystemWindows(window, true)
                val windowInsetsController = WindowCompat.getInsetsController(window,
                    window.decorView)
                // Configure the behavior of the hidden system bars.
                windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
                windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }

    override fun postProgress(@IntRange(0, 1000) newState: Int) {
        return progressBarState.postValue(newState.coerceIn(0..1000))
    }
}