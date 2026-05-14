package com.tripmate.app.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tripmate.app.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

@Composable
fun SplashScreen(navController: NavController) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )
    val scaleAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1.0f else 0.7f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(1500)
        navController.navigate(Screen.Login.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.surface)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = com.tripmate.app.R.drawable.app_logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer(alpha = alphaAnim, scaleX = scaleAnim, scaleY = scaleAnim)
            )
            Text(
                "TripMate",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier.graphicsLayer(alpha = alphaAnim)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Your Journey, Optimized",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.graphicsLayer(alpha = alphaAnim)
            )
        }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("admin@gmail.com") }
    var password by remember { mutableStateOf("12345") }
    var isLoading by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        DynamicBackground()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .imePadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AuthHeader(
                title = "Welcome Back",
                subtitle = "Sign in to plan your next adventure"
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                color = Color.White.copy(alpha = 0.1f),
                border = BoxBorder(Color.White.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    AnimatedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        icon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AnimatedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        icon = Icons.Default.Lock,
                        isPassword = true,
                        keyboardType = KeyboardType.Password
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            var rememberMe by remember { mutableStateOf(false) }
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                            )
                            Text("Remember Me", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
                        }
                        Text(
                            "Forgot?",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    AnimatedButton(
                        text = if (isLoading) "Signing in..." else "Login",
                        onClick = {
                            if (email.isNotEmpty() && password.isNotEmpty()) {
                                scope.launch {
                                    isLoading = true
                                    try {
                                        val loginResult = com.tripmate.app.network.SupabaseRepository.validateLogin(email, password)
                                        if (loginResult.isSuccess) {
                                            val userId = loginResult.getOrNull()!!
                                            val profile = com.tripmate.app.network.SupabaseRepository.getUserProfile(userId)
                                            if (profile != null) {
                                                com.tripmate.app.data.MockDataProvider.currentUser = profile
                                                navController.navigate(Screen.Home.route) {
                                                    popUpTo(Screen.Login.route) { inclusive = true }
                                                }
                                            } else {
                                                if (email == "admin@gmail.com" && password == "12345") {
                                                    com.tripmate.app.data.MockDataProvider.currentUser = com.tripmate.app.models.UserProfile(
                                                        id = "admin-local",
                                                        name = "Admin User",
                                                        status = "Administrator",
                                                        email = email,
                                                        tripsCount = 0,
                                                        countriesCount = 0,
                                                        budgetSpent = "₹0",
                                                        profileImage = null
                                                    )
                                                    navController.navigate(Screen.Home.route) {
                                                        popUpTo(Screen.Login.route) { inclusive = true }
                                                    }
                                                } else {
                                                    android.widget.Toast.makeText(navController.context, "Login failed: Profile not found", android.widget.Toast.LENGTH_LONG).show()
                                                }
                                            }
                                        } else {
                                            android.widget.Toast.makeText(navController.context, "Login failed: ${loginResult.exceptionOrNull()?.message}", android.widget.Toast.LENGTH_LONG).show()
                                        }
                                    } catch (e: Exception) {
                                        android.widget.Toast.makeText(navController.context, "Login failed: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Row(
                modifier = Modifier.clickable { navController.navigate(Screen.Signup.route) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account? ", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f))
                Text(
                    "Sign Up",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Black
                )
            }
        }
        
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SignupScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var agreeToTerms by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(modifier = Modifier.fillMaxSize()) {
        DynamicBackground()
        
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                AuthHeader(
                    title = "Create Account",
                    subtitle = "Join thousands of travelers worldwide"
                )

                Spacer(modifier = Modifier.height(32.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    color = Color.White.copy(alpha = 0.1f),
                    border = BoxBorder(Color.White.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        AnimatedTextField(value = name, onValueChange = { name = it }, label = "Full Name", icon = Icons.Default.Person)
                        Spacer(modifier = Modifier.height(16.dp))
                        AnimatedTextField(value = email, onValueChange = { email = it }, label = "Email", icon = Icons.Default.Email, keyboardType = KeyboardType.Email)
                        Spacer(modifier = Modifier.height(16.dp))
                        AnimatedTextField(value = password, onValueChange = { password = it }, label = "Password", icon = Icons.Default.Lock, isPassword = true, keyboardType = KeyboardType.Password)

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(checked = agreeToTerms, onCheckedChange = { agreeToTerms = it })
                            Text(
                                "I agree to the Terms & Privacy Policy",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.clickable { agreeToTerms = !agreeToTerms }
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        AnimatedButton(
                            text = if (isLoading) "Creating Account..." else "Sign Up",
                            onClick = {
                                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && agreeToTerms) {
                                    scope.launch {
                                        isLoading = true
                                        try {
                                            val signUpResult = com.tripmate.app.network.SupabaseRepository.registerUser(email, password, name)
                                            if (signUpResult.isSuccess) {
                                                android.widget.Toast.makeText(navController.context, "Signup successful! You can now login.", android.widget.Toast.LENGTH_LONG).show()
                                                navController.popBackStack()
                                            } else {
                                                snackbarHostState.showSnackbar("Signup failed: ${signUpResult.exceptionOrNull()?.message}")
                                            }
                                        } catch (e: Exception) {
                                            snackbarHostState.showSnackbar("Signup failed: ${e.message}")
                                        } finally {
                                            isLoading = false
                                        }
                                    }
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Please fill all fields and agree to terms")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isLoading
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text("Already have an account?", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f))
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("Login Now", fontWeight = FontWeight.Black)
                }
            }
        }
        
        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun AnimatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        singleLine = true
    )
}

@Composable
fun AnimatedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f)

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        enabled = enabled,
        modifier = modifier
            .scale(scale)
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.Black
        )
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
    }
}

// PREMIUM COMPONENTS
@Composable
fun DynamicBackground() {
    val infiniteTransition = rememberInfiniteTransition()
    val color1 by infiniteTransition.animateColor(
        initialValue = MaterialTheme.colorScheme.primary,
        targetValue = Color(0xFF6366F1), // Indigo
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val color2 by infiniteTransition.animateColor(
        initialValue = Color(0xFF1E1B4B), // Deep Blue
        targetValue = Color(0xFF000000), // Black
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(color1.copy(alpha = 0.2f), color2)))
    )
}

@Composable
fun AuthHeader(title: String, subtitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = com.tripmate.app.R.drawable.app_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(100.dp).padding(bottom = 16.dp)
        )
        Text(
            title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}


fun BoxBorder(color: Color) = androidx.compose.foundation.BorderStroke(1.dp, color)
