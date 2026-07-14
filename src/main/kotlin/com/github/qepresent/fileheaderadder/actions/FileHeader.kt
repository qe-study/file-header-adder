package com.github.qepresent.fileheaderadder.actions

import com.github.qepresent.fileheaderadder.settings.HeaderSettings
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiFile
import java.text.SimpleDateFormat
import java.util.Date

class FileHeader : AnAction(){
    override fun update(e: AnActionEvent) {
        // 只在文本编辑器里显示这个右键菜单
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = editor != null
    }
    override fun actionPerformed(e: AnActionEvent) {
        // 获取项目
        val project=e.getData(CommonDataKeys.PROJECT)
        // 获取编辑器
        val editor=e.getData(CommonDataKeys.EDITOR)?:return
        // 获取文件
        val psiFile=e.getData(CommonDataKeys.PSI_FILE)?:return
        val header=getHeader(psiFile)
        WriteCommandAction.runWriteCommandAction(project) {
            val offset=calculateOffset(editor.document)
            editor.document.insertString(offset,header)

        }
    }
    private fun getHeader(psiFile: PsiFile):String{
        val settings= HeaderSettings.getInstance()
        val fileName=psiFile.name
        val date= SimpleDateFormat(settings.dateFormat).format(Date())
        return when(psiFile.language.displayName){
            "Java","Kotlin","Groovy"->generateJavaStyle(settings,fileName,date)
            else -> generateGenericStyle(settings,fileName,date)
        }
    }
    private fun generateJavaStyle(settings: HeaderSettings,fileName: String,date: String):String{
        return buildString{
            appendLine("/**")
            appendLine("author ${settings.author }")
            if(settings.company.isNotEmpty())appendLine("company ${settings.company}")
            appendLine("date $date")
            appendLine("file $fileName")
            appendLine("*/")
            appendLine()

        }
    }
    private fun generateGenericStyle(s: HeaderSettings, fileName: String, date: String): String {
        return buildString {
            appendLine("/*")
            appendLine(" * Author:    ${s.author}")
            if (s.company.isNotEmpty()) appendLine(" * Company:   ${s.company}")
            appendLine(" * Date:      $date")
            appendLine(" * File:      $fileName")
            appendLine(" */")
            appendLine()
        }
    }
    private fun calculateOffset(document: Document): Int{
        val text = document.text
        if(text.startsWith("#!")){
            val firstNewLine=text.indexOf('\n')
            if(firstNewLine!=-1){
                return  firstNewLine + 1
            }
        }
        return 0

    }


}