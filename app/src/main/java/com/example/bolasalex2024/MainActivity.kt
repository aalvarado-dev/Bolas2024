package com.example.bolasalex2024

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.bolasalex2024.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        //setContentView(view)
        val splashScreen = installSplashScreen() //esto se tiene que poner en el activity que sera el que inicie el spalshscreen
        setContentView(view)//junto con esta
        splashScreen.setKeepOnScreenCondition{false} //i tambien con esta

        val container = binding.container
        container.addView(MyAnimationView(this))
    }
}