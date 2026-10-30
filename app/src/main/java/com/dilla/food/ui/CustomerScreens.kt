package com.dilla.food.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.dilla.food.data.*

@Composable
fun CustomerShell(
    vm: AppVm,
    onPickRestaurant: () -> Unit,
    onOpenOrder: (String) -> Unit,
    onSignOut: () -> Unit
) {
    var tab by remember { mutableStateOf(0) }
    Scaffold(
        containerColor = SurfaceBg,
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                listOf(
                    "Home" to Icons.Default.Home,
                    "Orders" to Icons.Default.List,
                    "Alerts" to Icons.Default.Notifications,
                    "Profile" to Icons.Default.Person
                ).forEachIndexed { i, (l, ic) ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { tab = i },
                        icon = { Icon(ic, l) },
                        label = { Text(l, fontSize = 11.sp, fontWeight = if (tab == i) FontWeight.Bold else FontWeight.Normal) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryColor,
                            selectedTextColor = PrimaryColor,
                            indicatorColor = PrimaryColor.copy(alpha = 0.12f),
                            unselectedIconColor = GrayColor,
                            unselectedTextColor = GrayColor
                        )
                    )
                }
            }
        }
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .fillMaxSize()
        ) {
            when (tab) {
                0 -> CustomerHome(vm, onPickRestaurant, onOpenOrder)
                1 -> {
                    ScreenHeader("My Orders")
                    OrdersList(vm, onOpenOrder, onPickRestaurant)
                }
                2 -> {
                    ScreenHeader("Notifications")
                    EmptyState(
                        "No new notifications",
                        "Order status updates and rider tracking alerts will appear here."
                    )
                }
                else -> {
                    ScreenHeader("Profile & Settings")
                    ProfileCard(vm, onSignOut)
                }
            }
        }
    }
}

