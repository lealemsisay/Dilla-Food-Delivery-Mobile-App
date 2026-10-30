package com.dilla.food.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.dilla.food.R
import com.dilla.food.data.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onDone: () -> Unit) {
    LaunchedEffect(Unit) { delay(1400); onDone() }
    Box(Modifier.fillMaxSize().background(PrimaryColor), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = "Dilla Food App Icon",
                modifier = Modifier
                    .size(100.dp)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
            )
            Spacer(Modifier.height(16.dp))
            Text("Dilla Food Delivery", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Dilla Town & Campus Network", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
        }
    }
}

@Composable
fun SignInScreen(
    vm: AppVm,
    onSignedIn: (Role) -> Unit
) {
    var role by remember { mutableStateOf(Role.Customer) }
    var createAccount by remember { mutableStateOf(false) }
    var showForgotPassword by remember { mutableStateOf(false) }

    // Common Auth Form Fields
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var infoMessage by remember { mutableStateOf<String?>(null) }

    // Role-Specific Fields
    var restaurantName by remember { mutableStateOf("") }
    var cuisineType by remember { mutableStateOf("Traditional · Tibs & Shiro") }
    var restaurantAddress by remember { mutableStateOf("Piazza Main Street, Kebele 01") }
    var vehicleType by remember { mutableStateOf("TVS Motorcycle") }
    var plateNumber by remember { mutableStateOf("ET-3-4819") }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .imePadding()
    ) {
        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.app_icon),
                contentDescription = "App Icon",
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text("DILLA FOOD DELIVERY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryColor, letterSpacing = 1.sp)
                Text("Town & Campus Network", fontSize = 12.sp, color = GrayColor)
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            if (createAccount) "Create Your Account" else "Sign In",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = InkColor
        )

        Spacer(Modifier.height(16.dp))
        Text("I am signing in as:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GrayColor)
        Row(
            Modifier
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Role.values().forEach { r ->
                Pill(r.label, role == r) {
                    role = r
                    error = null
                    infoMessage = null
                }
            }
        }

        if (createAccount) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            )
        }

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it.filter(Char::isDigit).take(9) },
            label = { Text("Phone Number") },
            prefix = { Text("+251 ") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address (Optional)") },
            placeholder = { Text("e.g. user@dilla.edu.et") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        )

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        )

        // Role Specific Inputs for Registration
        if (createAccount) {
            when (role) {
                Role.Restaurant -> {
                    Spacer(Modifier.height(8.dp))
                    Text("Restaurant Application Details", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                    OutlinedTextField(
                        value = restaurantName,
                        onValueChange = { restaurantName = it },
                        label = { Text("Restaurant / Kitchen Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = cuisineType,
                        onValueChange = { cuisineType = it },
                        label = { Text("Cuisine Category") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = restaurantAddress,
                        onValueChange = { restaurantAddress = it },
                        label = { Text("Dilla Town Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = SecondaryColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            "⏳ Note: Restaurant accounts remain PENDING until approved by Dilla Platform Administrators.",
                            Modifier.padding(10.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6C3E00)
                        )
                    }
                }
                Role.Rider -> {
                    Spacer(Modifier.height(8.dp))
                    Text("Courier Verification Details", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                    OutlinedTextField(
                        value = vehicleType,
                        onValueChange = { vehicleType = it },
                        label = { Text("Vehicle Model") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = plateNumber,
                        onValueChange = { plateNumber = it },
                        label = { Text("License Plate Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = SecondaryColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            "⏳ Note: Rider accounts remain PENDING until driving license verification is approved.",
                            Modifier.padding(10.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6C3E00)
                        )
                    }
                }
                Role.Admin -> {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = PrimaryColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            "🔒 Admin Registration requires trusted backend approval.",
                            Modifier.padding(10.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor
                        )
                    }
                }
                else -> {}
            }
        }

        error?.let {
            Text(it, color = PrimaryColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
        }

        infoMessage?.let {
            Text(it, color = TertiaryColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(Modifier.height(16.dp))
        PrimaryButton(if (createAccount) "Register Account" else "Sign In") {
            error = when {
                createAccount && name.isBlank() -> "Please enter your full name."
                phone.length != 9 -> "Please enter a valid 9-digit Ethiopian phone number after +251."
                pass.length < 6 -> "Password must be at least 6 characters."
                createAccount && role == Role.Restaurant && restaurantName.isBlank() -> "Please enter your restaurant name."
                else -> null
            }

            if (error == null) {
                vm.userName = if (name.isNotBlank()) name else "+251 $phone"
                vm.role = role

                if (createAccount) {
                    if (role == Role.Restaurant) {
                        vm.restaurantApplications.add(
                            0,
                            RestaurantApplication(
                                id = "ra_" + System.currentTimeMillis(),
                                name = restaurantName,
                                ownerName = name,
                                cuisine = cuisineType,
                                phone = "+251 $phone",
                                address = restaurantAddress,
                                status = "Pending"
                            )
                        )
                        infoMessage = "Restaurant application submitted! Status is PENDING admin approval."
                    } else if (role == Role.Rider) {
                        vm.riderApplications.add(
                            0,
                            RiderApplication(
                                id = "ri_" + System.currentTimeMillis(),
                                name = name,
                                phone = "+251 $phone",
                                vehicleType = vehicleType,
                                plateNumber = plateNumber,
                                status = "Pending"
                            )
                        )
                        infoMessage = "Rider application submitted! Status is PENDING admin approval."
                    } else {
                        infoMessage = "Account registered successfully!"
                    }
                }

                onSignedIn(role)
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { showForgotPassword = true }) {
                Text("Forgot Password?", fontSize = 11.sp, color = GrayColor)
            }
            TextButton(onClick = {
                createAccount = !createAccount
                error = null
                infoMessage = null
            }) {
                Text(
                    if (createAccount) "Existing Account? Sign In" else "New User? Register",
                    fontSize = 12.sp,
                    color = PrimaryColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showForgotPassword) {
        var resetTarget by remember { mutableStateOf(email.ifBlank { phone }) }
        var resetSent by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showForgotPassword = false },
            title = { Text("Reset Your Password", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter your registered email or phone number to receive a password reset link.", fontSize = 12.sp, color = GrayColor)
                    OutlinedTextField(
                        value = resetTarget,
                        onValueChange = { resetTarget = it },
                        label = { Text("Email or Phone") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (resetSent) {
                        Text("✓ Password reset instructions sent!", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TertiaryColor)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (resetTarget.isNotBlank()) {
                            resetSent = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    Text("Send Reset Link", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPassword = false }) { Text("Close", color = GrayColor) }
            }
        )
    }
}

/** Status screen shown when an application is pending administrator approval. */
@Composable
fun PendingApprovalScreen(
    roleName: String,
    onSignOut: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(SurfaceBg)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = SecondaryColor.copy(alpha = 0.15f),
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = SecondaryColor, modifier = Modifier.size(40.dp))
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Application Pending Approval", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = InkColor)
        Text(
            "Your $roleName registration is currently under review by Dilla Platform Administrators. You will receive an alert once approved.",
            fontSize = 13.sp,
            color = GrayColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(Modifier.height(24.dp))
        PrimaryButton("Sign Out", onClick = onSignOut)
    }
}
