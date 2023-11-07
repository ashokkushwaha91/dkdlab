package com.agro.dkdlab.ui.view

import android.R.array
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View.VISIBLE
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.agro.dkdlab.BuildConfig
import com.agro.dkdlab.app.AppPermission.checkCameraPermission
import com.agro.dkdlab.custom.Util.fromListJson
import com.agro.dkdlab.custom.Util.getProgress
import com.agro.dkdlab.custom.Util.json
import com.agro.dkdlab.databinding.ActivityReportDetailsBinding
import com.agro.dkdlab.ui.database.entities.ReportEntity
import com.agro.dkdlab.ui.dialog.ConfirmAlertDialog
import com.agro.dkdlab.ui.viewmodel.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


@AndroidEntryPoint
class ReportDetails : AppCompatActivity() {
    private lateinit var binding: ActivityReportDetailsBinding
    private var reportData: ReportEntity?=null
    private var sampleBarCode: String?=null
    private val viewModel: ReportViewModel by viewModels()

    private var file:File?=null
    private var bitmap:Bitmap?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.include.textTitle.text="Soil report details"
        sampleBarCode=intent.getStringExtra("barcode")
        lifecycleScope.launch {
            viewModel.getReportByBarcode(sampleBarCode.toString()).observe(this@ReportDetails) { datas ->
                reportData = datas.json().fromListJson()
                dataBind()
            }
        }
        binding.include.imageViewBack.setOnClickListener {
            finish()
        }
        binding.btnNitrogen.setOnClickListener {
            nextPage("Nitrogen",binding.nValue.text.toString())
        }
        binding.btnPhosphorus.setOnClickListener {
            nextPage("Phosphorus",binding.pValue.text.toString())
        }
        binding.btnPotassium.setOnClickListener {
            nextPage("Potassium",binding.kValue.text.toString())
        }
        binding.btnOrganicCarbon.setOnClickListener {
            nextPage("Organic Carbon",binding.ocValue.text.toString())
        }
        binding.btnCopper.setOnClickListener {
            nextPage("Copper",binding.copperValue.text.toString())
        }
        binding.btnManganese.setOnClickListener {
            nextPage("Manganese",binding.manganeseValue.text.toString())
        }
        binding.btnIron.setOnClickListener {
            nextPage("Iron",binding.ironValue.text.toString())
        }
        binding.btnZinc.setOnClickListener {
            nextPage("Zinc",binding.zincValue.text.toString())
        }

        binding.btnBoron.setOnClickListener {
            nextPage("Boron",binding.boronValue.text.toString())
        }
        binding.btnSulphur.setOnClickListener {
            nextPage("Sulphur",binding.sulphurValue.text.toString())
        }
        binding.include.imageCal.visibility=VISIBLE
        binding.include.imageCal.setOnClickListener {
         startActivity(Intent(this,ManualFertilizerCalculatorActivity::class.java).putExtra("reportData", reportData?.json()))
        }
        binding.btnShare.setOnClickListener {
            if (checkCameraPermission()){
                bitmap = Bitmap.createBitmap(binding.layoutSoilHealth.width, binding.layoutSoilHealth.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap!!)
                binding.layoutSoilHealth.draw(canvas)
                saveMediaToStorage(bitmap!!,"soil Report"!!)
            }
        }
    }
    private fun nextPage(str: String, testValue: String) {
        if(testValue == "---") {
            gotoNextPage(str)
        }else {
            try {
                val dialog = ConfirmAlertDialog(this, "Alert!", "Do you want to update current value?")
                dialog.setDialogDismissListener {
                    gotoNextPage(str)
                }
                dialog.show()
            }catch (e: Exception){}
        }
    }
    private fun gotoNextPage(str: String){
        startActivity(
            Intent(this, ReportResultFragmentActivity::class.java)
                .putExtra("selectedValue", str)
                .putExtra("reportData", reportData?.json())
                .putExtra("updateReport", true)
        )
    }
    private fun dataBind(){
        reportData?.let { reportDatas->
            binding.textViewBarcode.text=reportDatas.sampleBarCode
            binding.textViewName.text=reportDatas.name
            binding.textViewMobile.text=reportDatas.mobile
            binding.textViewFarmerName.text=reportDatas.farmerName
            binding.textViewFarmerMobNo.text=reportDatas.farmerMobile
            binding.textViewDate.text=reportDatas.date
            binding.nValue.text= "Nitrogen".findTestValue()
            binding.pValue.text= "Phosphorus".findTestValue()
            binding.kValue.text= "Potassium".findTestValue()
            binding.ocValue.text= "Organic Carbon".findTestValue()
            binding.copperValue.text= "Copper".findTestValue()
            binding.manganeseValue.text= "Manganese".findTestValue()
            binding.ironValue.text= "Iron".findTestValue()
            binding.zincValue.text= "Zinc".findTestValue()
            binding.boronValue.text= "Boron".findTestValue()
            binding.sulphurValue.text= "Sulphur".findTestValue()
        }
    }
    private fun String.findTestValue(): String {
        var findData= reportData?.testParametersModel?.find { it.testType==this }?.result
        return if(findData.isNullOrEmpty())"---" else findData
    }

    private fun saveMediaToStorage(bitmap: Bitmap,name:String) {
        //Generating a file name
        val filename = "${name}.jpg"

        //Output stream
        var fos: OutputStream? = null

        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            contentResolver?.also { resolver ->

                //Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                //Opening an outputstream with the Uri that we got
                file = File(getPath(imageUri))
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            file = File(imagesDir, filename)
            fos = FileOutputStream(file)
        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            val absolutePath: String = file!!.getAbsolutePath()
//            showToast("Saved to Photos")
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
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val s = cursor.getString(column_index)
        cursor.close()
        return s
    }
    private fun createPDF(bitmap: Bitmap, cb: ((File)) -> Unit) {
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadDir, "soil_health_report.pdf")
        val dialog = getProgress("Please wait...")
        dialog.show()
        Thread {
            val document = PdfDocument()
            val pageInfo = PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            Log.e("PDF", "pdf = " + bitmap.width + "x" + bitmap.height)
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            document.finishPage(page)
            val fos: FileOutputStream
            try {
                fos = FileOutputStream(file)
                document.writeTo(fos)
                document.close()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            runOnUiThread {
                dialog.dismiss()
                cb.invoke(file)
            }
        }.start()
    }


}