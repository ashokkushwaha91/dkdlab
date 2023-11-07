package com.agro.dkdlab.ui.view.guest

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RadioButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.agro.dkdlab.R
import com.agro.dkdlab.app.AppPermission.askLocationPermission
import com.agro.dkdlab.app.AppPermission.checkCameraPermission
import com.agro.dkdlab.app.AppPermission.hasLocationPermissions
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.constant.FileUtils
import com.agro.dkdlab.constant.categoryList
import com.agro.dkdlab.custom.Util.fromListJson
import com.agro.dkdlab.custom.Util.getProgress
import com.agro.dkdlab.custom.Util.hideKeyboard
import com.agro.dkdlab.custom.Util.isValidMobile
import com.agro.dkdlab.custom.Util.json
import com.agro.dkdlab.custom.Util.showAlert
import com.agro.dkdlab.custom.Util.showToast
import com.agro.dkdlab.custom.validateForError
import com.agro.dkdlab.databinding.ActivitySoilSampleGuestBinding
import com.agro.dkdlab.network.config.Status
import com.agro.dkdlab.network.model.CropModel
import com.agro.dkdlab.network.model.SoilSampleModel
import com.agro.dkdlab.network.tool.ErrorUtils
import com.agro.dkdlab.ui.dialog.IrrigationDialog
import com.agro.dkdlab.ui.view.MultipleCropSelectionActivity
import com.agro.dkdlab.ui.viewmodel.ReportViewModel
import com.agro.dkdlab.ui.viewmodel.SoilSampleViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.appbarlayout.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class SoilSampleActivity : AppCompatActivity() {
    private var lastCropData: List<CropModel>? = null
    private var lastCropId: String = "0"
    private var farmerPhotoUri: Uri? = null
    private var farmerPhotoFile: File? = null
    private val farmerPhotoCode = 205 // don't use 203, 203 for cropping image
    private val lastCropCode = 101
    private val location = 201
    private var irrList = listOf<String>()

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var khasraExist = false
    private var uploadImageSize = 0
    private var uploadImageResponseSize = 0

    private lateinit var locationManager: LocationManager
    var locationGps: Location? = null
    private var surveyImagesParts = ArrayList<MultipartBody.Part>()
    private var currentCropping = ""
    lateinit var binding: ActivitySoilSampleGuestBinding

    var state: String? = null
    var district: String? = null
    var block: String? = null
    var village: String? = null
    var pincode: String? = null

    private val soilSampleViewModel by viewModels<SoilSampleViewModel>()
    private val viewModel: ReportViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoilSampleGuestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        textTitle.text = "Soil Sample"
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        hideKeyboard()
//        fetchLocation()
        bindCategory()

        imageViewBack.setOnClickListener {
            finish()
        }

        binding.inputLayoutName.validateForError()
        binding.inputLayoutFatherName.validateForError()
        binding.inputLayoutMobileNumber.validateForError()
        binding.inputLayoutLocation.validateForError()
        binding.inputLayoutKhasra.validateForError()
        binding.inputLayoutLandSize.validateForError()
        binding.inputLayoutSampleCode.validateForError()
        binding.inputLayoutCropName.validateForError()
        binding.inputLayoutIrrigationType.validateForError()



        binding.editLocation.setOnClickListener {
            startActivityForResult(Intent(this, SampleAddressActivity::class.java), location)
        }
        binding.btnCollectSample.setOnClickListener {
            validateForm()
        }
        /* binding.editKhasra.setOnClickListener {
              startActivityForResult(Intent(this, UserVillageActivity::class.java),khasraCode)
          }
 */
        binding.editCropName.setOnClickListener {
            startActivityForResult(
                Intent(this, MultipleCropSelectionActivity::class.java).putExtra(
                    "selectedCropData",
                    lastCropData?.json()
                ).putExtra("otherCrop", binding.editOtherCrop.text.toString()), lastCropCode
            )
        }
        binding.editIrrigationType.setOnClickListener {
            val dialog = IrrigationDialog(this, irrList)
            dialog.setDialogDismissListener {
            }
            dialog.show()
            dialog.setItemClick { it ->
                irrList = it.filter { !it.isNullOrBlank() }
                Log.e("items", it.toString())
                binding.editIrrigationType.setText(irrList.joinToString { it.trim() })
            }
        }
        binding.imageViewFarmerPic.setOnClickListener {
            if (checkCameraPermission()) {
                val values = ContentValues()
                farmerPhotoUri =
                    contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, farmerPhotoUri)
                startActivityForResult(cameraIntent, farmerPhotoCode)
            }
        }
    }


    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mFileName = "@dkd_image$timeStamp"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(mFileName, ".jpg", storageDir)
    }

    private fun bindCategory() {
        val list: List<String> = categoryList[MyApp.get().getLanguage()]!!
        val adapter = ArrayAdapter(this, R.layout.spinner_source_layout, list)
        adapter.setDropDownViewResource(R.layout.spinner_source_layout)
        binding.spinnerCategory.adapter = adapter
        binding.spinnerCategory.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long,
                ) {
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            requestCode == location && resultCode === RESULT_OK -> {
                state = data?.getStringExtra("state")
                district = data?.getStringExtra("district")
                block = data?.getStringExtra("block")
                village = data?.getStringExtra("village")
                pincode = data?.getStringExtra("pincode")
                binding.editLocation.setText("$village,$block, $district, $state, $pincode")
            }

            requestCode == lastCropCode && resultCode === RESULT_OK -> {
                lastCropData = data?.getStringExtra("cropData").toString().fromListJson()
                lastCropData?.let {
                    lastCropId = it.map { it.id }.joinToString { it }
                    Log.i("cropID", "onActivityResult: " + lastCropId)
                    binding.editCropName.setText(it.map { it.cropName }.joinToString { it!! })
                    var otherCropData = it.filter { it.cropName == "Other Crop" }
//                    if (!otherCropData.isNullOrEmpty())
//                        binding.inputLayoutOtherCrop.visibility = VISIBLE
//                    else binding.inputLayoutOtherCrop.visibility = GONE
                }
            }

            requestCode == farmerPhotoCode -> {
                if (resultCode == RESULT_OK) {
                    if (farmerPhotoUri != null) {
                        farmerPhotoFile = File(FileUtils.getPath(this, farmerPhotoUri!!))
                        if (farmerPhotoFile != null) {
                            currentCropping = "farmer"
                            setPhoto(farmerPhotoFile, binding.imageViewFarmerPic)
                        }
                        binding.layoutCameraIcon.visibility = GONE
//                        Glide.with(this).load(farmerPhotoUri).into(imageViewFarmerPic)
                    } else {
                        showToast("Farmer photo not found")
                    }
                } else {
                    farmerPhotoUri = null
                }
            }

            requestCode == 432 && resultCode == RESULT_OK -> {
                if (hasLocationPermissions()) {
                    fetchLocation()
                } else askLocationPermission(200)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun validateForm() {
        when {
             /*latitude==0.0-> {
                 showToast("Location not found")
//                 fetchLocation()
             }*/
//            longitude==0.0->showToast("Location not found")
            binding.editFarmerName.text.toString().trim().isEmpty() -> {
                binding.inputLayoutName.requestFocus()
                binding.inputLayoutName.error = resources.getString(R.string.enter_name)
            }

            binding.editFatherName.text.toString().trim().isEmpty() -> {
                binding.inputLayoutFatherName.requestFocus()
                binding.inputLayoutFatherName.error =
                    resources.getString(R.string.enter_father_name)
            }

            binding.inputLayoutMobileNumber.editText?.text.toString().trim().isEmpty() -> {
                binding.inputLayoutMobileNumber.editText?.requestFocus()
                binding.inputLayoutMobileNumber.error =
                    resources.getString(R.string.please_enter_mobile_number)
            }

            binding.inputLayoutMobileNumber.editText?.text.toString().trim().length < 10 -> {
                binding.inputLayoutMobileNumber.editText?.requestFocus()
                binding.inputLayoutMobileNumber.error =
                    getString(R.string.enter_10_digit_mobile_number)
            }

            binding.editMobileNumber.text.toString().trim()
                .isNotBlank() && !isValidMobile(binding.editMobileNumber.text.toString()) -> {
                binding.inputLayoutMobileNumber.editText?.requestFocus()
                binding.inputLayoutMobileNumber.error =
                    resources.getString(R.string.please_enter_valid_mobile_number)
            }

            binding.editLocation.text.toString().trim().isEmpty() -> {
                binding.inputLayoutLocation.requestFocus()
                binding.inputLayoutLocation.error = resources.getString(R.string.enter_address)
            }

            binding.editKhasra.text.toString().trim().isEmpty() -> {
                binding.inputLayoutKhasra.requestFocus()
                binding.inputLayoutKhasra.error =
                    resources.getString(R.string.Khasra_number_required)
            }

            khasraExist -> {
                binding.inputLayoutKhasra.requestFocus()
                binding.inputLayoutKhasra.error = resources.getString(R.string.khasra_already_exist)
            }

            binding.editLandSize.text.toString().trim().isEmpty() -> {
                binding.inputLayoutLandSize.requestFocus()
                binding.inputLayoutLandSize.error = resources.getString(R.string.enter_land_size)
            }

            binding.editSampleCode.text.toString().trim().isEmpty() -> {
                binding.inputLayoutSampleCode.requestFocus()
                binding.inputLayoutSampleCode.error = "Enter sample code"
            }

            binding.radioGroup.checkedRadioButtonId == -1 -> showToast(getString(R.string.select_gender))

            binding.spinnerCategory.selectedItem.toString()
                .isEmpty() || binding.spinnerCategory.selectedItemPosition == 0 -> showToast(
                getString(R.string.select_category)
            )

            binding.editCropName.text.toString().trim().isEmpty() -> {
                binding.inputLayoutCropName.requestFocus()
                binding.inputLayoutCropName.error =
                    resources.getString(R.string.enter_last_crop_name)
            }

            binding.editCropName.text.toString() == "Other Crop" && binding.editOtherCrop.text.toString()
                .trim().isEmpty() -> {
                binding.inputLayoutOtherCrop.requestFocus()
                binding.inputLayoutOtherCrop.error =
                    resources.getString(R.string.enter_last_crop_name)
            }

            binding.editIrrigationType.text.toString().trim().isEmpty() -> {
                binding.inputLayoutIrrigationType.requestFocus()
                binding.inputLayoutIrrigationType.error =
                    resources.getString(R.string.select_irrigation_type)
            }
//            farmerPhotoUri?.path==null-> showToast(resources.getString(R.string.farmer_photo_required))
            else -> if (MyApp.get().getProfile()?.type != "Offline") createNewSample()
            else saveSoilSample()
        }

    }

    private fun createNewSample() {
        uploadImageSize = 0
        uploadImageResponseSize = 0
        val radio: RadioButton = findViewById(binding.radioGroup.checkedRadioButtonId)

        val pd = getProgress()
        val body = HashMap<String, Any>()

        body["farmerName"] = binding.editFarmerName.text.toString()
        body["fatherName"] = binding.editFatherName.text.toString()
        body["farmerMobileNumber"] = binding.editMobileNumber.text.toString()
        body["gender"] = radio.text.toString()
        body["khasraNumber"] = binding.editKhasra.text.toString()
        body["farmSize"] = binding.editLandSize.text.toString()
        body["category"] = binding.spinnerCategory.selectedItem.toString()
        body["cropName"] = binding.editCropName.text.toString()
        body["irrigationSource"] = binding.editIrrigationType.text.toString()
        body["latitude"] = latitude.toString()
        body["longitude"] = longitude.toString()
        body["sampleBarCode"] = binding.editSampleCode.text.toString().trim()
        body["stateName"] = state.toString()
        body["districtName"] = district.toString()
        body["blockName"] = block.toString()
        body["villageName"] = village.toString()
        body["pincode"] = pincode.toString()
        body["primaryPhone"] = MyApp.get().getProfile()?.primaryPhone.toString()
        Log.e("soilSampleParams", body.json())

        val sampleRequestDTO: RequestBody =
            RequestBody.create("application/json".toMediaTypeOrNull(), body.json())

        surveyImagesParts.clear()
        if (farmerPhotoFile != null) {
            addSampleImages(farmerPhotoFile!!, "farmer")
        }
        soilSampleViewModel!!.createSoilSample(sampleRequestDTO, surveyImagesParts).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        pd.dismiss()
                        if (it.data?.isSuccessful == true && it.data.body() != null) {
                            Log.d("soilSampleResponse:", it.data.body()!!.toString())
                            if (it.data.body()!!.status) {
                                showToast(getString(R.string.soil_sample_successfully_collected))
                                val intent = Intent()
                                setResult(RESULT_OK, intent)
                                finish()
                            } else showToast(it.data.body()?.message.toString())
                        } else {
                            val apiError = ErrorUtils.parseError(it.data!!)
                            showAlert(
                                getString(R.string.error),
                                if (it.data?.code() == 500) getString(R.string.internal_server_error) else apiError.message
                            )
                        }
                    }

                    Status.ERROR -> {
                        pd.dismiss()
                        if (it.message == "internet") {
                            showAlert(
                                getString(R.string.network_error),
                                getString(R.string.no_internet)
                            )
                        } else {
                            showAlert(getString(R.string.error), it?.message.toString())
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun saveSoilSample() {
        val radio: RadioButton = findViewById(binding.radioGroup.checkedRadioButtonId)
        var sampleData = SoilSampleModel(
            ids = 0,
            farmerName = binding.editFarmerName.text.toString(),
            fatherName = binding.editFatherName.text.toString(),
            farmerMobileNumber = binding.editMobileNumber.text.toString(),
            khasraNumber = binding.editKhasra.text.toString(),
            farmSize = binding.editLandSize.text.toString(),
            gender = radio.text.toString(),
            primaryPhone = MyApp.get().getProfile()?.primaryPhone.toString(),
            category = binding.spinnerCategory.selectedItem.toString(),
            cropName = binding.editCropName.text.toString(),
            irrigationSource = binding.editIrrigationType.text.toString(),
            latitude = latitude,
            longitude = longitude,
            creationDate = "2012-12-12",
            creationTime = "10:10 AM"
        )
        lifecycleScope.launch {
            viewModel.insertNew(sampleData)
            showToast("Record successfully saved")
            finish()
        }
    }

    private fun addSampleImages(imageFile: File, fileType: String) {
        val surveyBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), imageFile)
        var file = MultipartBody.Part.createFormData(
            "file",
            "$fileType${"_"} ${imageFile.name}",
            surveyBody
        )
        surveyImagesParts.add(file)
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
//        var hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (hasGps) {
            /*locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object :
                LocationListener {
                override fun onLocationChanged(locationGps: Location) {
                    if (locationGps != null) {
//                            showToast("GPS Longitude: ${locationGps!!.longitude} Latitude: ${locationGps!!.latitude}")
                        latitude = locationGps.latitude
                        longitude = locationGps.longitude
                        Log.e("locationSample: ","$latitude : $longitude" )
                    }
                }
                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                    Log.e("GPS", "onStatusChanged")
//                        showToast("onStatusChanged")
                }
                override fun onProviderEnabled(provider: String) {
                    Log.e("GPS", "onProviderEnabled")
//                        showToast("onProviderEnabled")
                }
                override fun onProviderDisabled(provider: String) {
                    Log.e("GPS", "onProviderDisabled")
//                        showToast("onProviderDisabled")
                }
            })*/

           /* val localGpsLocation =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (localGpsLocation != null) {
                locationGps = localGpsLocation
                latitude = locationGps?.latitude!!
                longitude = locationGps?.longitude!!
            }*/

           var mLocationManager = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
            val providers: List<String> = mLocationManager.getProviders(true)
            var bestLocation: Location? = null
            for (provider in providers) {
                val l: Location = mLocationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                    // Found best last known location: %s", l);
                    bestLocation = l
                    latitude = bestLocation.latitude
                    longitude = bestLocation.longitude
                }
            }

        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private fun setPhoto(imageFile: File?, imageView: ImageView) {
        Glide.with(this).load(imageFile).transform().into(imageView)
    }

    private fun stopLocationUpdates() {
        locationManager?.let {
            locationManager?.removeUpdates { this }
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        fetchLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocationUpdates()
    }
}