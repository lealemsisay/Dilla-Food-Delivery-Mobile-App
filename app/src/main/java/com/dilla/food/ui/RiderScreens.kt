package com.dilla.food.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.dilla.food.data.*

@Composable
fun RiderMainShell(
    vm: AppVm,
    onOpenProfile: () -> Unit = {},
    onOpenChangePass: () -> Unit = {},
    onSignOut: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        "Active Jobs" to Icons.Default.ShoppingCart,
        "Dashboard" to Icons.Default.Home,
        "Earnings" to Icons.Default.Place,
        "Settings" to Icons.Default.Settings
    )

    GlobalRoleDrawer(
        vm = vm,
        role = Role.Rider,
        currentSelectedLabel = tabs[selectedTab].first,
        onSelectItem = { routeKey ->
            when (routeKey) {
                "dashboard" -> selectedTab = 1
                "requests" -> selectedTab = 0
                "active" -> selectedTab = 0
                "history" -> selectedTab = 2
                "earnings" -> selectedTab = 2
                "reports" -> selectedTab = 2
                "notifications" -> selectedTab = 1
                "profile" -> onOpenProfile()
                "settings" -> selectedTab = 3
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
                                        .background(PrimaryColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Place, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                }
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Amanuel Girma", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = InkColor)
                                        Spacer(Modifier.width(4.dp))
                                        StatusChip("Approved Rider", TertiaryColor)
                                    }
                                    Text("TVS Motor · ET-3-4819", fontSize = 11.sp, color = GrayColor)
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
                    0 -> RiderJobsView(vm)
                    1 -> RiderDashboardView(vm)
                    2 -> RiderEarningsView(vm)
                    else -> RiderSettingsView(vm, onSignOut)
                }
            }
        }
    }
}

