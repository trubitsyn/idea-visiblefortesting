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

package io.github.trubitsyn.visiblefortesting

import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.impl.source.codeStyle.ImportHelper
import com.intellij.psi.search.GlobalSearchScope

object AnnotationApplier {
    private val QUALIFIED_NAME = "android.support.annotation.VisibleForTesting"
    private val NAME = "VisibleForTesting"

    fun canAnnotate(method: PsiMethod): Boolean {
        return !method.hasModifierProperty(PsiModifier.PUBLIC) && !isAnnotated(method)
    }

    fun isAnnotated(method: PsiMethod): Boolean {
        return method.modifierList.annotations
                .asSequence()
                .map { it.qualifiedName }
                .any { NAME == it || QUALIFIED_NAME == it }
    }

    fun addAnnotation(method: PsiMethod) {
        val annotation = method.modifierList.addAnnotation(NAME)

        if (!method.hasModifierProperty(PsiModifier.PRIVATE)) {
            val value = JavaPsiFacade.getElementFactory(method.project)
                    .createExpressionFromText("VisibleForTesting." + findModifier(method), method)
            annotation.setDeclaredAttributeValue("otherwise", value)
        }

        method.modifierList.setModifierProperty(PsiModifier.PUBLIC, true)

        if (!ImportHelper.isAlreadyImported(method.containingFile as PsiJavaFile, QUALIFIED_NAME)) {
            val clazz = getAnnotationClass(method.project)
            (method.containingFile as PsiJavaFile).importClass(clazz)
        }
    }

    private fun findModifier(method: PsiMethod): String {
        return when {
            method.hasModifierProperty(PsiModifier.PRIVATE) -> "PRIVATE"
            method.hasModifierProperty(PsiModifier.PACKAGE_LOCAL) -> "PACKAGE_PRIVATE"
            method.hasModifierProperty(PsiModifier.PROTECTED) -> "PROTECTED"
            else -> "PUBLIC"
        }
    }

    fun isAnnotationAvailable(project: Project): Boolean {
        return getAnnotationClass(project) != null
    }

    private fun getAnnotationClass(project: Project): PsiClass? {
        return JavaPsiFacade.getInstance(project).findClass(QUALIFIED_NAME, GlobalSearchScope.allScope(project))
    }
}
