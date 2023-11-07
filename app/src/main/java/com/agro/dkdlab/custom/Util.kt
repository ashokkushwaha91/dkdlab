package com.agro.dkdlab.custom

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.location.Location
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Transformation
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import com.agro.dkdlab.R
import com.agro.dkdlab.ui.dialog.AppAlertDialog
import com.agro.dkdlab.ui.dialog.ConfirmAlertDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


object Util {

    private const val UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
    private const val DEFAULT_FORMAT = "dd/MM/yyyy"


    var userTypeBoth= false
    var currentUserTrader= false
    var preSelectedvillageCode = "0"
    var preSelectedVillageName = "0"
    var preSelectedBlockCode = "0"

    inline fun <reified T : Any> T.json(): String = Gson().toJson(this, T::class.java)
    inline fun <reified T : Any> String.fromJson(): T = Gson().fromJson(this, T::class.java)
    inline fun <reified T : Any> String.fromListJson(): T = Gson().fromJson(this,object : TypeToken<T>() {}.type)




    fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }

    fun Context.showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun Context.getToast(message: String): Toast {
        return Toast.makeText(this, message, Toast.LENGTH_SHORT)
    }

    fun String.isEqualTo(str: String): Boolean = this.equals(str, ignoreCase = true)

    fun Context.findColor(color: Int) = ContextCompat.getColor(this, color)

    fun Activity.showAlert(title: String, message: String?) {
        try {
            val dialog = AppAlertDialog(this, title, message)
            dialog.setDialogDismissListener {
            }
            dialog.show()
        }catch (e: Exception){}
    }
    fun Activity.showAlertWithListner(title: String, message: String) {

        var onActionYes: ((String) -> Unit)? = null
        fun setDialogDismissListener(listener: ((String) -> Unit)) {
            onActionYes = listener
        }
        try {
            val dialog = ConfirmAlertDialog(this, title, message)
            dialog.setDialogDismissListener {
                onActionYes?.invoke("ok")
            }
            dialog.show()
        }catch (e: Exception){}
    }

    fun Activity.getProgress(message: String = getString(R.string.please_wait)): Dialog {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.progress_layout)
        var textViewMessage=dialog.findViewById<TextView>(R.id.textViewMessage)

        dialog.setCancelable(false)

        if (message.isNotEmpty()) {
            textViewMessage.text = message
        }
        dialog.show()
        return dialog
    }
    fun Activity.getProgress2(message: String = getString(R.string.please_wait)): Dialog {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.progress_layout)
        var textViewMessage=dialog.findViewById<TextView>(R.id.textViewMessage)

        dialog.setCancelable(false)

        if (message.isNotEmpty()) {
            textViewMessage.text = message
        }
        return dialog
    }

    fun View.blink() {
        val startAnimation: Animation = AnimationUtils.loadAnimation(this.context, R.anim.anim_blink)
        this.startAnimation(startAnimation)
    }

    fun View.startRotation() {
        val startAnimation: Animation = AnimationUtils.loadAnimation(
            this.context,
            R.anim.anim_rotation
        )
        this.startAnimation(startAnimation)
    }


    fun <T> Call<T>.enqueue(callback: CallBackKt<T>.() -> Unit) {
        val callBackKt = CallBackKt<T>()
        callback.invoke(callBackKt)
        this.enqueue(callBackKt)
    }

    fun String.toDate(
        dateFormat: String = UTC_FORMAT,
        timeZone: TimeZone = TimeZone.getTimeZone("UTC"),
    ): Date {
        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(this)
    }

    fun Date.formatTo(
        dateFormat: String = DEFAULT_FORMAT,
        timeZone: TimeZone = TimeZone.getDefault(),
    ): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(this)
    }
    fun parseDate(format: String, c: Calendar): String {
        val sdf = SimpleDateFormat(format, Locale.getDefault())
        return sdf.format(c.time)
    }
    fun Activity.hideKeyboard()
    {
        this.window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }
    fun Activity.openKeyboard()
    {
        this.window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }


    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboards() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    fun ImageView.setTint(@ColorRes colorRes: Int) {
        ImageViewCompat.setImageTintList(
            this, ColorStateList.valueOf(
                ContextCompat.getColor(context, colorRes)
            )
        )
    }

    fun ImageView.setColorFilter(@ColorRes colorRes: Int) {
        this.setColorFilter(
            ContextCompat.getColor(context, colorRes),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }


    class CallBackKt<T> : Callback<T> {
        var onSuccess: ((Response<T>) -> Unit)? = null
        var onFailure: ((t: Throwable?) -> Unit)? = null
        override fun onFailure(call: Call<T>, t: Throwable) {
            onFailure?.invoke(t)
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {

            try {
                if (!response.isSuccessful){
                    Log.e("responseError:", response.errorBody()!!.json())
                }
            }catch (e: Exception){}

            if (response.code() == -1) {
                /* val ctx = MyApp.get().applicationContext
                 MyApp.get().logout()
                 ctx.startActivity(
                     Intent(ctx, SplashActivity::class.java).apply {
                         addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                         addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                         addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                     }
                 )*/
            } else onSuccess?.invoke(response)
        }
    }

    fun ViewGroup.inflate(@LayoutRes l: Int): View {
        return LayoutInflater.from(context).inflate(l, this, false)
    }


    fun View.expand() {
        val matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(
            (this.parent as View).width,
            View.MeasureSpec.EXACTLY
        )
        val wrapContentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        this.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = this.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        this.layoutParams.height = 1
        this.visibility = View.VISIBLE
        val v = this
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                v.layoutParams.height = if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Expansion speed of 1dp/ms
        val d = (targetHeight / v.context.resources.displayMetrics.density).toInt()
        a.duration = (d + 200).toLong()
        v.startAnimation(a)
    }

    fun View.collapse() {
        val initialHeight = this.measuredHeight
        val v = this
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Collapse speed of 1dp/ms
        val d = (initialHeight / v.context.resources.displayMetrics.density).toInt()
        a.duration = (d + 200).toLong()
        v.startAnimation(a)
    }

    fun Float.toCelsius() = (this-32)*(0.5556)
    fun Int.toCelsius() = (this-32)*(0.5556)
    fun Int.toKm() = (this)*(1.852)

    fun Activity.getKmForLatLong(lat1: Float, lon1: Float, lat2: Float, lon2: Float,): Float {
        val loc1 = Location("")
        loc1.latitude = lat1.toDouble()
        loc1.longitude = lon1.toDouble()

        val loc2 = Location("")
        loc2.latitude = lat2.toDouble()
        loc2.longitude = lon2.toDouble()

        return loc1.distanceTo(loc2)
        //return loc1.distanceTo(loc2)/1000
    }

    fun Context.readJsonFromAsset(fileName: String): String?{
        var json: String?
        try {
            val  inputStream: InputStream = assets.open(fileName)
            json = inputStream.bufferedReader().use{it.readText()}
        } catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }
        return json
    }


    fun View.fadeOut(){
        val animFadeOut = AnimationUtils.loadAnimation(this.context, R.anim.fade_out)
        this.startAnimation(animFadeOut)
    }

    fun View.fadeIn(){
        val animFadeOut = AnimationUtils.loadAnimation(this.context, R.anim.fade_in)
        this.startAnimation(animFadeOut)
    }

    fun View.startXAnimation(x: Float = 600f, d: Long = 700){
        this.alpha = 0f
        this.translationY = x
        this.visibility = View.VISIBLE
        this.animate().alpha(1f).translationYBy(-x).duration = d
    }


    fun String.toHexByteArray(): ByteArray{
        val byte: Byte = when(this){
            "0" -> 0x30
            "N" -> 0x4E
            "P" -> 0x50
            "K" -> 0x4B
            "C" -> 0x43
            "Copper" -> 0x63
            "Manganese" -> 0x4D
            "Iron" -> 0x46
            "Zinc" -> 0x5A
            "Boron" -> 0x42
            "Sulphur" -> 0x53
            else -> 0x30
        }
        return byteArrayOf(byte)
    }
    fun String.toNPKValue(): String{
        val result = when(this){
            "1" -> "Very Low"
            "2" -> "Low"
            "3" -> "Medium"
            "4" -> "High"
            "5" -> "Very High"
            "6" -> "Out of Rang"
            else -> "Out of Rang"
        }
        return result
    }
    fun String.toExceptNPKValue(): String{
        val result = when(this){
            "1","2" -> "Deficiency"
            "3","4","5"-> "Sufficient"
            "6" -> "Out of Rang"
            else -> "Out of Rang"
        }
        return result
    }

    fun String.toDigit():String{
        var p = Pattern.compile("\\d+");
        val m: Matcher = p.matcher(this)
        var output="0"
        while(m.find()) {
            output += m.group()
//            println("result:$output")
        }
        return output
    }
    fun getDateTime():String{
         val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd MMM, yyyy hh:mm a")
        return  formatter.format(time)
    }

    fun isValidMobile(s: String): Boolean {
        val regex = "^[6-9]\\d{9}\$"
        val mobileMatcher = Pattern.compile(regex)
        val m: Matcher = mobileMatcher.matcher(s)
        return m.matches()
    }

    fun isValidAadhaar(s: String?): Boolean {
        Log.e("Aadhaar No.", "$s")
        val regex = "^[2-9]{1}[0-9]{3}\\s{1}[0-9]{4}\\s{1}[0-9]{4}\$"
        val pattern = Pattern.compile(regex)
        val m: Matcher = pattern.matcher(s)
        Log.e("Aadhaar", "${m.matches()}")
        return m.matches()
    }

    val listNPK = listOf(
        "Select",
        "Very Low",
        "Low",
        "Medium",
        "High",
        "Very High",
    )
    val listPh = listOf(
        "Select",
        "Strongly Alkaline",
        "Moderately Alkaline",
        "Neutral",
        "Slightly Acidic",
        "Moderately Acidic",
        "Highly Acidic",
        "Strongly Acidic",
        "Acid Sulphate"
    )
}