package com.dilla.food

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dilla.food.data.*
import com.dilla.food.ui.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DillaTheme { App() } }
    }
}

@Composable
fun App(vm: AppVm = viewModel()) {
    val nav = rememberNavController()
    fun signOut(n: NavHostController) { vm.role = null; n.navigate("signin") { popUpTo(n.graph.id) { inclusive = true } } }

    NavHost(nav, startDestination = "splash") {
        composable("splash") { SplashScreen { nav.navigate("signin") { popUpTo("splash") { inclusive = true } } } }
        composable("signin") {
            SignInScreen(vm) { r -> nav.navigate(r.name.lowercase()) { popUpTo("signin") { inclusive = true } } }
        }
        composable("customer") { CustomerShell(vm, { nav.navigate("friends") }, { nav.navigate("order/$it") }, { signOut(nav) }) }
        composable("restaurant") { RestaurantScreen(vm) { signOut(nav) } }
        composable("rider") { RiderScreen(vm) { signOut(nav) } }
        composable("admin") { AdminScreen(vm) { signOut(nav) } }
        composable("friends") { FriendsScreen(vm, { nav.popBackStack() }, { nav.navigate("food") }) }
        composable("food") { FoodScreen(vm, { nav.popBackStack() }, { nav.navigate("checkout") }) }
        composable("checkout") {
            CheckoutScreen(vm, { nav.popBackStack() }) { id -> nav.navigate("order/$id") { popUpTo("customer") } }
        }
        composable("order/{id}") { e -> OrderDetailScreen(vm, e.arguments?.getString("id") ?: "") { nav.popBackStack() } }
    }
}
