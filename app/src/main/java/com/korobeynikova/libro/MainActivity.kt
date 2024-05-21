package com.korobeynikova.libro

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.korobeynikova.libro.databinding.FragmentLibroStartOneBinding
import com.korobeynikova.libro.databinding.FragmentProfileBinding
import com.korobeynikova.libro.databinding.FragmentSettingsProfileBinding
import com.korobeynikova.libro.databinding.HederMenuBinding
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
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: HederMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = HederMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
        val navController = navHostFragment.navController

        //updateStarsCount()

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

        val drawerLayout : DrawerLayout = findViewById(R.id.main)
        val navView : NavigationView = findViewById(R.id.nav_main)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayUseLogoEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.auto -> {
                    navController.navigate(R.id.bookLibrary)
                    closeDrawer()
                }
                R.id.setting -> {
                    navController.navigate(R.id.settingsProfile)
                    closeDrawer()
                }
                R.id.profile -> {
                    navController.navigate(R.id.profile)
                    closeDrawer()
                }
                R.id.logOut -> {
                    firebaseAuth = FirebaseAuth.getInstance()
                    val dialog = MyDialogFragment()
                    dialog.setButtons(
                        "Выйти",
                        "Отмена",
                        "Подтверждение выхода",
                        "Вы точно хотите выйти из аккаунта?",
                        {
                            firebaseAuth.signOut()
                            Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(this, MainLog::class.java)
                            startActivity(intent)
                            finish()
                        }, { })

                    dialog.show(supportFragmentManager, "MyDialogFragment")
                    closeDrawer()
                }
                R.id.help -> {
                    Toast.makeText(applicationContext, "help", Toast.LENGTH_SHORT).show()
                }
                R.id.rate -> {
                    Toast.makeText(applicationContext, "rate", Toast.LENGTH_SHORT).show()
                }
                R.id.fof -> {
                    val dialog = MyDialogFragment()
                    dialog.setButtons(
                        "Реклама",
                        "Отмена",
                        "Получение цветочков",
                        "По просмотру рекламы, вы получите 15 цветочков",
                        {
                            showAd()
                        }, { })
                    dialog.show(supportFragmentManager, "MyDialogFragment")
                }
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
    fun openDrawer() {
        val drawerLayout : DrawerLayout = findViewById(R.id.main)
        drawerLayout.openDrawer(GravityCompat.START)
    }

    fun closeDrawer() {
        val drawerLayout : DrawerLayout = findViewById(R.id.main)
        drawerLayout.closeDrawer(findViewById(R.id.nav_main))
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
                            binding.starsCountTextView.text = "$stars"
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

    private fun updateStarsCount() {
        val database = FirebaseDatabase.getInstance().reference
        val uid = FirebaseAuth.getInstance().currentUser!!.uid

        database.child("users").child(uid).get()
            .addOnSuccessListener { dataSnapshot ->
                val starsValue = dataSnapshot.child("stars").value
                binding.starsCountTextView.text = "$starsValue"
            }
    }
}
