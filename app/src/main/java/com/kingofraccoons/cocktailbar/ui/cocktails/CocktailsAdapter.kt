package com.kingofraccoons.cocktailbar.ui.cocktails

import android.media.browse.MediaBrowser.ItemCallback
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kingofraccoons.cocktailbar.R
import com.kingofraccoons.cocktailbar.databinding.ItemCocktailBinding
import com.kingofraccoons.cocktailbar.model.Cocktail
import org.bson.types.ObjectId

class CocktailsAdapter(
    val openDetail : (ObjectId) -> Unit
) : ListAdapter<Cocktail, CocktailsAdapter.CocktailViewHolder>(itemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailViewHolder {
        return CocktailViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cocktail, parent, false))
    }

    override fun onBindViewHolder(holder: CocktailViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class CocktailViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val binding = ItemCocktailBinding.bind(view)

        fun bind(cocktail: Cocktail) {
            binding.imageCocktail.setImageURI(Uri.parse(cocktail.image))
            binding.textTitle.text = cocktail.title
            binding.root.setOnClickListener {
                openDetail(cocktail.id)
            }
        }
    }

    companion object {
        val itemCallback = object: DiffUtil.ItemCallback<Cocktail>(){
            override fun areItemsTheSame(oldItem: Cocktail, newItem: Cocktail): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: Cocktail, newItem: Cocktail): Boolean {
                return oldItem == newItem
            }
        }
    }
}