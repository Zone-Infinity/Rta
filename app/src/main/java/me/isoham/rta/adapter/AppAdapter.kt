package me.isoham.rta.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.isoham.rta.R
import me.isoham.rta.data.AppInfo

class AppAdapter(
    private var allApps: List<AppInfo>,
    private val hiddenApps: Set<String>,
    private val favoriteApps: Set<String>,
    private val onClick: (AppInfo) -> Unit,
    private val onLongClick: (AppInfo) -> Unit,
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
        holder.itemView.setOnClickListener { onClick(app) }
        holder.itemView.setOnLongClickListener {
            onLongClick(app)
            true
        }
    }

    override fun getItemCount(): Int = visibleApps.size

    /** Call ONLY when app list changes (install / uninstall / hide) */
    fun updateApps(newApps: List<AppInfo>) {
        allApps = newApps.filterNot { hiddenApps.contains(it.packageName) }
        visibleApps = allApps
        notifyDataSetChanged()
    }

    /** Call ONLY when query changes */
    fun filter(rawQuery: String) {
        val query = rawQuery.trim().lowercase()

        visibleApps =
            if (query.isBlank()) {
                val favs = mutableListOf<AppInfo>()
                val rest = mutableListOf<AppInfo>()

                for (app in allApps) {
                    if (favoriteApps.contains(app.packageName)) favs.add(app)
                    else rest.add(app)
                }
                favs + rest
            } else {
                val starts = mutableListOf<AppInfo>()
                val contains = mutableListOf<AppInfo>()

                for (app in allApps) {
                    val name = app.name.lowercase()
                    when {
                        name.startsWith(query) -> starts.add(app)
                        name.contains(query) -> contains.add(app)
                    }
                }
                starts + contains
            }

        notifyDataSetChanged()
    }

    fun getTopApp(): AppInfo? = visibleApps.firstOrNull()
}