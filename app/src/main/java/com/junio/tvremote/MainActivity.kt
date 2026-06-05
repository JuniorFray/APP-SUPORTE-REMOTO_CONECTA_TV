package com.junio.tvremote

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var titleView: TextView
    private lateinit var statusView: TextView
    private lateinit var instructionsView: TextView
    private lateinit var primaryButton: Button
    private lateinit var secondaryButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val padding = 60

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(padding, padding, padding, padding)
        }

        titleView = TextView(this).apply {
            text = "Suporte Remoto - Conecta TV"
            textSize = 24f
        }

        statusView = TextView(this).apply {
            textSize = 20f
            setPadding(0, 30, 0, 20)
        }

        instructionsView = TextView(this).apply {
            textSize = 18f
            setLineSpacing(12f, 1.1f)
            setPadding(0, 0, 0, 30)
        }

        primaryButton = Button(this)
        secondaryButton = Button(this)

        layout.addView(titleView)
        layout.addView(statusView)
        layout.addView(instructionsView)
        layout.addView(primaryButton)
        layout.addView(secondaryButton)

        setContentView(layout)
        updateUi()
    }

    override fun onResume() {
        super.onResume()
        updateUi()
    }

    private fun updateUi() {
        val accessibilityEnabled = isAccessibilityServiceEnabled()
        val captureEnabled = ScreenCaptureStore.hasPermission()

        when {
            !accessibilityEnabled -> {
                statusView.text = "Passo 1 de 2"
                instructionsView.text =
                    "1. Aperte o botão abaixo.\n" +
                    "2. Vai abrir a tela de acessibilidade.\n" +
                    "3. Entre em Suporte Remoto - Conecta TV.\n" +
                    "4. Deixe em On.\n" +
                    "5. Depois aperte Voltar para voltar ao app."

                primaryButton.text = "Abrir acessibilidade"
                primaryButton.setOnClickListener {
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
                primaryButton.visibility = View.VISIBLE

                secondaryButton.text = "Atualizar tela"
                secondaryButton.setOnClickListener { updateUi() }
                secondaryButton.visibility = View.VISIBLE
            }

            !captureEnabled -> {
                statusView.text = "Passo 2 de 2"
                instructionsView.text =
                    "A acessibilidade já está ligada.\n\n" +
                    "Agora vamos liberar a captura da tela.\n" +
                    "1. Aperte o botão abaixo.\n" +
                    "2. Na próxima janela, escolha Permitir.\n" +
                    "3. Depois você volta para esta tela sozinho."

                primaryButton.text = "Permitir captura da tela"
                primaryButton.setOnClickListener {
                    startActivity(Intent(this, ScreenCaptureActivity::class.java))
                }
                primaryButton.visibility = View.VISIBLE

                secondaryButton.text = "Abrir acessibilidade novamente"
                secondaryButton.setOnClickListener {
                    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
                secondaryButton.visibility = View.VISIBLE
            }

            else -> {
                statusView.text = "Tudo pronto"
                instructionsView.text =
                    "A acessibilidade está ligada.\n" +
                    "A captura da tela foi permitida.\n\n" +
                    "Seu app já pode continuar o próximo passo do controle remoto."

                primaryButton.text = "Iniciar captura novamente"
                primaryButton.setOnClickListener {
                    startActivity(Intent(this, ScreenCaptureActivity::class.java))
                }
                primaryButton.visibility = View.VISIBLE

                secondaryButton.visibility = View.GONE
            }
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val expectedComponent =
            ComponentName(this, RemoteAccessibilityService::class.java).flattenToString()

        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val splitter = TextUtils.SimpleStringSplitter(':')
        splitter.setString(enabledServices)

        while (splitter.hasNext()) {
            val service = splitter.next()
            if (service.equals(expectedComponent, ignoreCase = true)) {
                return true
            }
        }

        return false
    }
}
