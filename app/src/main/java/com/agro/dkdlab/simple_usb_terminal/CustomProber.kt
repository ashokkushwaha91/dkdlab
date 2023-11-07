package com.agro.dkdlab.simple_usb_terminal

import com.hoho.android.usbserial.driver.UsbSerialProber
import com.hoho.android.usbserial.driver.ProbeTable
import com.hoho.android.usbserial.driver.CdcAcmSerialDriver

/**
 * add devices here, that are not known to DefaultProber
 *
 * if the App should auto start for these devices, also
 * add IDs to app/src/main/res/xml/usb_device_filter.xml
 */
internal object CustomProber {
    // e.g. Digispark CDC
    @JvmStatic
    val customProber: UsbSerialProber
        get() {
            val customTable = ProbeTable()
            customTable.addProduct(
                0x16d0,
                0x087e,
                CdcAcmSerialDriver::class.java
            ) // e.g. Digispark CDC
            return UsbSerialProber(customTable)
        }
}