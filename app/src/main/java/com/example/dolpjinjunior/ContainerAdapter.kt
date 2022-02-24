package com.example.dolpjinjunior

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dolpjinjunior.utils.Container

class ContainerAdapter(val items : MutableList<Container>, val context : Context):
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.items_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.containerId.text = items[position].getContainerId()
        holder.containerSize.text = items[position].getContainerSize().toString()
        holder.containerType.text = items[position].getContainerType()
        holder.containerDamageLevel.text = items[position].getContainerDamageLv()
        holder.containerEorDate.text = items[position].getContainerDateStart()
        holder.containerFinishDate.text = items[position].getContainerDateFinish()
        holder.containerAnnot.text = items[position].getContainerLateDay().toString()
    }

}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val containerId : TextView = view.findViewById(R.id.container_id_table)
    val containerSize : TextView = view.findViewById(R.id.container_size_table)
    val containerType : TextView = view.findViewById(R.id.container_type_table)
    val containerDamageLevel : TextView = view.findViewById(R.id.container_damage_level)
    val containerEorDate : TextView = view.findViewById(R.id.container_eor_table)
    val containerFinishDate : TextView = view.findViewById(R.id.container_finish_table)
    val containerAnnot : TextView = view.findViewById(R.id.container_annotation_table)
}
