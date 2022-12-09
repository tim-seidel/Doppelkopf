package de.timseidel.doppelkopf.ui.sessionhistory

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.timseidel.doppelkopf.databinding.FragmentSessionHistoryBinding
import de.timseidel.doppelkopf.ui.session.SessionCreationActivity

class SessionHistoryFragment : Fragment() {

    private var _binding: FragmentSessionHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val sessionHistoryVM =
            ViewModelProvider(this)[SessionHistoryViewModel::class.java]

        _binding = FragmentSessionHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val gotoSessionCreationButton: FloatingActionButton = binding.fabAddSession

        gotoSessionCreationButton.setOnClickListener {
            val intent = Intent(context, SessionCreationActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}