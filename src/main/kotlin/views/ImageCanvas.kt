package views

import Model
import NotificationType
import javafx.scene.Group
import javafx.scene.control.ScrollPane
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.stage.Stage
import java.io.File
import java.io.FileInputStream
import java.lang.Double.max
import kotlin.math.abs

class ImageCanvas(model: Model, stage: Stage): ScrollPane(), View {
    private val degrees = 10.0
    private val factor = 0.25
    private val setHeight = 200.0
    private val padding = 10.0

    private var model: Model
    private var stage: Stage
    private var imagePane = Pane()
    private var images = Group()
    private var startX: Double = 0.0
    private var startY: Double = 0.0
    private var currentState = ImageState.NONE
    private var fileMap: MutableMap<File, ImageView> = mutableMapOf()
    private var imageClicked = false

    init {
        model.addView(this)
        this.content = imagePane
        this.vbarPolicy = ScrollBarPolicy.AS_NEEDED
        this.hbarPolicy = ScrollBarPolicy.AS_NEEDED
        this.model = model
        this.stage = stage
        this.prefHeight = 900000000000.0
        this.setOnMouseClicked {
            if (!imageClicked) {
                model.setSelectedFile(null)
            }
            imageClicked = false
        }
        imagePane.children.add(images)
    }

    private fun dragImage(image: ImageView, event: MouseEvent) {
        if (currentState == ImageState.DRAG) {
            val dx = event.sceneX - startX
            val dy = event.sceneY - startY
            if (image.boundsInParent.minX + dx >= 0.0 && image.boundsInParent.maxX + dx <= max(imagePane.width, stage.width - 15.0)) {
                image.x += dx
                startX = event.sceneX
            }

            if (image.boundsInParent.minY + dy >= 0.0 && image.boundsInParent.maxY + dy <= max(imagePane.height ,stage.height - 91.0)) {
                image.y += dy
                startY = event.sceneY
            }
            println(imagePane.height)

            model.enableCascadeMode()
        }
    }

    private fun resetImage(image: ImageView) {
        image.rotate = 0.0
        image.scaleX = 1.0
        image.scaleY = 1.0
    }

    private fun tileLayout() {
        for (i in images.children) {
            val image = i as ImageView
            resetImage(image)
        }
        var currentX = 0.0
        var currentY = 0.0
        for (i in images.children) {
            val image = i as ImageView
            val imageWidth = image.boundsInParent.maxX - image.boundsInParent.minX
            if (currentX + imageWidth + padding > stage.width - 15.0) {
                currentX = 0.0
                currentY += setHeight + padding
            }
            image.x = currentX
            image.y = currentY
            currentX += imageWidth + padding
        }
    }

    private fun findImageView(file: File?): ImageView? {
        return if (file != null) {
            val currentFile = fileMap[file]
            val imageView = images.children.indexOf(currentFile)
            images.children[imageView] as ImageView
        } else {
            null
        }
    }

    private fun addImage(currentImage: File) {
        val fileInput = FileInputStream(currentImage)
        val imageToShow = Image(fileInput)
        val imageViewToShow = ImageView(imageToShow)
        imageViewToShow.fitHeight = setHeight
        imageViewToShow.isPreserveRatio = true

        imageViewToShow.setOnMousePressed {
            currentState = ImageState.DRAG
            startX = it.sceneX
            startY = it.sceneY
            model.setSelectedFile(currentImage)
        }

        imageViewToShow.setOnMouseDragged {
            imageViewToShow.toFront()
            model.moveToFront()
            dragImage(imageViewToShow, it)
        }

        imageViewToShow.setOnMouseReleased {
            currentState = ImageState.NONE
        }

        imageViewToShow.setOnMouseClicked {
            imageViewToShow.toFront()
            model.moveToFront()
            model.setSelectedFile(currentImage)
            imageClicked = true
        }

        imageViewToShow.x = model.getCascadeX()
        imageViewToShow.y = model.getCascadeY()
        fileMap[currentImage] = imageViewToShow
        images.children.add(imageViewToShow)
    }

    override fun update(type: NotificationType, file: File?) {
        if (type == NotificationType.ADD_IMAGE) {
            if (file != null) {
                addImage(file)
            }
        } else if (type == NotificationType.DELETE_IMAGE) {
            val imageToDelete = fileMap[file]
            images.children.removeAt(images.children.indexOf(imageToDelete))
            fileMap.remove(file)
            if (model.getViewMode() == ViewState.TILE) {
                tileLayout()
            }
        } else if (type == NotificationType.SCALE_UP_IMAGE) {
            val currentImage = findImageView(file)
            if (currentImage != null) {
                if ((currentImage.scaleX + factor) < 2.0) {
                    currentImage.scaleX += factor
                    currentImage.scaleY += factor
                    if (currentImage.boundsInParent.minX < 0.0) {
                        currentImage.x += abs(currentImage.boundsInParent.minX)
                    }
                    if (currentImage.boundsInParent.maxX > max(stage.width - 15.0, imagePane.width)) {
                        currentImage.x -= abs( max(stage.width - 15.0, imagePane.width) - currentImage.boundsInParent.maxX)
                    }
                    if (currentImage.boundsInParent.minY < 0.0) {
                        currentImage.y += abs(currentImage.boundsInParent.minY)
                    }
                    if (currentImage.boundsInParent.maxY > max(stage.height - 91.0, imagePane.height)) {
                        currentImage.y -= abs( max(stage.height - 91.0, imagePane.height) - currentImage.boundsInParent.maxY)
                    }
                }
            }
        } else if (type == NotificationType.SCALE_DOWN_IMAGE) {
            val currentImage = findImageView(file)
            if (currentImage != null && currentImage.scaleX - factor > 0.25) {
                currentImage.scaleX -= factor
                currentImage.scaleY -= factor
            }
        } else if (type == NotificationType.ROTATE_IMAGE_LEFT) {
            val currentImage = findImageView(file)
            currentImage!!.rotate -= degrees
        } else if (type == NotificationType.ROTATE_IMAGE_RIGHT) {
            val currentImage = findImageView(file)
            currentImage!!.rotate += degrees
        } else if (type == NotificationType.RESET_IMAGE) {
            val currentImage = findImageView(file)
            if (currentImage != null) {
                resetImage(currentImage)
            }
        } else if (type == NotificationType.TILE_MODE) {
            tileLayout()
        }
    }
}

