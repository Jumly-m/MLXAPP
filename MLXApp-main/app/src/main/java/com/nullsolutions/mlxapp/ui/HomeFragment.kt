package com.nullsolutions.mlxapp.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.webkit.URLUtil
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.nullsolutions.mlxapp.R
import com.nullsolutions.mlxapp.api.*
import com.nullsolutions.mlxapp.databinding.FragmentHomeBinding
import com.nullsolutions.mlxapp.databinding.FragmentLoginBinding
import com.nullsolutions.mlxapp.utils.Constants
import com.nullsolutions.mlxapp.utils.LoadingDialog
import com.nullsolutions.mlxapp.utils.SessionManager
import com.nullsolutions.mlxapp.utils.SessionManager.set
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!
    private lateinit var pref: SharedPreferences
    private lateinit var profile: LoginData
    private lateinit var codeScanner: CodeScanner

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        pref = SessionManager.sessionPrefs(requireContext())

        val gson = Gson()
        profile = gson.fromJson(
            pref.getString(Constants.SharedPref.USER_PROFILE, ""), LoginData::class.java
        )
        binding.tvName.text = "Welcome\n${profile.name}"
        binding.tvCoin.text = "Coin: ${profile.coin}"


        binding.profile.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
        }

        binding.refer.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(HomeFragmentDirections.actionHomeFragmentToReferralFragment())
        }

        binding.withdraw.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(HomeFragmentDirections.actionHomeFragmentToWithdrawFragment())
        }

        binding.log.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(HomeFragmentDirections.actionHomeFragmentToScanLogFragment())
        }

        codeScanner = CodeScanner(requireContext(), binding.scannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = arrayListOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.decodeCallback = DecodeCallback {
            requireActivity().runOnUiThread {
                val sku = it.text
                if (URLUtil.isValidUrl(sku) && sku.contains("mlxcoin")) {
                    performApiCallAddLog(
                        ApiRequest(
                            "addlog",
                            arrayListOf(profile.id.toString(), sku)
                        )
                    )
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Scan a valid qrcode",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        codeScanner.errorCallback = ErrorCallback {
            requireActivity().runOnUiThread {
                Toast.makeText(
                    requireContext(),
                    "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
        if (Constants.callBalanceApi) {
            performApiCall(ApiRequest("balance", arrayListOf(profile.id!!)))
        }
    }


    private fun performApiCallAddLog(payload: ApiRequest) {
        val api = RetrofitHelper.getInstance().create(APIInterface::class.java)
        LoadingDialog.showProgress(requireActivity())
        api.addLog(payload).enqueue(object : Callback<UpdateResponse> {
            override fun onResponse(
                call: Call<UpdateResponse>, response: Response<UpdateResponse>
            ) {
                LoadingDialog.dismissProgress()

                if (response.isSuccessful) {
                    if (response.body()!!.status!!) {
                        Toasty.success(
                            requireContext(),
                            "${response.body()!!.msg}",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                        Constants.callBalanceApi = true
                    }
                }

                Navigation.findNavController(binding.root).navigate(
                    HomeFragmentDirections.actionHomeFragmentToWebviewFragment(payload.reqdata[1])
                )
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

    private fun performApiCall(payload: ApiRequest) {
        val api = RetrofitHelper.getInstance().create(APIInterface::class.java)
        LoadingDialog.showProgress(requireActivity())
        api.getBalanceUpdate(payload).enqueue(object : Callback<GetBalanceResponse> {
            override fun onResponse(
                call: Call<GetBalanceResponse>, response: Response<GetBalanceResponse>
            ) {
                LoadingDialog.dismissProgress()

                if (response.isSuccessful) {
                    if (response.body()!!.status!!) {
                        binding.tvCoin.text = "Coin: ${response.body()!!.coin}"
                        Toasty.success(
                            requireContext(),
                            "${response.body()!!.msg}",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                        val gson = Gson()
                        profile.coin = response.body()!!.coin
                        pref[Constants.SharedPref.USER_PROFILE] = gson.toJson(profile)
                        Constants.callBalanceApi = false
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
                        "Balance Update Failed. Please try again.",
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
            }

            override fun onFailure(call: Call<GetBalanceResponse>, t: Throwable) {
                LoadingDialog.dismissProgress()
                t.printStackTrace()
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