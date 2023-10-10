package com.networkmonitor

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.modules.core.DeviceEventManagerModule
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import com.facebook.react.bridge.*

class NetworkMonitorModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return NAME
  }

  private val numberOfListeners = 0
  private val connectivityManager = getSystemService(reactContext, ConnectivityManager::class.java)

  private fun sendEvent(reactContext: ReactApplicationContext = reactApplicationContext, eventName: String, params: WritableMap?) {
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(eventName, params)
  }

  @RequiresApi(Build.VERSION_CODES.M)
  @ReactMethod
  fun isNetworkReachable(promise: Promise) {
    promise.resolve(connectivityManager?.activeNetwork != null)
  }

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  fun multiply(a: Double, b: Double, promise: Promise) {
    promise.resolve(a * b)
  }

  @RequiresApi(Build.VERSION_CODES.N)
  @ReactMethod
  fun startMonitoring() {
    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
      return
    }
    connectivityManager?.registerDefaultNetworkCallback(object: ConnectivityManager.NetworkCallback() {
      override fun onAvailable(network : Network) {
        Log.d("NET", "The default network is now: $network")
        sendEvent(eventName = "onChange", params = Arguments.createMap().apply {
          putString("status", "connected")
        })
      }

      override fun onLost(network : Network) {
        Log.d("NET", "The application no longer has a default network. The last default network was $network")
        sendEvent(eventName = "onChange", params = Arguments.createMap().apply {
          putString("status", "disconnected")
        })
      }
    })
  }

  @ReactMethod
  fun stopMonitoring() {
    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
      return
    }
    try {
      connectivityManager?.unregisterNetworkCallback(ConnectivityManager.NetworkCallback())
    } catch (exception: IllegalArgumentException) {
      Log.d("already de-registered", exception.toString())
    }

  }

  @ReactMethod
  fun addListener(type: String?) {
    // Keep: Required for RN built in Event Emitter Calls.
  }

  @ReactMethod
  fun removeListeners(count: Int?) {
    // Keep: Required for RN built in Event Emitter Calls.
  }

  companion object {
    const val NAME = "NetworkMonitor"
  }
}
