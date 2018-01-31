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

package io.github.trubitsyn.visiblefortesting.annotable

import com.intellij.psi.codeStyle.CodeStyleManager
import io.github.trubitsyn.visiblefortesting.annotation.base.Annotation
import io.github.trubitsyn.visiblefortesting.annotation.impl.AndroidAnnotation
import io.github.trubitsyn.visiblefortesting.extension.smartImportClass
import io.github.trubitsyn.visiblefortesting.visibility.KtFunctionVisibility
import org.jetbrains.kotlin.idea.util.addAnnotation
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClass

object KtAnnotableUtil {

    fun hasAnnotation(function: KtFunction, annotation: Annotation): Boolean {
        return function.findAnnotation(FqName(annotation.qualifiedName)) != null
    }

    fun canAddAnnotation(function: KtFunction, annotation: Annotation): Boolean {
        return (!isPublic(function) || isProtectedInFinalClass(function)) && !hasAnnotation(function, annotation)
    }

    private fun isPublic(function: KtFunction): Boolean {
        return (!function.hasModifier(KtTokens.PROTECTED_KEYWORD) &&
                !function.hasModifier(KtTokens.INTERNAL_KEYWORD) &&
                !function.hasModifier(KtTokens.PRIVATE_KEYWORD))
    }

    private fun isProtectedInFinalClass(function: KtFunction): Boolean {
        val isProtected = function.hasModifier(KtTokens.PROTECTED_KEYWORD)

        if (!isProtected) {
            return false
        }

        val ktClass = function.containingClass()

        if (ktClass != null) {
            return !ktClass.hasModifier(KtTokens.OPEN_KEYWORD)
        }
        return false
    }

    fun addAnnotation(function: KtFunction, annotation: Annotation) {
        val file = function.containingKtFile
        val name = file.smartImportClass(annotation.qualifiedName)

        var text: String? = null
        if (annotation is AndroidAnnotation) {
            val visibility = KtFunctionVisibility(function)
            val element = annotation.innerText(visibility, name, function)
            text = if (element != null) "${element.first}=${element.second}" else null
        }

        function.addModifier(KtTokens.PUBLIC_KEYWORD)
        function.removeModifier(KtTokens.PUBLIC_KEYWORD)
        function.addAnnotation(FqName(annotation.qualifiedName), text)

        val leftBrace = function.bodyExpression!!.prevSibling
        CodeStyleManager.getInstance(function.project).reformat(leftBrace)
    }
}