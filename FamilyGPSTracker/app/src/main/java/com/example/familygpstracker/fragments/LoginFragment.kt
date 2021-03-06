package com.example.familygpstracker.fragments

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.familygpstracker.R
import com.example.familygpstracker.apis.RetrofitHelper
import com.example.familygpstracker.databinding.FragmentLoginBinding
import com.example.familygpstracker.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


class LoginFragment : Fragment(R.layout.activity_login) {
    private var auth=  FirebaseAuth.getInstance()
    private var isEmailValid = false
    private var isPasswordValid = false
    private var isFormValid = false
    private lateinit var binding : FragmentLoginBinding
    private val directionToDecideUserFragment=LoginFragmentDirections.actionLoginFragmentToDecideUserFragment()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeStatusBarColor()
        registerListeners()
    }

    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= 21) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requireActivity().window.statusBarColor= resources.getColor(R.color.white,null)
            }
        }
    }

    private fun registerListeners() {

        binding.email.setOnFocusChangeListener{view,focused ->
            if(!focused){
                binding.emailContainer.helperText = validateEmail()
            }
        }
        binding.password.setOnFocusChangeListener{view,focused ->
            if(!focused){
                binding.passwordContainer.helperText= validatePassword()
            }
        }
        binding.signUpText.setOnClickListener{view ->

            findNavController().navigate(directionToDecideUserFragment)
        }
        binding.loginBtn.setOnClickListener { view ->
            binding.progressBar.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                reValidate()
                if(isFormValid) verifyCredentials()
                else binding.progressBar.visibility = View.INVISIBLE
               // submitForm()
            }, 1500)

        }
    }
    private fun submitForm() {
        val email = binding.emailContainer.helperText == null
        val password = binding.passwordContainer.helperText == null
        val direction=SignUpFragmentDirections.actionSignUpFragmentToDecideUserFragment()
        if(!email){
            binding.progressBar.visibility = View.INVISIBLE
            binding.email.requestFocus()
            return
        }
        if(!password){
            binding.progressBar.visibility = View.INVISIBLE
            binding.password.requestFocus()
            return
        }

        else if (!binding.email.text.toString().isEmpty() && !binding.password.text.toString().isEmpty() ){
            auth.signInWithEmailAndPassword(binding.email.text.toString(),binding.password.text.toString()).addOnCompleteListener{
                if(it.isSuccessful){
                    findNavController().navigate(directionToDecideUserFragment)
                    binding.progressBar.visibility = View.INVISIBLE

                }
                else{
                    binding.progressBar.visibility = View.INVISIBLE
                    Toast.makeText(requireActivity(), it.exception?.message,Toast.LENGTH_SHORT).show()
                }
            }
            return

        }

        binding.progressBar.visibility = View.INVISIBLE
        binding.emailContainer.helperText="required"
        binding.passwordContainer.helperText="required"
    }

    private fun verifyCredentials(){
        var retrofit = RetrofitHelper.getInstance()
        var userService = retrofit.create(User::class.java)
        GlobalScope.launch {
            
        }
    }
    private fun reValidate2(){
        isEmailValid = validateEmail() == null
        isPasswordValid = validatePassword() == null

        if(!isEmailValid) binding.emailContainer.helperText=validateEmail()
        else isFormValid = false

        if(!isPasswordValid) binding.passwordContainer.helperText=validatePassword()
        else isFormValid = false

        if(isEmailValid && isPasswordValid ) isFormValid = true
    }
    private fun reValidate() {
        var email=binding.email.text.toString()
        var password = binding.password.text.toString()
        if((binding.email.isFocused && !email.isEmpty()) || (email.isEmpty())){
            binding.emailContainer.helperText=validateEmail()
        }
        if((binding.password.isFocused && !password.isEmpty()) || (password.isEmpty())){
            binding.passwordContainer.helperText=validatePassword()
        }
    }
    private fun validateEmail(): String? {
        var email=binding.email.text.toString()
        if(!email.isEmpty()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return "invalid email address"
            }
        }else {
            return "required"
        }
        return null
    }
    private fun validatePassword(): String? {
        var password = binding.password.text.toString()
        if(!password.isEmpty()) {
            if (password.length < 8) {
                return "Minimum 8 Character Password"
            }
            if (!password.matches(".*[A-Z].*".toRegex())) {
                return "Must contain 1 Upper-case Character"
            }
            if (!password.matches(".*[a-z].*".toRegex())) {
                return "Must contain 1 Upper-case Character"
            }
            if (!password.matches(".*[@#\$%^&+=].*".toRegex())) {
                return "Must contain 1 Special Character"
            }
        }else {
            return "required"
        }
        return null;
    }
}