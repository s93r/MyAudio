package it.uninsubria.pdm.rizzi.myaudio

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_recorder_page.*

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
        var isRecording: Boolean = false
        rec_btn.setOnClickListener {
            if (isRecording) {
                rec_btn.backgroundTintList = resources.getColorStateList(android.R.color.holo_blue_light, null)
                rec_btn.text = "REC: OFF"
                isRecording = false
            } else {
                rec_btn.backgroundTintList = resources.getColorStateList(android.R.color.holo_red_light, null)
                rec_btn.text = "REC: ON"
                isRecording = true
            }
        }
        list_img.setOnClickListener {
            controller.navigate(R.id.action_recorderPageFragment_to_recordingListFragment)
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