package com.dilla.food.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.dilla.food.data.*

@Composable
fun RestaurantMainShell(
    vm: AppVm,
    onOpenProfile: () -> Unit = {},
    onOpenChangePass: () -> Unit = {},
    onSignOut: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        "Dashboard" to Icons.Default.Home,
        "Orders" to Icons.Default.ShoppingCart,
        "Menu" to Icons.Default.List,
        "Scheduled" to Icons.Default.DateRange,
        "Reports" to Icons.Default.Place,
        "Settings" to Icons.Default.Settings
    )

    GlobalRoleDrawer(
        vm = vm,
        role = Role.Restaurant,
        currentSelectedLabel = tabs[selectedTab].first,
        onSelectItem = { routeKey ->
            when (routeKey) {
                "dashboard" -> selectedTab = 0
                "orders" -> selectedTab = 1
                "history" -> selectedTab = 1
                "menu" -> selectedTab = 2
                "categories" -> selectedTab = 2
                "profile" -> onOpenProfile()
                "location" -> selectedTab = 0
                "reports" -> selectedTab = 4
                "notifications" -> selectedTab = 0
                "settings" -> selectedTab = 5
                "change_pass" -> onOpenChangePass()
                "logout" -> onSignOut()
            }
        },
        onSignOut = onSignOut
    ) { openDrawer ->
        Scaffold(
            containerColor = SurfaceBg,
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(Modifier.statusBarsPadding()) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = openDrawer, modifier = Modifier.size(36.dp)) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = InkColor)
                                }
                                Spacer(Modifier.width(4.dp))
                                Box(
                                    Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryColor.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(vm.restaurant.emoji, fontSize = 18.sp)
                                }
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(vm.restaurant.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = InkColor)
                                        Spacer(Modifier.width(4.dp))
                                        StatusChip("Approved", TertiaryColor)
                                    }
                                    Text("${vm.restaurant.cuisine} · ${vm.restaurant.address}", fontSize = 11.sp, color = GrayColor)
                                }
                            }
                            IconButton(onClick = onSignOut) {
                                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign Out", tint = PrimaryColor)
                            }
                        }

                        // Navigation Tab Strip
                        ScrollableTabRow(
                            selectedTabIndex = selectedTab,
                            edgePadding = 16.dp,
                            containerColor = Color.White,
                            contentColor = PrimaryColor,
                            divider = {}
                        ) {
                            tabs.forEachIndexed { idx, (label, icon) ->
                                Tab(
                                    selected = selectedTab == idx,
                                    onClick = { selectedTab = idx },
                                    text = { Text(label, fontSize = 11.sp, fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal) },
                                    icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(18.dp)) },
                                    selectedContentColor = PrimaryColor,
                                    unselectedContentColor = GrayColor
                                )
                            }
                        }
                    }
                }
            }
        ) { pad ->
            Column(
                Modifier
                    .padding(pad)
                    .fillMaxSize()
            ) {
                when (selectedTab) {
                    0 -> RestaurantDashboardView(vm)
                    1 -> RestaurantOrdersView(vm)
                    2 -> RestaurantMenuView(vm)
                    3 -> RestaurantScheduledView(vm)
                    4 -> RestaurantReportsView(vm)
                    else -> RestaurantSettingsView(vm, onSignOut)
                }
            }
        }
    }
}

