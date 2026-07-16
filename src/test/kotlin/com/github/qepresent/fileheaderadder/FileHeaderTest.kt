package com.github.qepresent.fileheaderadder

import com.github.qepresent.fileheaderadder.actions.FileHeader
import com.github.qepresent.fileheaderadder.settings.HeaderSettings
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * 测试 [FileHeader] 的文件头生成与偏移量计算逻辑。
 *
 * 纯逻辑（[FileHeader.generateJavaStyle]、[FileHeader.generateGenericStyle]、
 * [FileHeader.calculateOffset]）只依赖传入的字符串，可以稳定断言；
 * 分发逻辑通过 [myFixture] 创建真实 PSI 文件来验证。
 */
class FileHeaderTest : BasePlatformTestCase() {

    // 构造一个确定性的设置，避免依赖运行环境的 user.name
    private fun fixedSettings(
        author: String = "alice",
        company: String = "",
        dateFormat: String = "yyyy-MM-dd"
    ) = HeaderSettings().apply {
        this.author = author
        this.company = company
        this.dateFormat = dateFormat
    }

    // -------------------- Java 风格 --------------------

    fun testJavaStyleWithoutCompany() {
        val header = FileHeader.generateJavaStyle(fixedSettings(author = "alice"), "Demo.kt", "2026-07-16")
        val expected = "/**\n" +
            "author alice\n" +
            "date 2026-07-16\n" +
            "file Demo.kt\n" +
            "*/\n" +
            "\n"
        assertEquals(expected, header)
    }

    fun testJavaStyleWithCompany() {
        val header = FileHeader.generateJavaStyle(
            fixedSettings(author = "bob", company = "ACME"),
            "A.java",
            "2026/07/16"
        )
        assertTrue(header.contains("author bob"))
        assertTrue(header.contains("company ACME"))
        assertTrue(header.contains("date 2026/07/16"))
        assertTrue(header.contains("file A.java"))
    }

    fun testJavaStyleEndsWithBlankLine() {
        val header = FileHeader.generateJavaStyle(fixedSettings(), "X.kt", "2026-07-16")
        // 生成结果在结束注释后再补一个空行，方便和后续代码分隔
        assertTrue(header.endsWith("*/\n\n"))
    }

    // -------------------- 通用风格 --------------------

    fun testGenericStyleWithoutCompany() {
        val header = FileHeader.generateGenericStyle(fixedSettings(author = "alice"), "page.xml", "2026-07-16")
        val expected = "/*\n" +
            " * Author:    alice\n" +
            " * Date:      2026-07-16\n" +
            " * File:      page.xml\n" +
            " */\n" +
            "\n"
        assertEquals(expected, header)
    }

    fun testGenericStyleWithCompany() {
        val header = FileHeader.generateGenericStyle(
            fixedSettings(author = "bob", company = "ACME"),
            "x.xml",
            "2026-07-16"
        )
        assertTrue(header.contains(" * Author:    bob"))
        assertTrue(header.contains(" * Company:   ACME"))
        assertTrue(header.contains(" * File:      x.xml"))
    }

    // -------------------- 偏移量计算 --------------------

    fun testOffsetForPlainFileIsZero() {
        assertEquals(0, FileHeader.calculateOffset("hello world"))
    }

    fun testOffsetForEmptyTextIsZero() {
        assertEquals(0, FileHeader.calculateOffset(""))
    }

    fun testOffsetSkipsShebangLine() {
        // "#!/bin/bash" 是 11 个字符，加上换行符共 12，文件头应插在 shebang 行之后
        val text = "#!/bin/bash\necho hi\n"
        assertEquals(12, FileHeader.calculateOffset(text))
    }

    fun testOffsetDoesNotSkipShebangWithoutNewline() {
        // 只有 shebang 一行、没有换行：无处可插，回到文件开头
        assertEquals(0, FileHeader.calculateOffset("#!/bin/bash"))
    }

    fun testOffsetIgnoresShebangNotAtStart() {
        // 不以 #! 开头，不应当作 shebang 处理
        assertEquals(0, FileHeader.calculateOffset("plain #!text\nmore"))
    }

    // -------------------- 按语言分发（纯逻辑）--------------------

    fun testSelectHeaderUsesJavaStyleForJavaKotlinGroovy() {
        val settings = fixedSettings()
        for (displayName in listOf("Java", "Kotlin", "Groovy")) {
            val header = FileHeader.selectHeader(displayName, settings, "X.kt", "2026-07-16")
            assertTrue("$displayName 应使用 Java 风格（/**）", header.startsWith("/**"))
            // 不应混入通用风格的对齐星号
            assertFalse(header.contains(" * Author:"))
        }
    }

    fun testSelectHeaderUsesGenericStyleForOtherLanguages() {
        val settings = fixedSettings()
        for (displayName in listOf("XML", "Python", "JavaScript", "Plain text", "")) {
            val header = FileHeader.selectHeader(displayName, settings, "x", "2026-07-16")
            assertTrue("$displayName 应使用通用风格（/*）", header.startsWith("/*"))
            assertFalse("$displayName 不应使用 Java 风格（/**）", header.startsWith("/**"))
        }
    }

    // -------------------- 按语言分发（端到端，真实 PSI）--------------------

    fun testGenerateHeaderProducesHeaderForJavaFile() {
        val psiFile = myFixture.configureByText("Demo.java", "class A {}")
        val header = FileHeader.generateHeader(psiFile)

        // generateHeader 能对真实 PSI 文件产出文件头；具体走哪种风格取决于运行环境是否
        // 加载了 Java 语言支持，分发逻辑本身已由 selectHeader 的测试单独覆盖。
        // 无论走哪种风格，文件头都是以块注释 "/*" 开头、且带上文件名的，用这两个不变量断言。
        assertTrue("文件头应以块注释开头", header.startsWith("/*"))
        assertTrue(header.contains("Demo.java"))
    }

    fun testGenerateHeaderUsesGenericStyleForXmlFile() {
        val psiFile = myFixture.configureByText("page.xml", "<a/>")
        val header = FileHeader.generateHeader(psiFile)

        assertTrue("XML 文件应使用通用风格（/*）", header.startsWith("/*"))
        assertTrue(header.contains(" * File:      page.xml"))
        assertTrue(header.contains(" * Author:"))
        // 不应使用 Java 风格的紧凑注释
        assertFalse(header.startsWith("/**"))
    }
}
