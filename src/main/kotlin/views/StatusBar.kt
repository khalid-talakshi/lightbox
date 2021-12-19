package views

import Model
import NotificationType
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.text.Text
import java.io.File

class StatusBar(private val model: Model): View, HBox() {
    private val actionLabel = Text()
    private val imageCount = Text()
    private val region = Region()
    init {
        setHgrow(region,Priority.ALWAYS)
        val length = model.getImages().size
        imageCount.text = "${model.getImages().size} ${if (length == 1) {"image"} else {"images"}} Loaded"
        children.addAll(actionLabel, region, imageCount)
        model.addView(this)
    }

    override fun update(type: NotificationType, file: File?) {
        if (type == NotificationType.UPDATE_ACTION) {
            actionLabel.text = model.getAction()
        } else if (type == NotificationType.SELECT_IMAGE) {
            val selectedFile = model.getSelectedFile()
            if (selectedFile != null) {
                actionLabel.text = "${model.getSelectedFile()?.name} selected"
            } else {
                actionLabel.text = ""
            }
        }
        val length = model.getImages().size
        imageCount.text = "${model.getImages().size} ${if (length == 1) {"image"} else {"images"}} loaded"
    }
}