package synottip.trose.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import synottip.trose.data.ItemWorldCup

class CupAdapter(
    private val list: List<ItemWorldCup>,
    private val onClickListener: OnStateClickListener
) :
    RecyclerView.Adapter<CupHolder>() {
    interface OnStateClickListener {
        fun onStateClick(item: ItemWorldCup, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CupHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CupHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: CupHolder, position: Int) {
        val item: ItemWorldCup = list[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            onClickListener.onStateClick(item, position)
        }
    }

    override fun getItemCount(): Int = list.size

}