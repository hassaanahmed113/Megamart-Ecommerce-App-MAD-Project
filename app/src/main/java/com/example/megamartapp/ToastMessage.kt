package com.example.megamartapp

import android.app.Activity
import android.widget.Toast

object ToastMessage {

    fun Activity.toast(msg:String){
        Toast.makeText(this , msg , Toast.LENGTH_SHORT).show()
    }

}