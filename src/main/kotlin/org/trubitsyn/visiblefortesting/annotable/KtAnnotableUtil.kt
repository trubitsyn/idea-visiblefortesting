/*
 * Copyright 2017, 2018 Nikola Trubitsyn
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

package org.trubitsyn.visiblefortesting.annotable

import com.intellij.psi.codeStyle.CodeStyleManager
import org.trubitsyn.visiblefortesting.annotation.base.AnnotationType
import org.trubitsyn.visiblefortesting.annotation.impl.AndroidAnnotationType
import org.trubitsyn.visiblefortesting.extension.smartImportClass
import org.trubitsyn.visiblefortesting.visibility.KtFunctionVisibility
import org.jetbrains.kotlin.idea.util.addAnnotation
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClass

object KtAnnotableUtil {

    fun hasAnnotation(function: KtFunction, annotationType: AnnotationType): Boolean {
        return function.findAnnotation(FqName(annotationType.qualifiedName)) != null
    }

    fun canAddAnnotation(function: KtFunction, annotationType: AnnotationType): Boolean {
        return (!isPublic(function) || isProtectedInFinalClass(function)) && !hasAnnotation(function, annotationType)
    }

    private fun isPublic(function: KtFunction): Boolean {
        return (!function.hasModifier(KtTokens.PROTECTED_KEYWORD) &&
                !function.hasModifier(KtTokens.INTERNAL_KEYWORD) &&
                !function.hasModifier(KtTokens.PRIVATE_KEYWORD))
    }

    fun isProtectedInFinalClass(function: KtFunction): Boolean {
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

    fun addAnnotation(function: KtFunction, annotationType: AnnotationType) {
        val file = function.containingKtFile
        val name = file.smartImportClass(annotationType.qualifiedName)

        var text: String? = null
        if (annotationType is AndroidAnnotationType) {
            val visibility = KtFunctionVisibility(function)
            val element = annotationType.innerText(visibility, name, function)
            text = if (element != null) "${element.first}=${element.second}" else null
        }

        function.addModifier(KtTokens.PUBLIC_KEYWORD)
        function.removeModifier(KtTokens.PUBLIC_KEYWORD)
        function.addAnnotation(FqName(annotationType.qualifiedName), text)

        val leftBrace = function.bodyExpression!!.prevSibling
        CodeStyleManager.getInstance(function.project).reformat(leftBrace)
    }
}