package com.kidozh.photi.activity

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.wear.ambient.AmbientModeSupport

open class BaseAmbientActivity: AppCompatActivity(), AmbientModeSupport.AmbientCallbackProvider {

    public lateinit var ambientController : AmbientModeSupport.AmbientController


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        ambientController = AmbientModeSupport.attach(this)
    }

    override fun getAmbientCallback(): AmbientModeSupport.AmbientCallback {
        return BaseAmbientCallback()
    }

    public class BaseAmbientCallback : AmbientModeSupport.AmbientCallback(){

    }
}