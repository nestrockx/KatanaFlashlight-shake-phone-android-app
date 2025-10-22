package com.wegielek.katanaflashlight.di

import com.wegielek.katanaflashlight.presentation.viewmodels.LandingViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule =
    module {
        viewModel { LandingViewModel(androidApplication(), get(), get(), get()) }
    }
