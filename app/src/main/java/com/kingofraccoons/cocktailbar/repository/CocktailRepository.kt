package com.kingofraccoons.cocktailbar.repository

import com.kingofraccoons.cocktailbar.model.Cocktail
import com.kingofraccoons.cocktailbar.repository.database.CocktailsDao
import org.bson.types.ObjectId

class CocktailRepository(private val cocktailsDao: CocktailsDao) {

    fun getCocktails() = cocktailsDao.getCocktails()
    fun addCocktail(cocktail: Cocktail) = cocktailsDao.addCocktail(cocktail)
    fun removeCocktail(cocktailId: ObjectId) = cocktailsDao.removeCocktail(cocktailId)
}