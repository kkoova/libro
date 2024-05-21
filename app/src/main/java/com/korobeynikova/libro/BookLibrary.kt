package com.korobeynikova.libro

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.korobeynikova.libro.databinding.FragmentBookLibraryBinding

class BookLibrary : Fragment(), BookItemClickListener {

    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var binding: FragmentBookLibraryBinding
    private lateinit var database: DatabaseReference
    private lateinit var recyclerViewBooks: RecyclerView
    private lateinit var bookKlass: String
    private var booksList = mutableListOf<Book>()

    private var mainActivity: MainActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mainActivity = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mainActivity = null
    }
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

        firebaseAuth = FirebaseAuth.getInstance()
        binding.nineKl.setBackgroundResource(R.drawable.custom_button_black)
        binding.nineKl.setTextColor(Color.WHITE)

        buttonClick()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {requireActivity().finish()}

        setupRecyclerView()

        text()

        binding.menuBtn.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }
    }
    private fun updateStarsCount(newStars: String) {
        //val starsCount = view?.findViewById<TextView>(R.id.starsCount)
        //starsCount?.text = newStars
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
                    updateStarsCount(stars)
                    binding.helloTextLibr.text = "Привет, $login!"
                }.addOnFailureListener {

                }
        } else {
            val intent = Intent(context, MainLog::class.java)
            startActivity(intent)
            MainActivity().finish()
        }

        binding.searchView.setOnClickListener {
            val dialog = SearchBooksDialog { title, author, classes ->
                performSearch(title, author, classes)
            }
            dialog.show(childFragmentManager, "SearchBooksDialog")
        }
    }

    private fun performSearch(title: String?, author: String?, classes: List<String>) {
        Log.d("help", "${title} ${author} ${classes}")
        val query = FirebaseDatabase.getInstance().reference.child("books")
        val bookList = mutableListOf<Book>()

        // Поиск по названию
        if (!title.isNullOrEmpty()) {
            for (className in classes) {
                query.child(className).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            val bookTitle = snapshot.child("title").getValue(String::class.java)
                            Log.d("help", "${title} ${bookTitle}")
                            if (bookTitle == title){
                                val path = "books/$className/${snapshot.key}"
                                Log.d("help", "${path}")
                                val book = Book(bookTitle, path)
                                bookList.add(book)
                            }
                        }
                        updateRecyclerView(bookList)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(requireContext(), "Ошибка при загрузке данных", Toast.LENGTH_SHORT).show()
                        updateRecyclerView(bookList)
                    }
                })
            }
        }
        // Поиск по автору
        if (!author.isNullOrEmpty()) {
            for (className in classes) {
                query.child(className).orderByChild("author").equalTo(author).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (snapshot in dataSnapshot.children) {
                            val bookTitle = snapshot.child("author").getValue(String::class.java)
                            bookTitle?.let {
                                val path = "books/$className/${snapshot.key}"
                                val book = Book(it, path)
                                if (!bookList.contains(book)) {
                                    bookList.add(book)
                                }
                            }
                        }
                        updateRecyclerView(bookList)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(requireContext(), "Ошибка при загрузке данных", Toast.LENGTH_SHORT).show()
                        updateRecyclerView(bookList)
                    }
                })
            }
        }
    }

    private fun updateRecyclerView(bookList: List<Book>) {
        Log.d("SearchBooksDialog", "Updating RecyclerView with ${bookList.size} books")

        val backgroundImagesArray = getBackgroundImagesArray()
        val bookAdapter = BookAdapter(bookList, backgroundImagesArray, this@BookLibrary)
        binding.recyclerViewBooks.adapter = bookAdapter
        binding.recyclerViewBooks.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupRecyclerView() {
        val query = FirebaseDatabase.getInstance().reference.child("books").child(bookKlass)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                booksList.clear()
                if (binding.progressBar != null) {
                    binding.progressBar.visibility = View.GONE
                }
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
    private fun exitProfile(){
        val dialog = MyDialogFragment()
        dialog.setButtons(
            "Выйти",
            "Отмена",
            "Подтверждение выхода",
            "Вы точно хотите выйти из аккаунта?",
            {
                firebaseAuth.signOut()
                Toast.makeText(requireContext(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(context, MainLog::class.java)
                startActivity(intent)
                MainActivity().finish()
            }, { })
        dialog.show(childFragmentManager, "MyDialogFragment")
    }
}
