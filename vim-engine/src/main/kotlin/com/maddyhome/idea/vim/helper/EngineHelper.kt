/*
 * Copyright 2003-2023 The IdeaVim authors
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE.txt file or at
 * https://opensource.org/licenses/MIT.
 */

package com.maddyhome.idea.vim.helper

import com.maddyhome.idea.vim.api.VimEditor
import com.maddyhome.idea.vim.api.injector
import com.maddyhome.idea.vim.api.options
import com.maddyhome.idea.vim.state.VimStateMachine
import com.maddyhome.idea.vim.common.TextRange
import com.maddyhome.idea.vim.options.OptionConstants
import com.maddyhome.idea.vim.state.mode.Mode
import com.maddyhome.idea.vim.state.mode.SelectionType
import com.maddyhome.idea.vim.state.mode.isSingleModeActive
import com.maddyhome.idea.vim.state.mode.mode
import com.maddyhome.idea.vim.state.mode.returnTo
import java.util.*

public inline fun <reified T : Enum<T>> noneOfEnum(): EnumSet<T> = EnumSet.noneOf(T::class.java)

public val TextRange.endOffsetInclusive: Int
  get() = if (this.endOffset > 0 && this.endOffset > this.startOffset) this.endOffset - 1 else this.endOffset

public val VimEditor.inRepeatMode: Boolean
  get() = this.vimStateMachine.isDotRepeatInProgress

public val VimEditor.vimStateMachine: VimStateMachine
  get() = VimStateMachine.getInstance(this)

public val VimEditor.usesVirtualSpace: Boolean
  get() = injector.options(this).virtualedit.contains(OptionConstants.virtualedit_onemore)

public val VimEditor.isEndAllowed: Boolean
  get() = this.isEndAllowed(this.mode)

public fun VimEditor.isEndAllowed(mode: Mode): Boolean {
  return when (mode) {
    is Mode.INSERT, is Mode.VISUAL, is Mode.SELECT -> true
    is Mode.NORMAL, Mode.CMD_LINE, Mode.REPLACE, is Mode.OP_PENDING -> {
      // One day we'll use a proper insert_normal mode
      if (mode.isSingleModeActive) true else usesVirtualSpace
    }
  }
}

public inline fun <reified T : Enum<T>> enumSetOf(vararg value: T): EnumSet<T> = when (value.size) {
  0 -> noneOfEnum()
  1 -> EnumSet.of(value[0])
  else -> EnumSet.of(value[0], *value.slice(1..value.lastIndex).toTypedArray())
}

public fun VimStateMachine.setSelectMode(submode: SelectionType) {
  mode = Mode.SELECT(submode, this.mode.returnTo)
}

public fun VimStateMachine.pushVisualMode(submode: SelectionType) {
  mode = Mode.VISUAL(submode, this.mode.returnTo)
}
