package com.example.currencyexchange.di

import com.example.currencyexchange.presentation.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, RepositoryModule::class, ViewModelModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
}