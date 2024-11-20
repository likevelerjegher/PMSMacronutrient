package com.example.macronutrient.screens.saving

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.macronutrient.R
import com.example.macronutrient.entity.Food
import com.example.macronutrient.entity.Saving

class SavingAdapter(
    private val savings: List<Saving>,
    private val foodDetails: Map<Int, Food>,
    private val onSavingLongPress: (Saving) -> Unit,
    private val onSavingSelected: (Saving) -> Unit
) : RecyclerView.Adapter<SavingAdapter.SavingViewHolder>() {

    class SavingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodInfo: TextView = itemView.findViewById(R.id.foodInfo)
        val calories: TextView = itemView.findViewById(R.id.Calories)
        val carbs: TextView = itemView.findViewById(R.id.Carbs)
        val fats: TextView = itemView.findViewById(R.id.Fats)
        val proteins: TextView = itemView.findViewById(R.id.Proteins)
        val itemLayout: LinearLayout = itemView.findViewById(R.id.itemLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_saving, parent, false)
        return SavingViewHolder(view)
    }

    override fun onBindViewHolder(holder: SavingViewHolder, position: Int) {
        val saving = savings[position]
        val food = foodDetails[saving.foodId]
        holder.foodInfo.text = "${food?.productName}"
        holder.calories.text = "Calories: ${food?.calories}"
        holder.carbs.text = "Carbs: ${food?.carbs}"
        holder.fats.text = "Fats: ${food?.fats}"
        holder.proteins.text = "Proteins: ${food?.proteins}"

        holder.itemView.setOnLongClickListener {
            onSavingLongPress(saving)
            onSavingSelected(saving)
            true
        }
    }

    override fun getItemCount(): Int {
        return savings.size
    }
}