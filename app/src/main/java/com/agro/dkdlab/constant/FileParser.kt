package com.agro.dkdlab.constant

import com.agro.dkdlab.network.model.BlockModel


val userRole = listOf(
    /*"Crop Survey",
    "GIS",*/
    "Lab",
    "Soil Collector",
    "Sub Admin",
    "Surveyor",
    "Guest"
)
val getFilterList = listOf(
    /* "All",*/
    "Select Role",
    "Crop Survey",
    "GIS",
    "Lab",
    "Soil Collector",
    "Sub Admin",
    "Surveyor"
)

val cropType = listOf(
    "Single Crop",
    "Mixed Crop",
    "Intercropping",
    "No Crop"
)
fun getBlock():List<BlockModel>{
    var blockList = mutableListOf<BlockModel>()
    blockList.add(BlockModel("UK0001","Bahadrabad"))
    blockList.add(BlockModel("UK0004","Bhagwanpur"))
    blockList.add(BlockModel("UK0006","Khanpur"))
    blockList.add(BlockModel("UK0005","Laksar"))
    blockList.add(BlockModel("UK0003","Narsan"))
    blockList.add(BlockModel("UK0002","Roorkee"))
    return blockList
}

val listIrrigation = hashMapOf<String, List<String>>().apply {
    put("Hindi", listOf(/*"Select irrigation type",*/"Rain fed","Tube well","Canal","Pond","None"))
    put("English", listOf(/*"Select irrigation type",*/"Rain fed","Tube well","Canal","Pond","None"))
}
//val listIrrigation = listOf("Select irrigation type","Rain Sad","Tube Well","Canal","Pond")

val categoryList = hashMapOf<String, List<String>>().apply {
    put("Hindi", listOf("वर्ग का चयन करें","सामान्य","अन्य पिछड़ा वर्ग","अनुसूचित जाति","अनुसूचित जनजाति"))
    put("English", listOf("Select category","General","OBC","SC","ST"))
}

/*fun Int.toValue(): String{
    return when(this){
        1 -> "Very Low"
        2 -> "Low"
        3 -> "Medium"
        4 -> "High"
        5 -> "Very High"
        else -> "---"
    }
}

fun String.toRange(): Int {
    return when (this) {
        "Very Low" -> 1
        "Low" -> 2
        "Medium" -> 3
        "High" -> 4
        "Very High" -> 5
        else -> 0
    }
}*/


val getFarmingTypes = listOf(
//    "Select farming type",
    "Organic Farming",
    "Mixed 25% Organic",
    "Mixed 50% Organic",
    "Mixed 75% Organic",
    "Inorganic Farming"
)
fun getFarmingType(farmingType: String?): String {
    return when (farmingType) {
        "0" -> "ORGANIC"
        "1" -> "MIX25"
        "2" -> "MIX50"
        "3" -> "MIX75"
        "4" -> "INORGANIC"
        else -> "INORGANIC"
    }
}

val getNValueHint = listOf(
    "Low, less than 280",
    "Medium, between 280-460",
    "High, more than 460"
)
val getPValueHint = listOf(
    "Low, less than 12.5",
    "Medium, between 12.50-25",
    "High,more than 25"
)
val getKValueHint = listOf(
    "Low, less than 115",
    "Medium, between 115-280",
    "High, more than 280"
)
val getOCValueHint = listOf(
    "Low,less than 0.50 ",
    "Medium, between 0.50-0.75",
    "High, more than 0.75"
)
val getPHValueHint = listOf(
    "Acidic, less than 7",
    "Neutral, equal to 7",
    "Alkaline, more than 7"
)

val getNPKListMap = listOf(
    NPKMOdel("Very Low", "<140","#C00000"),
    NPKMOdel("Low", "140-280","#FF0000"),
    NPKMOdel("Medium", "280-560","#92D050"),
    NPKMOdel("High", "560-700","#FFFF00"),
    NPKMOdel("Very High", ">700","#FFC000")
)

