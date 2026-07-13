package com.github.qepresent.fileheaderadder.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
// Service 告诉IDE这个类是应用级单例服务。整个IDE里只有一个实例
@Service
/*
State 标记为可持久化状态State 标记为可持久化状态
name 这个状态在内部注册表里的唯一标识名，
storages 指定序列化后的数据存到FileHeaderSettings.xml文件里面。IDE 关闭时自动保存，下次启动时自动读取
 */
@State(
    name = "com.github.qepresent.fileheaderadder.settings.HeaderSettings",
    storages = [Storage("FileHeaderSettings.xml")]
)
// 泛型写自己，意思是 这个组件的状态类型就是我自己这个类
class HeaderSettings: PersistentStateComponent<HeaderSettings> {
    // var 可读写属性，默认值从系统属性user.name读取当前操作系统用户名，读不到就返回unknown
    var author: String= System.getProperty("user.name", "unknown")
    var company: String=""
    var dateFormat: String="yyyy-MM-dd"
    //IDE 要保存设置到磁盘时，会调用这个方法，直接返回this
    override fun getState(): HeaderSettings=this
    // IDE 启动时，从XML读出数据，构造一个临时的HeaderSettings，copyBean用反射把state字段的值拷贝到this实例上
    override fun loadState(state: HeaderSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }
    companion object {
        fun getInstance(): HeaderSettings=com.intellij.openapi.components.service()
    }
}