package com.example.userblinkitclone

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData

class NetworkManager(context: Context) : LiveData<Boolean>() {
    override fun onActive() {
        super.onActive()
        checkInternetConnectivity()
    }

    override fun onInactive() {
        super.onInactive()
        releaseCheckingInternetConnectivity()
    }

    private var networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(true)
        }

        override fun onUnavailable() {
            super.onUnavailable()
            postValue(false)
        }


        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(false)
        }

    }
    private var networkRequest = NetworkRequest.Builder().apply {
        addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
    }.build()
    private var connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private fun checkInternetConnectivity() {
        if (connectivityManager.activeNetwork == null) postValue(false)
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun releaseCheckingInternetConnectivity() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}