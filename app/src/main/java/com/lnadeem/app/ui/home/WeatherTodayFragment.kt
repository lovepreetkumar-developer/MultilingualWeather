package com.lnadeem.app.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.lnadeem.app.R
import com.lnadeem.app.base.BaseCallback
import com.lnadeem.app.base.BaseFragment
import com.lnadeem.app.data.preferences.PreferenceProvider
import com.lnadeem.app.databinding.FragmentWeatherTodayBinding
import com.lnadeem.app.models.WeatherResponse
import com.lnadeem.app.ui.fonty.Fonty
import com.lnadeem.app.util.MyApiException
import com.lnadeem.app.util.NoInternetException
import com.lnadeem.app.util.showMessageDialog
import com.lnadeem.app.util.valueIsNeitherNullNorEmpty
import com.lnadeem.app.view_models.HomeViewModel
import com.lnadeem.app.vm_factories.HomeViewModelFactory
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*
import kotlin.collections.HashMap

class WeatherTodayFragment : BaseFragment<FragmentWeatherTodayBinding>(), KodeinAware {

    override val kodein by kodein()

    private val mFactory: HomeViewModelFactory by instance()
    private val mPrefProvider: PreferenceProvider by instance()

    private lateinit var mHomeViewModel: HomeViewModel

    override fun getFragmentLayout(): Int = R.layout.fragment_weather_today

    override fun onFragmentCreateView(savedInstanceState: Bundle?) {
        super.onFragmentCreateView(savedInstanceState)
        initView()
    }

    private fun initView() {
        setBaseCallback(baseCallback)
        mHomeViewModel = ViewModelProvider(this, mFactory)[HomeViewModel::class.java]

        mBinding.swipeRefreshLayout.isRefreshing = true
        getTodayWeatherAPI()

        mBinding.swipeRefreshLayout.setOnRefreshListener {
            getTodayWeatherAPI()
        }

        Fonty.setFonts(mBinding.swipeRefreshLayout)
    }

    private fun getTodayWeatherAPI() {
        lifecycleScope.launch {
            try {
                val queryMap = HashMap<String, String>()
                queryMap["q"] = mBinding.tvCityName.text.toString()
                queryMap["appid"] = "f3912bba660ef4e02a59c29bf78277d1"
                if (mPrefProvider.getCelsius()) {
                    queryMap["units"] = "metric"
                } else {
                    queryMap["units"] = "imperial"
                }
                queryMap["lang"] = mPrefProvider.getLanguage()!!
                val response = mHomeViewModel.getTodayWeather(queryMap)
                setData(response)
                mBinding.swipeRefreshLayout.isRefreshing = false
            } catch (ex: MyApiException) {
                mBinding.swipeRefreshLayout.isRefreshing = false
                ex.printStackTrace()
                if (valueIsNeitherNullNorEmpty(ex.message)) requireContext().showMessageDialog(ex.message.toString())
            } catch (ex: NoInternetException) {
                mBinding.swipeRefreshLayout.isRefreshing = false
                ex.printStackTrace()
            } catch (ex: Exception) {
                mBinding.swipeRefreshLayout.isRefreshing = false
                ex.printStackTrace()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData(response: WeatherResponse) {
        mBinding.tvCurrentTemperature1.text = response.main.temp.toString()
        mBinding.tvCurrentTemperature2.text = response.main.temp.toString() + "\u00B0"
        mBinding.tvFeelsLike.text = response.main.feels_like.toString() + "\u00B0"
        mBinding.tvHumidity.text = response.main.humidity.toString() + "%"
        mBinding.tvPressure.text = response.main.pressure.toString()

        if (response.weather.isNotEmpty()) {
            mBinding.tvDescription.text = response.weather[0].description
            Glide.with(this)
                .load("https://openweathermap.org/img/wn/${response.weather[0].icon}@2x.png")
                .disallowHardwareConfig().centerCrop().error(R.drawable.ic_cloud)
                .placeholder(R.drawable.ic_cloud).into(mBinding.ivIcon)
        }
    }

    private val baseCallback = object : BaseCallback {
        override fun onClick(view: View) {
            when (view.id) {
                R.id.iv_country_georgia -> mBinding.tvCityName.text = getString(R.string.tbilisi)
                R.id.iv_country_britain -> mBinding.tvCityName.text = getString(R.string.london)
                R.id.iv_country_jamaica -> mBinding.tvCityName.text = getString(R.string.kingston)
                R.id.iv_menu -> showOpenMenu()
            }
            mBinding.swipeRefreshLayout.isRefreshing = true
            getTodayWeatherAPI()
        }
    }

    private fun showOpenMenu() {
        val popupMenu = PopupMenu(requireContext(), mBinding.ivMenu)
        popupMenu.menuInflater.inflate(R.menu.option_menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_celsius -> {
                    mPrefProvider.setCelsius(true)
                    getTodayWeatherAPI()
                }
                R.id.action_fahrenheit -> {
                    mPrefProvider.setCelsius(false)
                    getTodayWeatherAPI()
                }
                R.id.action_language_settings -> {
                    startActivity(Intent(requireContext(), LanguageSettingsActivity::class.java))
                }
                R.id.action_sound_settings -> {
                    startActivity(Intent(requireContext(), SoundSettingsActivity::class.java))
                }
                R.id.action_web_view -> {
                    FragmentWebView().show(childFragmentManager, "FragmentWebView")
                }
            }
            goNextAnimation()
            true
        }
        popupMenu.show()
    }

    override fun onResume() {
        super.onResume()
        setLanguageForApp(mPrefProvider.getLanguage()!!)
        if (mPrefProvider.getCurrentThemeColor() != 0) mBinding.swipeRefreshLayout.setBackgroundColor(
            mPrefProvider.getCurrentThemeColor()
        )
    }

    private fun setLanguageForApp(selectedLanguageCode: String) {
        val locale = Locale(selectedLanguageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }
}