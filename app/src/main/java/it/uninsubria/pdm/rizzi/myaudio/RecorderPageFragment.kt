package it.uninsubria.pdm.rizzi.myaudio

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.os.SystemClock.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    private var recorder: MediaRecorder? = null
    //val recorder: MediaRecorder = MediaRecorder()

    private var timer: Chronometer? = null
    private var text: TextView? = null

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
        return inflater.inflate(R.layout.fragment_recorder_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val controller: NavController = Navigation.findNavController(view)

        val rec_btn: Button = btn_rec
        val list_img: ImageView = iv_playlist
        timer = chronometer
        text = tv_filename


        var isRecording: Boolean = false
        rec_btn.setOnClickListener {
            if (isRecording) {
                stopRecording()
                rec_btn.backgroundTintList = resources.getColorStateList(android.R.color.holo_blue_light, null)
                rec_btn.text = "REC: OFF"
                isRecording = false
            } else {
                if (hasPermission()) {
                    startRecording()
                    rec_btn.backgroundTintList = resources.getColorStateList(android.R.color.holo_red_light, null)
                    rec_btn.text = "REC: ON"
                    isRecording = true
                }
            }
        }
        list_img.setOnClickListener {
            controller.navigate(R.id.action_recorderPageFragment_to_recordingListFragment)
        }
    }

    private fun stopRecording() {
        timer?.stop()

        text?.text = "Recording terminated. File saved to memory."

        recorder?.stop()
        recorder?.release()
    }

    private fun startRecording() {
        timer?.base = SystemClock.elapsedRealtime()
        timer?.start()

        val path = Environment.getExternalStorageDirectory().absolutePath
        //val path = activity?.getExternalFilesDir(null)?.absolutePath
        val formatter: SimpleDateFormat = SimpleDateFormat("yyyy_MM_dd_hh_mm_ss")

        val file = "audio " + formatter.format(Date()) + ".3gp"

        text?.text = "Recording started. File name: " + file

        recorder = MediaRecorder()
        recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder?.setOutputFile(path+"/"+file)

        try {
            recorder?.prepare()
            recorder?.start()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun hasPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true
        } else {
            val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(activity!!, permissions,0)
            //ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.RECORD_AUDIO), 17)
            return false
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