package com.dilla.food.ui

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.dilla.food.data.*

@Composable
fun AdminMainShell(
    vm: AppVm,
    onSignOut: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        "Dashboard" to Icons.Default.Home,
        "Customers" to Icons.Default.Person,
        "Restaurants" to Icons.Default.ShoppingCart,
        "Riders" to Icons.Default.Place,
        "Orders" to Icons.Default.List,
        "Zones" to Icons.Default.LocationOn,
        "Reports" to Icons.Default.DateRange,
        "Audit" to Icons.Default.Lock,
        "Settings" to Icons.Default.Settings
    )

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
                            Box(
                                Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text("DILLA PLATFORM ADMIN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryColor, letterSpacing = 1.sp)
                                Text("Control & Management", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = InkColor)
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
                0 -> AdminDashboardView(vm)
                1 -> AdminCustomersView(vm)
                2 -> AdminRestaurantsView(vm)
                3 -> AdminRidersView(vm)
                4 -> AdminOrdersView(vm)
                5 -> AdminZonesView(vm)
                6 -> AdminReportsView(vm)
                7 -> AdminAuditView(vm)
                else -> AdminSettingsView(vm, onSignOut)
            }
        }
    }
}

/* ---------- 1. Admin Dashboard View ---------- */
@Composable
private fun AdminDashboardView(vm: AppVm) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Real-Time Platform Overview", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkColor)

        // Metrics Grid
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Total Customers", vm.customers.size.toString(), Icons.Default.Person, PrimaryColor, Modifier.weight(1f))
            MetricCard("Active Orders", vm.orders.count { it.status != OrderStatus.Delivered && it.status != OrderStatus.Rejected }.toString(), Icons.Default.ShoppingCart, SecondaryColor, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Completed Orders", vm.orders.count { it.status == OrderStatus.Delivered }.toString(), Icons.Default.CheckCircle, TertiaryColor, Modifier.weight(1f))
            MetricCard("Gross Revenue", etb(vm.orders.filter { it.status == OrderStatus.Delivered }.sumOf { it.totalEtb }), Icons.Default.ShoppingCart, PrimaryColor, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Pending Vendors", vm.restaurantApplications.count { it.status == "Pending" }.toString(), Icons.Default.Home, SecondaryColor, Modifier.weight(1f))
            MetricCard("Pending Riders", vm.riderApplications.count { it.status == "Pending" }.toString(), Icons.Default.Place, TertiaryColor, Modifier.weight(1f))
        }

        // Live Operational Summary
        SoftCard {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Dilla Town & Campus Service Status", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                StatusChip("Active Geofence", TertiaryColor)
            }
            Spacer(Modifier.height(6.dp))
            Text("Center: Dilla Town Center (6.4083, 38.3102) · Radius: 8.0 km", fontSize = 12.sp, color = GrayColor)
            Text("Service Zones Active: ${vm.serviceZones.count { it.isActive }} of ${vm.serviceZones.size} zones", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryColor, modifier = Modifier.padding(top = 2.dp))
        }

        // Audit Log Snapshot
        SoftCard {
            Text("Recent Administrative Actions", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
            Spacer(Modifier.height(6.dp))
            vm.auditLogs.take(3).forEach { log ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        Text(log.action + " · " + log.targetRecord, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkColor)
                        Text(log.details, fontSize = 10.sp, color = GrayColor)
                    }
                    Text(log.timestamp, fontSize = 9.sp, color = GrayColor)
                }
            }
        }
    }
}

/* ---------- 2. Customers Management View ---------- */
@Composable
private fun AdminCustomersView(vm: AppVm) {
    var query by remember { mutableStateOf("") }
    val filtered = vm.customers.filter { it.name.contains(query, ignoreCase = true) || it.phone.contains(query) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Registered Customers (${vm.customers.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkColor)
        SearchBarWithFilters(query = query, onQueryChange = { query = it }, placeholder = "Search customer by name or phone...")

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filtered) { c ->
                SoftCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(c.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                                Spacer(Modifier.width(6.dp))
                                StatusChip(if (c.isActive) "Active" else "Suspended", if (c.isActive) TertiaryColor else PrimaryColor)
                            }
                            Text(c.phone + " · " + c.email, fontSize = 11.sp, color = GrayColor)
                            Text("Registered: ${c.registeredDate} · Orders: ${c.totalOrders}", fontSize = 11.sp, color = GrayColor)
                            Text("Address: ${c.defaultAddress}", fontSize = 11.sp, color = InkColor, fontWeight = FontWeight.SemiBold)
                        }
                        OutlineButton(if (c.isActive) "Suspend" else "Activate") {
                            vm.toggleCustomerActive(c.id)
                        }
                    }
                }
            }
        }
    }
}

