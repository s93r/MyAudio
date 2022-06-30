package it.uninsubria.pdm.rizzi.myaudio

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Chronometer
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_recorder_page.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecorderPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecorderPageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    // Declare GUI variables
    private lateinit var filenameTextView: TextView
    private lateinit var timerChronometer: Chronometer
    private lateinit var recorderButtonImageView: ImageView
    private lateinit var recorderStatusTextView: TextView
    private lateinit var tracklistImageView: ImageView

    // Declare non-GUI variables
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var navController: NavController

    // Other variables
    private var isRecording: Boolean = false
    private var recordingPermission: String = Manifest.permission.RECORD_AUDIO
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
        return inflater.inflate(R.layout.fragment_recorder_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialise GUI elements
        filenameTextView = filename_text_view
        recorderButtonImageView = recorder_button_image_view
        recorderStatusTextView = recorder_status_text_view
        timerChronometer = timer_chronometer
        tracklistImageView = tracklist_image_view

        // Initialise non-GUI elements
        navController = Navigation.findNavController(view)

        // Implement OnClickListener interface
        recorderButtonImageView.setOnClickListener {
            if (checkPermission()) {
                if (isRecording) {
                    stopRecording()
                    recorderButtonImageView.drawable.setTintList(resources.getColorStateList(android.R.color.black, null))
                    recorderStatusTextView.text = "Recorder Status: OFF"
                    isRecording = false
                } else {
                    startRecording()
                    recorderButtonImageView.drawable.setTintList(resources.getColorStateList(android.R.color.holo_red_dark, null))
                    recorderStatusTextView.text = "Recorder Status: ON"
                    isRecording = true
                }
            } else {
                requestPermission()
            }
        }

        tracklistImageView.setOnClickListener {
            navController.navigate(R.id.action_recorderPageFragment_to_recordingListFragment)
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context!!, recordingPermission) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context!!, writingPermission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(activity!!, arrayOf(recordingPermission, writingPermission),0)
    }

    private fun stopRecording() {
        timerChronometer.stop()

        filenameTextView.text = "Recording terminated. File saved to memory."

        mediaRecorder.stop()
        mediaRecorder.release()
    }

    private fun startRecording() {
        timerChronometer.base = SystemClock.elapsedRealtime()
        timerChronometer.start()

        // val path = Environment.getExternalStorageDirectory().absolutePath
        val path = activity?.getExternalFilesDir(null)?.absolutePath
        val formatter: SimpleDateFormat = SimpleDateFormat("yyyy_MM_dd_hh_mm_ss")

        val file = "audio " + formatter.format(Date()) + ".3gp"

        filenameTextView.text = "Recording started. File name: " + file

        mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mediaRecorder.setOutputFile(path+"/"+file)

        try {
            mediaRecorder.prepare()
            mediaRecorder.start()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecorderPageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecorderPageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}