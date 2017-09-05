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

package io.github.trubitsyn.visiblefortesting.annotation.impl

import com.intellij.openapi.project.Project
import com.intellij.psi.*
import io.github.trubitsyn.visiblefortesting.annotation.base.Annotation

class AndroidAnnotation : Annotation(name = "VisibleForTesting", qualifiedName = "android.support.annotation.VisibleForTesting") {
    private val otherwise = "otherwise"

    override fun buildElements(method: PsiMethod, useQualifiedName: Boolean, onElementBuilt: (name: String, value: PsiExpression) -> Unit) {

        if (!hasElements(method.project)) {
            return
        }

        if (!method.hasModifierProperty(PsiModifier.PRIVATE)) {
            val name = if (useQualifiedName) qualifiedName else name
            val text = "$name.${findModifier(method)}"
            val value = buildElementValue(method, text)

            onElementBuilt(otherwise, value)
        }
    }

    private fun hasElements(project: Project): Boolean {
        findPsiClass(project)?.let {
            val method = JavaPsiFacade.getElementFactory(project).createMethod(otherwise, PsiType.INT)
            return it.findMethodBySignature(method, false) != null
        }
        return false
    }

    private fun findModifier(method: PsiMethod): String {
        return when {
            method.hasModifierProperty(PsiModifier.PRIVATE) -> "PRIVATE"
            method.hasModifierProperty(PsiModifier.PACKAGE_LOCAL) -> "PACKAGE_PRIVATE"
            method.hasModifierProperty(PsiModifier.PROTECTED) -> "PROTECTED"
            else -> "PUBLIC"
        }
    }
}