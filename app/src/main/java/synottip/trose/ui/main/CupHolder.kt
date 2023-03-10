package synottip.trose.ui.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import synottip.trose.R
import synottip.trose.data.ItemWorldCup

class CupHolder (inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_history, parent, false)) {

    private var flag: ImageView? = null
    private var year: TextView? = null
    private var country: TextView? = null

    init {
        flag = itemView.findViewById(R.id.flagItem)
        year = itemView.findViewById(R.id.year)
        country = itemView.findViewById(R.id.country)

    }
    @SuppressLint("SetTextI18n")
    fun bind(item: ItemWorldCup) {
        flag?.setImageResource(item.flag)
        year?.text = "championship year: ${item.year}"
        country?.text = "host country: ${item.country}"
    }

}