/* ---------- 3. Restaurant Management View ---------- */
@Composable
private fun AdminRestaurantsView(vm: AppVm) {
    var selectedReasonDialogAppId by remember { mutableStateOf<String?>(null) }
    var rejectionReasonInput by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Pending Restaurant Registrations", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkColor)

        val pendingApps = vm.restaurantApplications.filter { it.status == "Pending" }
        if (pendingApps.isEmpty()) {
            SoftCard { Text("No pending restaurant applications.", fontSize = 12.sp, color = GrayColor) }
        } else {
            pendingApps.forEach { app ->
                SoftCard {
                    Text(app.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = InkColor)
                    Text("Owner: ${app.ownerName} · Phone: ${app.phone}", fontSize = 12.sp, color = GrayColor)
                    Text("Cuisine: ${app.cuisine} · Address: ${app.address}", fontSize = 12.sp, color = GrayColor)
                    Text("Submitted: ${app.submittedAt} · Verification Doc: ${app.docUrl}", fontSize = 11.sp, color = PrimaryColor)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PrimaryButton("Approve Vendor", Modifier.weight(1f)) {
                            vm.approveRestaurantApplication(app.id)
                        }
                        OutlineButton("Reject Vendor", Modifier.weight(1f)) {
                            selectedReasonDialogAppId = app.id
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("Approved Dilla Kitchens (${Demo.restaurants.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkColor)
        Demo.restaurants.forEach { r ->
            SoftCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(r.emoji, fontSize = 28.sp)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(r.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                        Text("${r.cuisine} · ${r.address}", fontSize = 11.sp, color = GrayColor)
                        Text("Rating: ★${r.rating} · Distance: ${r.distanceKm} km", fontSize = 11.sp, color = GrayColor)
                    }
                    StatusChip(if (r.open) "Open" else "Closed", if (r.open) TertiaryColor else GrayColor)
                }
            }
        }
    }

    if (selectedReasonDialogAppId != null) {
        AlertDialog(
            onDismissRequest = { selectedReasonDialogAppId = null },
            title = { Text("Reject Restaurant Registration", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = rejectionReasonInput,
                    onValueChange = { rejectionReasonInput = it },
                    label = { Text("Rejection Reason") },
                    placeholder = { Text("e.g. Incomplete health permit or food license") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedReasonDialogAppId?.let { id ->
                            vm.rejectRestaurantApplication(id, rejectionReasonInput)
                        }
                        selectedReasonDialogAppId = null
                        rejectionReasonInput = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    Text("Confirm Rejection", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedReasonDialogAppId = null }) { Text("Cancel", color = GrayColor) }
            }
        )
    }
}

/* ---------- 4. Rider Management View ---------- */
@Composable
private fun AdminRidersView(vm: AppVm) {
    var rejectRiderId by remember { mutableStateOf<String?>(null) }
    var reasonText by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Pending Rider Registrations", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkColor)

        val pendingRiders = vm.riderApplications.filter { it.status == "Pending" }
        if (pendingRiders.isEmpty()) {
            SoftCard { Text("No pending rider applications.", fontSize = 12.sp, color = GrayColor) }
        } else {
            pendingRiders.forEach { r ->
                SoftCard {
                    Text(r.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = InkColor)
                    Text("Phone: ${r.phone} · Vehicle: ${r.vehicleType} (${r.plateNumber})", fontSize = 12.sp, color = GrayColor)
                    Text("Submitted: ${r.submittedAt} · Driving License: ${r.licenseDocUrl}", fontSize = 11.sp, color = PrimaryColor)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PrimaryButton("Approve Rider", Modifier.weight(1f)) {
                            vm.approveRiderApplication(r.id)
                        }
                        OutlineButton("Reject Rider", Modifier.weight(1f)) {
                            rejectRiderId = r.id
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("Approved Active Riders (${vm.riderApplications.count { it.status == "Approved" }})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkColor)
        vm.riderApplications.filter { it.status == "Approved" }.forEach { r ->
            SoftCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(r.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                        Text("Plate: ${r.plateNumber} · Deliveries: ${r.completedDeliveries}", fontSize = 11.sp, color = GrayColor)
                        Text("Earnings: ${etb(r.totalEarningsEtb)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TertiaryColor)
                    }
                    OutlineButton(if (r.isOnline) "Online" else "Offline") {
                        vm.toggleRiderActive(r.id)
                    }
                }
            }
        }
    }

    if (rejectRiderId != null) {
        AlertDialog(
            onDismissRequest = { rejectRiderId = null },
            title = { Text("Reject Rider Registration", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = reasonText,
                    onValueChange = { reasonText = it },
                    label = { Text("Rejection Reason") },
                    placeholder = { Text("e.g. Invalid driver's license") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        rejectRiderId?.let { id -> vm.rejectRiderApplication(id, reasonText) }
                        rejectRiderId = null
                        reasonText = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    Text("Confirm Rejection", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = { TextButton(onClick = { rejectRiderId = null }) { Text("Cancel", color = GrayColor) } }
        )
    }
}

/* ---------- 5. Order & Live Map View ---------- */
@Composable
private fun AdminOrdersView(vm: AppVm) {
    var statusFilter by remember { mutableStateOf("All") }
    val statuses = listOf("All", "Pending", "Preparing", "Ready", "InTransit", "Delivered", "Rejected")

    val filteredOrders = vm.orders.filter { o ->
        statusFilter == "All" || o.status.name.equals(statusFilter, ignoreCase = true)
    }

    val activeOrderForMap = vm.orders.firstOrNull { it.status == OrderStatus.InTransit || it.status == OrderStatus.Ready } ?: vm.orders.firstOrNull()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Live Delivery & Order Dispatch (${vm.orders.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkColor)

        // Status Filter Strip
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(statuses) { s ->
                Pill(s, statusFilter == s) { statusFilter = s }
            }
        }

        // Live TomTom Map View Component
        if (activeOrderForMap != null) {
            TomTomMapView(order = activeOrderForMap)
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(filteredOrders) { o ->
                SoftCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("ORDER #${o.id} · ${o.restaurant}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                            Text("Total: ${etb(o.totalEtb)} · Payment: ${o.payment}", fontSize = 11.sp, color = GrayColor)
                            Text("Destination: ${o.deliveryAddress.addressText}", fontSize = 11.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
                            Text("Rider: ${o.riderName} (${o.vehiclePlate})", fontSize = 11.sp, color = GrayColor)
                        }
                        StatusChip(o.status.label)
                    }
                    if (o.status != OrderStatus.Delivered && o.status != OrderStatus.Rejected) {
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlineButton("Cancel Order") { vm.cancelOrder(o.id) }
                        }
                    }
                }
            }
        }
    }
}

/* ---------- 6. Service Zones & Fees View ---------- */
@Composable
private fun AdminZonesView(vm: AppVm) {
    var showAddZoneDialog by remember { mutableStateOf(false) }

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
            Text("Service Zones & Pricing", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkColor)
            Button(
                onClick = { showAddZoneDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
            ) {
                Text("+ Add Zone", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        vm.serviceZones.forEach { z ->
            SoftCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(z.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                            Spacer(Modifier.width(6.dp))
                            StatusChip(if (z.isActive) "Active" else "Inactive", if (z.isActive) TertiaryColor else GrayColor)
                        }
                        Text(z.areaDescription, fontSize = 11.sp, color = GrayColor)
                        Text("Base Delivery Fee: ${etb(z.baseFeeEtb)} · Min Order: ${etb(z.minOrderEtb)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                    }
                    Switch(z.isActive, { vm.toggleServiceZoneActive(z.id) }, colors = SwitchDefaults.colors(checkedTrackColor = PrimaryColor))
                }
            }
        }
    }

    if (showAddZoneDialog) {
        var zoneName by remember { mutableStateOf("") }
        var areaDesc by remember { mutableStateOf("") }
        var baseFeeInput by remember { mutableStateOf("30") }

        AlertDialog(
            onDismissRequest = { showAddZoneDialog = false },
            title = { Text("Add New Service Zone", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = zoneName, onValueChange = { zoneName = it }, label = { Text("Zone Name") }, placeholder = { Text("e.g. Oda Roba Sector") }, singleLine = true)
                    OutlinedTextField(value = areaDesc, onValueChange = { areaDesc = it }, label = { Text("Description") }, placeholder = { Text("Residential kebeles") }, singleLine = true)
                    OutlinedTextField(value = baseFeeInput, onValueChange = { baseFeeInput = it }, label = { Text("Base Delivery Fee (ETB)") }, singleLine = true)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (zoneName.isNotBlank()) {
                            val newZ = ServiceZone(
                                id = "z_${System.currentTimeMillis()}",
                                name = zoneName,
                                areaDescription = areaDesc,
                                baseFeeEtb = baseFeeInput.toIntOrNull() ?: 30,
                                minOrderEtb = 100,
                                isActive = true
                            )
                            vm.addServiceZone(newZ)
                            showAddZoneDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) { Text("Save Zone", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showAddZoneDialog = false }) { Text("Cancel", color = GrayColor) } }
        )
    }
}

/* ---------- 7. Reports & Financial Analytics View ---------- */
@Composable
private fun AdminReportsView(vm: AppVm) {
    val totalRevenue = vm.orders.filter { it.status == OrderStatus.Delivered }.sumOf { it.totalEtb }
    val platformServiceFees = vm.orders.size * 10
    val riderEarnings = vm.orders.size * 25
    val restaurantEarnings = (totalRevenue - platformServiceFees - riderEarnings).coerceAtLeast(0)

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Financial & Operational Analytics", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkColor)

        SoftCard {
            Text("FINANCIAL REVENUE BREAKDOWN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrayColor)
            Spacer(Modifier.height(8.dp))
            ReportRow("Gross Order Value", etb(totalRevenue))
            ReportRow("Platform Service Fees (Dilla)", etb(platformServiceFees))
            ReportRow("Restaurant Net Earnings", etb(restaurantEarnings))
            ReportRow("Rider Courier Earnings", etb(riderEarnings))
            HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color(0xFFEAEAEA))
            ReportRow("Cash Collected (COD)", etb(totalRevenue))
        }

        SoftCard {
            Text("DELIVERY PERFORMANCE METRICS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrayColor)
            Spacer(Modifier.height(8.dp))
            ReportRow("Total Orders Processed", vm.orders.size.toString())
            ReportRow("Successful Deliveries", vm.orders.count { it.status == OrderStatus.Delivered }.toString())
            ReportRow("Active In-Transit", vm.orders.count { it.status == OrderStatus.InTransit }.toString())
            ReportRow("Cancelled / Rejected", vm.orders.count { it.status == OrderStatus.Rejected }.toString())
        }

        PrimaryButton("Export CSV Financial Report") {
            vm.addAuditLog("REPORT_EXPORTED", "Financial Report CSV", "Exported financial and operational summary")
        }
    }
}

/* ---------- 8. Audit Logs & System Activity View ---------- */
@Composable
private fun AdminAuditView(vm: AppVm) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("System Audit Logs (${vm.auditLogs.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkColor)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vm.auditLogs) { log ->
                SoftCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PrimaryColor.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(log.action, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                            Text("Target: ${log.targetRecord}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkColor)
                            Text(log.details, fontSize = 11.sp, color = GrayColor)
                        }
                        Text(log.timestamp, fontSize = 9.sp, color = GrayColor)
                    }
                }
            }
        }
    }
}

/* ---------- 9. Admin Settings View ---------- */
@Composable
private fun AdminSettingsView(vm: AppVm, onSignOut: () -> Unit) {
    var serviceFeeInput by remember { mutableStateOf("10") }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SoftCard {
            Text("Administrator Account", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = InkColor)
            Text("ID: admin_01 · Role: Platform Administrator", fontSize = 12.sp, color = GrayColor)
            Text("Authorized Scope: Dilla Town & Campus Network", fontSize = 11.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
        }

        SoftCard {
            Text("Global Fee Configuration", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = serviceFeeInput,
                onValueChange = { serviceFeeInput = it },
                label = { Text("Platform Service Fee (ETB)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            PrimaryButton("Save Fee Settings") {
                vm.addAuditLog("FEE_CONFIG_UPDATED", "Global Service Fee", "Set to $serviceFeeInput ETB")
            }
        }

        PrimaryButton("Sign Out of Admin Account", onClick = onSignOut)
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
