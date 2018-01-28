/*
 * Copyright 2017 Nikola Trubitsyn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.trubitsyn.visiblefortesting.visibility

import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtFunction

class KtFunctionVisibility(val function: KtFunction) : Visibility {
    override val isProtected: Boolean
        get() = function.hasModifier(KtTokens.PROTECTED_KEYWORD)
    override val isPackageLocal: Boolean
        get() = function.hasModifier(KtTokens.INTERNAL_KEYWORD)
    override val isPrivate: Boolean
        get() = function.hasModifier(KtTokens.PRIVATE_KEYWORD)
}