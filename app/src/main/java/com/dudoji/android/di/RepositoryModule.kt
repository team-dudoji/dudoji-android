package com.dudoji.android.di

import com.dudoji.android.data.repository.FollowRepositoryImpl
import com.dudoji.android.domain.repository.FollowRepository
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
}