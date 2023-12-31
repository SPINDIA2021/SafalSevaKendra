package com.satmatgroup.safalsevakendra.dmt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.satmatgroup.safalsevakendra.R
import com.satmatgroup.safalsevakendra.model.UserModel
import com.satmatgroup.safalsevakendra.utils.AppPrefs
import java.util.*

class DmtCommisionSlabadapter(
    context: Context?,
    rechargeHistoryModalList: ArrayList<DmtCommissionSlabModel>
) :
    RecyclerView.Adapter<DmtCommisionSlabadapter.ViewHolder>() {
    private var rechargeHistoryModalList: List<DmtCommissionSlabModel>
    private val mInflater: LayoutInflater
    private var mContext: Context? = null
    lateinit var userModel: UserModel

    var user_type: String = ""
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        mContext = parent.context
        user_type = AppPrefs.getStringPref("user_type", mContext).toString()

        val gson = Gson()
        val json = AppPrefs.getStringPref("userModel", mContext)
        userModel = gson.fromJson(json, UserModel::class.java)

        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem: View =
            layoutInflater.inflate(R.layout.layout_list_commisionslab_dmt, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val rechargeHistoryModal: DmtCommissionSlabModel =
            rechargeHistoryModalList[position]

        holder.tvSlab.text = ""+rechargeHistoryModal.slab
        holder.tvType.text = ""+rechargeHistoryModal.type


        if(userModel.cus_type.equals("retailer")) {
            holder.tvCommission.text = ""+rechargeHistoryModal.retailer_comm
        } else if (userModel.cus_type.equals("distributor")) {
            holder.tvCommission.text = ""+rechargeHistoryModal.distributor_comm
        } else if (userModel.cus_type.equals("master")) {
            holder.tvCommission.text = ""+rechargeHistoryModal.master_comm
        }

    }

    override fun getItemCount(): Int {
        return rechargeHistoryModalList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // var ivStatus: ImageView
        var tvSlab: TextView
        var tvCommission: TextView
        var tvType: TextView


        init {
            tvSlab = itemView.findViewById(R.id.tvSlab)
            tvCommission = itemView.findViewById(R.id.tvCommission)
            tvType = itemView.findViewById(R.id.tvType)

//            ivStatus = itemView.findViewById(R.id.ivStatus)
        }
    }

    fun filterList(filterdNames: ArrayList<DmtCommissionSlabModel>) {
        rechargeHistoryModalList = filterdNames
        notifyDataSetChanged()
    }

    companion object {
        const val imgUrl = " http://edigitalvillage.in/assets/operator_img/"
    }

    // RecyclerView recyclerView;
    init {
        mInflater = LayoutInflater.from(context)
        this.rechargeHistoryModalList = rechargeHistoryModalList
    }
}