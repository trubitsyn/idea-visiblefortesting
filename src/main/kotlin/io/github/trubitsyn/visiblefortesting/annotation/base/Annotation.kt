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

package io.github.trubitsyn.visiblefortesting.annotation.base

import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.impl.source.codeStyle.ImportHelper
import com.intellij.psi.search.GlobalSearchScope

abstract class Annotation(val name: String, val qualifiedName: String) {

    protected open fun buildElements(method: PsiMethod, useQualifiedName: Boolean, onElementBuilt: (name: String, value: PsiExpression) -> Unit) {}

    protected fun buildElementValue(method: PsiMethod, text: String): PsiExpression {
        return JavaPsiFacade.getElementFactory(method.project).createExpressionFromText(text, method)
    }

    fun isAvailable(project: Project) = findPsiClass(project) != null

    fun isAppliedTo(method: PsiMethod): Boolean {
        return method.modifierList.annotations
                .asSequence()
                .map { it.qualifiedName }
                .any { name == it || qualifiedName == it }
    }

    fun isApplicableTo(method: PsiMethod): Boolean {
        return !method.hasModifierProperty(PsiModifier.PUBLIC) && !isAppliedTo(method)
    }

    fun applyTo(method: PsiMethod) {
        val javaFile = method.containingFile as PsiJavaFile

        if (!ImportHelper.isAlreadyImported(javaFile, qualifiedName)) {
            findPsiClass(method.project).let {
                javaFile.importClass(it)
            }
        }

        val imports = javaFile
                .importList
                ?.importStatements
                ?.filter { (it.qualifiedName?.endsWith(name) == true) && it.qualifiedName != qualifiedName }

        val useQualifiedName = imports != null && !imports.isEmpty()

        val desiredName = if (useQualifiedName) qualifiedName else name

        val psiAnnotation = method.modifierList.addAnnotation(desiredName)

        buildElements(method, useQualifiedName, { name, value ->
            psiAnnotation.setDeclaredAttributeValue(name, value)
        })

        method.modifierList.setModifierProperty(PsiModifier.PUBLIC, true)
    }

    fun findPsiClass(project: Project): PsiClass? {
        return JavaPsiFacade.getInstance(project)
                .findClass(qualifiedName, GlobalSearchScope.allScope(project))
    }
}