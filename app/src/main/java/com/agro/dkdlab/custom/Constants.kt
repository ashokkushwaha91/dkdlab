package com.agro.dkdlab.custom

import com.agro.dkdlab.BuildConfig

interface Constants {
    companion object {
        const val INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB"
        const val INTENT_ACTION_DISCONNECT = BuildConfig.APPLICATION_ID + ".Disconnect"
        const val NOTIFICATION_CHANNEL = BuildConfig.APPLICATION_ID + ".Channel"
        const val INTENT_CLASS_MAIN_ACTIVITY = BuildConfig.APPLICATION_ID + ".MainActivity"

        // values have to be unique within each app
        const val NOTIFY_MANAGER_START_FOREGROUND_SERVICE = 1001
    }
}