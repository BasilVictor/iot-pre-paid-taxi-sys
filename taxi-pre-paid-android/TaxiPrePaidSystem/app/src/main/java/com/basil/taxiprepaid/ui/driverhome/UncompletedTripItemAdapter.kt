package com.basil.taxiprepaid.ui.driverhome

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

class UncompletedTripItemAdapter(val context: Context,
                                 private val uncompletedTripList: List<UncompletedTripDetails>,
                                 val onTripMarkedCompleteListener: OnTripMarkedCompleteListener) :
    RecyclerView.Adapter<UncompletedTripItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UncompletedTripItemAdapter.ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_uncompleted_trip,
                parent,
                false
            ), onTripMarkedCompleteListener
        )
    }

    override fun onBindViewHolder(holder: UncompletedTripItemAdapter.ViewHolder, position: Int) {
        holder.bind(uncompletedTripList[position])
    }

    override fun getItemCount() = uncompletedTripList.size

    inner class ViewHolder(private val binding: ItemUncompletedTripBinding,
                           private val onTripMarkedCompleteListener: OnTripMarkedCompleteListener) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        fun bind(
            uncompletedTripDetails: UncompletedTripDetails
        ) {
            binding.executePendingBindings()
            binding.uncompletedTrip = uncompletedTripDetails

            binding.call.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:"+uncompletedTripDetails.passengerContact)
                context.startActivity(intent);
            }

            binding.location.setOnClickListener{
                val uri: String = "https://maps.google.co.in/maps?q="+uncompletedTripDetails.destination
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri.replace(' ', '+')))
                context.startActivity(intent)
            }

            binding.completeTrip.setOnClickListener(this)
            /*{
                MaterialDialog(context).show {
                    customView(R.layout.otp_dialog)
                    cancelable(false)
                    cancelOnTouchOutside(false)
                    positiveButton(R.string.complete_trip) {
                        dismiss()
                    }
                    negativeButton(R.string.cancel){ dismiss() }
                }
            }*/
        }

        override fun onClick(view: View?) {
            onTripMarkedCompleteListener.onTripMarkedCompleted(adapterPosition)
        }
    }

}

interface OnTripMarkedCompleteListener {
    fun onTripMarkedCompleted(position: Int)
}
