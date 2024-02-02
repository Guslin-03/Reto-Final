package com.example.reto_final.ui.message

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.widget.Toast
import com.example.reto_final.data.model.message.Message
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class FileManager(private val context: Context) {

    fun convertFileToBase64(filePath: String): String {
        val file = File(filePath)
        if (!file.exists()) {
            Toast.makeText(context, "No existe el archivo en local", Toast.LENGTH_SHORT).show()
            return ""
        }

        try {
            val inputStream: InputStream = FileInputStream(file)
            val buffer = ByteArray(8192)
            val output = ByteArrayOutputStream()

            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
            }

            val byteArray = output.toByteArray()
            return Base64.encodeToString(byteArray, Base64.NO_WRAP)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "No se ha podido convertir el archivo", Toast.LENGTH_SHORT).show()
        }

        return ""
    }
    fun saveBase64ToFile(base64String: String): String {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)

        // Intentar interpretar como imagen
        try {
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            val folder = File(context.getExternalFilesDir(null), "RetoFinalImage")

            if (!folder.exists()) {
                folder.mkdirs()
            }
            val fileName = "imagen_${System.currentTimeMillis()}.png"
            val filePath = File(folder, fileName).absolutePath

            FileOutputStream(filePath).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
            }

            return filePath
        } catch (e: Exception) {
            // Si hay una excepción, es un archivo PDF
            val folder = File(context.getExternalFilesDir(null), "RetoFinalPdf")

            if (!folder.exists()) {
                folder.mkdirs()
            }
            val fileName = "archivo_${System.currentTimeMillis()}.pdf"
            val filePath = File(folder, fileName).absolutePath

            try {
                FileOutputStream(filePath).use { fos ->
                    fos.write(decodedBytes)
                    fos.flush()
                }

                return filePath
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Error al guardar el archivo", Toast.LENGTH_SHORT).show()
            }
        }

        return ""
    }

    fun downloadPDF(message: Message) {
        val path=message.text
        if (path.isNotEmpty()) {
            val pdfFile = File(path)

            // Guarda el PDF en la carpeta de Downloads
            val destinationFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                pdfFile.name
            )
            try {
                FileInputStream(pdfFile).use { input ->
                    FileOutputStream(destinationFile).use { output ->
                        input.copyTo(output)
                    }
                }

                Toast.makeText(context, "PDF descargado correctamente", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Error al descargar el PDF", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Error al obtener la ruta del PDF", Toast.LENGTH_SHORT).show()
        }
    }
     fun saveImageToFolder(bitmap: Bitmap): String {
        val folder = File(context.getExternalFilesDir(null), "RetoFinalImage")

        if (!folder.exists()) {
            folder.mkdirs()
        }

        val fileName = "imagen_${System.currentTimeMillis()}.png"
        val filePath = File(folder, fileName).absolutePath

        try {
            FileOutputStream(filePath).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return filePath
    }

     fun saveFileToFolder(fileUri: Uri): String {
        val folder = File(context.getExternalFilesDir(null), "RetoFinalPdf")

        if (!folder.exists()) {
            folder.mkdirs()
        }

        val fileName = "archivo_${System.currentTimeMillis()}.pdf" // Asegúrate de tener la extensión .pdf
        val filePath = File(folder, fileName).absolutePath

        try {
            context.contentResolver.openInputStream(fileUri)?.use { input ->
                FileOutputStream(filePath).use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                    }
                    output.flush()
                }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

        return filePath
    }
}
