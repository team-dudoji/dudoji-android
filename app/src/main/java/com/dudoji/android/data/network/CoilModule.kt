package com.dudoji.android.data.network

import android.content.Context
import coil.Coil
import coil.ImageLoader
import com.dudoji.android.network.Coil.imageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object CoilModule {
    @Provides
    @Singleton
    fun provideCoilImageLoader(
        @ApplicationContext context: Context,
        @Named("Authed") client: OkHttpClient
    ): ImageLoader {
        imageLoader = ImageLoader.Builder(context)
            .okHttpClient(client)
            .build()
        Coil.setImageLoader(imageLoader)

        return imageLoader
    }
}