val getPhListMap = listOf(
    NPKMOdel("Strongly Alkaline", ">8.5","#808080"),
    NPKMOdel("Moderately Alkaline", "7.1-8.5","#A9D08E"),
    NPKMOdel("Neutral", "7.0","#92D050"),
    NPKMOdel("Slightly Acidic", "6.6-6.9","#FFFF00"),
    NPKMOdel("Moderately Acidic", "5.6-6.5","#FFC000"),
    NPKMOdel("Highly Acidic", "4.6-5.5","#FF0000"),
    NPKMOdel("Strongly Acidic", "3.5-4.6","#C00000"),
    NPKMOdel("Acid Sulphate", "<3.5","#7030A0")
)

val getNList = listOf(
    NPKMOdel( "Select", "","#ffffff"),
    NPKMOdel("Very Low", "<140","#FF0000"),
    NPKMOdel("Low", "140-280","#FFEF4949"),
    NPKMOdel("Medium", "280-560","#F1C40F"),
    NPKMOdel("High", "560-700","#239B56"),
    NPKMOdel("Very High", ">700","#1E8449")
)
val getPList = listOf(
    NPKMOdel( "Select", "","#ffffff"),
    NPKMOdel("Very Low", "<5","#FF0000"),
    NPKMOdel("Low", "5-10","#FFEF4949"),
    NPKMOdel("Medium", "10-25","#F1C40F"),
    NPKMOdel("High", "25-40","#239B56"),
    NPKMOdel("Very High", ">40","#1E8449")
)
val getKList = listOf(
    NPKMOdel( "Select", "","#ffffff"),
    NPKMOdel("Very Low", "<60","#FF0000"),
    NPKMOdel("Low", "60-120","#FFEF4949"),
    NPKMOdel("Medium", "120-280","#F1C40F"),
    NPKMOdel("High", "280-560","#239B56"),
    NPKMOdel("Very High", ">560","#1E8449")
)
val getOCList = listOf(
    NPKMOdel( "Select", "","#ffffff"),
    NPKMOdel("Very Low", "<0.25","#FF0000"),
    NPKMOdel("Low", "0.25-0.50","#FFEF4949"),
    NPKMOdel("Medium", "0.50-0.75","#F1C40F"),
    NPKMOdel("High", "0.75-1.0","#239B56"),
    NPKMOdel("Very High", ">1.0","#1E8449")
)
val getPhList = listOf(
    NPKMOdel( "Select", "","#ffffff"),
    NPKMOdel("Strongly Alkaline", ">8.5","#595F94"),
    NPKMOdel("Moderately Alkaline", "7.1-8.5","#637687"),
    NPKMOdel("Neutral", "7.0","#698389"),
    NPKMOdel("Slightly Acidic", "6.6-6.9","#9EA178"),
    NPKMOdel("Moderately Acidic", "5.6-6.5","#B1A94D"),
    NPKMOdel("Highly Acidic", "4.6-5.5","#D6CB5C"),
    NPKMOdel("Strongly Acidic", "3.5-4.6","#D88051"),
    NPKMOdel("Acid Sulphate", "<3.5","#D45365")
)
data class NPKMOdel(
    val name: String,
    val value: String,
    val colorCode: String
)

fun getNPKOCColor(value: String?): String {
    return when (value) {
        "Very Low" -> "#FF0000"
        "Low" -> "#FFEF4949"
        "Medium" -> "#F1C40F"
        "High" -> "#239B56"
        "Very High" -> "#1E8449"

        "Strongly Alkaline" -> "#595F94"
        "Moderately Alkaline" -> "#637687"
        "Neutral" -> "#698389"
        "Slightly Acidic" -> "#9EA178"
        "Moderately Acidic" -> "#B1A94D"
        "Highly Acidic" -> "#D6CB5C"
        "Strongly Acidic" -> "#D88051"
        "Acid Sulphate" -> "#D45365"
        else -> "#D04A4A4A"
    }
}
fun getNPKOCColorMap(value: String?): String {
    return when (value) {
        "Very Low" -> "#C00000"
        "Low" -> "#FF0000"
        "Medium" -> "#92D050"
        "High" -> "#FFFF00"
        "Very High" -> "#FFC000"

        "Strongly Alkaline" -> "#808080"
        "Moderately Alkaline" -> "#A9D08E"
        "Neutral" -> "#92D050"
        "Slightly Acidic" -> "#FFFF00"
        "Moderately Acidic" -> "#FFC000"
        "Highly Acidic" -> "#FF0000"
        "Strongly Acidic" -> "#C00000"
        "Acid Sulphate" -> "#7030A0"
//        else -> "#00FFFFFF"
        else -> "#D04A4A4A"
    }
}

