package com.dilla.food.ui

import androidx.compose.runtime.Composable
import com.dilla.food.data.AppVm

/* ---------- Restaurant Dashboard ---------- */
@Composable
fun RestaurantScreen(
    vm: AppVm,
    onOpenProfile: () -> Unit = {},
    onOpenChangePass: () -> Unit = {},
    onSignOut: () -> Unit
) {
    RestaurantMainShell(vm, onOpenProfile, onOpenChangePass, onSignOut)
}

/* ---------- Rider Dashboard & Active Delivery ---------- */
@Composable
fun RiderScreen(
    vm: AppVm,
    onOpenProfile: () -> Unit = {},
    onOpenChangePass: () -> Unit = {},
    onSignOut: () -> Unit
) {
    RiderMainShell(vm, onOpenProfile, onOpenChangePass, onSignOut)
}

/* ---------- Admin Dashboard ---------- */
@Composable
fun AdminScreen(
    vm: AppVm,
    onOpenProfile: () -> Unit = {},
    onOpenChangePass: () -> Unit = {},
    onSignOut: () -> Unit
) {
    AdminMainShell(vm, onOpenProfile, onOpenChangePass, onSignOut)
}
