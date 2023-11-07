package com.agro.dkdlab.ui.view.guest.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.agro.dkdlab.R
import com.agro.dkdlab.custom.Util.json
import com.agro.dkdlab.custom.clearSearch
import com.agro.dkdlab.custom.searchValue
import com.agro.dkdlab.network.model.SoilSampleModel
import com.agro.dkdlab.ui.adapter.SampleListAdapter
import com.agro.dkdlab.ui.view.guest.GuestDashboardActivity
import com.agro.dkdlab.ui.view.guest.GuestDashboardActivity.Companion.viewPagerPosition
import com.agro.dkdlab.ui.view.guest.ReportDetailsActivity
import kotlinx.android.synthetic.main.fragment_completed.*
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ReportFragment(val list: List<SoilSampleModel>, private val userType: String) : Fragment() {
    private val adapter = SampleListAdapter(list,userType)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_completed, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emptyMessageViewReport.text="No samples are tested yet"
        editSearchReportSample.visibility = if (list.isEmpty()) GONE else VISIBLE
        recyclerViewReport.visibility = if (list.isEmpty()) GONE else VISIBLE
        emptyMessageViewReport.visibility = if (list.isEmpty()) VISIBLE else GONE


        adapter.apply {

            onItemClick {
//                writeDataToExcel(it)
                sampleDetailsLauncher.launch(
                    Intent(requireActivity(), ReportDetailsActivity::class.java)
                        .putExtra("sampleData",  it.json()))
            }

            onVisibilityCheck {
                emptyMessageViewReport.text="No result found"
                emptyMessageViewReport.visibility=if(adapter.itemCount==0)View.VISIBLE else View.GONE
                recyclerViewReport.visibility=if(adapter.itemCount==0)View.GONE else View.VISIBLE
            }

        }
        editSearchReportSample.searchValue {
            adapter.filter(it)
        }
        editSearchReportSample.clearSearch()

        recyclerViewReport.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewReport.adapter = adapter
        swipeRefreshCompleted.setOnRefreshListener {
            swipeRefreshCompleted.isRefreshing = false
            when(activity){
                is GuestDashboardActivity -> {
                    viewPagerPosition =1
                    (activity as GuestDashboardActivity?)?.soilSampleByPrimaryPhone()
                }
            }
        }
    }

    private fun writeDataToExcel(sample: SoilSampleModel) {
        try {
            val workbook = XSSFWorkbook()
            val spreadsheet = workbook.createSheet(" Student Data ")
            var row: XSSFRow
            val studentData: MutableMap<String, Array<Any>> = TreeMap()
            studentData["1"] = arrayOf("Roll No", "NAME", "Year")

            studentData["2"] = arrayOf(sample.ids!!,sample.cropName!!,sample.nrangName!!)

            studentData["3"] = arrayOf("129", "Narayana", "2nd year")

            studentData["4"] = arrayOf(
                "130", "Mohan",
                "2nd year"
            )

            studentData["5"] = arrayOf(
                "131", "Radha",
                "2nd year"
            )

            studentData["6"] = arrayOf(
                "132", "Gopal",
                "2nd year"
            )

            val keyid: Set<String> = studentData.keys

            var rowid = 0

            // writing the data into the sheets...


            // writing the data into the sheets...
            for (key in keyid) {
                row = spreadsheet.createRow(rowid++)
                val objectArr = studentData[key]!!
                var cellid = 0
                for (obj in objectArr) {
                    val cell: Cell = row.createCell(cellid++)
                    cell.setCellValue("$obj")
                }
            }

            // .xlsx is the format for Excel Sheets...
            // writing the workbook into the file...

            // .xlsx is the format for Excel Sheets...
            // writing the workbook into the file...

            val timeStamp: String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
            val mFileName = "GFGsheet$timeStamp"
            val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val out = FileOutputStream(File.createTempFile(mFileName, ".xlsx", storageDir)
//            File("C:/savedexcel/GFGsheet.xlsx")
            )


            workbook.write(out)
            out.close()
        }catch (e: Exception){
        Log.e("Exception:", e.printStackTrace().toString())
        }

    }

    // Receiver
    private val sampleDetailsLauncher =registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            (activity as GuestDashboardActivity?)?.soilSampleByPrimaryPhone()
        }
    }
}