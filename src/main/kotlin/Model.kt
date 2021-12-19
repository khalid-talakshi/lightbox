import views.View
import java.io.File

class Model {
    private val views = ArrayList<View>()
    private val cascadeHeight = 200.0
    private val cascadeWidth = 200.0

    private var images = ArrayList<File>()
    private var selectedFile: File? = null
    private var viewMode: ViewState = ViewState.CASCADE
    private var actionText = ""
    private var cascadeX = 0.0
    private var cascadeY = 0.0

    private fun notifyObservers(type: NotificationType, file: File? = null) {
        for (observer in views) {
            observer.update(type, file)
        }
    }

    fun getAction() : String {
        return actionText
    }

    fun setAction(text: String) {
        actionText = text
        notifyObservers(NotificationType.UPDATE_ACTION)
    }

    fun addView(view: View) {
        views.add(view)
    }

    fun addImage(image: File) {
        images.add(image)
        notifyObservers(NotificationType.ADD_IMAGE, image)
        if (viewMode == ViewState.TILE) {
            notifyObservers(NotificationType.TILE_MODE)
        }
    }

    fun addImages(images: MutableList<File>) {
        val numFiles = images.size
        val fileOffsetX = cascadeWidth / numFiles
        val fileOffsetY = cascadeHeight / numFiles
        for (file in images) {
            addImage(file)
            cascadeX += fileOffsetX
            cascadeY += fileOffsetY
        }
        cascadeX = 0.0
        cascadeY = 0.0
    }

    fun getImages(): ArrayList<File> {
        return images
    }

    fun getSelectedFile(): File? {
        return selectedFile
    }

    fun setSelectedFile(value: File?) {
        selectedFile = value
        notifyObservers(NotificationType.SELECT_IMAGE, value)
    }

    fun getViewMode(): ViewState {
        return viewMode
    }

    fun deleteSelectedFile() {
        if (selectedFile != null) {
            actionText = "${selectedFile!!.name} was deleted"
            val index = images.indexOf(selectedFile)
            images.removeAt(index)
            notifyObservers(NotificationType.DELETE_IMAGE,  selectedFile)
            notifyObservers(NotificationType.UPDATE_ACTION)
            selectedFile = null
        }
    }

    fun scaleUpSelectedFile() {
        if (selectedFile != null) {
            viewMode = ViewState.CASCADE
            notifyObservers(NotificationType.SCALE_UP_IMAGE, selectedFile)
        }
    }

    fun scaleDownSelectedFile() {
        if (selectedFile != null) {
            viewMode = ViewState.CASCADE
            notifyObservers(NotificationType.SCALE_DOWN_IMAGE, selectedFile)
        }
    }

    fun rotateSelectedFileLeft() {
        if (selectedFile != null) {
            viewMode = ViewState.CASCADE
            notifyObservers(NotificationType.ROTATE_IMAGE_LEFT, selectedFile)
        }
    }

    fun rotateSelectedFileRight() {
        if (selectedFile != null) {
            viewMode = ViewState.CASCADE
            notifyObservers(NotificationType.ROTATE_IMAGE_RIGHT, selectedFile)
        }
    }

    fun enableTileMode() {
        viewMode = ViewState.TILE
        notifyObservers(NotificationType.TILE_MODE)
    }

    fun resetImage() {
        if (selectedFile != null) {
            viewMode = ViewState.CASCADE
            notifyObservers(NotificationType.RESET_IMAGE, selectedFile)
        }
    }

    fun enableCascadeMode() {
        viewMode = ViewState.CASCADE
        notifyObservers(NotificationType.CASCADE_MODE)
    }

    fun moveToFront() {
        if (selectedFile != null) {
            val index = images.indexOf(selectedFile)
            val oldFile = selectedFile
            images.removeAt(index)
            if (oldFile != null) {
                images.add(0, oldFile)
            }
        }
    }

    fun getCascadeX(): Double {
        return cascadeX
    }

    fun getCascadeY(): Double {
        return cascadeY
    }
}