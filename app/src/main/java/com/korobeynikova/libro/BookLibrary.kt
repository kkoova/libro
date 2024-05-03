package com.korobeynikova.libro

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.korobeynikova.libro.databinding.FragmentBookLibraryBinding
import com.yandex.mobile.ads.common.MobileAds
import com.yandex.mobile.ads.rewarded.RewardedAd

const val AD_ID = ""
class BookLibrary : Fragment(), BookItemClickListener {

    private lateinit var profileBtn: ImageView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: FragmentBookLibraryBinding
    private lateinit var database: DatabaseReference
    private lateinit var recyclerViewBooks: RecyclerView
    private lateinit var bookKlass: String
    private var rewardedAd: RewardedAd? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bookKlass = "nine"
        binding = FragmentBookLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileBtn = view.findViewById(R.id.profileBtn)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.nineKl.setBackgroundResource(R.drawable.custom_button_black)
        binding.nineKl.setTextColor(Color.WHITE)

        buttonClick()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}
        setupRecyclerView()
        val book = view.findViewById<ImageView>(R.id.bookBnt)

        val color = ContextCompat.getColor(requireContext(), R.color.black)
        book.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        text()

        MobileAds.initialize(requireContext()) {}

        loadRewardedAd()

        binding.floofers.setOnClickListener {
            rewardedAd?.show(requireActivity()) {}
                database = FirebaseDatabase.getInstance().reference
                val uid = FirebaseAuth.getInstance().currentUser!!.uid

                database.child("users").child(uid).get()
                    .addOnSuccessListener { dataSnapshot ->
                        val starsValue = dataSnapshot.child("stars").value
                        val stars = starsValue.toString().toInt()
                        val newStars = (stars + 35).toString()
                        database.child("users").child(uid).child("stars").setValue(newStars)
                    }
                Toast.makeText(requireContext(), "Поздравляю! вы получили 35 цветочков", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadRewardedAd() {
        // Initialize rewarded ad
        rewardedAd = RewardedAd(requireContext(), "8167134")

        // Set rewarded ad event listener
        rewardedAd?.setRewardedAdEventListener(object : RewardedAdEventListener() {
            override fun onRewarded(ad: RewardedAd) {
                // Handle rewarded ad event
                Toast.makeText(requireContext(), "Rewarded!", Toast.LENGTH_SHORT).show()
            }

            override fun onRewardedFailedToLoad(ad: RewardedAd) {
                // Handle rewarded ad failed to load event
                Toast.makeText(requireContext(), "Failed to load rewarded ad", Toast.LENGTH_SHORT).show()
            }

            override fun onRewardedStarted(ad: RewardedAd) {
                // Handle rewarded ad started event
            }

            override fun onRewardedStopped(ad: RewardedAd) {
                // Handle rewarded ad stopped event
            }
        })

        // Load rewarded ad
        rewardedAd?.loadAd()
    }

    private fun text(){
        val clickListener = View.OnClickListener { view ->

            binding.nineKl.setBackgroundResource(R.drawable.custom_button_white)
            binding.eghtKl.setBackgroundResource(R.drawable.custom_button_white)
            binding.sevenKl.setBackgroundResource(R.drawable.custom_button_white)
            binding.sixKl.setBackgroundResource(R.drawable.custom_button_white)
            binding.fiveKl.setBackgroundResource(R.drawable.custom_button_white)

            binding.nineKl.setTextColor(Color.BLACK)
            binding.eghtKl.setTextColor(Color.BLACK)
            binding.sevenKl.setTextColor(Color.BLACK)
            binding.sixKl.setTextColor(Color.BLACK)
            binding.fiveKl.setTextColor(Color.BLACK)

            view.setBackgroundResource(R.drawable.custom_button_black)
            (view as TextView).setTextColor(Color.WHITE)

            when (view.id) {
                R.id.nineKl -> bookKlass = "nine"
                R.id.eghtKl -> bookKlass = "eight"
                R.id.sevenKl -> bookKlass = "seven"
                R.id.sixKl -> bookKlass = "six"
                R.id.fiveKl -> bookKlass = "five"
            }

            setupRecyclerView()
        }

        binding.nineKl.setOnClickListener(clickListener)
        binding.eghtKl.setOnClickListener(clickListener)
        binding.sevenKl.setOnClickListener(clickListener)
        binding.sixKl.setOnClickListener(clickListener)
        binding.fiveKl.setOnClickListener(clickListener)

    }

    private fun buttonClick(){
        val container = findNavController()
        val currentUser = firebaseAuth.currentUser
        database = FirebaseDatabase.getInstance().reference

        recyclerViewBooks = binding.recyclerViewBooks

        if (currentUser != null) {
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            database.child("users").child(uid).get()
                .addOnSuccessListener {
                    val login = it.child("username").value.toString()
                    val stars = it.child("stars").value.toString()
                    binding.starsCount.text = stars
                    binding.helloTextLibr.text = "Привет, $login!"
                }.addOnFailureListener {

                }
        } else {
            val intent = Intent(context, MainLog::class.java)
            startActivity(intent)
            MainActivity().finish()
        }

        profileBtn.setOnClickListener{
            container.navigate(R.id.profile)
        }
    }

    private fun setupRecyclerView() {
        val query = FirebaseDatabase.getInstance().reference.child("books").child(bookKlass)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (binding.progressBar != null){
                    binding.progressBar.visibility = View.GONE
                }
                val booksList = mutableListOf<Book>()
                for (snapshot in dataSnapshot.children) {
                    val title = snapshot.child("title").getValue(String::class.java)
                    title?.let {
                        val path = "books/" + snapshot.ref.parent!!.key + "/" + snapshot.key
                        val book = Book(it, path)
                        booksList.add(book)
                    }
                }
                val backgroundImagesArray = getBackgroundImagesArray()
                val bookAdapter = BookAdapter(booksList, backgroundImagesArray, this@BookLibrary)
                binding.recyclerViewBooks.adapter = bookAdapter
                binding.recyclerViewBooks.layoutManager = LinearLayoutManager(requireContext())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Ошибка при загрузке данных", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onBookItemClick(book: Book) {
        val bundle = Bundle()
        bundle.putString("bookPath", book.path)

        val navController = findNavController()
        navController.navigate(R.id.startBook, bundle)
    }

    private fun getBackgroundImagesArray(): IntArray {
        return intArrayOf(R.drawable.fon_1, R.drawable.fon_2, R.drawable.fon_3, R.drawable.fon_4,
            R.drawable.fon_5, R.drawable.fon_6, R.drawable.fon_7, R.drawable.fon_8, R.drawable.fon_9,
            R.drawable.fon_10)
    }

}
