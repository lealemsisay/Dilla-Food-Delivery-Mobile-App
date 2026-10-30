package com.dilla.food.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import com.dilla.food.data.*

@Composable
private fun RoleScaffold(title: String, vm: AppVm, onSignOut: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().background(SurfaceBg)) {
        ScreenHeader(title) { TextButton(onSignOut) { Text("Sign out", color = PrimaryColor, fontSize = 12.sp, fontWeight = FontWeight.Bold) } }
        content()
    }
}

/* ---------- Restaurant Dashboard ---------- */
@Composable
fun RestaurantScreen(vm: AppVm, onSignOut: () -> Unit) {
    RestaurantMainShell(vm, onSignOut)
}

/* ---------- Rider Dashboard & Active Delivery ---------- */
@Composable
fun RiderScreen(vm: AppVm, onSignOut: () -> Unit) {
    var online by remember { mutableStateOf(true) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }

    RoleScaffold("Courier Delivery Hub", vm, onSignOut) {
        SoftCard(Modifier.padding(horizontal = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(if (online) "You are Online in Dilla" else "You are Offline", fontWeight = FontWeight.Bold, color = InkColor)
                    Text("Location updates active for Dilla Town & Campus orders", fontSize = 11.sp, color = GrayColor)
                }
                Switch(online, { online = it }, colors = SwitchDefaults.colors(checkedTrackColor = PrimaryColor))
            }
        }

        val jobs = vm.orders.filter { it.status == OrderStatus.Ready || it.status == OrderStatus.InTransit }
        if (!online) {
            EmptyState("Go online to see delivery offers", "Only online approved riders receive dispatch offers.")
        } else if (jobs.isEmpty()) {
            EmptyState("No active delivery jobs", "Orders marked ready by kitchens in town or campus will appear here.")
        } else {
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(jobs) { o ->
                    SoftCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("JOB #${o.id} · ${o.restaurant}", Modifier.weight(1f), fontWeight = FontWeight.Bold, color = InkColor)
                            StatusChip(o.status.label, if (o.status == OrderStatus.InTransit) PrimaryColor else SecondaryColor)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("PICKUP: ${o.pickupLocation}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkColor)
                        Text("DELIVERY DESTINATION: ${o.deliveryAddress.label.uppercase()} (${o.deliveryAddress.areaZone})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                        Text(o.deliveryAddress.addressText, fontSize = 12.sp, color = InkColor)
                        if (o.deliveryAddress.landmark.isNotBlank()) {
                            Text("Landmark: ${o.deliveryAddress.landmark}", fontSize = 11.sp, color = GrayColor)
                        }
                        Text("${etb(o.totalEtb)} · ${o.payment}", fontSize = 11.sp, color = GrayColor, modifier = Modifier.padding(top = 2.dp))

                        Spacer(Modifier.height(10.dp))
                        if (o.status == OrderStatus.Ready) {
                            OutlineButton("Accept Job & Confirm Pickup") {
                                vm.setStatus(o.id, OrderStatus.InTransit)
                            }
                        } else {
                            OutlinedTextField(
                                value = pinInput,
                                onValueChange = { pinInput = it.filter(Char::isDigit).take(4) },
                                label = { Text("Customer Handover 4-Digit PIN") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                modifier = Modifier.fillMaxWidth()
                            )
                            pinError?.let { Text(it, color = PrimaryColor, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp)) }
                            Spacer(Modifier.height(8.dp))
                            OutlineButton("Confirm Handover & Complete Delivery") {
                                if (pinInput == o.pin) {
                                    vm.setStatus(o.id, OrderStatus.Delivered)
                                    pinInput = ""
                                    pinError = null
                                } else {
                                    pinError = "Incorrect PIN. Ask customer for 4-digit security PIN."
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/* ---------- Admin Dashboard ---------- */
@Composable
fun AdminScreen(vm: AppVm, onSignOut: () -> Unit) {
    AdminMainShell(vm, onSignOut)
}
