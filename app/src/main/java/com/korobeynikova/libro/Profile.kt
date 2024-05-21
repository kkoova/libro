package com.korobeynikova.libro

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.korobeynikova.libro.databinding.FragmentProfileBinding


class Profile : Fragment(), BookItemLikeClick  {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var uid: String
    private lateinit var database: DatabaseReference
    private lateinit var adapter: FavoriteBookAdapter
    private val booksList = mutableListOf<Book>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val exit = view.findViewById<ImageView>(R.id.exitImage)

        val controller = findNavController()

        updateVisibility()

        val recyclerView: RecyclerView = view.findViewById(R.id.likeRes)
        adapter = FavoriteBookAdapter(booksList, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        exit.setOnClickListener { controller.navigate(R.id.bookLibrary) }

        binding.progressBar.visibility = View.VISIBLE

        database = Firebase.database.reference
        uid = FirebaseAuth.getInstance().currentUser!!.uid

        binding.menuBtn.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }

        database.child("users").child(uid).get()
            .addOnSuccessListener {
                val login = it.child("username").value.toString()
                binding.textProfName.text = login
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Данные не были загруженны", Toast.LENGTH_SHORT).show()
            }

        loadFavoriteBooks()
        loadReadBooksCount()
    }
    private fun loadReadBooksCount() {
        val readBooksRef = database.child("users").child(uid).child("readBooks")
        readBooksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val readBooksCount = snapshot.childrenCount
                binding.textBook.text = readBooksCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Ошибка при загрузке количества прочитанных книг", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadFavoriteBooks() {
        val userRef = database.child("users").child(uid).child("likedBooks")
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                booksList.clear()
                updateVisibility()
                binding.textLikeBook.text = snapshot.childrenCount.toString()
                for (bookSnapshot in snapshot.children) {
                    val bookPath = bookSnapshot.getValue(String::class.java)
                    bookPath?.let { path ->
                        val bookRef = database.child(path)
                        bookRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(bookSnapshot: DataSnapshot) {
                                val title = bookSnapshot.child("title").getValue(String::class.java)
                                title?.let {
                                    val book = Book(it, path)
                                    booksList.add(book)
                                    adapter.updateData(booksList)
                                    updateVisibility()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                updateVisibility()
                            }
                        })
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Ошибка при загрузке избранных книг", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun updateVisibility() {
        if (booksList.size == 0) {
            binding.likeNo.visibility = View.VISIBLE
            binding.likeRes.visibility = View.GONE
        } else {
            binding.likeNo.visibility = View.GONE
            binding.likeRes.visibility = View.VISIBLE
        }
        binding.progressBar.postDelayed({
            binding.progressBar.visibility = View.GONE
        }, 500)
    }
    override fun onBookItemLikeClick(book: Book) {
        val bundle = Bundle()
        bundle.putString("bookPath", book.path)

        val navController = findNavController()
        navController.navigate(R.id.startBook, bundle)
    }
}