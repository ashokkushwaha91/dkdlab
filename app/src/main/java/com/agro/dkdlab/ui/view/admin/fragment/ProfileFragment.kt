package com.agro.dkdlab.ui.view.admin.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.agro.dkdlab.BuildConfig
import com.agro.dkdlab.R
import com.agro.dkdlab.app.MyApp
import com.agro.dkdlab.custom.Util.capitalizeWords
import com.agro.dkdlab.custom.Util.hideKeyboard
import com.agro.dkdlab.ui.dialog.ConfirmAlertDialog
import com.agro.dkdlab.ui.view.login.GuestLoginActivity
import kotlinx.android.synthetic.main.profile_fragment.*

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().hideKeyboard()

        textUserType.text = MyApp.get().getProfile()?.type?.capitalizeWords()
        txtNameAdmin.text = MyApp.get().getProfile()?.name?.capitalizeWords()
        txtMob.text = MyApp.get().getProfile()?.primaryPhone
        txtVersion.text = "Version: ${BuildConfig.VERSION_NAME}"
        btnLogout.setOnClickListener {
            ConfirmAlertDialog(requireActivity(),
                getString(R.string.logout_title),
                getString(R.string.logout_message)
            ).apply {
                setDialogDismissListener {
                    MyApp.get().logout()
                    startActivity(Intent(requireActivity(), GuestLoginActivity::class.java))
                    requireActivity().finishAffinity()
                }
            }.show()
        }
        profileEdit.setOnClickListener {
//            recordUpdateResult.launch(Intent(requireContext(), UpdateAdminProfileActivity::class.java))
        }
    }
    private val recordUpdateResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            txtNameAdmin.text = MyApp.get().getProfile()?.name?.capitalizeWords()
        }
}