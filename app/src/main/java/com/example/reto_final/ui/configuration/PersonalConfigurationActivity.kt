package com.example.reto_final.ui.configuration

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.reto_final.R
import com.example.reto_final.data.model.LoginUser
import com.example.reto_final.databinding.PersonalConfigurationActvityBinding
import com.example.reto_final.ui.group.GroupActivity
import com.example.reto_final.utils.MyApp
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class PersonalConfigurationActivity: AppCompatActivity() {

    private lateinit var binding: PersonalConfigurationActvityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PersonalConfigurationActvityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textViewNumberPage.setText(R.string.paso_1_2)

        val user = MyApp.userPreferences.getUser()
        if(user != null) {
            setData(user)
        }

        binding.next.setOnClickListener {
            if (user != null) {
                if (checkData(user)) nextConfiguration()
//                nextConfiguration()
            }
        }

        binding.back.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("¿Deseas continuar?")
            builder.setMessage("Se perderán las modificaciones realizadas")

            builder.setPositiveButton("Continuar") { _, _ ->
                backToGroupActivity()
            }
            builder.setNegativeButton("Cancelar", null)

            val dialog = builder.create()
            dialog.show()
        }

        binding.profilePicture.setOnClickListener { pickPhoto() }

    }

    private fun pickPhoto() {
        val options = arrayOf<CharSequence>("Tomar foto", "Elegir de la galería", "Cancelar")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Elige una opción")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> takePhotoFromCamera()
                1 -> pickPhotoFromGallery()
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun takePhotoFromCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, 1)
    }

    private fun pickPhotoFromGallery() {
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {

                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
                    startActivityForResult(intent, 2)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }

                override fun onPermissionRationaleShouldBeShown(p0: PermissionRequest?, p1: PermissionToken?) {
                }

            }).check()
    }

    private fun setData(loginUser: LoginUser) {
        binding.name.setText(loginUser.name)
        binding.surname.setText(loginUser.surname)
        binding.address.setText(loginUser.address)
        binding.mobilePhoneNumber.setText(loginUser.phone_number1.toString())
        binding.phoneNumber.setText(loginUser.phone_number2.toString())
        binding.dni.setText(loginUser.DNI)
    }

    private fun checkData(loginUser: LoginUser): Boolean {
        val hintColor = ContextCompat.getColor(this, R.color.hint)
        val name = binding.name.text.toString()
        val surname = binding.surname.text.toString()
        val address = binding.address.text.toString()
        val mobilePhoneNumber = binding.mobilePhoneNumber.text.toString()
        val phoneNumber = binding.phoneNumber.text.toString()
        val dni = binding.dni.text.toString()

        if (name.isEmpty() || surname.isEmpty() || address.isEmpty() || mobilePhoneNumber.isEmpty() || dni.isEmpty()) {
            Toast.makeText(this, R.string.toast_empty_2, Toast.LENGTH_LONG).show()
            binding.name.setHintTextColor(Color.RED)
            binding.surname.setHintTextColor(Color.RED)
            binding.address.setHintTextColor(Color.RED)
            binding.mobilePhoneNumber.setHintTextColor(Color.RED)
            binding.dni.setHintTextColor(Color.RED)

            return false
        }

        if (!validatePhone(mobilePhoneNumber)) {
            Toast.makeText(this, R.string.toast_format_mobile_phone, Toast.LENGTH_LONG).show()
            binding.mobilePhoneNumber.setTextColor(Color.RED)

            binding.name.setHintTextColor(hintColor)
            binding.surname.setHintTextColor(hintColor)
            binding.address.setHintTextColor(hintColor)
            binding.phoneNumber.setHintTextColor(hintColor)
            binding.dni.setHintTextColor(hintColor)

            return false

        }
        if (phoneNumber != "" ) {
            if(!validatePhone(phoneNumber)) {
                Toast.makeText(this, R.string.toast_format_landline_phone, Toast.LENGTH_LONG).show()
                binding.phoneNumber.setTextColor(Color.RED)

                binding.name.setHintTextColor(hintColor)
                binding.surname.setHintTextColor(hintColor)
                binding.address.setHintTextColor(hintColor)
                binding.mobilePhoneNumber.setHintTextColor(hintColor)
                binding.dni.setHintTextColor(hintColor)

                return false
            }
        }

        if (!validateDNI(dni)) {

            Toast.makeText(this, R.string.toast_format_DNI, Toast.LENGTH_LONG).show()
            binding.dni.setTextColor(Color.RED)

            binding.name.setHintTextColor(hintColor)
            binding.surname.setHintTextColor(hintColor)
            binding.address.setHintTextColor(hintColor)
            binding.mobilePhoneNumber.setHintTextColor(hintColor)
            binding.phoneNumber.setHintTextColor(hintColor)
            return false
        }

        loginUser.DNI = binding.dni.text.toString()
        loginUser.name = binding.name.text.toString()
        loginUser.surname = binding.surname.text.toString()
        loginUser.phone_number1 = binding.mobilePhoneNumber.text.toString().toInt()
        loginUser.phone_number2 = binding.phoneNumber.text.toString().toInt()
        loginUser.address = binding.address.text.toString()
        MyApp.userPreferences.saveUser(loginUser)

        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

                val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, data?.extras?.get("data") as? Bitmap)
                roundedBitmapDrawable.isCircular = true

                binding.profilePicture.setImageDrawable(roundedBitmapDrawable)

            }else if (requestCode == 2) {

                Glide.with(this)
                    .load(data?.data)
                    .apply(RequestOptions().transform(CircleCrop()))
                    .into(binding.profilePicture)

            }

        }

    }

    private fun validatePhone(cadena: String): Boolean {
        val regex = Regex("\\d{9}")
        return cadena.matches(regex)
    }

    private fun validateDNI(cadena: String): Boolean {
        val regex = Regex("\\d{8}[A-Z]")
        return cadena.matches(regex)
    }

    private fun backToGroupActivity() {
        val intent = Intent(this, GroupActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun nextConfiguration() {
        val intent = Intent(this, ConfigurationActivity::class.java)
        startActivity(intent)
        finish()
    }

}