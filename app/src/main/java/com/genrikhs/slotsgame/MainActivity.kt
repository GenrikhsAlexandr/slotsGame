package com.genrikhs.slotsgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.genrikhs.slotsgame.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.play.setOnClickListener {
            binding.play.isVisible = false
            binding.loadingContainer.isVisible = true
            binding.textProgress.isVisible = true
            startLoading()
        }
    }

    private fun startLoading() {
        lifecycleScope.launch {
            (0 until binding.loadingContainer.childCount).forEach { index ->
                delay(300.milliseconds)
                binding.loadingContainer.children.toList()[index].isVisible = true
                binding.textProgress.text = "${(index * 10)}%"

            }
            replaceFragment(PrimaryFragment.newInstance())
            binding.loadingContainer.isVisible = false
            binding.textProgress.isVisible = false
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.startContainer, fragment)
            .commit()
    }
}