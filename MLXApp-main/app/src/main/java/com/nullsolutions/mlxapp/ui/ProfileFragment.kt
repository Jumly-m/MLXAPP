package com.nullsolutions.mlxapp.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.nullsolutions.mlxapp.R
import com.nullsolutions.mlxapp.api.*
import com.nullsolutions.mlxapp.databinding.FragmentHomeBinding
import com.nullsolutions.mlxapp.databinding.FragmentProfileBinding
import com.nullsolutions.mlxapp.utils.Constants
import com.nullsolutions.mlxapp.utils.LoadingDialog
import com.nullsolutions.mlxapp.utils.SessionManager
import com.nullsolutions.mlxapp.utils.SessionManager.set
import com.nullsolutions.mlxapp.utils.hideKeyboard
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding get() = _binding!!
    lateinit var pref: SharedPreferences
    lateinit var profile: LoginData
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)
        pref = SessionManager.sessionPrefs(requireContext())

        val gson = Gson()
        profile = gson.fromJson(
            pref.getString(Constants.SharedPref.USER_PROFILE, ""),
            LoginData::class.java
        )

        binding.etName.setText(profile.name)
        binding.etEmail.setText(profile.email)
        binding.etAddress.setText(profile.address)

        binding.etEmail.setOnClickListener {
            hideKeyboard()
            Toast.makeText(requireContext(), "Not allowed to edit", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            pref[Constants.SharedPref.IS_LOGIN] = false
            requireActivity().finish()
            Toast.makeText(requireContext(), "Logout Successful", Toast.LENGTH_SHORT).show()
        }

        binding.ivClose.setOnClickListener {
            Navigation.findNavController(binding.root).navigateUp()
        }

        binding.btnUpdate.setOnClickListener {
            hideKeyboard()
            val name = binding.etName.text.toString()
            val address = binding.etAddress.text.toString()

            if (name.isEmpty()) {
                binding.etName.error = "Name required"
                return@setOnClickListener
            }

            performApiCall(ApiRequest("updateprofile", arrayListOf(profile.id!!, name, address)))

        }
    }


    private fun performApiCall(payload: ApiRequest) {
        val api = RetrofitHelper.getInstance().create(APIInterface::class.java)
        LoadingDialog.showProgress(requireActivity())
        api.updateProfile(payload)
            .enqueue(object : Callback<UpdateResponse> {
                override fun onResponse(
                    call: Call<UpdateResponse>,
                    response: Response<UpdateResponse>
                ) {
                    LoadingDialog.dismissProgress()

                    if (response.isSuccessful) {

                        if (response.body()!!.status!!) {
                            profile.address = binding.etAddress.text.toString()
                            profile.name = binding.etName.text.toString()
                            val gson = Gson()
                            pref[Constants.SharedPref.USER_PROFILE] = gson.toJson(profile)
                            Toasty.success(
                                requireContext(),
                                "${response.body()!!.msg}",
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        } else {
                            Toasty.error(
                                requireContext(),
                                "${response.body()!!.msg}",
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                    } else {
                        Toasty.error(
                            requireContext(),
                            "Update Failed. Please try again.",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                }

                override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                    LoadingDialog.dismissProgress()
                    Toasty.error(
                        requireContext(),
                        "Something went wrong. Please try again",
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }

            })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}