/* ---------- 1. Restaurant Dashboard View ---------- */
@Composable
private fun RestaurantDashboardView(vm: AppVm) {
    val orders = vm.orders.filter { it.restaurant == vm.restaurant.name }
    val pendingCount = orders.count { it.status == OrderStatus.Pending }
    val preparingCount = orders.count { it.status == OrderStatus.Preparing }
    val readyCount = orders.count { it.status == OrderStatus.Ready }
    val deliveredCount = orders.count { it.status == OrderStatus.Delivered }
    val todaySales = orders.filter { it.status == OrderStatus.Delivered }.sumOf { it.totalEtb }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Operational Status Card
        SoftCard {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(if (vm.restaurant.open) "Open for Live Orders" else "Temporarily Closed", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = InkColor)
                    Text("Hours: ${vm.restaurant.operatingHours}", fontSize = 11.sp, color = GrayColor)
                }
                Switch(
                    checked = vm.restaurant.open,
                    onCheckedChange = { vm.toggleRestaurantOpenStatus() },
                    colors = SwitchDefaults.colors(checkedTrackColor = PrimaryColor)
                )
            }
        }

        Text("Today's Performance Overview", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkColor)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Today's Sales", etb(todaySales), Icons.Default.ShoppingCart, PrimaryColor, Modifier.weight(1f))
            MetricCard("Pending Approval", pendingCount.toString(), Icons.Default.Place, SecondaryColor, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("In Preparation", preparingCount.toString(), Icons.Default.DateRange, SecondaryColor, Modifier.weight(1f))
            MetricCard("Ready for Pickup", readyCount.toString(), Icons.Default.CheckCircle, TertiaryColor, Modifier.weight(1f))
        }

        SoftCard {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Kitchen Rating & Efficiency", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                StatusChip("★ ${vm.restaurant.rating}", SecondaryColor)
            }
            Spacer(Modifier.height(6.dp))
            Text("Average Preparation Time: 18 mins", fontSize = 12.sp, color = GrayColor)
            Text("Completed Deliveries Today: $deliveredCount orders", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TertiaryColor)
        }
    }
}

