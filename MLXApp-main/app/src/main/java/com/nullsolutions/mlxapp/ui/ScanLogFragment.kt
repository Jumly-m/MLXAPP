package com.nullsolutions.mlxapp.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.gson.Gson
import com.nullsolutions.mlxapp.R
import com.nullsolutions.mlxapp.adapter.GenericViewBindingAdapter
import com.nullsolutions.mlxapp.adapter.HistoryViewHolder
import com.nullsolutions.mlxapp.adapter.WithdrawViewHolder
import com.nullsolutions.mlxapp.api.*
import com.nullsolutions.mlxapp.databinding.FragmentHomeBinding
import com.nullsolutions.mlxapp.databinding.FragmentScanLogBinding
import com.nullsolutions.mlxapp.databinding.SingleBinding
import com.nullsolutions.mlxapp.utils.Constants
import com.nullsolutions.mlxapp.utils.LoadingDialog
import com.nullsolutions.mlxapp.utils.SessionManager
import es.dmoral.toasty.Toasty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ScanLogFragment : Fragment(R.layout.fragment_scan_log) {

    private var _binding: FragmentScanLogBinding? = null
    private val binding: FragmentScanLogBinding get() = _binding!!
    private lateinit var pref: SharedPreferences
    private lateinit var profile: LoginData
    private lateinit var logAdapter: GenericViewBindingAdapter<LogData>
    var data: ArrayList<LogData> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentScanLogBinding.bind(view)
        pref = SessionManager.sessionPrefs(requireContext())

        val gson = Gson()
        profile = gson.fromJson(
            pref.getString(Constants.SharedPref.USER_PROFILE, ""), LoginData::class.java
        )


        logAdapter = object : GenericViewBindingAdapter<LogData>() {
            override val bindingInflater: (LayoutInflater, ViewGroup?, Int) -> ViewBinding
                get() = { inflater, parent, _ ->
                    SingleBinding.inflate(inflater, parent, false)
                }

            override fun getLayoutId(position: Int, obj: LogData): Int = R.layout.single

            override fun getViewHolder(binding: ViewBinding): RecyclerView.ViewHolder {
                return when (binding) {
                    is SingleBinding -> HistoryViewHolder(binding, onItemClick = {
                        Navigation.findNavController(binding.root).navigate(
                            ScanLogFragmentDirections.actionScanLogFragmentToWebviewFragment(it.scandata.toString())
                        )
                    })
                    else -> throw IllegalArgumentException("Unknown ViewBinding")
                }
            }
        }

        binding.ivClose.setOnClickListener {
            Navigation.findNavController(binding.root).navigateUp()
        }

        binding.recyclerView.apply {
            adapter = logAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        if (data.isEmpty()) {
            performApiCallForList(
                ApiRequest(
                    "scanlog",
                    arrayListOf(profile.id!!)
                )
            )
        } else {
            logAdapter.setItems(data)
        }
    }


    private fun performApiCallForList(payload: ApiRequest) {
        val api = RetrofitHelper.getInstance().create(APIInterface::class.java)
        LoadingDialog.showProgress(requireActivity())
        api.getLog(payload)
            .enqueue(object : Callback<LogResponse> {
                override fun onResponse(
                    call: Call<LogResponse>,
                    response: Response<LogResponse>
                ) {
                    LoadingDialog.dismissProgress()
                    if (response.isSuccessful) {
                        if (response.body()!!.status!!) {
                            data = response.body()!!.data
                            logAdapter.setItems(response.body()!!.data)
                        } else {
                            Toasty.error(
                                requireContext(),
                                "No log found.",
                                Toast.LENGTH_SHORT,
                                true
                            ).show()
                        }
                    } else {
                        Toasty.error(
                            requireContext(),
                            "Fetch log failed.",
                            Toast.LENGTH_SHORT,
                            true
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LogResponse>, t: Throwable) {
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