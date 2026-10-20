package chingiz.simulationform.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import chingiz.simulationform.presentation.form.FormRoute
import chingiz.simulationform.presentation.image.ImageDetailRoute
import kotlinx.serialization.Serializable

@Serializable
data object FormDestination

@Serializable
data class ImageDetailDestination(val imageId: Long) {
    companion object {
        /** Must match the property name above; used to read the argument from SavedStateHandle. */
        const val ARG_IMAGE_ID = "imageId"
    }
}

/** Single-activity navigation graph. */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = FormDestination) {
        composable<FormDestination> {
            FormRoute(onOpenImage = { id -> navController.navigate(ImageDetailDestination(id)) })
        }
        composable<ImageDetailDestination> {
            ImageDetailRoute(onBack = { navController.popBackStack() })
        }
    }
}
