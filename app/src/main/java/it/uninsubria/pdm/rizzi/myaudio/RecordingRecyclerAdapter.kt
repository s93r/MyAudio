package it.uninsubria.pdm.rizzi.myaudio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recording_list_item.view.*
import java.io.File

class RecordingRecyclerAdapter(var allFiles: Array<File>?) : RecyclerView.Adapter<RecordingRecyclerAdapter.RecordingViewHolder>() {

    val time = CreationTime()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordingViewHolder {
        return RecordingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recording_list_item, parent,false))
    }

    override fun onBindViewHolder(holder: RecordingViewHolder, position: Int) {
        holder.name.text = allFiles?.get(position)?.name
        holder.time.text = allFiles?.get(position)?.lastModified()?.let { time.timeOfCreation(it) }
    }

    override fun getItemCount(): Int {

        return allFiles!!.size
    }

    class RecordingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var image: ImageView = itemView.iv_audio_track
        var name: TextView = itemView.tv_track_name
        var time: TextView = itemView.tv_last_modified

    }
}