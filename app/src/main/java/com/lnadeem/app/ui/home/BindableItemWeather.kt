package com.lnadeem.app.ui.home

import android.view.View
import androidx.databinding.library.baseAdapters.BR
import com.xwray.groupie.viewbinding.BindableItem
import com.lnadeem.app.R
import com.lnadeem.app.databinding.ItemHourlyWeatherBinding
import com.lnadeem.app.models.WeatherResponse
import com.lnadeem.app.ui.fonty.Fonty

class BindableItemWeather(
    private val model: WeatherResponse
) : BindableItem<ItemHourlyWeatherBinding>() {

    override fun getLayout(): Int = R.layout.item_hourly_weather

    override fun bind(viewBinding: ItemHourlyWeatherBinding, position: Int) {
        viewBinding.setVariable(BR.model, model)
        viewBinding.setVariable(BR.pos, position)
        Fonty.setFonts(viewBinding.llParent)
    }

    override fun initializeViewBinding(view: View): ItemHourlyWeatherBinding {
        return ItemHourlyWeatherBinding.bind(view)
    }
}