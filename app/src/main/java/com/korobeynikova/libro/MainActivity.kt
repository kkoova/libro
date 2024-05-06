package com.korobeynikova.libro

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
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
class MainActivity : AppCompatActivity(), AdListener {

    private var rewardedAd: RewardedAd? = null
    private var rewardedAdLoader: RewardedAdLoader? = null
    private lateinit var adListener: AdListener
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, 0)

            ViewCompat.getWindowInsetsController(v)?.apply {
                this.isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
            }

            insets
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as? NavHostFragment
        val navController = navHostFragment?.navController


        val bookLibraryFragment = navHostFragment!!.childFragmentManager.findFragmentById(R.id.bookLibrary) as? BookLibrary
        bookLibraryFragment?.setAdListener(this)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainerView2,fragment)
            .commit()
    }

    override fun onAdWatched(newStars: String) {
        val bookLibraryFragment = supportFragmentManager.findFragmentById(R.id.bookLibrary) as? BookLibrary
        bookLibraryFragment?.updateStarsCount(newStars)
    }

    private fun loadRewAd(){
        val adRewardConfiguration = AdRequestConfiguration.Builder("R-M-8167134-2").build()
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
                    val database = FirebaseDatabase.getInstance().reference
                    val uid = FirebaseAuth.getInstance().currentUser!!.uid

                    database.child("users").child(uid).get()
                        .addOnSuccessListener { dataSnapshot ->
                            val starsValue = dataSnapshot.child("stars").value
                            val stars = starsValue.toString().toInt()
                            val newStars = (stars + 15).toString()
                            database.child("users").child(uid).child("stars").setValue(newStars)
                            if (::adListener.isInitialized) {
                                adListener.onAdWatched(newStars)
                            } else {
                                Log.d("MainActivity", "adListener is not initialized")
                            }
                        }
                        .addOnCanceledListener {}
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
