package com.agro.dkdlab.ui.view.guest

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.agro.dkdlab.BuildConfig
import com.agro.dkdlab.R
import com.agro.dkdlab.app.AppPermission.checkStoragePermission
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.custom.Util.capitalizeWords
import com.agro.dkdlab.custom.Util.fromJson
import com.agro.dkdlab.custom.Util.getProgress
import com.agro.dkdlab.custom.Util.json
import com.agro.dkdlab.custom.Util.showAlert
import com.agro.dkdlab.network.config.Status
import com.agro.dkdlab.network.model.SoilSampleModel
import com.agro.dkdlab.ui.view.FertilizerCalculationActivity
import com.agro.dkdlab.ui.view.FertilizerRecommendationActivity
import com.agro.dkdlab.ui.view.ManualFertilizerCalculatorActivity
import com.agro.dkdlab.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_report_details2.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


@AndroidEntryPoint
class ReportDetailsActivity : AppCompatActivity() {
    private var file: File?=null
    private var bitmap:Bitmap?=null
    lateinit var soilSampleResult:SoilSampleModel
    private val viewModel by viewModels<MainViewModel>()
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_details2)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        collapsing_toolbar.isTitleEnabled = true
        toolbar.setNavigationOnClickListener { view -> onBackPressed() }
        getCrop()
         soilSampleResult = intent.getStringExtra("sampleData")!!.fromJson()
        soilSampleResult.let {
            collapsing_toolbar.title = it.farmerName?.capitalizeWords()
            textFarmerName.text = it.farmerName?.capitalizeWords()
            textFathername.text = it.fatherName?.capitalizeWords()
//            txtVillageName.text = it.villageName
            it.villageName?.let{ location->
                textLocation.text="${it.villageName}, ${it.blockName}, ${it.districtName}, ${it.stateName}, ${it.pincode}"
                layoutLocation.visibility= View.VISIBLE
            }
            textKhasra.text = it.khasraNumber
            textLandArea.text = "${it.farmSize} Acre"
            textGender.text = it.gender
            textCategory.text = it.category
            textMobileNumber.text = it.farmerMobileNumber
//            val dateTimeFormat = "${it.creationDate.toDate("yyyy-MM-dd'T'HH:mm:ss").formatTo("dd-MM-yyyy")}, ${it.creationTime}"
            textSampleDate.text = "${it.creationDate} ${it.creationTime}"
            textCropName.text = it.cropName
            textIrrigationType.text = it.irrigationSource
            //For Card Value Set....
            nValue.text = it.nrangName
            pValue.text = it.prangName
            kValue.text = it.krangName
            ocValue.text = it.ocRangName
            copperValue.text = it.copperRangName
            manganeseValue.text = it.maganeseRangName
            ironValue.text = it.ironRangName
            zincValue.text = it.zincRangName
            boronValue.text = it.boronRangName
            sulphurValue.text = it.sulphurRangName
            phValue.text = it.phRangName
        }

        var model=Build.MANUFACTURER
        Log.e("Model:" ,"onCreate: $model")
        btnShare.visibility=if(model=="LANDI") View.GONE else View.VISIBLE
        btnShare.setOnClickListener {
            if (Environment.isExternalStorageManager()) {
                if (checkStoragePermission()) {
                    bitmap = Bitmap.createBitmap(linearLayoutScreenshot.width, linearLayoutScreenshot.height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap!!)
                    linearLayoutScreenshot.draw(canvas)
                    saveMediaToStorage(bitmap!!,"soilReport")
                }
            } else {
                val permissionIntent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(permissionIntent)
            }
        }
        layoutOrganicFert.setOnClickListener {
//            fertCalculateManual()
                    startActivity(Intent(this, FertilizerCalculationActivity::class.java)
                        .putExtra("sampleData",  soilSampleResult.json()))
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menu = menu
        menuInflater.inflate(R.menu.menu_scrolling, menu)
//        hideOption(R.id.action_info)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         if (item.itemId == R.id.action_fertCal) {
             startActivity(Intent(this, ManualFertilizerCalculatorActivity::class.java).putExtra("reportData", soilSampleResult?.json()))
             return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getCrop() {
        //val pd = getProgress()
        viewModel.getCropMasterNative("English","UK000410").observe(this){
            it?.let { resource->
                when (resource.status) {
                    Status.SUCCESS -> {
                            Log.e("cropModel==>", it.data!!.json())
                            MyApp.get().setCropData(it.data!!.sortedBy { it.cropName }.json())
                    }
                    Status.ERROR -> {
                        if (it.message == "internet") {
                            showAlert(getString(R.string.network_error),getString(R.string.no_internet))
                        } else {
                            showAlert(getString(R.string.error), it.message.toString())
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun fertCalculateManual() {
        val pd = getProgress()
        viewModel.storeFertCalcManual(
            farmSize = soilSampleResult?.farmSize.toString(),
            nRangName =  soilSampleResult?.nrangName.toString(),
            pRangName = soilSampleResult?.prangName.toString(),
            kRangName = soilSampleResult?.krangName.toString(),
            ocRangName = soilSampleResult?.ocRangName.toString(),
            phRangName = "Neutral"
        ).observe(this){
            it?.let { resource->
                when (resource.status) {
                    Status.SUCCESS -> {
                        pd.dismiss()
                        startActivity(Intent(this, FertilizerRecommendationActivity::class.java).putExtra("recommendation",it.data?.json()).putExtra("farmSize",soilSampleResult?.farmSize.toString()))
                    }
                    Status.ERROR -> {
                        pd.dismiss()
                        if (it.message == "internet") {
                            showAlert(getString(R.string.network_error),getString(R.string.no_internet))
                        } else {
                            showAlert(getString(R.string.error), it.message.toString())
                        }
                    }
                    else -> {}
                }
            }
        }
    }
    private fun saveMediaToStorage(bitmap: Bitmap,name:String) {
        val filename = "${name}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                file = File(getPath(imageUri))
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            file = File(imagesDir, filename)
            fos = FileOutputStream(file)
        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            val absolutePath: String = file!!.getAbsolutePath()
        }
        file?.let {
            createPDF(bitmap) { file ->
                val apkURI = FileProvider.getUriForFile(
                    Objects.requireNonNull(applicationContext),
                    BuildConfig.APPLICATION_ID + ".provider", file!!
                )
                val intent = Intent().apply {
                    this.action = Intent.ACTION_SEND
                    this.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    this.putExtra(Intent.EXTRA_STREAM, apkURI)
                    this.setDataAndType(apkURI, "application/pdf")
                }
                startActivity(Intent.createChooser(intent, null))
            }
        }
    }

    private fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri!!, projection, null, null, null) ?: return null
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val s = cursor.getString(columnIndex)
        cursor.close()
        return s
    }

    private fun createPDF(bitmap: Bitmap, cb: ((File)) -> Unit) {
        /*val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadDir, "soil_report_details.pdf")
        val dialog = getProgress("Please wait...")
        dialog.show()*/
        var file: File? =null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "soil_report_details.pdf")
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val imageUri: Uri? = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                 file = File(getPath(imageUri))
            }
        } else {
            val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
             file = File(downloadDir, "soil_report_details.pdf")
        }
        val dialog = getProgress("Please wait...")
        dialog.show()

        Thread {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            Log.e("PDF", "pdf = " + bitmap.width + "x" + bitmap.height)
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            document.finishPage(page)
            val fos: FileOutputStream
            try {
                if (file!!.exists())
                    file!!.delete()
                fos = FileOutputStream(file)
                document.writeTo(fos)
                document.close()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            runOnUiThread {
                dialog.dismiss()
                cb.invoke(file!!)
            }
        }.start()
    }

}