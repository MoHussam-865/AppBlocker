package com.android_a865.appblocker.feature_home.domain

import kotlinx.coroutines.flow.Flow

interface PkgsRepository {
    fun getPkgs(): Flow<List<AppsPackage>>

    suspend fun insertPkg(pkg: AppsPackage)

    suspend fun deletePkg(pkg: AppsPackage)

}