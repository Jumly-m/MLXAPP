package com.nullsolutions.mlxapp.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.nullsolutions.mlxapp.R
import com.nullsolutions.mlxapp.api.APIInterface
import com.nullsolutions.mlxapp.api.ApiRequest
import com.nullsolutions.mlxapp.api.LoginResponse
import com.nullsolutions.mlxapp.api.RetrofitHelper
import com.nullsolutions.mlxapp.databinding.FragmentForgetPasswordBinding
import com.nullsolutions.mlxapp.databinding.FragmentHomeBinding
import com.nullsolutions.mlxapp.utils.Constants
import com.nullsolutions.mlxapp.utils.LoadingDialog
import com.nullsolutions.mlxapp.utils.SessionManager.set
import com.nullsolutions.mlxapp.utils.hideKeyboard
import com.nullsolutions.mlxapp.utils.isValidEmail
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgetPasswordFragment : Fragment(R.layout.fragment_forget_password) {
    private var _binding: FragmentForgetPasswordBinding? = null
    private val binding: FragmentForgetPasswordBinding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentForgetPasswordBinding.bind(view)


        binding.ivClose.setOnClickListener {
            Navigation.findNavController(binding.root).navigateUp()
        }

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString()

            hideKeyboard()

            if (email.isEmpty()) {
                binding.etEmail.error = "Email address required"
                return@setOnClickListener
            }
            if (!email.isValidEmail()) {
                binding.etEmail.error = "Provide valid email address"
                return@setOnClickListener
            }
            performApiCall(ApiRequest("forgetpassword", arrayListOf(email)))

        }

    }


    private fun performApiCall(payload: ApiRequest) {
        val api = RetrofitHelper.getInstance().create(APIInterface::class.java)
        LoadingDialog.showProgress(requireActivity())
        api.login(payload)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    LoadingDialog.dismissProgress()

                    if (response.isSuccessful) {

                        if (response.body()!!.status!!) {
                            Navigation.findNavController(binding.root)
                                .navigateUp()
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
                            "Password reset failed. Please try again.",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
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