package com.google.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.sample.fragment.GridFragment

class MainActivity : AppCompatActivity() {
    companion object {
        var currentPosition = 0
    }
    private val KEY_CURRENT_POSITION = "com.google.samples.gridtopager.key.currentPosition"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.commit {
            add(R.id.fragment_container, GridFragment(), GridFragment::class.java.simpleName)
        }
    }
}