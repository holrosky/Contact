package com.qcellls.contact.data.module

import android.content.Context
import com.qcellls.contact.data.repository.ContactRepositoryImpl
import com.qcellls.contact.ui.repository.ContactRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideContactRepository(@ApplicationContext appContext: Context): ContactRepository {
        return ContactRepositoryImpl(appContext)
    }
}