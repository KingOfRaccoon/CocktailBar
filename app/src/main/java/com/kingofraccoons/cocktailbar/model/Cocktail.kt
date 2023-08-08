package com.kingofraccoons.cocktailbar.model

import android.net.Uri
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.bson.types.ObjectId

open class Cocktail(
    @PrimaryKey var id: ObjectId = ObjectId(),
    var title: String,
    var desc: String,
    var ingredients: RealmList<String>,
    var recipe: String,
    var image: String
) : RealmObject() {
    constructor() : this(ObjectId(), "", "", RealmList(), "", "")
}