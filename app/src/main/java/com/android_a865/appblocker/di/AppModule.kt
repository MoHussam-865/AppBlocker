package com.android_a865.appblocker.di

import android.app.Application
import androidx.room.Room
import com.android_a865.appblocker.common.MyDatabase
import com.android_a865.appblocker.common.MyDatabase.Companion.DATABASE_NAME
import com.android_a865.appblocker.feature_home.data.PkgsRepositoryImpl
import com.android_a865.appblocker.feature_home.domain.PkgsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRoomDatabase(app: Application): MyDatabase =
        Room.databaseBuilder(app, MyDatabase::class.java, DATABASE_NAME)
            .build()

    @Provides
    @Singleton
    fun providePkgsRepository(db: MyDatabase): PkgsRepository {
        return PkgsRepositoryImpl(db.getPkgs())
    }


}