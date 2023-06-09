package com.example.megamartapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.megamartapp.ToastMessage.toast
import com.example.megamartapp.databinding.FragmentSigninBinding
import com.google.firebase.auth.FirebaseAuth

class SignInFragment : Fragment(R.layout.fragment_signin) {

    private lateinit var binding: FragmentSigninBinding
    private lateinit var auth: FirebaseAuth


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSigninBinding.bind(view)
        auth = FirebaseAuth.getInstance()



        if (auth.currentUser != null) {
            Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                .navigate(R.id.action_signInFragment_to_mainFragment)
        }

        binding.btnSignIn.setOnClickListener {


            val emailRegex = Regex("^[\\w!#$%&amp;'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&amp;'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");

            if(binding.etEmailSignIn.text.isNotEmpty()  &&
                binding.etPasswordSignIn.text.isNotEmpty() && emailRegex.matches(binding.etEmailSignIn.text)){
                signinUser(binding.etEmailSignIn.text.toString(),
                    binding.etPasswordSignIn.text.toString())
                return@setOnClickListener
            }
            else if(binding.etEmailSignIn.text.isEmpty() &&
                binding.etPasswordSignIn.text.isNotEmpty() ){
                requireActivity().toast("Please enter email address")
                return@setOnClickListener
            }
            else if(binding.etEmailSignIn.text.isNotEmpty() && emailRegex.matches(binding.etEmailSignIn.text) &&
                binding.etPasswordSignIn.text.isEmpty() ){
                requireActivity().toast("Please enter password")
                return@setOnClickListener
            }
            else if(binding.etEmailSignIn.text.isNotEmpty()  &&
                binding.etPasswordSignIn.text.isEmpty() || binding.etPasswordSignIn.text.isNotEmpty() && !emailRegex.matches(binding.etEmailSignIn.text)){
                requireActivity().toast("Please enter valid email address")
                return@setOnClickListener
            }
            else{
                requireActivity().toast("All Fields are Empty")
                return@setOnClickListener
            }

        }

        binding.tvNavigateToSignUp.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                .navigate(R.id.action_signInFragment_to_signUpFragment)
        }


    }

    private fun signinUser(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    requireActivity().toast("Sign In Successful")

                    Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                        .navigate(R.id.action_signInFragment_to_mainFragment)
                } else {
                    requireActivity().toast("No User Found")

                }


            }

    }


}