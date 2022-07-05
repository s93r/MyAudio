package it.uninsubria.pdm.rizzi.myaudio

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_recording_list.*
import kotlinx.android.synthetic.main.player_bottom_sheet.*
import java.io.File

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
    private lateinit var buttonPlay: ImageView
    private lateinit var buttonPause: ImageView
    private lateinit var buttonShare: ImageView
    private lateinit var buttonStop: ImageView
    private lateinit var listRecyclerView: RecyclerView
    private lateinit var listRecyclerViewAdapter: RecordingRecyclerAdapter
    private lateinit var seekbar: SeekBar
    private lateinit var tracknameTextView: TextView
    private lateinit var trackstatusTextView: TextView

    // Declare non-GUI variables
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var myAudioTrack: File
    private lateinit var myAudioTrackList: Array<File>
    private lateinit var myAudioTrackPath: String
    private lateinit var myAudioTrackUri: Uri

    // Other variables
    private var authority: String = "it.uninsubria.pdm.rizzi.myaudio.fileprovider"
    private var handler: Handler = Handler()
    private var intent: Intent = Intent()
    private var readingPermission: String = Manifest.permission.READ_EXTERNAL_STORAGE
    private var writingPermission: String = Manifest.permission.WRITE_EXTERNAL_STORAGE

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
        buttonPlay = play_button_image_view
        buttonPause = pause_button_image_view
        buttonStop = stop_button_image_view
        buttonShare = share_button_image_view
        listRecyclerView = list_recycler_view
        seekbar = seek_bar
        tracknameTextView = track_name_text_view
        trackstatusTextView = track_status_text_view
        // Retrieve audio track file information
        myAudioTrackPath = activity!!.getExternalFilesDir(null)!!.absolutePath
        myAudioTrackList = File(myAudioTrackPath).listFiles() as Array<File>
        // Prepare the recycler view and its adapter
        listRecyclerViewAdapter = RecordingRecyclerAdapter(myAudioTrackList)
        listRecyclerView.setHasFixedSize(true)
        listRecyclerView.layoutManager = LinearLayoutManager(context)
        listRecyclerView.adapter = listRecyclerViewAdapter
        // Implement OnItemClickListener interface
        listRecyclerViewAdapter.setOnItemClickListener(object: RecordingRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                // Prepare the media player and its seekbar
                myAudioTrack = myAudioTrackList[position]
                mediaPlayer = MediaPlayer.create(context, myAudioTrack.toUri())
                tracknameTextView.text = myAudioTrack.name
                trackstatusTextView.text = "NOT PLAYING"
                initialiseSeekBar()
                // Implement OnClickListener interface
                buttonPlay.setOnClickListener {
                    trackstatusTextView.text = "PLAYING"
                    mediaPlayer.start()
                }
                buttonPause.setOnClickListener {
                    trackstatusTextView.text = "PAUSED"
                    mediaPlayer.pause()
                }
                buttonStop.setOnClickListener {
                    trackstatusTextView.text = "STOPPED"
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                }
                buttonShare.setOnClickListener {
                    if (checkSharePermission()) {
                        myAudioTrackUri = FileProvider.getUriForFile(context!!, authority, myAudioTrack)
                        intent.action = Intent.ACTION_SEND
                        intent.putExtra(Intent.EXTRA_STREAM, myAudioTrackUri)
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        intent.type = "audio/3gpp"
                        startActivity(Intent.createChooser(intent,"Share"))
                    } else {
                        requestSharePermission()
                    }
                }
                // Implement OnCompletionListener interface
                mediaPlayer.setOnCompletionListener {
                    trackstatusTextView.text = "FINISHED"
                    mediaPlayer.stop()
                    mediaPlayer.reset()
                }
                // Implement OnSeekBarChangeListener interface
                seekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser)
                            mediaPlayer.seekTo(progress)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }
                })
            }
        })
    }

    private fun checkSharePermission(): Boolean {
        return ContextCompat.checkSelfPermission(context!!, readingPermission) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context!!, writingPermission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestSharePermission() {
        ActivityCompat.requestPermissions(activity!!, arrayOf(readingPermission, writingPermission), 0)
    }

    private fun initialiseSeekBar() {
        seekbar.max = mediaPlayer.duration
        handler.postDelayed(object: Runnable{
            override fun run() {
                seekbar.progress = mediaPlayer.currentPosition
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