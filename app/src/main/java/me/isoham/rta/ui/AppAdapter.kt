package me.isoham.rta.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.isoham.rta.R
import me.isoham.rta.model.AppInfo

class AppAdapter(
    private var allApps: List<AppInfo>,
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
        holder.itemView.setOnLongClickListener {
            val context = it.context

            if (app.packageName == context.packageName) {
                return@setOnLongClickListener true
            }
            val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE).apply {
                data = Uri.parse("package:${app.packageName}")
                addCategory(Intent.CATEGORY_DEFAULT)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(intent)
            true
        }
    }

    override fun getItemCount(): Int = visibleApps.size

    fun filter(query: String) {
        val query = query.trim().lowercase()

        visibleApps =
            if (query.isBlank()) {
                allApps
            } else {
                val startsWith = mutableListOf<AppInfo>()
                val contains = mutableListOf<AppInfo>()

                for (app in allApps) {
                    val name = app.name.lowercase()
                    when {
                        name.startsWith(query) -> startsWith.add(app)
                        name.contains(query) -> contains.add(app)
                    }
                }

                startsWith + contains
            }

        // notifyDataSetChanged is intentional: full list filter, no animations
        notifyDataSetChanged()
    }

    fun getTopApp(): AppInfo? {
        return visibleApps.firstOrNull()
    }

    fun updateApps(newApps: List<AppInfo>) {
        visibleApps = newApps
        allApps = newApps
        notifyDataSetChanged()
    }
}