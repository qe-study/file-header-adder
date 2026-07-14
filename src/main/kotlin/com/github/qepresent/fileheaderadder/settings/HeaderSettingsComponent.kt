package com.github.qepresent.fileheaderadder.settings

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JPanel

class HeaderSettingsComponent {
    val panel: JPanel
    private val authorField= JBTextField()
    private val companyField= JBTextField()
    private val dateFormatCombo= ComboBox<String>()
    init {
        val nowFormat=arrayOf(
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "yyyy年MM月dd日",
            "yyyy/MM/dd HH:mm:ss",
        )
        nowFormat.forEach {
            dateFormatCombo.addItem(it)
        }
        panel = FormBuilder.createFormBuilder()
            // 标签文本 实际组件 垂直间距系数 标签位置
            // 垂直间距系数，1 是标准间距，0 表示紧贴上一行
            // 标签位置，false = 标签在左边（水平并排），true = 标签在上方（垂直堆叠）
            .addLabeledComponent("Author", authorField,1,false)
            .addLabeledComponent("Company", companyField,1,false)
            .addLabeledComponent("DateFormat", dateFormatCombo,1,false)
            // 把前面的表单内容顶到面板顶部，下方空白自动填满
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }
    var author: String
        get() = authorField.text
        set(value) {
            authorField.text = value
        }
    var company: String
        get() = companyField.text
         set(value) {
             companyField.text = value
         }
    var dateFormat: String
        get() = dateFormatCombo.selectedItem as?String?:"yyyy-MM-dd"
        set(value) {
            dateFormatCombo.selectedItem = value
        }
}