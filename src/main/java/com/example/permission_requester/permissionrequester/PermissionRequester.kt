package com.example.permission_requester.permissionrequester

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.example.permission_requester.permissionrequests.PermissionParams
import com.example.permission_requester.permissionrequests.PermissionRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class PermissionRequester(
    private val activity: ComponentActivity,
    private val permissionRequests: List<PermissionRequest>,
    private val resultCallback: PermissionRequestResult? = null
) {
    constructor(
        activity: ComponentActivity,
        permissionRequest: PermissionRequest,
        result: PermissionRequestResult? = null
    ) : this(
        activity, listOf(permissionRequest), result
    )

    private val permissionResultStream: MutableStateFlow<Pair<Int, Boolean>?> =
        MutableStateFlow(null)

    private val permissionResult: MutableStateFlow<Boolean?> = MutableStateFlow(null)


    private val permissionRequester =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            permissionResult.tryEmit(it)
        }

    init {
        CoroutineScope(Dispatchers.IO).launch {
            permissionResultStream.filterNotNull().collect {
                resultCallback?.permissionRequestedResult(
                    it.first,
                    if (it.second) PermissionResult.GRANTED else PermissionResult.NOT_GRANTED
                )

            }
        }
    }

    fun requestPermission() {
        CoroutineScope(Dispatchers.IO).launch {
            permissionRequests.forEach {
                requestPermissionActual(it.permissionString, it.requestCode, it.permissionParams)
            }
        }
    }

    suspend fun requestPermissionSuspended() {

    }

    private suspend fun requestPermissionActual(
        permissionString: String,
        requestCode: Int,
        permissionParams: PermissionParams
    ) {
        permissionRequester.launch(permissionString)
        permissionResult.filterNotNull().collect{
            when(permissionParams){
                is PermissionParams.MandatoryBlockedPermission -> Log.d("SID_DEV","nono")
                PermissionParams.NonMandatoryNonRetryPermission -> permissionResultStream.emit(Pair(requestCode, it))
                is PermissionParams.RetryPermission -> Log.d("SID_DEV","nono")
            }
        }
    }
}

interface PermissionRequestResult {
    fun permissionRequestedResult(requestCode: Int, permissionResult: PermissionResult)
}

enum class PermissionResult {
    GRANTED, NOT_GRANTED
}