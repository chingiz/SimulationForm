package chingiz.simulationform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import chingiz.simulationform.presentation.form.FormRoute
import chingiz.simulationform.ui.theme.SimulationFormTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimulationFormTheme {
                FormRoute(onOpenImage = {})
            }
        }
    }
}
