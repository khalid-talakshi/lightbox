package views

import Model
import NotificationType
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File

class ActionBar(model: Model, stage: Stage): ToolBar(), View {
    private val model: Model
    private val stage: Stage
    private val fileChooser = FileChooser()
    private val imageFilter = FileChooser.ExtensionFilter(
        "Images (*.jpg, *.jpeg, *.png, *.bmp)",
        "*.jpg", "*.jpeg", "*.png", "*.bmp"
    )

    private val newImageButton = Button("Add Image")
    private val deleteImageButton = Button("Delete Button")
    private val scaleUpImageButton = Button("Zoom In")
    private val scaleDownImageButton = Button("Zoom Out")
    private val rotateLeftButton = Button("Rotate Left")
    private val rotateRightButton = Button("Rotate Right")
    private val resetButton = Button("Reset")

    private val tileModeButton = ToggleButton("Tile Mode")
    private val cascadeModeButton = ToggleButton("Cascade Mode")
    private val toggleGrouping = ToggleGroup()

    private val separator = Separator()

    private fun createIcon(icon: String): ImageView {
        val iconImage = ImageView(Image(icon))
        iconImage.fitHeight = 20.0
        iconImage.isPreserveRatio = true
        return iconImage
    }

    init {
        this.model = model
        this.stage = stage

        fileChooser.extensionFilters.addAll(imageFilter)
        fileChooser.selectedExtensionFilter = imageFilter
        fileChooser.initialDirectory = File(System.getProperty("user.dir"))

        newImageButton.setOnMouseClicked {
            val result = fileChooser.showOpenMultipleDialog(stage)
            if (result != null) {
                val loadingText = "Loading Images..."
                model.setAction(loadingText)
                val numFiles = result.size
                if (numFiles > 1) {
                    model.addImages(result)
                } else {
                    model.addImage(result[0])
                }
                val actionText =
                    "Added $numFiles new ${if (numFiles > 1) {"images"} else {"image"}}"
                model.setAction(actionText)
            }
        }
        newImageButton.graphic = createIcon("ios-add.png")

        deleteImageButton.setOnMouseClicked {
            model.deleteSelectedFile()
        }
        deleteImageButton.isDisable = model.getSelectedFile() == null
        deleteImageButton.graphic = createIcon("ios-trash.png")

        scaleUpImageButton.setOnMouseClicked {
            model.scaleUpSelectedFile()
        }
        scaleUpImageButton.isDisable = model.getSelectedFile() == null
        scaleUpImageButton.graphic = createIcon("zoom-in.png")

        scaleDownImageButton.setOnMouseClicked {
            model.scaleDownSelectedFile()
        }
        scaleDownImageButton.isDisable = model.getSelectedFile() == null
        scaleDownImageButton.graphic = createIcon("magnifying-glass.png")

        rotateLeftButton.setOnMouseClicked {
            model.rotateSelectedFileLeft()
        }
        rotateLeftButton.isDisable = model.getSelectedFile() == null
        rotateLeftButton.graphic = createIcon("rotate-left.png")

        rotateRightButton.setOnMouseClicked {
            model.rotateSelectedFileRight()
        }
        rotateRightButton.isDisable = model.getSelectedFile() == null
        rotateRightButton.graphic = createIcon("rotate-right.png")

        resetButton.setOnMouseClicked {
            model.resetImage()
        }
        resetButton.graphic = createIcon("reset.png")

        tileModeButton.setOnMouseClicked {
            model.enableTileMode()
        }
        tileModeButton.graphic = createIcon("tile.png")

        cascadeModeButton.setOnMouseClicked {
            model.enableCascadeMode()
        }
        cascadeModeButton.graphic = createIcon("stack.png")

        toggleGrouping.toggles.addAll(tileModeButton, cascadeModeButton)
        toggleGrouping.selectToggle(cascadeModeButton)

        items.addAll(
            newImageButton,
            deleteImageButton,
            scaleUpImageButton,
            scaleDownImageButton,
            rotateLeftButton,
            rotateRightButton,
            resetButton,
            separator,
            tileModeButton,
            cascadeModeButton
        )
        model.addView(this)
    }

    override fun update(type: NotificationType, file: File?) {
        if (type == NotificationType.SELECT_IMAGE) {
            deleteImageButton.isDisable = model.getSelectedFile() == null
            scaleUpImageButton.isDisable = model.getSelectedFile() == null || model.getViewMode() == ViewState.TILE
            scaleDownImageButton.isDisable = model.getSelectedFile() == null || model.getViewMode() == ViewState.TILE
            rotateLeftButton.isDisable = model.getSelectedFile() == null || model.getViewMode() == ViewState.TILE
            rotateRightButton.isDisable = model.getSelectedFile() == null || model.getViewMode() == ViewState.TILE
            resetButton.isDisable = model.getSelectedFile() == null || model.getViewMode() == ViewState.TILE
        }
        if (type == NotificationType.CASCADE_MODE) {
            toggleGrouping.selectToggle(cascadeModeButton)
        }
        if (type == NotificationType.TILE_MODE) {
            toggleGrouping.selectToggle(tileModeButton)
        }
    }

}