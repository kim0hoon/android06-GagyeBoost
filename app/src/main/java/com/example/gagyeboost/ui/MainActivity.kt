package com.example.gagyeboost.ui

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.ActivityMainBinding
import com.example.gagyeboost.ui.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        setBottomNavigationBar()
    }

    private fun setBottomNavigationBar() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fg_navigation_host) as NavHostFragment
        navController = navHostFragment.findNavController()

        binding.mainBottomNavigation.setupWithNavController(navController)
    }
}