package com.kingofraccoons.cocktailbar.repository.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kingofraccoons.cocktailbar.model.Cocktail
import com.kingofraccoons.cocktailbar.repository.CocktailRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.bson.types.ObjectId

class CocktailViewModel(private val cocktailRepository: CocktailRepository) : ViewModel() {
    private val _cocktailsStateFlow = MutableStateFlow(listOf<Cocktail>())
    val cocktailsLiveData = _cocktailsStateFlow.asLiveData(viewModelScope.coroutineContext)

    init {
        loadCocktails()
    }

    private fun loadCocktails() {
        cocktailRepository.getCocktails().addChangeListener { t, _ ->
            viewModelScope.launch(Dispatchers.Main) {
                _cocktailsStateFlow.emit(t)
            }
        }
    }

    fun addCocktail(cocktail: Cocktail) {
        viewModelScope.launch(Dispatchers.Main) {
            cocktailRepository.addCocktail(cocktail)
        }
    }

    fun deleteCocktail(cocktailId: ObjectId){
        viewModelScope.launch(Dispatchers.Main) {
            cocktailRepository.removeCocktail(cocktailId)
        }
    }
}