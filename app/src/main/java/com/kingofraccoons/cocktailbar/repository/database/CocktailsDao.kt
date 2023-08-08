package com.kingofraccoons.cocktailbar.repository.database

import com.kingofraccoons.cocktailbar.model.Cocktail
import io.realm.Realm
import io.realm.RealmResults
import org.bson.types.ObjectId

class CocktailsDao{
    private val realm = Realm.getDefaultInstance()

    fun addCocktail(cocktail: Cocktail){
        realm.executeTransactionAsync {
            val cocktailUpdate = it.where(Cocktail::class.java).equalTo("id", cocktail.id).findFirst()
            println(cocktailUpdate)
            if (cocktailUpdate != null){
                cocktailUpdate.image = cocktail.image
                cocktailUpdate.ingredients = cocktail.ingredients
                cocktailUpdate.desc = cocktail.desc
                cocktailUpdate.recipe = cocktail.recipe
                cocktailUpdate.title = cocktail.title
            } else {
                it.insertOrUpdate(cocktail)
            }
        }
    }

    fun getCocktails(): RealmResults<Cocktail> {
        return realm.where(Cocktail::class.java).findAllAsync()
    }

    fun removeCocktail(cocktailId: ObjectId) {
        realm.executeTransactionAsync {
            val cocktailDelete = it.where(Cocktail::class.java)
                .equalTo("id", cocktailId)
                .findFirst()
            cocktailDelete?.deleteFromRealm()
        }
    }
}