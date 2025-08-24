package com.example.ecommerceapp

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
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

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination is FragmentNavigator.Destination) {
                onChangeDestination(destination)
            }
        }

        binding.bottomNav.setOnNavigationItemSelectedListener(::bottomMenuSelected)
        binding.bottomNav.setOnNavigationItemReselectedListener(::bottomMenuReselected)
    }

    private fun startAppFlows() {
        viewModel.loadCartItemCount()
        observeCartCount()
        observeBottomNavigation()
    }

    private fun observeCartCount() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartItemCount.collect { count ->
                    val badge = binding.bottomNav.getOrCreateBadge(R.id.nav_basket)
                    badge.isVisible = count > 0
                    badge.number = count
                }
            }
        }
    }

    private fun observeBottomNavigation() {
        viewModel.bottomState.observe(this, ::updateBottomNavigation)
    }


    private fun getMenuItemIndex(itemId: Int): Int? {
        val menu = binding.bottomNav.menu
        for (i in 0 until menu.size()) {
            if (itemId == menu.getItem(i).itemId) return i
        }
        return null
    }

    private fun updateBottomNavigation(annotation: BottomNavigationAnnotation) {
        binding.bottomNav.visibility = View.VISIBLE
        if (binding.bottomNav.selectedItemId != annotation.menuItemId) {
            getMenuItemIndex(annotation.menuItemId)?.let {
                binding.bottomNav.menu.getItem(it).isChecked = true
            }
        }
    }

    private fun onChangeDestination(destination: FragmentNavigator.Destination) {
        val annotations = classLoader.loadClass(destination.className).annotations
        val bottomNavigationAnnotation =
            annotations.firstOrNull { BottomNavigationAnnotation::class.java.isAssignableFrom(it.javaClass) }
        if (bottomNavigationAnnotation != null) {
            viewModel.setBottomNavigationState(bottomNavigationAnnotation as BottomNavigationAnnotation)
        } else {
            binding.bottomNav.visibility = View.GONE
        }
    }

    private fun bottomMenuSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                navController.navigate(R.id.productListFragment, null, navOptions {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                })
            }

            R.id.nav_basket -> {
                navController.navigate(R.id.basketFragment, null, navOptions {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                })
            }
            R.id.nav_favorites -> {
                navController.navigate(R.id.favoriteFragment, null, navOptions {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                })
            }
        }
        return true
    }

    private fun bottomMenuReselected(item: MenuItem) {
        when (item.itemId) {
            R.id.nav_home -> navController.popBackStack(R.id.productListFragment, false)
            R.id.nav_basket -> navController.popBackStack(R.id.basketFragment, false)
            R.id.nav_favorites -> navController.popBackStack(R.id.favoriteFragment, false)
        }
    }
}
