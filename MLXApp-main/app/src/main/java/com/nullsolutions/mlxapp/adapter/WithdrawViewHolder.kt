package com.nullsolutions.mlxapp.adapter

import androidx.recyclerview.widget.RecyclerView
import com.nullsolutions.mlxapp.api.WithdrawData
import com.nullsolutions.mlxapp.databinding.SingleBinding

class WithdrawViewHolder(private val binding: SingleBinding) :
    RecyclerView.ViewHolder(binding.root), GenericViewBindingAdapter.Binder<WithdrawData> {
    override fun bind(data: WithdrawData) {
        binding.textViewTime.text = data.createdAt
        binding.textViewTitle.text = "Amount: ${data.amount} MLXCoin"
        var status = ""
        when (data.status) {
            "0" -> {
                status = "Processing"
            }
            "1" -> {
                status = "Approved"
            }
            "2" -> {
                status = "Rejected"
            }
        }
        binding.textViewDescription.text = "Address: ${data.address}\nStatus: $status"
    }
}