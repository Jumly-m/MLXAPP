package com.nullsolutions.mlxapp.adapter

import android.text.TextUtils
import android.view.View
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.nullsolutions.mlxapp.api.LogData
import com.nullsolutions.mlxapp.databinding.SingleBinding

class HistoryViewHolder(private val binding: SingleBinding, val onItemClick: (LogData) -> Unit) :
    RecyclerView.ViewHolder(binding.root), GenericViewBindingAdapter.Binder<LogData> {
    override fun bind(data: LogData) {
        binding.textViewTime.text = data.scanat
        binding.textViewTitle.text = data.scandata
        binding.textViewTitle.maxLines = 2
        binding.textViewTitle.ellipsize = TextUtils.TruncateAt.END
        binding.textViewDescription.visibility = View.GONE

        binding.parentLayout.setOnClickListener {
            onItemClick(data)
        }

        binding.textViewTitle.setPadding(0,10,0,0)
    }
}