package it.uninsubria.pdm.rizzi.myaudio

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_recording_list.*
import kotlinx.android.synthetic.main.player_bottom_sheet.*
import kotlinx.android.synthetic.main.recording_list_item.*
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

    // Declare GUI variables
    private lateinit var btnPlayImageView: ImageView
    private lateinit var listRecyclerView: RecyclerView
    private lateinit var listRecyclerViewAdapter: RecordingRecyclerAdapter
    private lateinit var tracknameTextView: TextView
    private lateinit var trackstatusTextView: TextView

    // Declare non-GUI variables
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var myAudioTrack: File
    private lateinit var myAudioTrackList: Array<File>
    private lateinit var myAudioTrackPath: String

    // Other variables
    private var isPlaying: Boolean = false



    private lateinit var pause: ImageView
    private lateinit var stop: ImageView


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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recording_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialise GUI variables
        btnPlayImageView = play_button_image_view
        listRecyclerView = list_recycler_view
        tracknameTextView = track_name_text_view
        trackstatusTextView = track_status_text_view

        pause = pause_button_image_view
        stop = stop_button_image_view


        seekbar = seek_bar

        // Retrieve audio track file information
        myAudioTrackPath = activity!!.getExternalFilesDir(null)!!.absolutePath
        myAudioTrackList = File(myAudioTrackPath).listFiles() as Array<File>
        // Initialise the recycler view and its personalised adapter
        listRecyclerViewAdapter = RecordingRecyclerAdapter(myAudioTrackList)
        listRecyclerView.setHasFixedSize(true)
        listRecyclerView.layoutManager = LinearLayoutManager(context)
        listRecyclerView.adapter = listRecyclerViewAdapter
        // Implement OnItemClickListener interface
        listRecyclerViewAdapter.setOnItemClickListener(object : RecordingRecyclerAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                myAudioTrack = myAudioTrackList[position]
                /*
                if (isPlaying) {
                    stopAudio()
                    playAudio(myAudioTrack)
                } else {
                    playAudio(myAudioTrack)
                }
                
                 */
                controlSound(myAudioTrack)

                //
                val btn_share = share_button_image_view
                btn_share.setOnClickListener {
                    if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        //val uri = Uri.parse(myFile.toString())
                        val path = FileProvider.getUriForFile(context!!, "it.uninsubria.pdm.rizzi.myaudio.fileprovider", myAudioTrack!!)
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

        /*
        btnPlayImageView.setOnClickListener {
            if (isPlaying) {
                pauseAudio()
            } else if (myAudioTrack != null) {
                resumeAudio()
            }
        }

         */













    }



    private fun controlSound(myAudioTrack: File) {
        mediaPlayer = MediaPlayer.create(context, myAudioTrack.toUri())
        tracknameTextView.text = myAudioTrack.name
        initialiseSeekBar()
        btnPlayImageView.setOnClickListener {
            mediaPlayer.start()
        }
        pause.setOnClickListener {
            mediaPlayer.pause()
        }
        stop.setOnClickListener {
            mediaPlayer.stop()
            //mediaPlayer.reset()
            //mediaPlayer.release()

        }

        seekbar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    mediaPlayer.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                /*
                if (myFile != null) {
                    pauseAudio()
                }

                 */
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                /*
                if (myFile != null) {
                    val progress = seekBar?.progress
                    progress?.let { media?.seekTo(it) }
                    resumeAudio()
                }

                 */
            }

        })

    }



/*
    private fun pauseAudio() {
        mediaPlayer.pause()
        btnPlayImageView.setImageDrawable(activity?.resources?.getDrawable(R.drawable.ic_btn_play, null))
        isPlaying = false
    }

    private fun resumeAudio() {
        mediaPlayer.start()
        btnPlayImageView.setImageDrawable(activity?.resources?.getDrawable(R.drawable.ic_btn_pause, null))
        isPlaying = true
    }

    private fun stopAudio() {

        btnPlayImageView.setImageDrawable(activity?.resources?.getDrawable(R.drawable.ic_btn_play, null))
        trackstatusTextView.text = "Playing"

        isPlaying = false
        mediaPlayer.stop()
    }

    private fun playAudio(file: File?) {

        mediaPlayer = MediaPlayer()

        //initialiseSeekBar()



        try {
            mediaPlayer.setDataSource(file?.absolutePath)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        btnPlayImageView.setImageDrawable(activity?.resources?.getDrawable(R.drawable.ic_btn_pause, null))
        tracknameTextView.text = file?.name
        trackstatusTextView.text = "Playing"

        isPlaying = true

        mediaPlayer.setOnCompletionListener {
            stopAudio()
            trackstatusTextView.text = "Finished"
        }

    }

 */

    private fun initialiseSeekBar() {
        seekbar?.max = mediaPlayer!!.duration
        val handler = Handler()
        handler.postDelayed(object : Runnable{
            override fun run() {
                seekbar?.progress = mediaPlayer!!.currentPosition
                handler.postDelayed(this, 1000)

            }

        }, 0)
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