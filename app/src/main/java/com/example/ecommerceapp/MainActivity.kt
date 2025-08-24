package com.example.ecommerceapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.ui.setupWithNavController
import com.example.ecommerceapp.base.BaseActivity
import com.example.ecommerceapp.databinding.ActivityMainBinding
import com.example.netflixcloneapp.utils.BottomNavigationAnnotation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavController()
        startAppFlows()
    }

    private fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)


        navController.addOnDestinationChangedListener { _, destination, _ ->
            val className = (destination as? FragmentNavigator.Destination)?.className
            onChangeDestination(className)
        }
    }

    private fun startAppFlows() {
        viewModel.loadCartItemCount()
        observeCartCount()
    }

    private fun observeCartCount() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartItemCount.collect { count ->
                    val badge = binding.bottomNav.getOrCreateBadge(R.id.basketFragment)
                    badge.isVisible = count > 0
                    badge.number = count
                }
            }
        }
    }

    private fun onChangeDestination(className: String?) {
        if (className == null) {
            binding.bottomNav.visibility = View.GONE
            return
        }

        val kClass = runCatching { classLoader.loadClass(className) }.getOrNull()
        if (kClass == null) {
            binding.bottomNav.visibility = View.GONE
            return
        }

        val annotations = kClass.annotations
        val bottomNavigationAnnotation =
            annotations.firstOrNull { BottomNavigationAnnotation::class.java.isAssignableFrom(it.javaClass) }

        if (bottomNavigationAnnotation != null) {
            viewModel.setBottomNavigationState(bottomNavigationAnnotation as BottomNavigationAnnotation)
            binding.bottomNav.visibility = View.VISIBLE
        } else {
            binding.bottomNav.visibility = View.GONE
        }
    }
}
