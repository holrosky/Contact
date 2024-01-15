package com.qcellls.contact.di

import android.content.Context
import com.qcellls.contact.repository.ContactRepository
import com.qcellls.contact.repository.ContactRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideContactRepository(@ApplicationContext appContext: Context): ContactRepository {
        return ContactRepositoryImpl(appContext)
    }
}