@Composable
private fun CustomerHome(
    vm: AppVm,
    onPick: () -> Unit,
    onOpenOrder: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var showAddressModal by remember { mutableStateOf(false) }

    val filteredList = Demo.restaurants.filter { r ->
        r.name.contains(query, ignoreCase = true) || r.cuisine.contains(query, ignoreCase = true)
    }

    val activeOrder = vm.orders.firstOrNull {
        it.status == OrderStatus.InTransit || it.status == OrderStatus.Preparing ||
                it.status == OrderStatus.Pending || it.status == OrderStatus.Ready
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp)
    ) {
        ScreenHeader("Dilla Food Delivery")
        LocationDropoffBanner(
            address = vm.selectedAddress,
            onChangeLocation = { showAddressModal = true }
        )

        // Active Order Banner on Home Screen
        if (activeOrder != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { onOpenOrder(activeOrder.id) },
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 3.dp,
                border = BorderStroke(1.5.dp, PrimaryColor)
            ) {
                Row(
                    Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(PrimaryColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("ACTIVE ORDER #${activeOrder.id}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                            Spacer(Modifier.width(6.dp))
                            StatusChip(activeOrder.status.label, TertiaryColor)
                        }
                        Text("${activeOrder.restaurant} · ${etb(activeOrder.totalEtb)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = InkColor)
                        Text("To: ${activeOrder.deliveryAddress.addressText}", fontSize = 11.sp, color = GrayColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Spacer(Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = PrimaryColor
                    ) {
                        Text("Track Order", Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        SearchBarWithFilters(
            query = query,
            onQueryChange = { query = it },
            placeholder = "Search shiro, tibs, burgers, juice..."
        )
        DeliveryModeToggleBar(
            activeTab = vm.deliveryModeTab,
            onTabSelected = { vm.deliveryModeTab = it }
        )
        DietaryChipsBar(
            selectedFilter = vm.activeDietaryFilter,
            onSelectFilter = { vm.activeDietaryFilter = it }
        )
        PromotionalHeroCard(
            onStartGroup = onPick
        )
        CategoryChipsCarousel(
            selectedCategory = vm.activeCategoryFilter,
            onSelectCategory = { vm.activeCategoryFilter = it }
        )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Dilla Town & Campus Kitchens", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkColor)
                Text("Hot meals delivered anywhere in Dilla service area", fontSize = 11.sp, color = GrayColor)
            }
            StatusChip("${filteredList.size} Open", TertiaryColor)
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredList.isEmpty()) {
                EmptyState("No kitchens found", "Try searching for Shiro, Tibs, Burgers or Juice.")
            } else {
                filteredList.forEach { r ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = r.open) {
                                vm.restaurant = r
                                onPick()
                            },
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        shadowElevation = 3.dp
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(SurfaceBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(r.emoji, fontSize = 34.sp)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(r.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = InkColor)
                                        Spacer(Modifier.width(6.dp))
                                        Text("★ ${r.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryColor)
                                    }
                                    Text(r.cuisine, fontSize = 12.sp, color = GrayColor)
                                    Text(
                                        "${r.etaMin} mins · ${r.distanceKm} km · Fee ${etb(r.feeEtb)}",
                                        fontSize = 11.sp,
                                        color = GrayColor,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                                StatusChip(if (r.open) "Open Now" else "Closed", if (r.open) TertiaryColor else GrayColor)
                            }
                            if (r.isBayenetuFriendly) {
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = SecondaryColor.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            "Bayenetu Friendly",
                                            Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF6C3E00)
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = PrimaryColor.copy(alpha = 0.1f)
                                    ) {
                                        Text(
                                            "Tap to Order Alone or in Group",
                                            Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddressModal) {
        AddressSelectionModal(
            vm = vm,
            onDismiss = { showAddressModal = false }
        )
    }
}

@Composable
fun OrdersList(
    vm: AppVm,
    onOpen: (String) -> Unit,
    onReorder: () -> Unit
) {
    if (vm.orders.isEmpty()) {
        EmptyState("No orders yet", "Your placed orders will show up here.")
        return
    }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(vm.orders) { o ->
            SoftCard(Modifier.clickable { onOpen(o.id) }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("${o.restaurant} · ${o.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                        Text("${o.itemCount} items · ${etb(o.totalEtb)}", fontSize = 12.sp, color = GrayColor)
                        Text("Destination: ${o.deliveryAddress.addressText}", fontSize = 11.sp, color = GrayColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    StatusChip(o.status.label, if (o.status == OrderStatus.InTransit) PrimaryColor else TertiaryColor)
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlineButton("Reorder") {
                        vm.reorder(o.id)
                        onReorder()
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileCard(vm: AppVm, onSignOut: () -> Unit) {
    var showAddressModal by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showChangePassDialog by remember { mutableStateOf(false) }

    // Notification Preferences
    var pushNotify by remember { mutableStateOf(true) }
    var orderNotify by remember { mutableStateOf(true) }
    var smsNotify by remember { mutableStateOf(false) }

    Column(
        Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SoftCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(vm.me, size = 56.dp)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(vm.userName, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = InkColor)
                        Spacer(Modifier.width(6.dp))
                        StatusChip("Active", TertiaryColor)
                    }
                    Text("Role: ${vm.role?.label ?: "Customer"}", color = GrayColor, fontSize = 12.sp)
                    Text("Active Address: ${vm.selectedAddress.label}", color = PrimaryColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlineButton("Edit Profile", Modifier.weight(1f)) { showEditProfileDialog = true }
                OutlineButton("Password", Modifier.weight(1f)) { showChangePassDialog = true }
            }
        }

        // Notification Preferences Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(Modifier.padding(14.dp)) {
                Text("Notification Preferences", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Push Notifications", fontSize = 12.sp, color = InkColor)
                    Switch(pushNotify, { pushNotify = it }, colors = SwitchDefaults.colors(checkedTrackColor = PrimaryColor))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Order Status Alerts", fontSize = 12.sp, color = InkColor)
                    Switch(orderNotify, { orderNotify = it }, colors = SwitchDefaults.colors(checkedTrackColor = PrimaryColor))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("SMS Receipts & Group Invites", fontSize = 12.sp, color = InkColor)
                    Switch(smsNotify, { smsNotify = it }, colors = SwitchDefaults.colors(checkedTrackColor = PrimaryColor))
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(Modifier.padding(14.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Saved Delivery Addresses", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                    TextButton(onClick = { showAddressModal = true }) {
                        Text("Manage", fontSize = 12.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(6.dp))
                vm.savedAddresses.forEach { addr ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (addr.isSelected) Icons.Default.LocationOn else Icons.Default.Place,
                            contentDescription = null,
                            tint = if (addr.isSelected) PrimaryColor else GrayColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text(addr.label + " (" + addr.areaZone + ")", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkColor)
                            Text(addr.addressText, fontSize = 11.sp, color = GrayColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        if (addr.isSelected) {
                            StatusChip("Active", PrimaryColor)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        PrimaryButton("Sign Out", onClick = onSignOut)
    }

    if (showAddressModal) {
        AddressSelectionModal(
            vm = vm,
            onDismiss = { showAddressModal = false }
        )
    }

    if (showEditProfileDialog) {
        var newName by remember { mutableStateOf(vm.userName) }
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile Details", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Full Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newName.isNotBlank()) vm.userName = newName.trim()
                    showEditProfileDialog = false
                }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { showEditProfileDialog = false }) { Text("Cancel", color = GrayColor) } }
        )
    }

    if (showChangePassDialog) {
        var oldPass by remember { mutableStateOf("") }
        var newPass by remember { mutableStateOf("") }
        var updatedMsg by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showChangePassDialog = false },
            title = { Text("Change Password", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = oldPass, onValueChange = { oldPass = it }, label = { Text("Current Password") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = newPass, onValueChange = { newPass = it }, label = { Text("New Password") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                    if (updatedMsg) {
                        Text("✓ Password updated successfully!", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TertiaryColor)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newPass.length >= 6) updatedMsg = true
                }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)) {
                    Text("Update Password", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { showChangePassDialog = false }) { Text("Close", color = GrayColor) } }
        )
    }
}

@Composable
fun OrderDetailScreen(vm: AppVm, id: String, onBack: () -> Unit) {
    val o = vm.order(id) ?: vm.orders.firstOrNull()
    Column(
        Modifier
            .fillMaxSize()
            .background(SurfaceBg)
    ) {
        ScreenHeader("Live Delivery Tracker", onBack)
        if (o == null) {
            EmptyState("Order not found", "It may have been removed.")
            return
        }
        Column(
            Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Hero Status Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                Column(Modifier.padding(14.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ORDER #${o.id}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrayColor)
                        StatusChip("Live GPS Tracking", SecondaryColor)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        if (o.status == OrderStatus.InTransit) "Rider ${o.riderName} is on the way!" else o.status.label,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = InkColor
                    )
                    Text(
                        "Estimated Arrival: 12:48 PM (Approx. 8 mins remaining)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Delivery Destination & Pickup Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("PICKUP: ${o.pickupLocation}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkColor)
                    }
                    HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color(0xFFEAEAEA))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Column {
                            Text("DESTINATION: ${o.deliveryAddress.label.uppercase()} (${o.deliveryAddress.areaZone})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                            Text(o.deliveryAddress.addressText, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = InkColor)
                            if (o.deliveryAddress.landmark.isNotBlank()) {
                                Text("Landmark: ${o.deliveryAddress.landmark}", fontSize = 11.sp, color = GrayColor)
                            }
                            if (o.deliveryAddress.deliveryInstructions.isNotBlank()) {
                                Text("Instructions: ${o.deliveryAddress.deliveryInstructions}", fontSize = 11.sp, color = GrayColor)
                            }
                        }
                    }
                }
            }

            // Interactive TomTom Map Canvas Box
            TomTomMapView(order = o)

            // Delivery Milestones Timeline
            SoftCard {
                Text("Delivery Milestones", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = InkColor, modifier = Modifier.padding(bottom = 10.dp))
                val stages = listOf(
                    OrderStatus.Pending to "Order Placed at ${o.restaurant}",
                    OrderStatus.Preparing to "Prepared & Packed in Thermal Bag",
                    OrderStatus.Ready to "Handed over to Rider ${o.riderName}",
                    OrderStatus.InTransit to "In Transit to ${o.deliveryAddress.label}",
                    OrderStatus.Delivered to "Delivered & PIN Confirmed"
                )
                stages.forEach { (st, label) ->
                    val done = st.ordinal <= o.status.ordinal
                    Row(Modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(if (done) TertiaryColor else Color(0xFFE0E0E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (done) Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(
                            label,
                            fontWeight = if (st == o.status) FontWeight.Bold else FontWeight.Normal,
                            color = if (st == o.status) PrimaryColor else InkColor,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Handover Delivery PIN Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                Column(
                    Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("SECURITY HANDOVER PIN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrayColor)
                    Text(o.pin, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryColor, letterSpacing = 4.sp)
                    Text("Give this 4-digit PIN to rider ${o.riderName} when receiving your food.", fontSize = 11.sp, color = GrayColor, textAlign = TextAlign.Center)
                }
            }
        }
    }
}
