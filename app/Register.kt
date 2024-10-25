package com.example.bloodbankapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.registeractivity.R

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Step 1: Link the components using findViewById
        val nameField: EditText = findViewById(R.id.editTextName)
        val emailField: EditText = findViewById(R.id.editTextEmail)
        val passwordField: EditText = findViewById(R.id.editTextPassword)
        val bloodGroupSpinner: Spinner = findViewById(R.id.spinnerBloodGroup)
        val registerButton: Button = findViewById(R.id.buttonRegister)

        // Step 2: Set up the dropdown (Spinner) with blood group options
        val bloodGroups = arrayOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodGroups)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bloodGroupSpinner.adapter = adapter

        // Step 3: Add a click listener for the Register button
        registerButton.setOnClickListener {
            // Get the entered values
            val name = nameField.text.toString()
            val email = emailField.text.toString()
            val password = passwordField.text.toString()
            val bloodGroup = bloodGroupSpinner.selectedItem.toString()

            // Check if all fields are filled
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                // Display a message (Toast) indicating successful registration
                Toast.makeText(this, "Registered successfully as $name with blood group $bloodGroup", Toast.LENGTH_LONG).show()
            } else {
                // Display a message indicating the need to fill all fields
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_LONG).show()
            }
        }
    }
}
