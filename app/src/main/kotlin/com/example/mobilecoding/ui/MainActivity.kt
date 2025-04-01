package com.example.mobilecoding.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mobilecoding.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // La Activity ahora solo contiene un contenedor para fragments
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            // Se carga el fragmento de lista de películas
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MovieListFragment())
                .commitNow()
        }
    }
}