fun getNValue(value: String?): String {
    return when (value) {
        "Very Low" -> "139"
        "Low" -> "210"
        "Medium" -> "420"
        "High" -> "630"
        "Very High" -> "701"
        else -> "#D04A4A4A"
    }
}
fun getPValue(value: String?): String {
    return when (value) {
        "Very Low" -> "4.5"
        "Low" -> "7.5"
        "Medium" -> "17.50"
        "High" -> "32.50"
        "Very High" -> "41"
        else -> "#D04A4A4A"
    }
}
fun getKValue(value: String?): String {
    return when (value) {
        "Very Low" -> "59"
        "Low" -> "90"
        "Medium" -> "200"
        "High" -> "420"
        "Very High" -> "561"
        else -> "#D04A4A4A"
    }
}
fun getOCValue(value: String?): String {
    return when (value) {
        "Very Low" -> "0.20"
        "Low" -> "0.38"
        "Medium" -> "0.63"
        "High" -> "0.88"
        "Very High" -> "1.05"
        else -> "#D04A4A4A"
    }
}
fun getPHValue(value: String?): String {
    return when (value) {
        "Strongly Alkaline" -> "8.75"
        "Moderately Alkaline" -> "7.8"
        "Neutral" -> "7.0"
        "Slightly Acidic" -> "6.75"
        "Moderately Acidic" -> "6.05"
        "Highly Acidic" -> "5.05"
        "Strongly Acidic" -> "4.05"
        "Acid Sulphate" -> "3.25"
        else -> "#D04A4A4A"
    }
}


val cropListHaridwarEng = listOf(
   "Rice",
   "Wheat",
   "Maize (zea Mays L)",
   "Pearl Millet",
   "Sorghum",
   "Sugarcane",
   "Bengal Gram (chickpea)",
   "Lentil (masur)",
   "Black Gram/urd",
   "Green Gram (moong)",
   "Berseem",
   "Groundnut",
   "Sesame",
   "Mustard",
   "Lemon Grass",
   "Menta Mint",
   "Lemon",
   "Cabbage",
   "Cauliflower",
   "Cucumber",
   "Rabi Onion (pyaz)",
   "Garlic",
   "Brinjal",
   "Bitter gourd",
   "Bottle gourd",
   "Pumpkin",
   "Potato",
   "Tomato",
   "Okra",
   "Chilli",
   "Capsicum",
   "Mango",
   "Guava",
   "Marigold",
   "Gladiolus"
)
val cropListHaridwarHindi = listOf(
    "धान",
    "गेंहू",
    "मक्का",
    "बाजरा",
    "ज्वार",
    "गन्ना",
    "चना",
    "मसूर",
    "उरद",
    "मूंग",
    "बरसीम",
    "मूंगफली",
    "तिल",
    "सरसो",
    "एक प्रकार का पौधा",
    "मेंटा मिंट",
    "नींबू",
    "पत्ता गोभी",
    "फूल गोभी",
    "खीरा",
    "प्याज",
    "लहसून",
    "बेंगन",
    "करेला",
    "लोकी",
    "कद्दू",
    "आलू",
    "टमाटर",
    "भिण्डी",
    "मिर्च",
    "शिमला मिर्च",
    "आम",
    "अमरूद",
    "गेंदा",
    "ग्लेडियोलस"
)