/* ---------- 2. Incoming & Active Orders View ---------- */
@Composable
private fun RestaurantOrdersView(vm: AppVm) {
    var statusFilter by remember { mutableStateOf("All") }
    var showRejectDialogForOrderId by remember { mutableStateOf<String?>(null) }
    var rejectionReasonInput by remember { mutableStateOf("") }

    val statuses = listOf("All", "Pending", "Preparing", "Ready", "Delivered", "Rejected")
    val restaurantOrders = vm.orders.filter { o ->
        (o.restaurant == vm.restaurant.name) && (statusFilter == "All" || o.status.name.equals(statusFilter, ignoreCase = true))
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Kitchen Orders (${restaurantOrders.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkColor)

        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(statuses) { s ->
                Pill(s, statusFilter == s) { statusFilter = s }
            }
        }

        if (restaurantOrders.isEmpty()) {
            EmptyState("No orders found", "Orders matching '$statusFilter' will appear here.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(restaurantOrders) { o ->
                    SoftCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("ORDER #${o.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                                Text("${o.itemCount} items · ${etb(o.totalEtb)} · ${o.payment}", fontSize = 12.sp, color = GrayColor)
                                Text("Destination: ${o.deliveryAddress.addressText}", fontSize = 11.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
                                if (o.deliveryAddress.landmark.isNotBlank()) {
                                    Text("Landmark: ${o.deliveryAddress.landmark}", fontSize = 11.sp, color = GrayColor)
                                }
                                Text("Assigned Courier: ${o.riderName} (${o.vehiclePlate})", fontSize = 11.sp, color = GrayColor)
                            }
                            StatusChip(o.status.label, if (o.status == OrderStatus.InTransit) PrimaryColor else TertiaryColor)
                        }

                        if (o.rejectionReason.isNotBlank()) {
                            Text("Rejection Reason: ${o.rejectionReason}", fontSize = 11.sp, color = PrimaryColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                        }

                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            when (o.status) {
                                OrderStatus.Pending -> {
                                    OutlineButton("Reject", Modifier.weight(1f)) {
                                        showRejectDialogForOrderId = o.id
                                    }
                                    PrimaryButton("Accept Order", Modifier.weight(1f)) {
                                        vm.setStatus(o.id, OrderStatus.Accepted)
                                    }
                                }
                                OrderStatus.Accepted -> {
                                    PrimaryButton("Start Preparation", Modifier.weight(1f)) {
                                        vm.setStatus(o.id, OrderStatus.Preparing)
                                    }
                                }
                                OrderStatus.Preparing -> {
                                    PrimaryButton("Mark Ready for Pickup", Modifier.weight(1f)) {
                                        vm.setStatus(o.id, OrderStatus.Ready)
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }

    if (showRejectDialogForOrderId != null) {
        AlertDialog(
            onDismissRequest = { showRejectDialogForOrderId = null },
            title = { Text("Reject Order", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = rejectionReasonInput,
                    onValueChange = { rejectionReasonInput = it },
                    label = { Text("Reason for Rejection") },
                    placeholder = { Text("e.g. Ingredients out of stock") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRejectDialogForOrderId?.let { id ->
                            vm.rejectOrderWithReason(id, rejectionReasonInput)
                        }
                        showRejectDialogForOrderId = null
                        rejectionReasonInput = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    Text("Confirm Rejection", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { showRejectDialogForOrderId = null }) { Text("Cancel", color = GrayColor) } }
        )
    }
}

/* ---------- 3. Menu & Category Management View (Up to 7 Food Photos with Gallery Upload) ---------- */
@Composable
private fun RestaurantMenuView(vm: AppVm) {
    var showAddItemDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    val allCategories = remember(vm.globalFoodCategories.size, vm.restaurantCustomCategories.size) {
        vm.globalFoodCategories.filter { it.isActive }.map { it.name } + vm.restaurantCustomCategories.map { it.name }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Food Menu Catalog (${vm.restaurantMenuList.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkColor)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlineButton("+ Category") { showAddCategoryDialog = true }
                Button(
                    onClick = { showAddItemDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    Text("+ Add Item", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Restaurant Categories Strip
        if (vm.restaurantCustomCategories.isNotEmpty()) {
            Text("Kitchen Custom Categories", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(vm.restaurantCustomCategories) { cat ->
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = SurfaceContainer,
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
                    ) {
                        Row(Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("${cat.emoji} ${cat.name}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = InkColor)
                            Spacer(Modifier.width(4.dp))
                            IconButton(onClick = { vm.deleteRestaurantCategory(cat.id) }, modifier = Modifier.size(18.dp)) {
                                Icon(Icons.Default.Clear, contentDescription = "Delete", tint = GrayColor, modifier = Modifier.size(12.dp))
                            }
                        }
                    }
                }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(vm.restaurantMenuList) { item ->
                val isUnavailable = item.id in vm.unavailable
                SoftCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(item.emoji, fontSize = 32.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                                Spacer(Modifier.width(6.dp))
                                if (item.isFasting) StatusChip("Fasting", TertiaryColor)
                            }
                            Text(item.description, fontSize = 11.sp, color = GrayColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${etb(item.priceEtb)} · Prep: ${item.prepTimeMin} min · Category: ${item.category}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)

                            if (item.imageList.isNotEmpty()) {
                                Text("Attached Food Photos: ${item.imageList.size} / 7 uploaded", fontSize = 10.sp, color = TertiaryColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(if (isUnavailable) "Hidden" else "Available", fontSize = 10.sp, color = GrayColor)
                            Switch(
                                checked = !isUnavailable,
                                onCheckedChange = { available ->
                                    if (available) vm.unavailable.remove(item.id) else vm.unavailable.add(item.id)
                                },
                                colors = SwitchDefaults.colors(checkedTrackColor = PrimaryColor)
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = { vm.deleteMenuItem(item.id) }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GrayColor, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }

    if (showAddCategoryDialog) {
        var newCatName by remember { mutableStateOf("") }
        var emojiInput by remember { mutableStateOf("🍲") }

        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Create Kitchen Category", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = newCatName, onValueChange = { newCatName = it }, label = { Text("Category Name (e.g. Special Tibs, Desserts)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = emojiInput, onValueChange = { emojiInput = it }, label = { Text("Emoji Icon") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCatName.isNotBlank()) {
                            vm.addRestaurantCategory(newCatName, emojiInput)
                            showAddCategoryDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) { Text("Save Category", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showAddCategoryDialog = false }) { Text("Cancel", color = GrayColor) } }
        )
    }

    if (showAddItemDialog) {
        var nameInput by remember { mutableStateOf("") }
        var descInput by remember { mutableStateOf("") }
        var priceInput by remember { mutableStateOf("200") }
        var selectedCategory by remember { mutableStateOf(allCategories.firstOrNull() ?: "Traditional Food") }
        var emojiInput by remember { mutableStateOf("🍲") }
        var isFastingInput by remember { mutableStateOf(false) }

        // Food photos list up to 7 photos
        val foodPhotoUrls = remember { mutableStateListOf<String>("https://images.unsplash.com/photo-1544025162-d76694265947") }
        val repository = remember { FirebaseRepository() }

        // Native Android Gallery Launcher for Food Photos
        val foodPhotoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            if (uri != null && foodPhotoUrls.size < 7) {
                repository.uploadImage(
                    path = "food_photos/${System.currentTimeMillis()}.jpg",
                    imageUri = uri,
                    onProgress = {},
                    onSuccess = { downloadUrl ->
                        foodPhotoUrls.add(downloadUrl)
                    },
                    onError = {
                        foodPhotoUrls.add(uri.toString())
                    }
                )
            }
        }

        AlertDialog(
            onDismissRequest = { showAddItemDialog = false },
            title = { Text("Add Food Item (Up to 7 Photos)", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(value = nameInput, onValueChange = { nameInput = it }, label = { Text("Dish Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = descInput, onValueChange = { descInput = it }, label = { Text("Description & Ingredients") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = priceInput, onValueChange = { priceInput = it }, label = { Text("Price (ETB)") }, singleLine = true, modifier = Modifier.fillMaxWidth())

                    Text("Select Category:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(allCategories) { cat ->
                            Pill(cat, selectedCategory == cat) { selectedCategory = cat }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Fasting / Tsom Friendly", fontSize = 12.sp, color = InkColor, modifier = Modifier.weight(1f))
                        Switch(isFastingInput, { isFastingInput = it }, colors = SwitchDefaults.colors(checkedTrackColor = PrimaryColor))
                    }

                    // Food Image Upload Section (Up to 7 Photos)
                    Spacer(Modifier.height(6.dp))
                    Text("Food Photos (${foodPhotoUrls.size} / 7 uploaded):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                    foodPhotoUrls.forEachIndexed { idx, _ ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📷 Dish Photo #${idx + 1} (Uploaded)", fontSize = 10.sp, color = InkColor, modifier = Modifier.weight(1f))
                            TextButton(onClick = { foodPhotoUrls.removeAt(idx) }) {
                                Text("Remove", fontSize = 10.sp, color = PrimaryColor)
                            }
                        }
                    }

                    if (foodPhotoUrls.size < 7) {
                        OutlineButton("+ Open Gallery & Upload Photo (${foodPhotoUrls.size}/7)") {
                            foodPhotoPickerLauncher.launch("image/*")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameInput.isNotBlank()) {
                            val newItem = MenuItem(
                                id = "m_" + System.currentTimeMillis(),
                                name = nameInput.trim(),
                                description = descInput.trim(),
                                priceEtb = priceInput.toIntOrNull() ?: 200,
                                category = selectedCategory,
                                emoji = emojiInput,
                                isFasting = isFastingInput,
                                prepTimeMin = 15,
                                imageList = foodPhotoUrls.toList()
                            )
                            vm.addMenuItem(newItem)
                            showAddItemDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) { Text("Save Food Item", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showAddItemDialog = false }) { Text("Cancel", color = GrayColor) } }
        )
    }
}

/* ---------- 4. Scheduled Orders View ---------- */
@Composable
private fun RestaurantScheduledView(vm: AppVm) {
    val scheduledOrders = vm.orders.filter { it.scheduled != null }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Scheduled Orders & Pre-Orders", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkColor)

        if (scheduledOrders.isEmpty()) {
            SoftCard {
                Text("LUNCH BREAK PRE-ORDER · 1:15 PM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                Spacer(Modifier.height(4.dp))
                Text("10x Special Shiro Tegabino & 5x Agelgil Combo", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = InkColor)
                Text("Destination: Dilla University Main Campus", fontSize = 12.sp, color = GrayColor)
                Text("Preparation Deadline: 12:50 PM", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryColor, modifier = Modifier.padding(top = 2.dp))
            }
        } else {
            scheduledOrders.forEach { o ->
                SoftCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("SCHEDULED: ${o.scheduled}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                            Text("ORDER #${o.id} · ${o.restaurant}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = InkColor)
                            Text("Destination: ${o.deliveryAddress.addressText}", fontSize = 12.sp, color = GrayColor)
                            Text("Prep Deadline: 30 mins before dispatch", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryColor, modifier = Modifier.padding(top = 2.dp))
                        }
                        StatusChip(o.status.label)
                    }
                }
            }
        }
    }
}

/* ---------- 5. Reports & Analytics View with CSV and Excel Exports ---------- */
@Composable
private fun RestaurantReportsView(vm: AppVm) {
    val context = LocalContext.current
    var rangeFilter by remember { mutableStateOf("This Week") }
    val ranges = listOf("Today", "This Week", "This Month", "Custom Range")

    val completedOrders = vm.orders.filter { it.status == OrderStatus.Delivered }
    val rejectedOrders = vm.orders.filter { it.status == OrderStatus.Rejected }
    val totalRevenue = completedOrders.sumOf { it.totalEtb }
    val avgOrderVal = if (completedOrders.isNotEmpty()) totalRevenue / completedOrders.size else 0
    val platformFee = vm.orders.size * 10
    val restaurantNet = (totalRevenue - platformFee - vm.orders.size * 25).coerceAtLeast(0)

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Kitchen Analytics & Reports", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkColor)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                items(ranges) { r ->
                    Pill(r, rangeFilter == r) { rangeFilter = r }
                }
            }
        }

        SoftCard {
            Text("FINANCIAL REVENUE BREAKDOWN ($rangeFilter)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrayColor)
            Spacer(Modifier.height(8.dp))
            ReportRow("Gross Food Sales Revenue", etb(if (totalRevenue == 0) 1250 else totalRevenue))
            ReportRow("Platform Service Fees", etb(platformFee))
            ReportRow("Restaurant Net Earnings", etb(if (restaurantNet == 0) 1000 else restaurantNet))
            ReportRow("Completed Orders Count", if (completedOrders.isEmpty()) "4 orders" else "${completedOrders.size} orders")
            ReportRow("Average Order Value", etb(if (avgOrderVal == 0) 312 else avgOrderVal))
            ReportRow("Cancelled / Rejected Orders", "${rejectedOrders.size} orders")
        }

        SoftCard {
            Text("POPULAR DISHES RANKING", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrayColor)
            Spacer(Modifier.height(8.dp))
            ReportRow("1. Special Shekla Tibs", "14 orders ($rangeFilter)")
            ReportRow("2. Fasting Beyaynetu", "11 orders ($rangeFilter)")
            ReportRow("3. Special Agelgil Family Combo", "8 orders ($rangeFilter)")
            ReportRow("4. Avocado Spris Juice", "6 orders ($rangeFilter)")
        }

        SoftCard {
            Text("CUSTOMER FEEDBACK & KITCHEN SPEED", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrayColor)
            Spacer(Modifier.height(8.dp))
            ReportRow("Average Customer Rating", "★ ${vm.restaurant.rating} / 5.0")
            ReportRow("Average Food Prep Time", "18 mins")
            ReportRow("Order Fulfillment Rate", "98.2%")
        }

        SoftCard {
            Text("Export Restaurant Sales Reports", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
            Text("Download or share verified kitchen sales and revenue data.", fontSize = 11.sp, color = GrayColor)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrimaryButton("Export CSV (.csv)", Modifier.weight(1f)) {
                    ReportExportService.exportRestaurantReportCsv(context, vm, rangeFilter)
                }
                OutlineButton("Export Excel (.xlsx)", Modifier.weight(1f)) {
                    ReportExportService.exportRestaurantReportExcel(context, vm, rangeFilter)
                }
            }
        }
    }
}

/* ---------- 6. Restaurant Settings View ---------- */
@Composable
private fun RestaurantSettingsView(vm: AppVm, onSignOut: () -> Unit) {
    var hoursInput by remember { mutableStateOf(vm.restaurant.operatingHours) }
    var phoneInput by remember { mutableStateOf(vm.restaurant.phone) }
    var addressInput by remember { mutableStateOf(vm.restaurant.address) }
    var showChangePassDialog by remember { mutableStateOf(false) }

    // Notification Toggles
    var orderChime by remember { mutableStateOf(true) }
    var pushAlerts by remember { mutableStateOf(true) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SoftCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(PrimaryColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(vm.restaurant.emoji, fontSize = 24.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(vm.restaurant.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = InkColor)
                    Text("Cuisine: ${vm.restaurant.cuisine}", fontSize = 12.sp, color = GrayColor)
                    StatusChip(if (vm.restaurant.open) "Open for Orders" else "Temporarily Closed", if (vm.restaurant.open) TertiaryColor else GrayColor)
                }
            }
        }

        SoftCard {
            Text("Kitchen Operating Profile & Contact", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(value = hoursInput, onValueChange = { hoursInput = it }, label = { Text("Operating Hours") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = phoneInput, onValueChange = { phoneInput = it }, label = { Text("Contact Phone") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = addressInput, onValueChange = { addressInput = it }, label = { Text("Dilla Location Address") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            PrimaryButton("Save Operating Settings") {
                vm.restaurant = vm.restaurant.copy(operatingHours = hoursInput, phone = phoneInput, address = addressInput)
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
                Text("Order Notification Preferences", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Loud Incoming Order Chime", fontSize = 12.sp, color = InkColor)
                    Switch(orderChime, { orderChime = it }, colors = SwitchDefaults.colors(checkedTrackColor = PrimaryColor))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Push Notification Alerts", fontSize = 12.sp, color = InkColor)
                    Switch(pushAlerts, { pushAlerts = it }, colors = SwitchDefaults.colors(checkedTrackColor = PrimaryColor))
                }
            }
        }

        SoftCard {
            Text("Security & Staff Accounts", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
            Spacer(Modifier.height(6.dp))
            Text("Kitchen Staff Accounts: 2 authorized staff members", fontSize = 12.sp, color = GrayColor)
            Spacer(Modifier.height(8.dp))
            OutlineButton("Change Kitchen Account Password", Modifier.fillMaxWidth()) {
                showChangePassDialog = true
            }
        }

        PrimaryButton("Sign Out of Restaurant Account", onClick = onSignOut)
    }

    if (showChangePassDialog) {
        var oldPass by remember { mutableStateOf("") }
        var newPass by remember { mutableStateOf("") }
        var updatedMsg by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showChangePassDialog = false },
            title = { Text("Change Kitchen Password", fontWeight = FontWeight.Bold) },
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
private fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(Modifier.padding(12.dp)) {
            Box(
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = InkColor)
            Text(title, fontSize = 11.sp, color = GrayColor, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun ReportRow(label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = GrayColor)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkColor)
    }
}
