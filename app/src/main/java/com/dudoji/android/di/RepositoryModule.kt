package com.dudoji.android.di

import com.dudoji.android.data.datasource.location.GPSLocationService
import com.dudoji.android.data.datasource.location.LocationService
import com.dudoji.android.data.repository.FollowRepositoryImpl
import com.dudoji.android.data.repository.LocationRepositoryImpl
import com.dudoji.android.data.repository.PinSkinRepositoryImpl
import com.dudoji.android.domain.repository.FollowRepository
import com.dudoji.android.domain.repository.LocationRepository
import com.dudoji.android.domain.repository.PinSkinRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindFollowRepository(
        impl: FollowRepositoryImpl
    ): FollowRepository

    @Binds
    abstract fun bindLocationService(
        impl: GPSLocationService
    ): LocationService

    @Binds
    abstract fun bindLocationRepository(
        impl: LocationRepositoryImpl
    ): LocationRepository

    @Binds
    abstract fun bindPinSkinRepository(
        impl: PinSkinRepositoryImpl
    ): PinSkinRepository
}