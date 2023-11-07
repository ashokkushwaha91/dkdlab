package com.agro.dkdlab.ui.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import androidx.fragment.app.Fragment
import com.agro.dkdlab.R
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.custom.Util.showToast
import com.agro.dkdlab.databinding.FragmentCreateReportBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportCreateFragment : Fragment() {
    companion object {
        fun newInstance() = ReportCreateFragment()
    }
    private lateinit var lpwUserType  : ListPopupWindow
    private val soilType = listOf(
        "Nitrogen",
        "Phosphorus",
        "Potassium",
        "Organic Carbon",
        "Copper",
        "Manganese",
        "Iron",
        "Zinc",
        "Boron",
        "Sulphur"
    )
    private var _binding: FragmentCreateReportBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentCreateReportBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val isConnected = MyApp.get().isUsbConnected()
        if (isConnected){
            MyApp.get().send("0")
//            requireActivity().showToast("send: reset")
            Log.e("send:","reset")
        }else{
//            requireActivity().showToast("Device is not connected!")
            Log.e("DeviceNotConnected:","Device is not connected!")
        }

        lpwUserType = ListPopupWindow(requireActivity())
        _binding!!.buttonCheck.setOnClickListener {
            when{
                _binding!!.editSoil.text.toString().trim().isEmpty()->  requireActivity().showToast(getString(R.string.select_soil_type))
                else -> {
                    val args = Bundle()
                    args.putString("selectedValue", _binding!!.editSoil.text.toString())
                    val fragment: Fragment = ReportResultFragment()
                    fragment.arguments = args
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.container, fragment, "reportFragment").addToBackStack(null).commit()
                }
            }
        }

        lpwUserType.setAdapter(
//            ArrayAdapter(requireActivity(),androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, soilType)
            ArrayAdapter(requireActivity(),R.layout.spinner_source_layout, soilType)
        )
        lpwUserType.anchorView = _binding!!.editSoil
        lpwUserType.isModal = true
        lpwUserType.setOnItemClickListener { parent, view, position, id ->
            _binding!!.editSoil.setText(soilType[position])
            lpwUserType.dismiss()
        }
        _binding!!.editSoil.setOnClickListener {
            lpwUserType.show()
        }
        return  root
    }


}