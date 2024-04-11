package com.example.permission_requester.permissionrequests

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi


sealed class PermissionRequest(open val requestCode: Int, open val  permissionParams: PermissionParams,open val permissionString: String) {
    data class CoarseLocationPermission(
      override val requestCode: Int = 1001,
       override val permissionParams: PermissionParams = PermissionParams.NonMandatoryNonRetryPermission
    ) : PermissionRequest(requestCode, permissionParams, Manifest.permission.ACCESS_COARSE_LOCATION)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    data class NotificationPermission(
       override val requestCode: Int = 1002,
       override val permissionParams: PermissionParams = PermissionParams.NonMandatoryNonRetryPermission
    ) : PermissionRequest(requestCode, permissionParams, Manifest.permission.POST_NOTIFICATIONS)
    data class StoragePermission(
       override val requestCode: Int = 1003,
       override val permissionParams: PermissionParams = PermissionParams.NonMandatoryNonRetryPermission
    ) : PermissionRequest(requestCode, permissionParams, Manifest.permission.READ_EXTERNAL_STORAGE)
}


sealed class PermissionParams(
    open val noOfRetries: Int?,
    open val permissionRationale: String?,
    open val appDeprecationMessage: String?
) {
    object NonMandatoryNonRetryPermission : PermissionParams(null, null, null)
    data class RetryPermission(
        val numberOfRetries: Int,
        override val permissionRationale: String,
        override val appDeprecationMessage: String?
    ) : PermissionParams(numberOfRetries, permissionRationale, appDeprecationMessage)

    data class MandatoryBlockedPermission(
        val numberOfRetries: Int,
        override val permissionRationale: String,
        val blockerMessage: String?
    ) : PermissionParams(numberOfRetries, permissionRationale, blockerMessage)

}