package it.unitn.disi.lpsmt.g03.mangacheck

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import it.unitn.disi.lpsmt.g03.mangacheck.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}