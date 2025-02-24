package com.dudoji.android.network

import com.dudoji.android.R
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class NoNetworkActivity : AppCompatActivity(){

    override fun onCreate(saveInstanceState: Bundle?){
        super.onCreate(saveInstanceState)
        setContentView(R.layout.activity_network)
    }
}