package com.basil.taxiprepaid.ui.completedtrips

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.basil.taxiprepaid.R
import com.basil.taxiprepaid.databinding.ItemUncompletedTripBinding
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import android.graphics.ColorSpace.Model
import com.basil.taxiprepaid.databinding.ItemCompletedTripBinding

class CompletedTripItemAdapter(val context: Context,
                                 private val completedTripList: List<CompletedTripDetails>):
    RecyclerView.Adapter<CompletedTripItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompletedTripItemAdapter.ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_completed_trip,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CompletedTripItemAdapter.ViewHolder, position: Int) {
        holder.bind(completedTripList[position])
    }

    override fun getItemCount() = completedTripList.size

    inner class ViewHolder(private val binding: ItemCompletedTripBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            completedTripDetails: CompletedTripDetails
        ) {
            binding.executePendingBindings()
            binding.completedTrip = completedTripDetails
        }
    }

}
