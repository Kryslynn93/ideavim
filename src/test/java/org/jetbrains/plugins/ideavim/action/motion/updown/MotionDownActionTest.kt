/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

@file:Suppress("RemoveCurlyBracesFromTemplate")

package org.jetbrains.plugins.ideavim.action.motion.updown

import com.intellij.codeInsight.daemon.impl.HintRenderer
import com.maddyhome.idea.vim.api.getVisualLineCount
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.state.mode.Mode
import com.maddyhome.idea.vim.state.mode.SelectionType
import com.maddyhome.idea.vim.newapi.IjVimEditor
import org.jetbrains.plugins.ideavim.VimTestCase
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * @author Alex Plate
 */
class MotionDownActionTest : VimTestCase() {
  @Test
  fun `test motion down in visual block mode`() {
    val keys = "<C-V>2kjjj"
    val before = """
            A Discovery

            I |found| it in a legendary land
            al|l roc|ks and lavender and tufted grass,
            wh|${c}ere i|t was settled on some sodden sand
            ha|rd by| the torrent of a mountain pass.
    """.trimIndent()
    val after = """
            A Discovery

            I |found| it in a legendary land
            al|l roc|ks and lavender and tufted grass,
            wh|${s}e${se}re i|t was settled on some sodden sand
            ha|${s}r${se}d by| the torrent of a mountain pass.
    """.trimIndent()
    doTest(keys, before, after, Mode.VISUAL(SelectionType.BLOCK_WISE))
  }

  @Test
  fun `test motion down in visual block mode with dollar motion`() {
    val keys = "<C-V>\$jj"
    val before = """
            A Discovery

            I |${c}found it in a legendary land
            al|l rocks and lavender and tufted grass,
            wh|ere it was settled on some sodden sand[additional Chars]
            hard by the torrent of a mountain pass.
    """.trimIndent()
    val after = """
            A Discovery

            I |${s}found it in a legendary lan${c}d${se}
            al|${s}l rocks and lavender and tufted grass${c},${se}
            wh|${s}ere it was settled on some sodden sand[additional Chars]${c}${se}
            hard by the torrent of a mountain pass.
    """.trimIndent()
    doTest(keys, before, after, Mode.VISUAL(SelectionType.BLOCK_WISE))
  }

  @Test
  @Disabled
  fun `test motion down in visual block mode with dollar motion2`() {
    val keys = "i<C-O>d<ESC>"
    val before = """
            A Discovery

            I |${c}found it in a legendary land
            al|l rocks and lavender and tufted grass,
            wh|ere it was settled on some sodden sand[additional Chars]
            hard by the torrent of a mountain pass.
    """.trimIndent()
    val after = """
            A Discovery

            I |${s}found it in a legendary lan${c}d${se}
            al|${s}l rocks and lavender and tufted grass${c},${se}
            wh|${s}ere it was settled on some sodden sand[additional Chars]${c}${se}
            hard by the torrent of a mountain pass.
    """.trimIndent()
    configureByTextX("aa.txt", before)
    typeText(keys)
//    println("Mode: " + mode())
//    doTest(keys, before, after, MyMode.VISUAL(SelectionType.BLOCK_WISE))
  }

  @Test
  @Disabled
  fun `test motion down in visual block mode with dollar motion3`() {
    val keys = "v"
    val before = """
            A Discovery

            I |${c}found it in a legendary land
            al|l rocks and lavender and tufted grass,
            wh|ere it was settled on some sodden sand[additional Chars]
            hard by the torrent of a mountain pass.
    """.trimIndent()
    val after = """
            A Discovery

            I |${s}found it in a legendary lan${c}d${se}
            al|${s}l rocks and lavender and tufted grass${c},${se}
            wh|${s}ere it was settled on some sodden sand[additional Chars]${c}${se}
            hard by the torrent of a mountain pass.
    """.trimIndent()
    configureByTextX("aa.txt", before)
    typeText(keys)
    println("register " + this.register("'<"))
    println("Mode: " + mode())
    typeText("h")
    println("register " + this.register("'<"))
    typeText(":")
    println("register " + this.register("'<"))
//    println("Mode: " + mode())
//    doTest(keys, before, after, MyMode.VISUAL(SelectionType.BLOCK_WISE))
  }

  @Test
  fun `test last column after line deletion`() {
    val keys = listOf("Vd", "j")
    val before = """
            I found it in a ${c}legendary land
            
            all rocks and lavender and tufted grass,
    """.trimIndent()
    val after = """
            
            ${c}all rocks and lavender and tufted grass,
    """.trimIndent()
    doTest(keys, before, after, Mode.NORMAL())
  }

  @Test
  fun `test with inlays - move down from line with preceding inlay`() {
    val keys = injector.parser.parseKeys("j")
    val before = """
            I found it in a ${c}legendary land
            all rocks and lavender and tufted grass,
    """.trimIndent()
    val after = """
            I found it in a legendary land
            all rocks and la${c}vender and tufted grass,
    """.trimIndent()
    configureByText(before)
    fixture.editor.inlayModel.addInlineElement(2, HintRenderer("Hello"))
    typeText(keys)
    assertState(after)
  }

