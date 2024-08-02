package com.project.covidvaccineschedule

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.project.covidvaccineschedule.ui.theme.CovidVaccineScheduleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CovidVaccineScheduleTheme {
                App()
            }
        }
    }
}

@Composable
fun App(){

    val navController =  rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable(route = "login" ){
            LoginScreen(navController, LocalContext.current)
        }
        composable(route = "register" ){
            RegisterScreen(navController, LocalContext.current)
        }
        composable(route = "mainScreen"){
            MainScreenActivity()
        }
    }
}

fun isUserLoggedIn(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("LoginPreferences", Context.MODE_PRIVATE)
    return sharedPreferences.getBoolean("isLoggedIn", false)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController,context: Context){

    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var showProgressBar by remember { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val auth = FirebaseAuth.getInstance()
    val (isLoading, setLoading) = remember { mutableStateOf(false) }

    if (isUserLoggedIn(context)) {
        navController.navigate("mainScreen"){
            popUpTo(0)
        }
    }

    fun readUserData(context: Context,email: String, setLoading: (Boolean) -> Unit){
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        val query = usersRef.orderByChild("email").equalTo(email)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                setLoading(false)
                Log.d("UserData", "Data snapshot: ${snapshot.value}")
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(UserModel::class.java)
                        // Process each user object
                        if (user != null) {
                            // Access user properties
                            var username = user.username
                            var email = user.email
                            var password = user.password

                            if (username != null && email != null && password != null) {
                                UserPreferences.saveUser(context, username, email, password)
                                UserPreferences.saveLoginState(context, true)

                                Log.d("UserData", "Navigating to mainScreen")
                                navController.navigate("mainScreen") {
                                    popUpTo(0)
                                }
                            } else {
                                Log.d("UserData", "User data is missing some fields.")
                            }


                            UserPreferences.saveUser(context, username ,email ,password)
                            UserPreferences.saveLoginState(context, true)

                            navController.navigate("mainScreen"){
                                popUpTo(0)
                            }
                        }
                        else{
                            Log.d("UserData", "User not found.")
                        }
                    }
                }
                else {
                    Log.d("UserData", "No users found with the email: $email")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                setLoading(false)
                Log.e("error: ", "Failed to read user data", error.toException())
            }
        })
    }

    fun login(context: Context, auth: FirebaseAuth, email: String, password: String,setLoading: (Boolean) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            setLoading(false)
            Toast.makeText(context, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        else{
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        setLoading(false)
                        readUserData(context,email,setLoading)
                    } else {
                        setLoading(false)
                        Toast.makeText(context, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00a57a)),
    ){
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp, start = 0.dp, end = 0.dp)
            .verticalScroll(rememberScrollState())
            .background(
                Color(0xFFf8f8f8),
                shape = RoundedCornerShape(
                    topStart = 40.dp,
                    topEnd = 40.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp
                )
            )
            .padding(20.dp)
        ) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
                    horizontalArrangement = Arrangement.Center) {

                Text(
                    text = "Log in",
                    color = Color(0xFF00a57a),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )

                Text(
                    text = " to your account",
                    color = Color.DarkGray,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )

            }

            Column(
                modifier = Modifier.padding(top = 30.dp)
            ) {
                Text(
                    text = "Enter your email address",
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        focusedBorderColor = Color(0xFF00a57a),
                        unfocusedBorderColor = Color.LightGray
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                Text(
                    text = "Enter your password",
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 15.dp)
                )

                OutlinedTextField(
                    value = pass,
                    onValueChange = { pass = it },
                    placeholder = { Text("Password") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        focusedBorderColor = Color(0xFF00a57a),
                        unfocusedBorderColor = Color.LightGray
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp)
                )

                Button(
                    onClick = {
                          setLoading(true)
                          login(context, auth, email, pass,setLoading)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00a57a)
                    )
                ) {
                    Text(
                        text = "Login",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                }

                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                    horizontalArrangement = Arrangement.Center) {

                    Text(
                        text = "Already have an account?",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,

                    )
                    Text(
                        text = " Sign up",
                        color = Color(0xFF00a57a),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        modifier = Modifier.clickable{
                            navController.navigate("register")
                        }
                    )

                }
            }
        }

        loginError?.let {
            LaunchedEffect(it) {
                snackbarHostState.showSnackbar(it)
                loginError = null
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )

        if (isLoading) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                elevation = CardDefaults.elevatedCardElevation(5.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(100.dp)
                ) {
                    CircularProgressIndicator()
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, context: Context) {
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showProgressBar by remember { mutableStateOf(false) }
    var registrationError by remember { mutableStateOf<String?>(null) }

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00a57a)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp, start = 0.dp, end = 0.dp)
                .verticalScroll(rememberScrollState())
                .background(
                    Color(0xFFf8f8f8),
                    shape = RoundedCornerShape(
                        topStart = 40.dp,
                        topEnd = 40.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Create",
                        color = Color(0xFF00a57a),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = " your new account",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }

                Column(modifier = Modifier.padding(top = 30.dp)) {
                    Text(
                        text = "Username",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                    )
                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        placeholder = { Text("Username") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        textStyle = TextStyle.Default.copy(color = Color.Black),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.White,
                            focusedBorderColor = Color(0xFF00a57a),
                            unfocusedBorderColor = Color.LightGray
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Text(
                        text = "Email address",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 15.dp)
                    )
                    OutlinedTextField(
                        value = userEmail,
                        onValueChange = { userEmail = it },
                        placeholder = { Text("Email") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.White,
                            focusedBorderColor = Color(0xFF00a57a),
                            unfocusedBorderColor = Color.LightGray
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Text(
                        text = "Password",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 15.dp)
                    )
                    OutlinedTextField(
                        value = userPassword,
                        onValueChange = { userPassword = it },
                        placeholder = { Text("Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.White,
                            focusedBorderColor = Color(0xFF00a57a),
                            unfocusedBorderColor = Color.LightGray
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Text(
                        text = "Confirm Password",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 15.dp)
                    )
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        placeholder = { Text("Confirm Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.White,
                            focusedBorderColor = Color(0xFF00a57a),
                            unfocusedBorderColor = Color.LightGray
                        ),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    if (registrationError != null) {
                        Snackbar(
                            action = {
                                Button(onClick = { registrationError = null }) {
                                    Text("Dismiss")
                                }
                            },
                            modifier = Modifier.padding(top = 10.dp)
                        ) {
                            Text(text = registrationError!!)
                        }
                    }

                    Button(
                        onClick = {
                            if (userName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty() || confirmPassword.isEmpty()) {
                                registrationError = "All fields are required"
                            } else if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                                registrationError = "Invalid email format"
                            } else if (userPassword != confirmPassword) {
                                registrationError = "Passwords do not match"
                            } else {
                                registrationError = null
                                showProgressBar = true
                                auth.createUserWithEmailAndPassword(userEmail, userPassword)
                                    .addOnCompleteListener { task ->
                                        showProgressBar = false
                                        if (task.isSuccessful) {

                                            val firebaseUser = auth.currentUser
                                            val uid = firebaseUser?.uid

                                            if (uid != null) {
                                                val databaseReference = FirebaseDatabase.getInstance().reference.child("users").child(uid)
                                                val userProfile = UserModel(
                                                    username = userName,
                                                    email = userEmail,
                                                    password = userPassword
                                                )
                                                databaseReference.setValue(userProfile)
                                                    .addOnCompleteListener { dbTask ->
                                                        if (dbTask.isSuccessful) {
                                                            navController.navigate("login") {
                                                                popUpTo(0)
                                                            }
                                                        } else {
                                                            registrationError = "Failed to save user profile"
                                                        }
                                                    }
                                            }
                                        } else {
                                            registrationError = task.exception?.localizedMessage
                                        }
                                    }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00a57a)
                        )
                    ) {
                        if (showProgressBar) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = "Register",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun MainScreenActivity(){
    val navController = rememberNavController()
    val selected = remember { mutableStateOf(Icons.Default.Home) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Venkata Ramana Medi Care" , fontSize = 20.sp) },
                modifier = Modifier.fillMaxWidth(),
                actions = {
                    IconButton(onClick = {
                        navController.navigate("Info") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }) {
                        Icon(Icons.Default.Info, contentDescription = "info")
                    }
                }
            )
        },

        bottomBar = {
            BottomAppBar(containerColor = Color(0xFF00a57a), modifier = Modifier.height(55.dp)) {
                IconButton(
                    onClick = {
                        selected.value = Icons.Default.Home
                        navController.navigate(FScreens.Home.screens) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = if (selected.value == Icons.Default.Home) Color.White else Color(
                            0xFFACABAB
                        ),
                    )
                }

                IconButton(
                    onClick = {
                        selected.value = Icons.Default.DateRange
                        navController.navigate(FScreens.Bookings.screens) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = if (selected.value == Icons.Default.DateRange) Color.White else Color(
                            0xFFACABAB
                        ),
                    )
                }
                IconButton(
                    onClick = {
                        selected.value = Icons.Default.Person
                        navController.navigate(FScreens.Profile.screens) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = if (selected.value == Icons.Default.Person) Color.White else Color(
                            0xFFACABAB
                        ),
                    )
                }

            }
        },
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = FScreens.Home.screens,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(FScreens.Home.screens) { MyApp() }
            composable(FScreens.Bookings.screens) { MyBookings() }
            composable(FScreens.Profile.screens) { ProfilePage(navController) }
            composable("Info"){ CovidInfoSection() }
        }
    }
}

@Composable
fun CovidInfoSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFF1F1F1), RoundedCornerShape(8.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Who Can Get The COVID-19 Vaccine?",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00796B)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "The NHS is now offering the COVID-19 vaccine in Marton to almost everyone. The Covid Vaccine is an innovative vaccine that has been developed to protect against coronavirus (COVID-19) infection. It’s being offered in GP surgeries, pharmacies, and larger vaccination centres across England.",
            style = TextStyle(
                fontSize = 16.sp,
                color = Color.Gray
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "The COVID-19 Vaccine is available for:",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF004D40)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val bulletPoints = listOf(
            "Everyone aged 5 and over can get a 1st and 2nd dose of the COVID-19 vaccine.",
            "People aged 16 and over, and some children aged 12 to 15, can also get a booster dose.",
            "People aged 12 and over who had a severely weakened immune system when they had their first 2 doses, will be offered a 3rd dose and a booster (4th dose).",
            "People aged 75 and over, people who live in care homes for older people, and people aged 12 and over who have a weakened immune system, will be offered a spring booster."
        )

        bulletPoints.forEach { point ->
            Row(modifier = Modifier.padding(bottom = 4.dp)) {
                Text(
                    text = "• ",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color(0xFF00796B) // Dark teal color
                    )
                )
                Text(
                    text = point,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "COVID-19 Symptoms:",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF004D40) // Teal color
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val symptoms = listOf(
            "Dry cough.",
            "Shortness of breath.",
            "Loss of taste or smell.",
            "Extreme tiredness, called fatigue.",
            "Digestive symptoms such as upset stomach, vomiting or loose stools.",
            "Pain, such as headaches and body or muscle aches.",
            "Fever or chills."
        )

        symptoms.forEach { symptom ->
            Row(modifier = Modifier.padding(bottom = 4.dp)) {
                Text(
                    text = "• ",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color(0xFF00796B)
                    )
                )
                Text(
                    text = symptom,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                )
            }
        }
    }
}

