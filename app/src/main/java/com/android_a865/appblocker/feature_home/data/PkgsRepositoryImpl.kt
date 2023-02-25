package com.android_a865.appblocker.feature_home.data

import com.android_a865.appblocker.feature_home.domain.AppsPackage
import com.android_a865.appblocker.feature_home.domain.PkgsRepository
import com.android_a865.appblocker.utils.toDomain
import com.android_a865.appblocker.utils.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class PkgsRepositoryImpl(
    private val dao: PkgDao
): PkgsRepository {

    override fun getPkgs(): Flow<List<AppsPackage>> {
        return dao.getItemsEntity().map { list ->
            list.map {pkg ->
                pkg.toDomain()
            }
        }
    }

    override suspend fun insertPkg(pkg: AppsPackage) {
        dao.insertPackage(
            pkg.toEntity()
        )
    }

    override suspend fun deletePkg(pkg: AppsPackage) {
        dao.deletePackage(
            pkg.toEntity()
        )
    }

    override suspend fun getActivePkg(): AppsPackage? {
        return dao.getActiveEntity()?.toDomain()
    }
}