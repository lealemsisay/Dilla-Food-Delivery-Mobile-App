package com.dilla.food.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.dilla.food.data.*

private val PageBg = Brush.verticalGradient(listOf(Color.White, SurfaceBg))

/* ================= Screen 1: Choose your friends ================= */
@Composable
fun FriendsScreen(vm: AppVm, onBack: () -> Unit, onProceed: () -> Unit) {
    var showAdd by remember { mutableStateOf(false) }
    var showRename by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        ScreenHeader("Group Order Room", onBack)
        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                SoftCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(vm.groupName, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = InkColor)
                                Spacer(Modifier.width(6.dp))
                                IconButton({ showRename = true }, Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Edit, contentDescription = "Rename", tint = PrimaryColor, modifier = Modifier.size(16.dp))
                                }
                            }
                            Text("Invite Code: ${vm.groupInviteCode} · Active Dilla Group", fontSize = 11.sp, color = GrayColor)
                        }
                        StatusChip(if (vm.groupMode) "Group Room" else "Individual", SecondaryColor)
                    }
                }
            }

            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Select Friends in this Group (${vm.participants.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                    TextButton({ showAdd = true }) {
                        Text("+ Invite Friend", fontSize = 12.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(vm.people) { friend ->
                val isSelected = friend.id in vm.selected
                SoftCard(Modifier.clickable { vm.toggle(friend.id) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Avatar(friend, size = 44.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(friend.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                            Text(friend.note, fontSize = 11.sp, color = GrayColor)
                        }
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = { vm.toggle(friend.id) },
                            colors = CheckboxDefaults.colors(checkedColor = PrimaryColor)
                        )
                    }
                }
            }
        }

        Surface(
            Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(Modifier.padding(16.dp)) {
                PrimaryButton("Proceed to Food Menu") { onProceed() }
            }
        }
    }

    if (showAdd) {
        var friendName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text("Invite Friend to Group", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = friendName,
                    onValueChange = { friendName = it },
                    label = { Text("Friend's Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (friendName.isNotBlank()) vm.addFriend(friendName)
                        showAdd = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) { Text("Add to Group", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton({ showAdd = false }) { Text("Cancel", color = GrayColor) } }
        )
    }

    if (showRename) {
        var newName by remember { mutableStateOf(vm.groupName) }
        AlertDialog(
            onDismissRequest = { showRename = false },
            title = { Text("Rename Group Room", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Group Room Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) vm.groupName = newName.trim()
                        showRename = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) { Text("Save Name", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton({ showRename = false }) { Text("Cancel", color = GrayColor) } }
        )
    }
}

/* ================= Screen 2: Food Menu ================= */
@Composable
fun FoodScreen(vm: AppVm, onBack: () -> Unit, onCheckout: () -> Unit) {
    var query by remember { mutableStateOf("") }
    val filteredMenu = vm.menu.filter { it.name.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true) }

    Column(Modifier.fillMaxSize().background(PageBg)) {
        ScreenHeader("Kitchen Menu · ${vm.restaurant.name}", onBack)

        SearchBarWithFilters(query = query, onQueryChange = { query = it }, placeholder = "Search menu items...")

        // Participant Switcher Header
        LazyRow(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(vm.participants) { p ->
                val isSelected = p.id == vm.activeId
                Surface(
                    modifier = Modifier.clip(RoundedCornerShape(50)).clickable { vm.activeId = p.id },
                    shape = RoundedCornerShape(50),
                    color = if (isSelected) PrimaryColor else Color.White,
                    border = BorderStroke(1.dp, if (isSelected) PrimaryColor else Color(0xFFE0E0E0))
                ) {
                    Row(Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Avatar(p, size = 20.dp)
                        Spacer(Modifier.width(6.dp))
                        Text(p.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else InkColor)
                    }
                }
            }
        }

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredMenu) { item ->
                val qty = vm.qtyOf(vm.activeId, item.id)
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
                            Text(item.description, fontSize = 11.sp, color = GrayColor, maxLines = 1)
                            Text(etb(item.priceEtb), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                        }

                        // Quantity Stepper
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (qty > 0) {
                                IconButton({ vm.remove(vm.activeId, item.id) }, Modifier.size(28.dp)) {
                                    Icon(Icons.Default.Clear, contentDescription = "Decrease", tint = PrimaryColor)
                                }
                                Text(qty.toString(), Modifier.padding(horizontal = 6.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            IconButton({ vm.add(vm.activeId, item.id) }, Modifier.size(28.dp)) {
                                Icon(Icons.Default.AddCircle, contentDescription = "Add", tint = PrimaryColor)
                            }
                        }
                    }
                }
            }
        }

        Surface(
            Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Row(
                Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Total (${vm.cartCount()} items)", fontSize = 11.sp, color = GrayColor)
                    Text(etb(vm.grandTotal()), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = InkColor)
                }
                PrimaryButton("View Cart & Checkout", Modifier.weight(1.2f), enabled = vm.cart.isNotEmpty()) {
                    onCheckout()
                }
            }
        }
    }
}

