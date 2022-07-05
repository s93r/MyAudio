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

    private lateinit var myListener: OnItemClickListener

    private var time = CreationTime()

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        myListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordingViewHolder {
        return RecordingViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recording_list_item, parent, false), myListener)
    }

    override fun onBindViewHolder(holder: RecordingViewHolder, position: Int) {
        holder.name.text = allFiles?.get(position)?.name
        holder.time.text = allFiles?.get(position)?.lastModified()?.let { time.timeOfCreation(it) }
    }

    override fun getItemCount(): Int {
        return allFiles!!.size
    }

    class RecordingViewHolder(itemView: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {

        val image: ImageView = itemView.iv_audio_track
        val name: TextView = itemView.tv_track_name
        val time: TextView = itemView.tv_last_modified

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}