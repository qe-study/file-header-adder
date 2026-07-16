package com.github.qepresent.fileheaderadder.actions

import com.github.qepresent.fileheaderadder.settings.HeaderSettings
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiFile
import java.text.SimpleDateFormat
import java.util.Date

class FileHeader : AnAction() {
    override fun update(e: AnActionEvent) {
        // 只在文本编辑器里显示这个右键菜单
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = editor != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        // 获取项目
        val project = e.getData(CommonDataKeys.PROJECT)
        // 获取编辑器
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        // 获取文件
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return
        val header = generateHeader(psiFile)
        WriteCommandAction.runWriteCommandAction(project) {
            val offset = calculateOffset(editor.document.text)
            editor.document.insertString(offset, header)
        }
    }

    companion object {
        // 根据当前文件的语言挑选对应的文件头风格
        internal fun generateHeader(psiFile: PsiFile): String {
            // 数据
            val settings = HeaderSettings.getInstance()
            val fileName = psiFile.name
            val date = SimpleDateFormat(settings.dateFormat).format(Date())
            return selectHeader(psiFile.language.displayName, settings, fileName, date)
        }

        // 按语言显示名分发：Java/Kotlin/Groovy 用紧凑风格，其余用通用风格。
        // 单独拆成纯函数，方便脱离 PSI 直接测试分发逻辑。
        internal fun selectHeader(
            languageDisplayName: String,
            settings: HeaderSettings,
            fileName: String,
            date: String
        ): String = when (languageDisplayName) {
            "Java", "Kotlin", "Groovy" -> generateJavaStyle(settings, fileName, date)
            else -> generateGenericStyle(settings, fileName, date)
        }

        // Java/Kotlin/Groovy 风格：紧凑的 javadoc 风格块注释
        internal fun generateJavaStyle(settings: HeaderSettings, fileName: String, date: String): String {
            return buildString {
                appendLine("/**")
                appendLine("author ${settings.author}")
                if (settings.company.isNotEmpty()) appendLine("company ${settings.company}")
                appendLine("date $date")
                appendLine("file $fileName")
                appendLine("*/")
                appendLine()
            }
        }

        // 通用风格：带对齐的星号块注释
        internal fun generateGenericStyle(settings: HeaderSettings, fileName: String, date: String): String {
            return buildString {
                appendLine("/*")
                appendLine(" * Author:    ${settings.author}")
                if (settings.company.isNotEmpty()) appendLine(" * Company:   ${settings.company}")
                appendLine(" * Date:      $date")
                appendLine(" * File:      $fileName")
                appendLine(" */")
                appendLine()
            }
        }

        // 计算文件头应该插入的偏移量：跳过开头的 shebang 行（如 #!/bin/bash）后再插入
        internal fun calculateOffset(text: String): Int {
            // 考虑开头
            if (text.startsWith("#!")) {
                val firstNewLine = text.indexOf('\n')
                if (firstNewLine != -1) {
                    return firstNewLine + 1
                }
            }
            return 0
        }
    }
}
