package com.cyber.restory.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.cyber.restory.R
import com.cyber.restory.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initNavigation()
    }

    private fun initNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_home) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationHome.setupWithNavController(navController)

        setupTabLayout()

        binding.bottomNavigationHome.setOnItemSelectedListener { item ->
            navController.navigate(item.itemId)
            updateTabLayout(item.itemId)
            true
        }

        // 초기 탭 선택
        updateTabLayout(binding.bottomNavigationHome.selectedItemId)
    }

    private fun setupTabLayout() {
        val tabLayout = binding.tabLayout
        val bottomNav = binding.bottomNavigationHome

        // TabLayout에 BottomNavigationView의 메뉴 아이템 수만큼 탭 추가
        for (i in 0 until bottomNav.menu.size()) {
            tabLayout.addTab(tabLayout.newTab())
        }

        // 탭 선택 리스너 설정
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    bottomNav.selectedItemId = bottomNav.menu.getItem(it.position).itemId
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun updateTabLayout(itemId: Int) {
        val position = when (itemId) {
            R.id.navigation_home -> 0
            R.id.navigation_place_map -> 1
            R.id.navigation_mypage -> 2
            else -> 0
        }
        binding.tabLayout.getTabAt(position)?.select()
    }
}