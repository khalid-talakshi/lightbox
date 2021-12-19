import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import views.ActionBar
import views.ImageCanvas
import views.StatusBar

class Main: Application() {

    override fun start(stage: Stage) {
        val model = Model()


        stage.minHeight = 600.0
        stage.minWidth = 800.0
        stage.maxHeight = 1200.0
        stage.maxWidth = 1600.0

        stage.width = 800.0
        stage.height = 600.0
        stage.title = "LightBox"

        stage.widthProperty().addListener { _, _, _ ->
            if (model.getViewMode() == ViewState.TILE) {
                model.enableTileMode()
            }
        }

        val statusBar = StatusBar(model)
        val actionBar = ActionBar(model, stage)
        val imageCanvas = ImageCanvas(model, stage)
        val borderPane = BorderPane()
        borderPane.top = actionBar
        borderPane.center = imageCanvas
        borderPane.bottom = statusBar
        val scene = Scene(borderPane)
        stage.scene = scene
        stage.show()
    }
}