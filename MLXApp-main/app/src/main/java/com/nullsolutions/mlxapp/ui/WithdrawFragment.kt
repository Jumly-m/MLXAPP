package com.nullsolutions.mlxapp.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.gson.Gson
import com.nullsolutions.mlxapp.R
import com.nullsolutions.mlxapp.adapter.GenericViewBindingAdapter
import com.nullsolutions.mlxapp.adapter.WithdrawViewHolder
import com.nullsolutions.mlxapp.api.*
import com.nullsolutions.mlxapp.databinding.FragmentWithdrawBinding
import com.nullsolutions.mlxapp.databinding.SingleBinding
import com.nullsolutions.mlxapp.utils.Constants
import com.nullsolutions.mlxapp.utils.LoadingDialog
import com.nullsolutions.mlxapp.utils.SessionManager
import com.nullsolutions.mlxapp.utils.hideKeyboard
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class WithdrawFragment : Fragment(R.layout.fragment_withdraw) {
    private var _binding: FragmentWithdrawBinding? = null
    private val binding: FragmentWithdrawBinding get() = _binding!!
    lateinit var pref: SharedPreferences
    private lateinit var withdrawAdapter: GenericViewBindingAdapter<WithdrawData>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentWithdrawBinding.bind(view)
        pref = SessionManager.sessionPrefs(requireContext())

        val gson = Gson()
        val profile = gson.fromJson(
            pref.getString(Constants.SharedPref.USER_PROFILE, ""), LoginData::class.java
        )

        binding.ivClose.setOnClickListener {
            Navigation.findNavController(binding.root).navigateUp()
        }

        binding.btnSignUp.setOnClickListener {
            hideKeyboard()
            val coin = binding.etCoin.text.toString()
            val address = binding.etAddress.text.toString()

            if (coin.isEmpty()) {
                binding.etCoin.error = "Amount of coin is required"
                return@setOnClickListener
            }

            if (address.isEmpty()) {
                binding.etCoin.error = "Address required"
                return@setOnClickListener
            }

            showConfirmDialog(
                ApiRequest(
                    "withdrawreq",
                    arrayListOf(profile.id!!, profile.name!!, address, coin)
                )
            )
        }

        withdrawAdapter = object : GenericViewBindingAdapter<WithdrawData>() {
            override val bindingInflater: (LayoutInflater, ViewGroup?, Int) -> ViewBinding
                get() = { inflater, parent, _ ->
                    SingleBinding.inflate(inflater, parent, false)
                }

            override fun getLayoutId(position: Int, obj: WithdrawData): Int = R.layout.single

            override fun getViewHolder(binding: ViewBinding): RecyclerView.ViewHolder {
                return when (binding) {
                    is SingleBinding -> WithdrawViewHolder(binding)
                    else -> throw IllegalArgumentException("Unknown ViewBinding")
                }
            }
        }


        binding.recyclerView.apply {
            adapter = withdrawAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        performApiCallForList(
            ApiRequest(
                "getwithdrawreq",
                arrayListOf(profile.id!!)
            )
        )

    }


    private fun showConfirmDialog(apiRequest: ApiRequest) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Warning!!")
        builder.setMessage("Are you sure want to make withdraw request?\nYour withdrawal may take up to 48 hours to be processed.")
        builder.setPositiveButton(android.R.string.yes) { dialog, _ ->
            dialog.dismiss()
            performApiCall(apiRequest)
        }
        builder.setNegativeButton(android.R.string.no) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun performApiCallForList(payload: ApiRequest) {
        val api = RetrofitHelper.getInstance().create(APIInterface::class.java)
        api.withdrawRequestLog(payload)
            .enqueue(object : Callback<WithdrawResponse> {
                override fun onResponse(
                    call: Call<WithdrawResponse>,
                    response: Response<WithdrawResponse>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()!!.status!!) {
                            withdrawAdapter.setItems(response.body()!!.data)
                        }
                    }
                }

                override fun onFailure(call: Call<WithdrawResponse>, t: Throwable) {
                }

            })
    }


    private fun performApiCall(payload: ApiRequest) {
        val api = RetrofitHelper.getInstance().create(APIInterface::class.java)
        LoadingDialog.showProgress(requireActivity())
        api.withdrawRequest(payload)
            .enqueue(object : Callback<UpdateResponse> {
                override fun onResponse(
                    call: Call<UpdateResponse>,
                    response: Response<UpdateResponse>
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
                            Navigation.findNavController(binding.root).navigateUp()
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
                            "Request Failed. Please try again.",
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