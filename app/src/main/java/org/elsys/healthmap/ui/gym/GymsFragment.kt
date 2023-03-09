package org.elsys.healthmap.ui.gym

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import org.elsys.healthmap.R
import org.elsys.healthmap.activities.AuthenticationActivity
import org.elsys.healthmap.databinding.FragmentGymsBinding
import org.elsys.healthmap.models.Gym
import org.elsys.healthmap.ui.viewmodels.GymsViewModel
import java.util.*

class GymsFragment : Fragment() {
    private val viewModel: GymsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentGymsBinding.inflate(inflater, container, false)
        val gyms = viewModel.gyms
        val addGymButton = binding.addGymButton

        addGymButton.setOnClickListener {
            // FIXME this looks a bit fishy, normally the ViewModel should have all the knowledge
            //  on how to create a new gym (meaning you should only call createGym()), then once
            //  the gym has been created the UI should be notified and the user redirected
            viewModel.viewModelScope.launch {
                val gym = Gym()
                val id = UUID.randomUUID().toString()
                viewModel.createGym(id, gym)
                val action = GymsFragmentDirections.actionGymsFragmentToGymEditFragment(id)
                findNavController().navigate(action)
            }
        }

        val recyclerView = binding.gymsRecyclerView
        val adapter = GymAdapter(gyms, requireContext().cacheDir, viewModel) {
            val builder = AlertDialog.Builder(context)

            builder.apply {
                // FIXME avoid hardcoding strings in the app, use the platform string resources
                setPositiveButton("Yes") { _, _ ->
                    viewModel.viewModelScope.launch {
                        viewModel.deleteGym(it)
                    }
                }
                setNegativeButton("No") { _, _ ->

                }
            }
            // FIXME typo in the text - shore
            builder.setMessage("Are you shore that you want to delete that gym?")
            builder.create().show()
        }
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(false)

        gyms.observe(viewLifecycleOwner) {
            // FIXME check DiffUtil
            adapter.notifyDataSetChanged()
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // FIXME There is a new way to create menus with a MenuProvider, you might check it if you
        //  have time
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionLogOut -> {
                startActivity(Intent(context, AuthenticationActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}