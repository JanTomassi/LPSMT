package it.unitn.DISI.LPSMT.G03.mangacheck

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add<ReadingListFragment>(R.id.fragment_container_view)
        }
    }
}