package com.example.megamartapp

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.findNavController


class SplashScreenFragment : Fragment(R.layout.fragment_splash_screen) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        Handler().postDelayed({
            val action = SplashScreenFragmentDirections.actionSplashScreenFragmentToSignInFragment()
            view?.findNavController()?.navigate(action)
        }, 3000)

    }


}