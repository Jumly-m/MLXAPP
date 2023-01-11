package com.nullsolutions.mlxapp.ui

import android.content.SharedPreferences
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
import com.nullsolutions.mlxapp.databinding.FragmentLoginBinding
import com.nullsolutions.mlxapp.utils.*
import com.nullsolutions.mlxapp.utils.SessionManager.set
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding get() = _binding!!
    lateinit var pref:SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)
        pref = SessionManager.sessionPrefs(requireContext())
        val isLogin = pref.getBoolean(Constants.SharedPref.IS_LOGIN, false)
        if (isLogin) {
            Navigation.findNavController(binding.root)
                .navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
        }

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            hideKeyboard()

            if (email.isEmpty()) {
                binding.etEmail.error = "Email address required"
                return@setOnClickListener
            }

            if (!email.isValidEmail()) {
                binding.etEmail.error = "Provide valid email address"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.etPassword.error = "Password required"
                return@setOnClickListener
            }

            Constants.callBalanceApi = false

            performApiCall(ApiRequest("login", arrayListOf(email, password)))

        }

        binding.txtSignUp.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment())
        }

        binding.tvForgetPassword.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(LoginFragmentDirections.actionLoginFragmentToForgetPasswordFragment())
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
                            val gson = Gson()
                            val loginResponse = gson.toJson(response.body()!!.data)

                            pref[Constants.SharedPref.IS_LOGIN] = true
                            pref[Constants.SharedPref.USER_PROFILE] = loginResponse

                            Navigation.findNavController(binding.root)
                                .navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
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
                            "Login Failed. Please try again.",
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