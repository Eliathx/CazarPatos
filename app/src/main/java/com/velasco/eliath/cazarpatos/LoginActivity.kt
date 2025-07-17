package com.velasco.eliath.cazarpatos
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    lateinit var manejadorArchivo: FileHandler
    lateinit var editTextEmail: EditText
    lateinit var editTextPassword: EditText
    lateinit var buttonLogin: Button
    lateinit var buttonNewUser: Button
    lateinit var checkBoxRecordarme: CheckBox
    lateinit var mediaPlayer: MediaPlayer
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        //Inicialización de variables
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonNewUser = findViewById(R.id.buttonNewUser)
        checkBoxRecordarme = findViewById(R.id.checkBoxRecordarme)
        // Initialize Firebase Auth
        auth = Firebase.auth
        leerDatosDePreferencias()

        //Eventos clic
        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val clave = editTextPassword.text.toString()
            //Validaciones de datos requeridos y formatos
            if (!validateRequiredData())
                return@setOnClickListener
            guardarDatosEnPreferenias()
            //Si pasa validación de datos requeridos, ir a pantalla principal
            //val intent = Intent(this, MainActivity::class.java)
            //intent.putExtra(EXTRA_LOGIN, email)
            //startActivity(intent)
            //finish()
            AutenticarUsuario(email, clave)
        }

        buttonNewUser.setOnClickListener {

        }

        mediaPlayer = MediaPlayer.create(this, R.raw.title_screen)
        mediaPlayer.start()
    }

    fun AutenticarUsuario(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(EXTRA_LOGIN, "signInWithEmail:success")
                    //Si pasa validación de datos requeridos, ir a pantalla principal
                    val intencion = Intent(this, MainActivity::class.java)
                    intencion.putExtra(EXTRA_LOGIN, auth.currentUser!!.email)
                    startActivity(intencion)
                    //finish()
                } else {
                    Log.w(EXTRA_LOGIN, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, task.exception!!.message,
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun guardarDatosEnPreferenias() {
        val email = editTextEmail.text.toString()
        val clave = editTextPassword.text.toString()
        val listadoAGrabar: Pair<String, String>
        if (checkBoxRecordarme.isChecked) {
            listadoAGrabar = email to clave
        } else {
            listadoAGrabar = "" to ""
        }
        manejadorArchivo = SharedPreferencesManager(this)
        manejadorArchivo.SaveInformation(listadoAGrabar)
        manejadorArchivo = EncryptedSharedPreferencesManager(this)
        manejadorArchivo.SaveInformation(listadoAGrabar)
        manejadorArchivo = FileInternalManager(this)
        manejadorArchivo.SaveInformation(listadoAGrabar)
        manejadorArchivo = FileExternalManager(this)
        manejadorArchivo.SaveInformation(listadoAGrabar)
    }

    private fun leerDatosDePreferencias() {
        var listadoLeido: Pair<String, String>
        manejadorArchivo = SharedPreferencesManager(this)
        listadoLeido = manejadorArchivo.ReadInformation()
        if (listadoLeido.first.isNotBlank()) {
            checkBoxRecordarme.isChecked = true
        }
        editTextEmail.setText(listadoLeido.first)
        editTextPassword.setText(listadoLeido.second)

        manejadorArchivo = EncryptedSharedPreferencesManager(this)
        listadoLeido = manejadorArchivo.ReadInformation()
        if (listadoLeido.first.isNotBlank()) {
            checkBoxRecordarme.isChecked = true
        }
        editTextEmail.setText(listadoLeido.first)
        editTextPassword.setText(listadoLeido.second)

        manejadorArchivo = FileInternalManager(this)
        listadoLeido = manejadorArchivo.ReadInformation()
        if (listadoLeido.first.isNotBlank()) {
            checkBoxRecordarme.isChecked = true
        }
        editTextEmail.setText(listadoLeido.first)
        editTextPassword.setText(listadoLeido.second)

        manejadorArchivo = FileExternalManager(this)
        listadoLeido = manejadorArchivo.ReadInformation()
        if (listadoLeido.first.isNotBlank()) {
            checkBoxRecordarme.isChecked = true
        }
        editTextEmail.setText(listadoLeido.first)
        editTextPassword.setText(listadoLeido.second)
    }

    private fun validateRequiredData(): Boolean {
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()
        if (email.isEmpty()) {
            editTextEmail.setError(getString(R.string.error_email_required))
            editTextEmail.requestFocus()
            return false
        }
        if (password.isEmpty()) {
            editTextPassword.setError(getString(R.string.error_password_required))
            editTextPassword.requestFocus()
            return false
        }
        if (password.length < MIN_PASSWORD_LENGTH) {
            editTextPassword.setError(getString(R.string.error_password_min_length))
            editTextPassword.requestFocus()
            return false
        }
        return true
    }

    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }
}
