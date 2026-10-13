package com.example.rentmate

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainRentMateNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {

        // =========================================================
        // WELCOME
        // =========================================================
        composable("welcome") {

            WelcomeScreen(
                onGetStartedClick = {
                    navController.navigate("signup")
                },

                onLoginClick = {
                    navController.navigate("login")
                }
            )
        }


        // =========================================================
        // SIGN UP
        // =========================================================
        composable("signup") {

            SignUpScreen(
                onSignUpSuccess = {

                    navController.navigate("household_gate") {

                        popUpTo("welcome") {
                            inclusive = false
                        }

                        launchSingleTop = true
                    }
                },

                onLoginClick = {
                    navController.navigate("login")
                }
            )
        }


        // =========================================================
        // LOGIN
        // =========================================================
        composable("login") {

            LoginScreen(
                onLoginSuccess = {

                    navController.navigate("household_gate") {

                        popUpTo("welcome") {
                            inclusive = false
                        }

                        launchSingleTop = true
                    }
                },

                onSignUpClick = {
                    navController.navigate("signup")
                }
            )
        }


        // =========================================================
        // DASHBOARD
        // =========================================================
        composable("household_gate") {
            HouseholdGateScreen(
                onApproved = { householdId ->
                    navController.navigate("dashboard") {
                        popUpTo("household_gate") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("household_gate") { inclusive = true }
                    }
                }
            )
        }

        composable("dashboard") {

            DashboardScreen(

                // -------------------------------------------------
                // PROFILE
                // -------------------------------------------------
                onProfileClick = {
                    navController.navigate("profile")
                },


                // -------------------------------------------------
                // SETTINGS
                // -------------------------------------------------
                //
                // Settings is already handled inside
                // DashboardScreen.
                //
                // The DashboardScreen itself controls the
                // Settings dialog, so we do not navigate to
                // another screen here.
                //
                onSettingsClick = {
                    // Dashboard handles Settings internally.
                },


                // -------------------------------------------------
                // LOGOUT
                // -------------------------------------------------
                onLogoutClick = {

                    navController.navigate("login") {

                        popUpTo("dashboard") {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }


        // =========================================================
        // PROFILE
        // =========================================================
        composable("profile") {

            ProfileScreen(

                onBackClick = {
                    navController.popBackStack()
                },

                onLogoutClick = {

                    navController.navigate("login") {

                        popUpTo("dashboard") {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

