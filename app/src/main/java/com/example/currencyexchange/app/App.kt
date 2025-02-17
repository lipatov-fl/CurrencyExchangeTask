package com.example.currencyexchange.app

import android.app.Application
import com.example.currencyexchange.di.AppComponent
import com.example.currencyexchange.di.AppModule
import com.example.currencyexchange.di.DaggerAppComponent

class App : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}