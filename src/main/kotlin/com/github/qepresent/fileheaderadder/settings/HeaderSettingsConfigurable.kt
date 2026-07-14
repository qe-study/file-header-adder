package com.github.qepresent.fileheaderadder.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.util.NlsContexts
import javax.swing.JComponent
// 实现了Configurable接口。成为IDE设置系统的一个页面控制器。
class HeaderSettingsConfigurable: Configurable {
    private var settingsComponent: HeaderSettingsComponent?=null
    // IDE 左侧设置列表里显示的名字
    override fun getDisplayName(): @NlsContexts.ConfigurableName String {
        return "File Header"
    }
    /*
    判断用户修改了设置
    1. 从磁盘读取已保存的配置
    2. 从UI输入框读取当前值
    3. 比较任意字段是否相同，如果有不同，返回true
    3.IDE根据返回值决定是否点亮Apply按钮
     */
    override fun isModified(): Boolean {
        val settings= HeaderSettings.getInstance()
        val component=settingsComponent?:return false
        return component.author!=settings.author||
                component.company!=settings.company||
                component.dateFormat!=settings.dateFormat

    }
    /*
    创建ui界面
     1.当点击File Header设置页面时候，IDE调用在这个方法
     2. 实例化表单组件HeaderSettingsComponent
     3。 返回panel
     */
    override fun createComponent(): JComponent {
        settingsComponent = HeaderSettingsComponent()
        return settingsComponent!!.panel
    }
    /*
    保存设置到磁盘
    当用户点击Apply。IDE调用这个方法
    把输入框里面的值写入到HeaderSettings的对象
    HeaderSettings自动序列化到FileHeaderSettings.xml文件，下次IDE读取
     */
    override fun apply() {
        val settings= HeaderSettings.getInstance()
        val component = settingsComponent?:return
        settings.author=component.author
        settings.company=component.company
        settings.dateFormat=component.dateFormat
    }
    /*
    重置UI为已保存的值
    1 当点击Cancel或者打开设置页面，IDE会调用这个方法
    2 把磁盘保存的值填到输入框
     */
    override fun reset() {
        val settings = HeaderSettings.getInstance()
        val component = settingsComponent?:return
        component.author=settings.author
        component.company=settings.company
        component.dateFormat=settings.dateFormat

    }
    /*
    设置页面关闭时释放 UI 组件引用
     */
    override fun disposeUIResources() {
        settingsComponent = null
    }
}