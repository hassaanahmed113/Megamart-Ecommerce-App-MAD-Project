package com.example.megamartapp.rvadapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.megamartapp.Models.CartModel
import com.example.megamartapp.databinding.CartproductItemBinding

class CartAdapter(
    private val context: Context,
    private val list: ArrayList<CartModel>,
    private val onClickRemove: OnClickRemove,
    private val onQuantityChanged: OnQuantityChanged
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: CartproductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onClickRemove.onClickRemove(list[position], position)
                }
            }

            binding.btnCartItemAdd.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onQuantityChanged.onQuantityIncreased(list[position], position)
                }
            }

            binding.btnCartItemMinus.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onQuantityChanged.onQuantityDecreased(list[position], position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CartproductItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = list[position]

        Glide
            .with(context)
            .load(currentItem.imageUrl)
            .into(holder.binding.ivCartProduct)

        holder.binding.tvCartProductName.text = currentItem.name
        holder.binding.tvCartProductPrice.text = "Rs. ${currentItem.price}"
        holder.binding.tvCartItemCount.text = currentItem.quantity.toString()
        holder.binding.tvCartProductSize.text = currentItem.size
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnClickRemove {
        fun onClickRemove(item: CartModel, position: Int)
    }

    interface OnQuantityChanged {
        fun onQuantityIncreased(item: CartModel, position: Int)
        fun onQuantityDecreased(item: CartModel, position: Int)
    }
}