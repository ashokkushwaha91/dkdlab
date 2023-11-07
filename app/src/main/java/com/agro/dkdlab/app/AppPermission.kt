package com.agro.dkdlab.app


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.agro.dkdlab.R
import com.agro.dkdlab.custom.Util.showToast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse


object AppPermission {
    const val MY_PERMISSIONS_REQUEST = 123
    fun Activity.checkCameraPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                val permission =arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, 121)
            } else {
                return true
            }
        } else {
            return true
        }
        return false
    }
    fun Activity.checkStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                val permission =arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, 121)
            } else {
                return true
            }
        } else {
            return true
        }
        return false
    }

    fun Activity.isLocationEnabled(): Boolean {
        var isGpsEnabled = false
        var isNetworkEnabled = false
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {

        }

        return isGpsEnabled && isNetworkEnabled
    }

    fun Activity.enableLocationSettings(code: Int) {
        val locationRequest: LocationRequest = LocationRequest.create()
            .setInterval(1000)
            .setFastestInterval(1000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        LocationServices
            .getSettingsClient(this)
            .checkLocationSettings(builder.build())
            .addOnSuccessListener(this) { response: LocationSettingsResponse? -> }
            .addOnFailureListener(this) { ex ->
                if (ex is ResolvableApiException) {
                    try {
                        ex.startResolutionForResult(this,code)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                        showToast("Location setting exception: ${sendEx.message}")
                    }
                }
            }
    }

    fun Activity.hasLocationPermission(): Boolean {
        val array = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in array) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    fun Activity.askLocationWithCameraPermission(requestCode: Int = 11, neverAsk : Boolean = false) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.e("dfdfd", "askLocationPermission: "+neverAsk)

            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), requestCode
            )


            //
        } else {

            Log.e("dfdfd", "askLocationPermission:else ")
            if (neverAsk) openAppSettings(this)
            else{
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), requestCode
                )
            }

        }
    }
    fun openAppSettings(activity: Activity) {
        val packageUri: Uri = Uri.fromParts(
            "package",
            activity.packageName,
            null
        )
        val applicationDetailsSettingsIntent = Intent()
        applicationDetailsSettingsIntent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        applicationDetailsSettingsIntent.data = packageUri
        applicationDetailsSettingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity
            .startActivity(applicationDetailsSettingsIntent)
    }

    fun Activity.hasLocationPermissions(): Boolean {
        val array = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in array) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    fun Activity.askLocationPermission(requestCode: Int = 11, neverAsk : Boolean = false) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Log.e("dfdfd", "askLocationPermission: "+neverAsk)

            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), requestCode
            )


            //
        } else {

            Log.e("dfdfd", "askLocationPermission:else ")
            if (neverAsk) openAppSettings(this)
            else{
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), requestCode
                )
            }

        }
    }


    fun showDialog(
        msg: String, context: Context?,
        permission: Array<String>
    ) {
        val alertBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
        alertBuilder.setCancelable(true)
        alertBuilder.setTitle("Permission necessary")
        alertBuilder.setMessage("$msg permission is necessary")
        alertBuilder.setPositiveButton(
            R.string.yes,
            DialogInterface.OnClickListener { dialog, which ->
                ActivityCompat.requestPermissions(
                    (context as Activity?)!!, permission,
                    MY_PERMISSIONS_REQUEST
                )
            })
        val alert: AlertDialog = alertBuilder.create()
        alert.show()
    }
}