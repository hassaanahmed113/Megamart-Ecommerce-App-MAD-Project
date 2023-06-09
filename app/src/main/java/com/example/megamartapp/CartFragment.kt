package com.example.megamartapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.megamartapp.ToastMessage.toast
import com.example.megamartapp.Models.CartModel
import com.example.megamartapp.databinding.FragmentCartpageBinding
import com.example.megamartapp.rvadapters.CartAdapter
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class CartFragment : Fragment(R.layout.fragment_cartpage), CartAdapter.OnClickRemove, CartAdapter.OnQuantityChanged{

    private lateinit var binding: FragmentCartpageBinding
    private lateinit var cartList: ArrayList<CartModel>
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: CartAdapter
    private var totalPrice = 0


    private var orderDatabaseReference = Firebase.firestore.collection("cartItem")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding = FragmentCartpageBinding.bind(view)
        auth = FirebaseAuth.getInstance()
        binding.cartActualToolbar.setNavigationOnClickListener {
            Navigation.findNavController(requireView()).popBackStack()
        }


        val layoutManager = LinearLayoutManager(context)


        cartList = ArrayList()


        // progess bar
        val progressBarLayout = LayoutInflater.from(requireContext()).inflate(R.layout.layout_loading_indicator, null)

        val dialog = AlertDialog.Builder(requireContext(), R.style.TransparentDialogStyle)
            .setView(progressBarLayout)
            .create()

        dialog.show()
        Handler().postDelayed({
            dialog.dismiss()
        }, 2000)


        retrieveCartItems()


        adapter = CartAdapter(requireContext(),cartList ,this,this)
        binding.rvCartItems.adapter = adapter
        binding.rvCartItems.layoutManager = layoutManager


        binding.btnCartCheckout.setOnClickListener {
            if (cartList.isNotEmpty()) {

                AlertDialog.Builder(context)
                    .setTitle("Order Summary")
                    .setMessage("You have Ordered Product Worth Rs. $totalPrice\n" +
                            "Your Product will be delivered in the next 7 days.")
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
                cartList.clear()
                clearCart()
                binding.tvLastTotalPrice.text = "Min 1 product is required"
                binding.tvLastTotalPrice.setTextColor(Color.RED)
                adapter.notifyDataSetChanged()
            } else {
                binding.tvLastTotalPrice.text = "Min 1 product is required"
                binding.tvLastTotalPrice.setTextColor(Color.RED)
                adapter.notifyDataSetChanged()
            }
        }
    }


    @SuppressLint("SuspiciousIndentation")
    private fun retrieveCartItems() {
        if (cartList.isEmpty()) {
            orderDatabaseReference
                .whereEqualTo("uid", auth.currentUser!!.uid)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    for (item in querySnapshot) {
                        val cartProduct = item.toObject<CartModel>()
                        cartList.add(cartProduct)
                        totalPrice += cartProduct.price!!.toInt()
                        binding.tvLastTotalPrice.text = "Rs. ${totalPrice.toString()}"
                        adapter.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener {
                    requireActivity().toast(it.localizedMessage!!)
                }
        }
    }

    override fun onClickRemove(item: CartModel, position: Int) {
        orderDatabaseReference
            .whereEqualTo("uid", item.uid)
            .whereEqualTo("pid", item.pid)
            .whereEqualTo("size", item.size)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (item in querySnapshot) {
                    orderDatabaseReference.document(item.id).delete()
                    cartList.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    updateTotalPrices()
                    requireActivity().toast("Removed item from cart Successfully")
                }
            }
            .addOnFailureListener {
                requireActivity().toast("Failed to remove")
            }
    }

    private fun updateTotalPrices() {
        totalPrice = cartList.sumBy { it.price!!.toInt() }
        binding.tvLastTotalPrice.text =  "Rs. ${totalPrice.toString()}"

    }

    override fun onQuantityIncreased(item: CartModel, position: Int) {
        // Handle quantity increase logic
        val currentItem = cartList[position]
        currentItem.quantity = currentItem.quantity?.plus(1)
        totalPrice += currentItem.price!!.toInt()
        binding.tvLastTotalPrice.text = "Rs. ${totalPrice.toString()}"
        adapter.notifyItemChanged(position)
    }

    override fun onQuantityDecreased(item: CartModel, position: Int) {
        // Handle quantity decrease logic
        val currentItem = cartList[position]
        if (currentItem.quantity!! > 1) {
            currentItem.quantity = currentItem.quantity!! - 1
            totalPrice -= currentItem.price!!.toInt()
            binding.tvLastTotalPrice.text = "Rs. ${totalPrice.toString()}"
            adapter.notifyItemChanged(position)
        } else {

            requireActivity().toast("Minimum quantity of 1 is required")
        }
    }

    private fun clearCart() {
        val userId = auth.currentUser!!.uid

        orderDatabaseReference
            .whereEqualTo("uid", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val copyTasks = mutableListOf<Task<Void>>()
                for (item in querySnapshot) {
                    val orderData = item.toObject<CartModel>()
                    val copyTask = Firebase.firestore.collection("orders")
                        .document(item.id)
                        .set(orderData)
                    copyTasks.add(copyTask)
                }
                Tasks.whenAllComplete(copyTasks)
                    .addOnSuccessListener {
                        // Clear the cart
                        val deleteTasks = mutableListOf<Task<Void>>()
                        for (item in querySnapshot) {
                            val deleteTask = orderDatabaseReference.document(item.id).delete()
                            deleteTasks.add(deleteTask)
                        }
                        Tasks.whenAllComplete(deleteTasks)
                            .addOnSuccessListener {
                                cartList.clear()
                                adapter.notifyDataSetChanged()
                                updateTotalPrices()
                                requireActivity().toast("Cart cleared successfully")
                            }
                            .addOnFailureListener {
                                requireActivity().toast("Failed to remove items from cart")
                            }
                    }
            }
    }



}