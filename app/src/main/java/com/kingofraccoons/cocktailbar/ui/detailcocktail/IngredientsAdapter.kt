package com.kingofraccoons.cocktailbar.ui.detailcocktail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kingofraccoons.cocktailbar.R
import com.kingofraccoons.cocktailbar.databinding.ItemIngredientBinding

class IngredientsAdapter(private val list: List<String>): RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        return IngredientViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ingredient, parent, false))
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    inner class IngredientViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val binding = ItemIngredientBinding.bind(view)

        fun bind(name: String){
            binding.root.text = name
        }
    }
}