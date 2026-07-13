package com.github.qepresent.fileheaderadder.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.util.NlsContexts
import javax.swing.JComponent

class HeaderSettingsConfigurable: Configurable {
    private var settingsComponent: HeaderSettingsComponent?=null
    override fun getDisplayName(): @NlsContexts.ConfigurableName String? {
        TODO("Not yet implemented")
    }

    override fun isModified(): Boolean {
        TODO("Not yet implemented")
    }

    override fun createComponent(): JComponent? {
        TODO("Not yet implemented")
    }

    override fun apply() {
        val settings= HeaderSettings.getInstance()

    }
}