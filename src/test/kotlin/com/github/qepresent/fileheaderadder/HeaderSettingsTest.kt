package com.github.qepresent.fileheaderadder

import com.github.qepresent.fileheaderadder.settings.HeaderSettings
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * 测试 [HeaderSettings] 的默认值与持久化状态读写（[HeaderSettings.getState] /
 * [HeaderSettings.loadState]）。
 *
 * 这里直接 new 出独立实例来验证 copyBean 拷贝行为，不改动应用级单例，避免污染同 JVM 内其他测试。
 */
class HeaderSettingsTest : BasePlatformTestCase() {

    fun testDefaults() {
        val settings = HeaderSettings()

        assertEquals("", settings.company)
        assertEquals("yyyy-MM-dd", settings.dateFormat)
        assertEquals(System.getProperty("user.name", "unknown"), settings.author)
    }

    fun testStateIsItself() {
        val settings = HeaderSettings()

        // getState 返回自身，IDE 会拿它来序列化
        assertSame(settings, settings.getState())
    }

    fun testLoadStateCopiesFields() {
        val source = HeaderSettings().apply {
            author = "alice"
            company = "Acme"
            dateFormat = "yyyy/MM/dd"
        }
        val target = HeaderSettings() // 默认值，等待被覆盖

        target.loadState(source)

        assertEquals("alice", target.author)
        assertEquals("Acme", target.company)
        assertEquals("yyyy/MM/dd", target.dateFormat)
    }

    fun testApplicationServiceInstanceIsAvailable() {
        // 在平台测试环境里，应用级 @Service 应能正常拿到实例
        val instance = HeaderSettings.getInstance()

        assertNotNull(instance)
        assertSame(instance, HeaderSettings.getInstance())
        assertEquals("yyyy-MM-dd", instance.dateFormat)
    }
}
