package com.korobeynikova.libro

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView
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

class MainActivity : AppCompatActivity(){

    private var rewardedAd: RewardedAd? = null
    private var rewardedAdLoader: RewardedAdLoader? = null
    private lateinit var starsCountTextView: TextView
    lateinit var toggle: ActionBarDrawerToggle
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
            starsCountTextView = findViewById(R.id.starsCount)
            loadRewAd()
        }

        val drawerLayout : DrawerLayout = findViewById(R.id.main)
        val navView : NavigationView = findViewById(R.id.nav_main)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayUseLogoEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.auto -> Toast.makeText(applicationContext, "Home", Toast.LENGTH_SHORT).show()
                R.id.setting -> Toast.makeText(applicationContext, "setting", Toast.LENGTH_SHORT).show()
                R.id.profile -> Toast.makeText(applicationContext, "profile", Toast.LENGTH_SHORT).show()
                R.id.logOut -> Toast.makeText(applicationContext, "logOut", Toast.LENGTH_SHORT).show()
                R.id.help -> Toast.makeText(applicationContext, "help", Toast.LENGTH_SHORT).show()
                R.id.rate -> Toast.makeText(applicationContext, "rate", Toast.LENGTH_SHORT).show()
            }
            true
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)){
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainerView2,fragment)
            .commit()
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
                            updateStarsCount(newStars.toInt())
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

    fun updateStarsCount(stars: Int) {
        runOnUiThread {
            starsCountTextView.text = "$stars"
        }
    }
}
