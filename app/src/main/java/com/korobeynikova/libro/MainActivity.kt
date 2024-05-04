package com.korobeynikova.libro

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.common.MobileAds
import com.yandex.mobile.ads.rewarded.Reward
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoadListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoader

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private var rewardedAd: RewardedAd? = null
    private var rewardedAdLoader: RewardedAdLoader? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        MobileAds.initialize(this){
            rewardedAdLoader = RewardedAdLoader(this).apply {
                setAdLoadListener(object : RewardedAdLoadListener {
                    override fun onAdFailedToLoad(error: AdRequestError) {
                        println("Реклама не загружена")
                    }

                    override fun onAdLoaded(rewarded: RewardedAd) {
                        rewardedAd = rewarded
                        println("Реклама загружена")
                    }
                })
            }
            loadRewAd()
        }

        // Настройка отступов для содержимого под системными панелями
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)

            // Установка цвета системных иконок
            ViewCompat.getWindowInsetsController(v)?.apply {
                this.isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
            }

            insets

        }
    }

    private fun loadRewAd(){
        val adRewardConfiguration = AdRequestConfiguration.Builder("demo-rewarded-yandex").build()
        rewardedAdLoader?.loadAd(adRewardConfiguration)
    }

    fun showAd(){
        rewardedAd?.apply {
            setAdEventListener(object : RewardedAdEventListener {
                override fun onAdClicked() {

                }

                override fun onAdDismissed() {
                    destroyRewAd()
                    loadRewAd()
                }

                override fun onAdFailedToShow(adError: AdError) {
                    destroyRewAd()
                    loadRewAd()
                }

                override fun onAdImpression(impressionData: ImpressionData?) {

                }

                override fun onAdShown() {

                }

                override fun onRewarded(reward: Reward) {
                    println("${reward.amount}")
                }
            })
            show(this@MainActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        rewardedAd?.setAdEventListener(null)
        rewardedAd = null
        destroyRewAd()
    }

    private fun destroyRewAd(){
        rewardedAd?.setAdEventListener(null)
        rewardedAd = null
    }
}
