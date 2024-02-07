package com.example.reto_final.ui.user

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.reto_final.R
import com.example.reto_final.data.model.group.Group
import com.example.reto_final.data.model.message.Message
import com.example.reto_final.data.model.user.User
import com.example.reto_final.data.repository.local.group.RoomGroupDataSource
import com.example.reto_final.data.repository.local.user.RoomUserDataSource
import com.example.reto_final.data.repository.remote.RemoteGroupDataSource
import com.example.reto_final.data.repository.remote.RemoteUserDataSource
import com.example.reto_final.databinding.UserFragmentBinding
import com.example.reto_final.ui.group.GroupInfo
import com.example.reto_final.ui.group.GroupViewModel
import com.example.reto_final.ui.group.RoomGroupViewModelFactory
import com.example.reto_final.utils.Resource
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class UserFragment(private val selectedGroup: Group) : DialogFragment() {

    private lateinit var binding: UserFragmentBinding
    private lateinit var userFragmentAdapter: UserFragmentAdapter
    private val userRepository = RoomUserDataSource()
    private val remoteUserRepository = RemoteUserDataSource()
    private val userViewModel: UserViewModel by viewModels { RoomUserViewModelFactory(userRepository,remoteUserRepository,requireContext().applicationContext) }

    private val groupRepository = RoomGroupDataSource()
    private val remoteGroupRepository = RemoteGroupDataSource()
    private val groupViewModel: GroupViewModel by viewModels {
        RoomGroupViewModelFactory(groupRepository,
            remoteGroupRepository,
            requireContext().applicationContext
        ) }
    private var onDismissListener: (() -> Unit)? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomDialogFragmentStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UserFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        userFragmentAdapter = UserFragmentAdapter(
            ::onUserListClickItem
        )
        binding.userList.adapter = userFragmentAdapter
        userViewModel.onUsers()

        userViewModel.users.observe(this) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
                    userFragmentAdapter.submitList(it.data)
                }
                Resource.Status.ERROR -> {
                }
                Resource.Status.LOADING -> {
                }

            }
        }

        groupViewModel.addUserToGroup.observe(this) {
            when(it.status) {
                Resource.Status.SUCCESS -> {
                    Toast.makeText(requireContext().applicationContext, "Usuario añadido con éxito", Toast.LENGTH_LONG).show()
                    dismiss()
                }
                Resource.Status.ERROR -> {
                    Toast.makeText(requireContext().applicationContext, it.message, Toast.LENGTH_LONG).show()
                }
                Resource.Status.LOADING -> {
                }

            }
        }

        binding.filterUser.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val searchText = s.toString().trim()

                val filteredList = userViewModel.users.value?.data?.filter { user ->
                    user.name.contains(searchText, ignoreCase = true)
                }
                userFragmentAdapter.submitList(filteredList)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.closeFragment.setOnClickListener {
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        // Obtener el 75% del alto y ancho de la pantalla
        val height = (resources.displayMetrics.heightPixels * 0.75).toInt()
        val width  = (resources.displayMetrics.widthPixels * 0.75).toInt()
        // Establecer el tamaño del DialogFragment
        dialog?.window?.setLayout(width, height)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()
    }

    fun setOnDismissListener(listener: () -> Unit) {
        onDismissListener = listener
    }

    private fun onUserListClickItem(user : User) {

        if (user.id != null) {
            if (selectedGroup.id != null) {
                val options = arrayOf<CharSequence>("Aceptar", "Cancelar")
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("¿Quieres añadir a ${user.name} ${user.surname} al grupo?")
                builder.setItems(options) { dialog, which ->
                    when (which) {
                        0 -> groupViewModel.onAddUserToGroup(selectedGroup.id!!, user.id!!)
                        1 -> dialog.dismiss()
                    }
                }
                builder.show()
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(user: List<User>) {
        if (selectedGroup.id != null) {
            userViewModel.onUsersGroup(selectedGroup.id!!)
        }
    }

}