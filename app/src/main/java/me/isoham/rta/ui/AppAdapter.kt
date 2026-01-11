package me.isoham.rta.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.isoham.rta.R
import me.isoham.rta.model.AppInfo

class AppAdapter(
    private val allApps: List<AppInfo>,
    private val onClick: (AppInfo) -> Unit
) : RecyclerView.Adapter<AppAdapter.ViewHolder>() {

    private var visibleApps: List<AppInfo> = allApps

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(R.id.app_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = visibleApps[position]
        holder.text.text = app.name
        holder.itemView.setOnClickListener {
            onClick(app)
        }
    }

    override fun getItemCount(): Int = visibleApps.size

    fun filter(query: String) {
        visibleApps =
            if (query.isBlank()) {
                allApps
            } else {
                allApps.filter {
                    it.name.contains(query, ignoreCase = true)
                }
            }

        // notifyDataSetChanged is intentional: full list filter, no animations
        notifyDataSetChanged()
    }
}