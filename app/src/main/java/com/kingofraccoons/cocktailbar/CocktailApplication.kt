package com.kingofraccoons.cocktailbar

import android.app.Application
import com.getkeepsafe.relinker.BuildConfig
import com.kingofraccoons.cocktailbar.repository.CocktailRepository
import com.kingofraccoons.cocktailbar.repository.database.CocktailsDao
import com.kingofraccoons.cocktailbar.repository.viewmodel.CocktailViewModel
import io.realm.Realm
import io.realm.RealmConfiguration
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class CocktailApplication : Application() {
    private val module = module {
        single { CocktailsDao() }
        single { CocktailRepository(get()) }
        single { CocktailViewModel(get()) }
    }

    override fun onCreate() {
        super.onCreate()
        Realm.init(this@CocktailApplication)
        val config = RealmConfiguration.Builder()
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)

        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(applicationContext)
            modules(module)
        }
    }
}