/* ---------- 1. Active Jobs & TomTom GPS Navigation View ---------- */
@Composable
private fun RiderJobsView(vm: AppVm) {
    var isOnline by remember { mutableStateOf(true) }
    var pinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }

    val activeJobs = vm.orders.filter { it.status == OrderStatus.Ready || it.status == OrderStatus.InTransit }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Online / Offline Status Toggle Card
        SoftCard {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(if (isOnline) "You are Online in Dilla" else "You are Offline", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = InkColor)
                    Text("GPS live updates active for Dilla Town & Campus", fontSize = 11.sp, color = GrayColor)
                }
                Switch(
                    checked = isOnline,
                    onCheckedChange = { isOnline = it },
                    colors = SwitchDefaults.colors(checkedTrackColor = PrimaryColor)
                )
            }
        }

        if (!isOnline) {
            EmptyState("You are currently offline", "Switch to online mode to receive courier dispatch offers in Dilla.")
        } else if (activeJobs.isEmpty()) {
            EmptyState("No active delivery offers", "Orders marked ready by kitchens in Piazza, Kebeles or Campus will appear here.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(activeJobs) { o ->
                    SoftCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("JOB #${o.id} · ${o.restaurant}", Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                            StatusChip(o.status.label, if (o.status == OrderStatus.InTransit) PrimaryColor else SecondaryColor)
                        }
                        Spacer(Modifier.height(6.dp))

                        // Pickup Location Info
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = TertiaryColor, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("PICKUP: ${o.pickupLocation}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkColor)
                        }

                        // Delivery Destination Info
                        Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Column {
                                Text("DROP-OFF: ${o.deliveryAddress.label.uppercase()} (${o.deliveryAddress.areaZone})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                                Text(o.deliveryAddress.addressText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkColor)
                                if (o.deliveryAddress.landmark.isNotBlank()) {
                                    Text("Landmark: ${o.deliveryAddress.landmark}", fontSize = 11.sp, color = GrayColor)
                                }
                                if (o.deliveryAddress.deliveryInstructions.isNotBlank()) {
                                    Text("Instructions: ${o.deliveryAddress.deliveryInstructions}", fontSize = 11.sp, color = GrayColor)
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                        // Interactive TomTom Navigation Map View Component
                        TomTomMapView(order = o)

                        Spacer(Modifier.height(10.dp))
                        if (o.status == OrderStatus.Ready) {
                            PrimaryButton("Accept Job & Confirm Pickup") {
                                vm.setStatus(o.id, OrderStatus.InTransit)
                            }
                        } else {
                            // Handover PIN Verification
                            OutlinedTextField(
                                value = pinInput,
                                onValueChange = { pinInput = it.filter(Char::isDigit).take(4) },
                                label = { Text("Customer Handover 4-Digit Security PIN") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                modifier = Modifier.fillMaxWidth()
                            )
                            pinError?.let { Text(it, color = PrimaryColor, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp)) }
                            Spacer(Modifier.height(8.dp))
                            PrimaryButton("Confirm Handover & Complete Delivery") {
                                if (pinInput == o.pin) {
                                    vm.setStatus(o.id, OrderStatus.Delivered)
                                    pinInput = ""
                                    pinError = null
                                } else {
                                    pinError = "Incorrect security PIN. Please ask customer for 4-digit handover PIN."
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/* ---------- 2. Rider Dashboard View ---------- */
@Composable
private fun RiderDashboardView(vm: AppVm) {
    val completedDeliveries = vm.orders.filter { it.status == OrderStatus.Delivered }
    val todayEarnings = completedDeliveries.size * 35

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Courier Operational Dashboard", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkColor)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Today's Earnings", etb(todayEarnings), Icons.Default.ShoppingCart, PrimaryColor, Modifier.weight(1f))
            MetricCard("Completed Today", "${completedDeliveries.size} jobs", Icons.Default.CheckCircle, TertiaryColor, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard("Courier Rating", "★ 4.9", Icons.Default.Star, SecondaryColor, Modifier.weight(1f))
            MetricCard("Total Distance", "14.2 km", Icons.Default.Place, PrimaryColor, Modifier.weight(1f))
        }

        SoftCard {
            Text("SERVICE COVERAGE AREA", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrayColor)
            Spacer(Modifier.height(4.dp))
            Text("Dilla Town Kebeles 01, 02, 03 & University Campus", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = InkColor)
            Text("Active Geofence: 8.0 km radius from Piazza Center", fontSize = 12.sp, color = PrimaryColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

/* ---------- 3. Rider Earnings & History View ---------- */
@Composable
private fun RiderEarningsView(vm: AppVm) {
    val context = LocalContext.current
    var selectedPeriod by remember { mutableStateOf("This Week") }
    val periods = listOf("Today", "This Week", "This Month", "Custom Range")

    val deliveredOrders = vm.orders.filter { it.status == OrderStatus.Delivered }
    val totalEarnings = deliveredOrders.size * 35 + 1200 // Base historical earnings

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
            Text("Earnings & Delivery History", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = InkColor)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                items(periods) { p ->
                    Pill(p, selectedPeriod == p) { selectedPeriod = p }
                }
            }
        }

        SoftCard {
            Text("EARNINGS SUMMARY ($selectedPeriod)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GrayColor)
            Spacer(Modifier.height(8.dp))
            ReportRow("Total Courier Earnings", etb(totalEarnings))
            ReportRow("Completed Deliveries", "${deliveredOrders.size + 18} jobs")
            ReportRow("Cash Collected (COD)", etb(deliveredOrders.sumOf { it.totalEtb }))
            ReportRow("Average Fee Per Delivery", etb(35))
        }

        SoftCard {
            Text("Export Rider Earnings Report", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
            Text("Download or share verified courier delivery log and earnings.", fontSize = 11.sp, color = GrayColor)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrimaryButton("Export CSV (.csv)", Modifier.weight(1f)) {
                    ReportExportService.exportRiderReportCsv(context, vm, selectedPeriod)
                }
                OutlineButton("Export Excel (.xlsx)", Modifier.weight(1f)) {
                    ReportExportService.exportRiderReportExcel(context, vm, selectedPeriod)
                }
            }
        }

        Text("Completed Job Logs", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = InkColor)
        if (deliveredOrders.isEmpty()) {
            SoftCard { Text("No completed jobs today yet.", fontSize = 12.sp, color = GrayColor) }
        } else {
            deliveredOrders.forEach { o ->
                SoftCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("JOB #${o.id} · ${o.restaurant}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = InkColor)
                            Text("To: ${o.deliveryAddress.addressText}", fontSize = 11.sp, color = GrayColor)
                            Text("Earnings: ${etb(35)} · Cash Collected: ${etb(o.totalEtb)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TertiaryColor)
                        }
                        StatusChip("Delivered", TertiaryColor)
                    }
                }
            }
        }
    }
}

/* ---------- 4. Rider Settings View ---------- */
@Composable
private fun RiderSettingsView(vm: AppVm, onSignOut: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SoftCard {
            Text("Amanuel Girma", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = InkColor)
            Text("Role: Delivery Courier · Vehicle: TVS Motor", fontSize = 12.sp, color = GrayColor)
            Text("License Plate: ET-3-4819 · Status: Verified Rider", fontSize = 11.sp, color = TertiaryColor, fontWeight = FontWeight.Bold)
        }

        SoftCard {
            Text("GPS & Location Settings", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
            Spacer(Modifier.height(4.dp))
            Text("Foreground Service Location Tracking: Active", fontSize = 12.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
            Text("Battery Optimization: Disabled for background location transmission", fontSize = 11.sp, color = GrayColor)
        }

        PrimaryButton("Sign Out of Rider Account", onClick = onSignOut)
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
