package com.lnadeem.app.ui.home

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.lnadeem.app.R
import com.lnadeem.app.base.BaseCallback
import com.lnadeem.app.base.BaseFragment
import com.lnadeem.app.data.preferences.PreferenceProvider
import com.lnadeem.app.databinding.FragmentWeatherHourlyBinding
import com.lnadeem.app.models.HourlyWeatherResponse
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

class WeatherHourlyFragment : BaseFragment<FragmentWeatherHourlyBinding>(), KodeinAware {

    override val kodein by kodein()

    private val mFactory: HomeViewModelFactory by instance()
    private val mPrefProvider: PreferenceProvider by instance()

    private lateinit var mHomeViewModel: HomeViewModel

    private var mWeatherAdapter = GroupAdapter<GroupieViewHolder>()

    override fun getFragmentLayout(): Int = R.layout.fragment_weather_hourly

    override fun onFragmentCreateView(savedInstanceState: Bundle?) {
        super.onFragmentCreateView(savedInstanceState)
        initView()
    }

    private fun initView() {
        setBaseCallback(baseCallback)
        mHomeViewModel = ViewModelProvider(this, mFactory)[HomeViewModel::class.java]
        mBinding.rvHourlyWeather.adapter = mWeatherAdapter

        mBinding.swipeRefreshLayout.isRefreshing = true
        getHourlyWeatherAPI()

        mBinding.swipeRefreshLayout.setOnRefreshListener {
            getHourlyWeatherAPI()
        }

        Fonty.setFonts(mBinding.swipeRefreshLayout)
    }

    private fun getHourlyWeatherAPI() {
        mWeatherAdapter.clear()
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
                val response = mHomeViewModel.getHourlyWeather(queryMap)
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

    private fun setData(response: HourlyWeatherResponse) {
        mWeatherAdapter.addAll(response.list.toWeatherItems())
    }

    private val baseCallback = object : BaseCallback {
        override fun onClick(view: View) {
            when (view.id) {
                R.id.iv_country_georgia -> mBinding.tvCityName.text = getString(R.string.tbilisi)
                R.id.iv_country_britain -> mBinding.tvCityName.text = getString(R.string.london)
                R.id.iv_country_jamaica -> mBinding.tvCityName.text = getString(R.string.kingston)
            }
            mBinding.swipeRefreshLayout.isRefreshing = true
            getHourlyWeatherAPI()
        }
    }

    private fun List<WeatherResponse>.toWeatherItems(): List<BindableItemWeather> {
        return this.map {
            BindableItemWeather(it)
        }
    }

    override fun onResume() {
        super.onResume()
        setLanguageForApp(mPrefProvider.getLanguage()!!)
        if (mPrefProvider.getCurrentThemeColor() != 0)
            mBinding.swipeRefreshLayout.setBackgroundColor(mPrefProvider.getCurrentThemeColor())
    }

    private fun setLanguageForApp(selectedLanguageCode: String) {
        val locale = Locale(selectedLanguageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }
}