package com.wegielek.katanaflashlight.di

import com.wegielek.katanaflashlight.data.AndroidFlashlightController
import com.wegielek.katanaflashlight.data.AndroidPermissionChecker
import com.wegielek.katanaflashlight.data.AndroidServiceController
import com.wegielek.katanaflashlight.domain.FlashlightController
import com.wegielek.katanaflashlight.domain.PermissionChecker
import com.wegielek.katanaflashlight.domain.ServiceController
import org.koin.dsl.module

val dataModule =
    module {
        single<PermissionChecker> { AndroidPermissionChecker(get()) }
        single<FlashlightController> { AndroidFlashlightController(get()) }
        single<ServiceController> { AndroidServiceController(get()) }
    }
