package com.nullsolutions.mlxapp.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.nullsolutions.mlxapp.BuildConfig
import com.nullsolutions.mlxapp.R
import com.nullsolutions.mlxapp.api.*
import com.nullsolutions.mlxapp.databinding.FragmentReferralBinding
import com.nullsolutions.mlxapp.utils.Constants
import com.nullsolutions.mlxapp.utils.LoadingDialog
import com.nullsolutions.mlxapp.utils.SessionManager
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ReferralFragment : Fragment(R.layout.fragment_referral) {

    private var _binding: FragmentReferralBinding? = null
    private val binding: FragmentReferralBinding get() = _binding!!
    lateinit var pref: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReferralBinding.bind(view)
        pref = SessionManager.sessionPrefs(requireContext())

        val gson = Gson()
        val profile = gson.fromJson(
            pref.getString(Constants.SharedPref.USER_PROFILE, ""), LoginData::class.java
        )

        binding.btnShare.setOnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Use code: ${binding.tvReferral.text} and earn coin\nDownload app from: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID
            )
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }

        binding.ivClose.setOnClickListener {
            Navigation.findNavController(binding.root).navigateUp()
        }

        if (Constants.referralCode.isEmpty()) {
            performApiCall(ApiRequest("referral", arrayListOf(profile.id.toString())))
        } else {
            binding.tvReferral.text = Constants.referralCode
        }
    }


    private fun performApiCall(payload: ApiRequest) {
        val api = RetrofitHelper.getInstance().create(APIInterface::class.java)
        LoadingDialog.showProgress(requireActivity())
        api.getReferralCode(payload).enqueue(object : Callback<ReferralResponse> {
            override fun onResponse(
                call: Call<ReferralResponse>, response: Response<ReferralResponse>
            ) {
                LoadingDialog.dismissProgress()

                if (response.isSuccessful) {

                    if (response.body()!!.status!!) {
                        binding.tvReferral.text = response.body()!!.code
                        Constants.referralCode = response.body()!!.code.toString()
                    } else {
                        Toasty.error(
                            requireContext(),
                            "Get referral code failed. Please try again.",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                } else {
                    Toasty.error(
                        requireContext(),
                        "Get referral code failed. Please try again.",
                        Toast.LENGTH_SHORT,
                        true
                    ).show()
                }
            }

            override fun onFailure(call: Call<ReferralResponse>, t: Throwable) {
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