  @Test
  fun `test with inlays - move down to correct column on line with preceding inlay`() {
    val keys = injector.parser.parseKeys("j")
    val before = """
            I found it in a ${c}legendary land
            all rocks and lavender and tufted grass,
    """.trimIndent()
    val after = """
            I found it in a legendary land
            all rocks and la${c}vender and tufted grass,
    """.trimIndent()
    configureByText(before)
    fixture.editor.inlayModel.addInlineElement(before.indexOf("rocks"), HintRenderer("Hello"))
    typeText(keys)
    assertState(after)
  }

  @Test
  fun `test with inlays - move down from line with preceding inlay to line with preceding inlay`() {
    val keys = injector.parser.parseKeys("j")
    val before = """
            I found it in a ${c}legendary land
            all rocks and lavender and tufted grass,
    """.trimIndent()
    val after = """
            I found it in a legendary land
            all rocks and la${c}vender and tufted grass,
    """.trimIndent()
    configureByText(before)
    fixture.editor.inlayModel.addInlineElement(before.indexOf("rocks"), HintRenderer("Hello"))
    fixture.editor.inlayModel.addInlineElement(before.indexOf("found"), HintRenderer("Hello"))
    typeText(keys)
    assertState(after)
  }

  @Test
  fun `test with inlays - move down from long line with inlay to correct column`() {
    val keys = injector.parser.parseKeys("j")
    val before = """
            I found it in a legendary ${c}land
            all rocks and lavender
    """.trimIndent()
    val after = """
            I found it in a legendary land
            all rocks and lavende${c}r
    """.trimIndent()
    configureByText(before)
    fixture.editor.inlayModel.addInlineElement(before.indexOf("found"), HintRenderer("Hello"))
    typeText(keys)
    assertState(after)
  }

  @Test
  fun `test with inlays - move down from line with inlay and back to correct column`() {
    val keys = injector.parser.parseKeys("jk")
    val before = """
            I found it in a legendary ${c}land
            all rocks and lavender
    """.trimIndent()
    val after = """
            I found it in a legendary ${c}land
            all rocks and lavender
    """.trimIndent()
    configureByText(before)
    fixture.editor.inlayModel.addInlineElement(before.indexOf("found"), HintRenderer("Hello"))
    typeText(keys)
    assertState(after)
  }

  @Test
  fun `test motion up down without inlays`() {
    val keys = injector.parser.parseKeys("jk")
    val before = """
            I found ${c}it in a legendary land
            all rocks and lavender
    """.trimIndent()
    val after = """
            I found ${c}it in a legendary land
            all rocks and lavender
    """.trimIndent()
    configureByText(before)
    typeText(keys)
    assertState(after)
  }

  @Test
  fun `test with inlays - long line moving onto shorter line with inlay before caret`() {
    val keys = injector.parser.parseKeys("j")
    val before = """
            I found it in a legendary ${c}land
            all rocks and lavender
    """.trimIndent()
    val after = """
            I found it in a legendary land
            all rocks and lavende${c}r
    """.trimIndent()
    configureByText(before)
    fixture.editor.inlayModel.addInlineElement(before.indexOf("rocks"), HintRenderer("Hello"))
    typeText(keys)
    assertState(after)
  }

  @Test
  fun `test with inlays - long line moving onto shorter line with trailing inlay`() {
    val keys = injector.parser.parseKeys("j")
    val before = """
            I found it in a legendary la${c}nd
            all rocks and lavender
            and tufted grass,
    """.trimIndent()
    val after = """
            I found it in a legendary land
            all rocks and lavende${c}r
            and tufted grass,
    """.trimIndent()
    configureByText(before)
    val inlayOffset = fixture.editor.document.getLineEndOffset(1)
    fixture.editor.inlayModel.addInlineElement(inlayOffset, HintRenderer("Hello"))
    typeText(keys)
    assertState(after)
  }

  @Test
  fun `test with inlays - move down onto line with inlay and back to correct column`() {
    val keys = injector.parser.parseKeys("jk")
    val before = """
            I found it in a legendary ${c}land
            all rocks and lavender
    """.trimIndent()
    val after = """
            I found it in a legendary ${c}land
            all rocks and lavender
    """.trimIndent()
    configureByText(before)
    fixture.editor.inlayModel.addInlineElement(before.indexOf("rocks"), HintRenderer("Hello"))
    typeText(keys)
    assertState(after)
  }

  @Test
  fun `test motion to the last empty line`() {
    doTest(
      "j",
      """
            I found it in a legendary ${c}land
            
      """.trimIndent(),
      """
            I found it in a legendary land
            ${c}
      """.trimIndent(),
      Mode.NORMAL(),
    )
  }

  @Test
  fun `test move on two empty strings`() {
    doTest(
      "\$j",
      """
            |${c}
            |
      """.trimMargin(),
      """
            |
            |${c}
      """.trimMargin(),
      Mode.NORMAL(),
    )
  }

  @Test
  fun `test vertical motions do not unfold text`() {
    configureByJavaText("""
      // My long comment that will be the longest line in the text
      /* 
      pu${c}pa
      */
    """.trimIndent())

    val foldingModel = fixture.editor.foldingModel
    foldingModel.runBatchFoldingOperation {
      val foldRegion = foldingModel.addFoldRegion(61, 71, "pupa")
      foldRegion!!.isExpanded = false
    }
    assertEquals(2, IjVimEditor(fixture.editor).getVisualLineCount())

    typeText("gg" + "$" + "j")
    assertEquals(2, IjVimEditor(fixture.editor).getVisualLineCount())
  }
}
