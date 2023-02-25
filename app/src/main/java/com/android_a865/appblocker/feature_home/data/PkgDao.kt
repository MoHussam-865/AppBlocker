package com.android_a865.appblocker.feature_home.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PkgDao {

    @Query("SELECT * FROM Packages")
    fun getItemsEntity(): Flow<List<PkgEntity>>

    @Query("SELECT * FROM Packages WHERE isActive = 1")
    suspend fun getActiveEntity(): PkgEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackage(pkg: PkgEntity)

    @Delete
    suspend fun deletePackage(pkg: PkgEntity)
}