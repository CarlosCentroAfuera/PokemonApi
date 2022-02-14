package com.carlostena.pokemonapi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.security.MessageDigest
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        launchRequest("PokeNoCifrado")
        launchRequestNombreCifrado()

    }

    private fun launchRequestNombreCifrado() {
        launchRequest(cifrar("PekeCifrado","12345678"))
    }

    private fun launchRequest(nombrePokemon: String) {
        val client = OkHttpClient()

        val request = Request.Builder()
        request.url("http://10.0.2.2:8083/pokemonBody")
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = Pokemon(nombrePokemon, 1).toString().toRequestBody(mediaType)
        request.post(requestBody)
        val call = client.newCall(request.build())
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println(e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                println(response.toString())
            }
        })
    }


    private fun cifrar(textoEnString : String, llaveEnString : String) : String {
        println("Voy a cifrar: $textoEnString")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, getKey(llaveEnString))
        val textCifrado = android.util.Base64.encode(cipher.doFinal(textoEnString.toByteArray(Charsets.UTF_8)), android.util.Base64.URL_SAFE).toString()

        println("He obtenido $textCifrado")
        return textCifrado
    }

    @Throws(BadPaddingException::class)
    private fun descifrar(textoCifrrado : String, llaveEnString : String) : String {
        println("Voy a descifrar $textoCifrrado")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, getKey(llaveEnString))
        //val textDescifrado = String(cipher.doFinal(Base64.getUrlDecoder().decode(textoCifrrado)))

        val textDescifrado =  String(cipher.doFinal(android.util.Base64.decode(textoCifrrado, android.util.Base64.URL_SAFE)))
        println("He obtenido $textDescifrado")
        return textDescifrado
    }


    private fun getKey(llaveEnString : String): SecretKeySpec {
        var llaveUtf8 = llaveEnString.toByteArray(Charsets.UTF_8)
        val sha = MessageDigest.getInstance("SHA-1")
        llaveUtf8 = sha.digest(llaveUtf8)
        llaveUtf8 = llaveUtf8.copyOf(16)
        return SecretKeySpec(llaveUtf8, "AES")
    }

}

