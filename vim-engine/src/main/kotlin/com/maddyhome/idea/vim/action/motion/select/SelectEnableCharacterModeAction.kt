/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package com.maddyhome.idea.vim.action.motion.select

import com.maddyhome.idea.vim.api.ExecutionContext
import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.api.getLineEndForOffset
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.command.Command
import com.maddyhome.idea.vim.command.OperatorArguments
import com.maddyhome.idea.vim.state.mode.SelectionType
import com.maddyhome.idea.vim.group.visual.vimSetSystemSelectionSilently
import com.maddyhome.idea.vim.handler.VimActionHandler

/**
 * @author Alex Plate
 */

public class SelectEnableCharacterModeAction : VimActionHandler.SingleExecution() {

  override val type: Command.Type = Command.Type.OTHER_READONLY

  override fun execute(
    editor: VimEditor,
    context: ExecutionContext,
    cmd: Command,
    operatorArguments: OperatorArguments,
  ): Boolean {
    editor.nativeCarets().sortedByDescending { it.offset.point }.forEach { caret ->
      val lineEnd = editor.getLineEndForOffset(caret.offset.point)
      caret.run {
        vimSetSystemSelectionSilently(offset.point, (offset.point + 1).coerceAtMost(lineEnd))
        moveToInlayAwareOffset((offset.point + 1).coerceAtMost(lineEnd))
      }
    }
    return injector.visualMotionGroup.enterSelectMode(editor, SelectionType.CHARACTER_WISE)
  }
}
