package com.example.megamartapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.megamartapp.ToastMessage.toast
import com.example.megamartapp.Models.LikeModel
import com.example.megamartapp.databinding.FragmentLikepageBinding
import com.example.megamartapp.rvadapters.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class LikeFragment(): Fragment(R.layout.fragment_likepage), LikedProductOnClickInterface,
    LikedOnClickInterface {

    private lateinit var binding: FragmentLikepageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: LikeAdapter
    private lateinit var likedProductList: ArrayList<LikeModel>


    private var likeDBRef = Firebase.firestore.collection("LikedProducts")


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLikepageBinding.bind(view)
        auth = FirebaseAuth.getInstance()
        likedProductList = ArrayList()
        adapter = LikeAdapter(requireContext(),likedProductList,this,this)


        binding.likeActualToolbar.setNavigationOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }


        val productLayoutManager = GridLayoutManager(context, 2)
        binding.rvLikedProducts.layoutManager = productLayoutManager
        binding.rvLikedProducts.adapter = adapter


        displayLikedProducts()

    }


    @SuppressLint("SuspiciousIndentation")
    private fun displayLikedProducts() {

        likeDBRef
            .whereEqualTo("uid" , auth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (item in querySnapshot) {
                    val likedProduct = item.toObject<LikeModel>()
                        likedProductList.add(likedProduct)
                        adapter.notifyDataSetChanged()
                }

            }
            .addOnFailureListener{
                requireActivity().toast(it.localizedMessage!!)
            }
    }

    override fun onClickProduct(item: LikeModel) {
        val direction = item.pid?.let {
            LikeFragmentDirections.actionLikeFragment2ToDetailsFragment(
                it
            )
        }
        if (direction != null) {
            Navigation.findNavController(requireView()).navigate(direction)
        }
    }

    override fun onClickLike(item: LikeModel) {

        likeDBRef
            .whereEqualTo("uid",auth.currentUser!!.uid)
            .whereEqualTo("pid",item.pid)
            .get()
            .addOnSuccessListener { querySnapshot ->

                for (item in querySnapshot){
                    likeDBRef.document(item.id).delete()
                    likedProductList.remove(item.toObject<LikeModel>())
                    adapter.notifyDataSetChanged()
                    requireActivity().toast("Removed item from wishlist successfully")
                }

            }
            .addOnFailureListener {
                requireActivity().toast("Failed to remove from wishlist items")
            }

    }

}