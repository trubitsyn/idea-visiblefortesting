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

import com.intellij.psi.PsiExpression
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifier
import com.intellij.psi.impl.source.codeStyle.ImportHelper

object AnnotationApplier {

    fun canAnnotate(method: PsiMethod, annotation: Annotation): Boolean {
        return !method.hasModifierProperty(PsiModifier.PUBLIC) && !isAnnotated(method, annotation)
    }

    fun isAnnotated(method: PsiMethod, annotation: Annotation): Boolean {
        return method.modifierList.annotations
                .asSequence()
                .map { it.qualifiedName }
                .any { annotation.name == it || annotation.qualifiedName == it }
    }

    fun addAnnotation(method: PsiMethod, annotation: Annotation) {
        val javaFile = method.containingFile as PsiJavaFile

        if (!ImportHelper.isAlreadyImported(method.containingFile as PsiJavaFile, annotation.qualifiedName)) {
            val clazz = Annotations.findClass(method.project, annotation)
            javaFile.importClass(clazz)
        }

        val imports = javaFile
                .importList
                ?.importStatements
                ?.filter { (it.qualifiedName?.endsWith(annotation.name) ?: false) && it.qualifiedName != annotation.qualifiedName }

        val useQualifiedName = imports != null && !imports.isEmpty();

        val desiredName = if (useQualifiedName) {
            annotation.qualifiedName
        } else {
            annotation.name
        }

        val psiAnnotation = method.modifierList.addAnnotation(desiredName)

        annotation.buildAttributes(method, useQualifiedName, onAttributeBuilt = { name: String, value: PsiExpression ->
            psiAnnotation.setDeclaredAttributeValue(name, value)
        })

        method.modifierList.setModifierProperty(PsiModifier.PUBLIC, true)
    }
}
