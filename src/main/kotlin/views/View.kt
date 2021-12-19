package views

import NotificationType
import java.io.File

interface View {
    fun update(type: NotificationType, file: File?)
}