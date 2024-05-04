package com.korobeynikova.libro

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.korobeynikova.libro.databinding.FragmentStartBookBinding

class CardTwoFragment : DialogFragment() {
    private var positiveOneText: String? = null
    private var positiveTwoText: String? = null
    private var positiveOneAction: (() -> Unit)? = null
    private var positiveTwoAction: (() -> Unit)? = null
    fun setButtons(
        positiveText: String,
        negativeText: String,
        positiveAction: () -> Unit,
        negativeAction: () -> Unit
    ) {
        this.positiveOneText = positiveText
        this.positiveTwoText = negativeText
        this.positiveOneAction = positiveAction
        this.positiveTwoAction = negativeAction
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.card_read, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val yesButton = view.findViewById<Button>(R.id.oneBtn)
        val noButton = view.findViewById<Button>(R.id.oneOneBtn)

        yesButton.text = positiveOneText
        noButton.text = positiveTwoText

        yesButton.setOnClickListener {
            positiveOneAction?.invoke()
            dismiss()
        }

        noButton.setOnClickListener {
            positiveTwoAction?.invoke()
            dismiss()
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
}
class StartBook : Fragment() {

    private lateinit var binding: FragmentStartBookBinding
    private lateinit var bookGo: String
    private lateinit var dialog: CardTwoFragment
    private lateinit var database: DatabaseReference
    private lateinit var stars: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fonCont.setBackgroundResource(getBackgroundImagesArray().random())
        val bookPath = arguments?.getString("bookPath")
        bookGo = bookPath.toString()
        settings(bookGo)

        val exit = view.findViewById<ImageView>(R.id.exitImage)
        val like = view.findViewById<ImageView>(R.id.likeImage)
        val controller = findNavController()
        exit.setOnClickListener { controller.navigate(R.id.bookLibrary) }

        dialog = CardTwoFragment()
    }

    private fun getBackgroundImagesArray(): IntArray {
        return intArrayOf(R.drawable.fon_book_1, R.drawable.fon_book_2, R.drawable.fon_book_3)
    }

    private fun settings(path: String) {

        val databaseReference = FirebaseDatabase.getInstance().reference

        databaseReference.child(path).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (binding.progressBar != null){
                    binding.progressBar.visibility = View.GONE
                }

                if (dataSnapshot.exists()) {
                    val title = dataSnapshot.child("title").getValue(String::class.java)
                    val author = dataSnapshot.child("author").getValue(String::class.java)
                    val age = dataSnapshot.child("age").getValue(String::class.java)

                    binding.titleText.text = title
                    binding.autorText.text = author
                    binding.ageText.text = age
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Ошибка при выполнении запроса", Toast.LENGTH_SHORT).show()
            }
        })

        binding.button.setOnClickListener {
            val dialog = CardTwoFragment()
            dialog.setButtons(
                "Читать",
                "35 цветочков",
                {
                    val bundle = Bundle()
                    bundle.putString("bookPath", bookGo)
                    bundle.putString("vid", "Краткое содержание")
                    bundle.putString("text", "text")

                    val navController = findNavController()
                    navController.navigate(R.id.readBook, bundle)
                }, {
                    database = FirebaseDatabase.getInstance().reference
                    val uid = FirebaseAuth.getInstance().currentUser!!.uid

                    database.child("users").child(uid).get()
                        .addOnSuccessListener { dataSnapshot ->
                            val starsValue = dataSnapshot.child("stars").value
                            if (starsValue != null) {
                                val stars = starsValue.toString().toInt()
                                if (stars >= 35) {
                                    val newStars = (stars - 35).toString()
                                    database.child("users").child(uid).child("stars").setValue(newStars)
                                    val bundle = Bundle()
                                    bundle.putString("bookPath", bookGo)
                                    bundle.putString("vid", "Очень краткое содержание")
                                    bundle.putString("text", "mineText")

                                    val navController = findNavController()
                                    navController.navigate(R.id.readBook, bundle)
                                } else {
                                    Toast.makeText(requireContext(), "Недостаточно цветочков", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                            }
                        }
                    database.child("users").child(uid).get()
                })
            dialog.show(childFragmentManager, "MyDialogFragment")
        }
    }
}