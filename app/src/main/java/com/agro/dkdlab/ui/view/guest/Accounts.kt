package com.agro.dkdlab.ui.view.guest

object Accounts {
    fun get(mobileNumber: String): Map<String, String> {
       return when(mobileNumber){
           "9877752167" -> hashMapOf<String, String>().apply {
               put("mobileNumber", "9877752167")
               put("name", "Inderjit Kaur")
               put("district", "Gurdaspur")
               put("block", "Derababa Nanak")
               put("village", "Tarpalla")
               put("villageCode", "PB_00001")
               put("pinCode", "143602")
           }

           "9779491850" -> hashMapOf<String, String>().apply {
               put("mobileNumber", "9779491850")
               put("name", "Ranjit Kaur")
               put("district", "Gurdaspur")
               put("block", "Derababa Nanak")
               put("village", "Loharanwali")
               put("villageCode", "PB_00002")
               put("pinCode", "143602")
           }

           "9592589758" -> hashMapOf<String, String>().apply {
               put("mobileNumber", "9592589758")
               put("name", "Jasvir kaur")
               put("district", "Gurdaspur")
               put("block", "Fatehgarh Churian")
               put("village", "Rupowali")
               put("villageCode", "PB_00003")
               put("pinCode", "143513")
           }

           "6239065091" -> hashMapOf<String, String>().apply {
               put("mobileNumber", "6239065091")
               put("name", "Harjinder Kaur")
               put("district", "Gurdaspur")
               put("block", "Fatehgarh Churian")
               put("village", "Nanak Chak")
               put("villageCode", "PB_00004")
               put("pinCode", "143602")
           }

           "7347493216" -> hashMapOf<String, String>().apply {
               put("mobileNumber", "7347493216")
               put("name", "Harpinder Kaur")
               put("district", "Gurdaspur")
               put("block", "Fatehgarh Churian")
               put("village", "Jaangla")
               put("villageCode", "PB_00004")
               put("pinCode", "143602")
           }

           "8288801730" -> hashMapOf<String, String>().apply {
               put("mobileNumber", "8288801730")
               put("name", "Nirmaljit Kaur")
               put("district", "Gurdaspur")
               put("block", "Dhariwal")
               put("village", "Bhojraj")
               put("villageCode", "PB_00005")
               put("pinCode", "143519")
           }

           "7814609714" -> hashMapOf<String, String>().apply {
               put("mobileNumber", "7814609714")
               put("name", "Prem Lata")
               put("district", "Pathankot")
               put("block", "Dharkalan")
               put("village", "Naryanpur")
               put("villageCode", "PB_00006")
               put("pinCode", "145001")
           }

           "7837612695" -> hashMapOf<String, String>().apply {
               put("mobileNumber", "7837612695")
               put("name", "Reeta Devi")
               put("district", "Pathankot")
               put("block", "Dharkalan")
               put("village", "Chibber")
               put("villageCode", "PB_00007")
               put("pinCode", "145001")
           }

           "9876029145" -> hashMapOf<String, String>().apply {
               put("mobileNumber", "9876029145")
               put("name", "Harjinder Kaur")
               put("district", "Tarantaran")
               put("block", "Valtoha")
               put("village", "Mastgarh")
               put("villageCode", "PB_00008")
               put("pinCode", "143419")
           }

           "8427335531" -> hashMapOf<String, String>().apply {
               put("mobileNumber", "8427335531")
               put("name", "Manjit Kaur")
               put("district", "Tarantaran")
               put("block", "Valtoha")
               put("village", "Madar Mathra Bhagi")
               put("villageCode", "PB_00009")
               put("pinCode", "143305")
           }

           "7528970380" -> hashMapOf<String, String>().apply {
               put("mobileNumber", "7528970380")
               put("name", "Ranjit Kaur")
               put("district", "Tarantaran")
               put("block", "Valtoha")
               put("village", "Kalia")
               put("villageCode", "PB_00010")
               put("pinCode", "143419")
           }

           "7696978048" -> hashMapOf<String, String>().apply {
               put("mobileNumber", "7696978048")
               put("name", "Simran Rani")
               put("district", "Fazilka")
               put("block", "Jalalabad")
               put("village", "Larian")
               put("villageCode", "PB_00011")
               put("pinCode", "152033")
           }

           "7626873158" -> hashMapOf<String, String>().apply {
               put("mobileNumber", "7626873158")
               put("name", "Chanderkala")
               put("district", "Fazilka")
               put("block", "Khuia Sarvar")
               put("village", "Bazidpur Kattianwali")
               put("villageCode", "PB_00012")
               put("pinCode", "152122")
           }
           "9012300082" -> hashMapOf<String, String>().apply {
               put("mobileNumber", "9012300082")
               put("name", "Shivam")
               put("district", "Haridwar")
               put("block", "Roorkee")
               put("village", "Phase 1")
               put("villageCode", "UK000160")
               put("pinCode", "249405")
           }
           "8218656616" -> hashMapOf<String, String>().apply {
               put("mobileNumber", "8218656616")
               put("name", "Sachin")
               put("district", "Haridwar")
               put("block", "Roorkee")
               put("village", "Phase 1")
               put("villageCode", "UK000160")
               put("pinCode", "249405")
           }
           "7088453839" -> hashMapOf<String, String>().apply {
               put("mobileNumber", "7088453839")
               put("name", "Teena saini")
               put("district", "Haridwar")
               put("block", "Roorkee")
               put("village", "Phase 1")
               put("villageCode", "UK000160")
               put("pinCode", "249405")
           }
           "8847267051" -> hashMapOf<String, String>().apply {
               put("mobileNumber", "8847267051")
               put("name", "Ashok")
               put("district", "Fazilka")
               put("block", "Jalalabad")
               put("village", "Bazidpur Kattianwali")
               put("villageCode", "PB_00012")
               put("pinCode", "152122")
           }
           else -> emptyMap()
       }
    }
}