package com.wegielek.katanaflashlight.di

import com.wegielek.katanaflashlight.data.AndroidFlashlightController
import com.wegielek.katanaflashlight.data.AndroidPermissionChecker
import com.wegielek.katanaflashlight.data.AndroidPrefsRepository
import com.wegielek.katanaflashlight.data.AndroidServiceController
import com.wegielek.katanaflashlight.data.FlashlightController
import com.wegielek.katanaflashlight.data.PermissionChecker
import com.wegielek.katanaflashlight.data.PrefsRepository
import com.wegielek.katanaflashlight.data.ServiceController
import org.koin.dsl.module

val dataModule =
    module {
        single<PrefsRepository> { AndroidPrefsRepository(get()) }
        single<PermissionChecker> { AndroidPermissionChecker(get()) }
        single<FlashlightController> { AndroidFlashlightController(get()) }
        single<ServiceController> { AndroidServiceController(get()) }
    }
