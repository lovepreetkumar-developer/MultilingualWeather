package com.lnadeem.app.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lnadeem.app.ui.home.WeatherHourlyFragment

class ScreenSlidePagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return com.lnadeem.app.ui.home.WeatherTodayFragment()
            1 -> return WeatherHourlyFragment()
        }
        return com.lnadeem.app.ui.home.WeatherTodayFragment()
    }
}