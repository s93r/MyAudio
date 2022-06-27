package it.uninsubria.pdm.rizzi.myaudio

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_recording_list.*
import kotlinx.android.synthetic.main.player_bottom_sheet.*
import java.io.File
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecordingListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecordingListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var list_view: RecyclerView? = null

    private var media: MediaPlayer? = null
    private var isPlaying: Boolean = false
    private var myFile: File? = null

    private var btn_play: ImageView? = null
    private var txt_status: TextView? = null
    private var txt_name: TextView? = null

    private var seekbar: SeekBar? = null
    private var seekbarHandler: Handler? = null
    private var seekbarRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recording_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list_view = rv_recording_list

        btn_play = iv_play_btn
        txt_status = tv_player_status
        txt_name = tv_audio_playing


        seekbar = seek_bar

        val path = activity?.getExternalFilesDir(null)?.absolutePath
        val directory = File(path)
        val allFiles = (directory.listFiles())

        val myAdapter = RecordingRecyclerAdapter(allFiles)
        list_view?.setHasFixedSize(true)
        list_view?.layoutManager = LinearLayoutManager(context)
        list_view?.adapter = myAdapter

        myAdapter.setOnItemClickListener(object : RecordingRecyclerAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                //val str = allFiles.get(position).name
                //Toast.makeText(context, "$position : $str",Toast.LENGTH_LONG).show()

                myFile = allFiles.get(position)

                if (isPlaying) {
                    stopAudio()
                    playAudio(myFile)
                } else {
                    playAudio(myFile)
                }

                //
                val btn_share = iv_share_btn
                btn_share.setOnClickListener {
                    if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        //val uri = Uri.parse(myFile.toString())
                        val path = FileProvider.getUriForFile(context!!, "it.uninsubria.pdm.rizzi.myaudio.fileprovider", myFile!!)
                        val intent = Intent()
                        intent.action = Intent.ACTION_SEND
                        intent.putExtra(Intent.EXTRA_STREAM, path)
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        intent.type = "audio/3gpp"
                        startActivity(Intent.createChooser(intent,"share audio file"))
                    }
                }
                //

            }
        })

        btn_play?.setOnClickListener {
            if (isPlaying) {
                pauseAudio()
            } else if (myFile != null) {
                resumeAudio()
            }
        }



/*
        seekbar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                TODO("Not yet implemented")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                if (myFile != null) {
                    pauseAudio()
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (myFile != null) {
                    val progress = seekBar?.progress
                    progress?.let { media?.seekTo(it) }
                    resumeAudio()
                }

            }

        })

 */

    }

    private fun pauseAudio() {
        media?.pause()
        btn_play?.setImageDrawable(activity?.resources?.getDrawable(R.drawable.ic_btn_play, null))
        isPlaying = false
    }

    private fun resumeAudio() {
        media?.start()
        btn_play?.setImageDrawable(activity?.resources?.getDrawable(R.drawable.ic_btn_pause, null))
        isPlaying = true
    }

    private fun stopAudio() {

        btn_play?.setImageDrawable(activity?.resources?.getDrawable(R.drawable.ic_btn_play, null))
        txt_status?.text = "Playing"

        isPlaying = false
        media?.stop()
    }

    private fun playAudio(myFile: File?) {

        media = MediaPlayer()



        try {
            media!!.setDataSource(myFile?.absolutePath)
            media!!.prepare()
            media!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        btn_play?.setImageDrawable(activity?.resources?.getDrawable(R.drawable.ic_btn_pause, null))
        txt_name?.text = myFile?.name
        txt_status?.text = "Playing"

        isPlaying = true

        media?.setOnCompletionListener {
            stopAudio()
            txt_status?.text = "Finished"
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecordingListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecordingListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}