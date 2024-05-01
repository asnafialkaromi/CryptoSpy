package com.nafi.cryptospy.di

import com.google.firebase.auth.FirebaseAuth
import com.nafi.cryptospy.data.datasource.DetailApiDataSource
import com.nafi.cryptospy.data.datasource.DetailDataSource
import com.nafi.cryptospy.data.repository.DetailRepository
import com.nafi.cryptospy.data.repository.DetailRepositoryImpl
import com.nafi.cryptospy.data.repository.UserRepository
import com.nafi.cryptospy.data.repository.UserRepositoryImpl
import com.nafi.cryptospy.data.source.firebase.FirebaseAuthDataSource
import com.nafi.cryptospy.data.source.firebase.FirebaseAuthDataSourceImpl
import com.nafi.cryptospy.data.source.network.service.CryptoSpyApiService
import com.nafi.cryptospy.presentation.detail.DetailViewModel
import com.nafi.cryptospy.presentation.login.LoginViewModel
import com.nafi.cryptospy.presentation.profile.ProfileViewModel
import com.nafi.cryptospy.presentation.register.RegisterViewModel
import com.nafi.cryptospy.presentation.splashscreen.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

object AppModules {
    private val networkModule =
        module {
            single<CryptoSpyApiService> { CryptoSpyApiService.invoke() }
        }

    private val firebaseModule =
        module {
            single<FirebaseAuth> { FirebaseAuth.getInstance() }
        }

    private val localModule =
        module {
        }

    private val datasource =
        module {
            single<FirebaseAuthDataSource> { FirebaseAuthDataSourceImpl(get()) }
            single<DetailDataSource> { DetailApiDataSource(get()) }
        }

    private val repository =
        module {
            single<UserRepository> { UserRepositoryImpl(get()) }
            single<DetailRepository> { DetailRepositoryImpl(get()) }
        }

    private val viewModel =
        module {
            viewModel {
                SplashViewModel(get())
            }
            viewModel {
                RegisterViewModel(get())
            }
            viewModel {
                LoginViewModel(get())
            }
            viewModel {
                ProfileViewModel(get())
            }
            viewModel { params ->
                DetailViewModel(
                    extras = params.get(),
                    detailRepository = get(),
                )
            }
        }

    val modules =
        listOf<Module>(
            networkModule,
            firebaseModule,
            localModule,
            datasource,
            repository,
            viewModel,
        )
}