/* ================= Screen 3: Checkout ================= */
@Composable
fun CheckoutScreen(vm: AppVm, onBack: () -> Unit, onPlaced: (String) -> Unit) {
    var payment by remember { mutableStateOf("Cash on delivery") }
    var showAddressModal by remember { mutableStateOf(false) }
    val slots = listOf<String?>(null, "Deliver Now (30 min)", "Lunch Break (1:15 PM)", "Evening 7:00 PM")

    val selAddr = vm.selectedAddress

    Column(Modifier.fillMaxSize().background(PageBg)) {
        ScreenHeader("Cart & Checkout", onBack)
        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("DELIVERY LOCATION: ${selAddr.label.uppercase()}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                            }
                            TextButton(onClick = { showAddressModal = true }) {
                                Text("Change", fontSize = 11.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(selAddr.addressText, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = InkColor)
                        if (selAddr.landmark.isNotBlank()) {
                            Text("Landmark: ${selAddr.landmark}", fontSize = 11.sp, color = GrayColor)
                        }
                    }
                }
            }

            item { Text("Delivery Schedule", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor, modifier = Modifier.padding(vertical = 4.dp)) }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(slots) { slot ->
                        val text = slot ?: "As Soon As Possible"
                        Pill(text, vm.scheduledFor == slot) { vm.scheduledFor = slot }
                    }
                }
            }

            item { Spacer(Modifier.height(12.dp)); Text("Order Breakdown by Participant", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor) }

            items(vm.participants.filter { vm.subtotal(it.id) > 0 }) { p ->
                SoftCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Avatar(p, size = 32.dp)
                        Spacer(Modifier.width(8.dp))
                        Text(p.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = InkColor, modifier = Modifier.weight(1f))
                        Text(etb(vm.subtotal(p.id)), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryColor)
                    }
                    Spacer(Modifier.height(6.dp))
                    vm.cart.filter { it.personId == p.id }.forEach { line ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${line.qty}x ${vm.menu.first { it.id == line.itemId }.name}", fontSize = 12.sp, color = InkColor)
                            Text(etb(vm.price(line.itemId) * line.qty), fontSize = 12.sp, color = GrayColor)
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(12.dp))
                SoftCard {
                    Text("Payment & Fee Details", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                    Spacer(Modifier.height(6.dp))
                    FeeRow("Items Subtotal", vm.cart.sumOf { vm.price(it.itemId) * it.qty })
                    FeeRow("Restaurant Delivery Fee", vm.restaurant.feeEtb)
                    FeeRow("Platform Service Fee", vm.serviceFee)
                    HorizontalDivider(Modifier.padding(vertical = 6.dp), color = Color(0xFFEAEAEA))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Grand Total", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                        Text(etb(vm.grandTotal()), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = PrimaryColor)
                    }
                }
            }
        }

        Surface(
            Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Activate Equal Payment Split", Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = InkColor)
                    Switch(
                        checked = vm.splitOn,
                        onCheckedChange = { vm.splitOn = it },
                        enabled = vm.participants.size > 1,
                        colors = SwitchDefaults.colors(checkedTrackColor = PrimaryColor)
                    )
                }
                HorizontalDivider(Modifier.padding(vertical = 10.dp), color = Color(0xFFEAEAEA))
                Row(Modifier.fillMaxWidth().padding(bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Total to Pay:", Modifier.weight(1f), fontSize = 12.sp, color = GrayColor)
                    Text(etb(vm.grandTotal()), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = PrimaryColor)
                }
                PrimaryButton("Make Payment of ${etb(vm.grandTotal())}", enabled = vm.cart.isNotEmpty()) {
                    onPlaced(vm.placeOrder(payment + if (vm.splitOn) " (split)" else ""))
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
private fun FeeRow(label: String, v: Int) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 11.sp, color = GrayColor)
        Text(etb(v), fontSize = 11.sp, color = InkColor)
    }
}
