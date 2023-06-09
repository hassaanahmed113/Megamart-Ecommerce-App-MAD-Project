package com.example.megamartapp

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.megamartapp.ToastMessage.toast
import com.example.megamartapp.databinding.FragmentSignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment : Fragment(R.layout.fragment_signup) {

    private lateinit var binding: FragmentSignupBinding
    private lateinit var auth : FirebaseAuth



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignupBinding.bind(view)
        auth = FirebaseAuth.getInstance()


        binding.btnSignUp.setOnClickListener {

            val emailRegex = Regex("^[\\w!#$%&amp;'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&amp;'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");

            if(binding.etEmailSignUp.text.isNotEmpty()  &&  binding.etNameSignUp.text.isNotEmpty() &&
                binding.etPasswordSignUp.text.isNotEmpty() && emailRegex.matches(binding.etEmailSignUp.text)){

                createUser(binding.etEmailSignUp.text.toString(),binding.etPasswordSignUp.text.toString())
                return@setOnClickListener
            }
            else if(binding.etEmailSignUp.text.isEmpty() && binding.etNameSignUp.text.isNotEmpty() &&
                binding.etPasswordSignUp.text.isNotEmpty() ){
                requireActivity().toast("Please enter email address")
                return@setOnClickListener
            }
            else if(binding.etEmailSignUp.text.isNotEmpty() && binding.etNameSignUp.text.isNotEmpty() && emailRegex.matches(binding.etEmailSignUp.text) &&
                binding.etPasswordSignUp.text.isEmpty() ){
                requireActivity().toast("Please enter password")
                return@setOnClickListener
            }
            else if(binding.etEmailSignUp.text.isNotEmpty() && binding.etNameSignUp.text.isEmpty() && emailRegex.matches(binding.etEmailSignUp.text) &&
                binding.etPasswordSignUp.text.isNotEmpty() ){
                requireActivity().toast("Please enter your name")
                return@setOnClickListener
            }
            else if(binding.etEmailSignUp.text.isNotEmpty()  && !emailRegex.matches(binding.etEmailSignUp.text)){
                requireActivity().toast("Please enter valid email address")
                return@setOnClickListener
            }
            else if(  binding.etNameSignUp.text.isNotEmpty() &&
                binding.etEmailSignUp.text.isEmpty()  &&
                binding.etPasswordSignUp.text.isEmpty() || binding.etNameSignUp.text.isEmpty() &&
                binding.etEmailSignUp.text.isNotEmpty()  &&
                binding.etPasswordSignUp.text.isEmpty() || binding.etNameSignUp.text.isEmpty() &&
                binding.etEmailSignUp.text.isEmpty()  &&
                binding.etPasswordSignUp.text.isNotEmpty()){
                requireActivity().toast("Some Fields are Empty")
                return@setOnClickListener
            }
            else{
                requireActivity().toast("All Fields are Empty")
                return@setOnClickListener
            }

        }
        binding.tvNavigateToSignIn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_signUpFragment_to_signInFragmentFragment)
        }


    }

    private fun createUser(email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {task ->
                if(task.isSuccessful){
                    requireActivity().toast("New User created")

                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_signUpFragment_to_mainFragment)

                }
                else{
                    requireActivity().toast(task.exception!!.localizedMessage)
                }

            }

    }


}