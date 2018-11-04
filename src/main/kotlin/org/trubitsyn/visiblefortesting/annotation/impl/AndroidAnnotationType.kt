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

package org.trubitsyn.visiblefortesting.annotation.impl

import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiType
import org.trubitsyn.visiblefortesting.annotation.base.AnnotationType
import org.trubitsyn.visiblefortesting.visibility.Visibility

class AndroidAnnotationType : AnnotationType(name = "VisibleForTesting", qualifiedName = "android.support.annotation.VisibleForTesting") {
    private val otherwise = "otherwise"

    fun innerText(visibility: Visibility, name: String, context: PsiElement): Pair<String, String>? {
        if (!hasElements(context.project)) {
            return null
        }

        if (!visibility.isPrivate) {
            val value = "$name.${findModifier(visibility)}"
            return Pair(otherwise, value)
        }
        return null
    }

    private fun hasElements(project: Project): Boolean {
        resolveClass(project)?.let {
            val method = JavaPsiFacade.getElementFactory(project).createMethod(otherwise, PsiType.INT)
            return it.findMethodBySignature(method, false) != null
        }
        return false
    }

    private fun findModifier(visibility: Visibility): String {
        return when {
            visibility.isPackageLocal -> "PACKAGE_PRIVATE"
            visibility.isProtected -> "PROTECTED"
            else -> throw IllegalArgumentException()
        }
    }
}