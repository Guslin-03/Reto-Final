package com.example.reto_final.ui.message

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class FileConverter(private val context: Context) {

    fun convertBitmapToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }
    fun convertFileToBase64(fileUri: Uri): String {
        val inputStream: InputStream? = context.contentResolver.openInputStream(fileUri)
        inputStream?.use { input ->
            val buffer = ByteArray(8192)
            val output = ByteArrayOutputStream()

            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
            }

            val byteArray = output.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }
        return ""
    }
    fun detectFileType(base64String: String): File {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)

        // Verificar la firma del archivo
        val signatureBytes = decodedBytes.copyOfRange(0, 8)
        val signatureHex = bytesToHex(signatureBytes)

        return when {
            signatureHex.startsWith("25504446") -> convertBase64ToPdf(base64String)
            signatureHex.startsWith("89504E470D0A1A0A") || signatureHex.startsWith("FFD8FF") -> convertBase64ToImage(base64String)
            else -> {
                Toast.makeText(context, "No se reconoce el formato del archivo", Toast.LENGTH_SHORT).show()
                File("") // Puedes devolver un objeto File vacío o nulo según tu lógica
            }
        }
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (i in bytes.indices) {
            val v = bytes[i].toInt() and 0xFF
            hexChars[i * 2] = "0123456789ABCDEF"[v ushr 4]
            hexChars[i * 2 + 1] = "0123456789ABCDEF"[v and 0x0F]
        }
        return String(hexChars)
    }

    private fun convertBase64ToImage(base64String: String): File {
        val outputfile = File(Environment.getExternalStorageDirectory(), "resultado")
        try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

            FileOutputStream(outputfile).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return outputfile
    }

    private fun convertBase64ToPdf(base64String: String): File {
        val outputfile = File(Environment.getExternalStorageDirectory(), "resultado")
        try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)

            FileOutputStream(outputfile).use { fos ->
                fos.write(decodedBytes)
                fos.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return outputfile
    }
}
