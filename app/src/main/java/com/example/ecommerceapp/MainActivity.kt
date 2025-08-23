package com.example.ecommerceapp

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.example.ecommerceapp.base.BaseActivity
import com.example.ecommerceapp.databinding.ActivityMainBinding
import com.example.netflixcloneapp.utils.BottomNavigationAnnotation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val navController by lazy {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment

        navHostFragment.navController
    }
    val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.bottomState.observe(this, ::updateBottomNavigation)
    }

    override fun onStart() {
        navController?.addOnDestinationChangedListener(::destinationListener)
        super.onStart()
        viewModel.loadCartItemCount()
        binding.bottomNav.setOnNavigationItemSelectedListener(::bottomMenuSelected)
        binding.bottomNav.setOnNavigationItemReselectedListener(::bottomMenuReselected)
        viewModel.cartItemCount.observe(this){count->
            val badge = binding.bottomNav.getOrCreateBadge(R.id.nav_basket)
            if (count > 0) {
                badge.isVisible = true
                badge.number = count
            } else {
                badge.isVisible = false
            }
        }
    }

    override fun onStop() {
        super.onStop()
        navController?.removeOnDestinationChangedListener(::destinationListener)
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
                binding.bottomNav.menu.getItem(it).setChecked(true)
            }

        }
    }

    private fun destinationListener(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (destination is FragmentNavigator.Destination) {
            arguments?.getString("toolbarTitle")?.let {
                destination.label = it
            }
            onChangeDestination(destination)
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
        }
        return true
    }


    private fun bottomMenuReselected(item: MenuItem) {
        when (item.itemId) {
            R.id.nav_home -> {
                navController?.popBackStack(R.id.productListFragment, false)
            }
            R.id.nav_basket->{
                navController?.popBackStack(R.id.basketFragment, false)
            }
        }
    }
}