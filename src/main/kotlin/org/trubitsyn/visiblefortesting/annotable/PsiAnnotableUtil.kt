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

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifier
import org.trubitsyn.visiblefortesting.annotation.base.AnnotationType
import org.trubitsyn.visiblefortesting.annotation.impl.AndroidAnnotationType
import org.trubitsyn.visiblefortesting.extension.smartImportClass
import org.trubitsyn.visiblefortesting.visibility.PsiMethodVisibility

object PsiAnnotableUtil {

    fun canAddAnnotation(method: PsiMethod, annotationType: AnnotationType): Boolean {
        return !method.hasModifierProperty(PsiModifier.PUBLIC) && !hasAnnotation(method, annotationType)
    }

    fun hasAnnotation(method: PsiMethod, annotationType: AnnotationType): Boolean {
        return method.modifierList.annotations
                .asSequence()
                .map { it.qualifiedName }
                .any { annotationType.name == it || annotationType.qualifiedName == it }
    }

    fun addAnnotation(method: PsiMethod, annotationType: AnnotationType) {
        val javaFile = method.containingFile as PsiJavaFile
        val name = javaFile.smartImportClass(annotationType.qualifiedName, annotationType.resolveClass(method.project))

        method.modifierList.addAnnotation(annotationType.qualifiedName).let { psi ->
            if (annotationType is AndroidAnnotationType) {
                val visibility = PsiMethodVisibility(method)
                annotationType.innerText(visibility, name, method)?.let {
                    if (it.second.isNotEmpty()) {
                        val value = JavaPsiFacade.getElementFactory(method.project)
                                .createExpressionFromText(it.second, method)
                        psi.setDeclaredAttributeValue(it.first, value)
                    }
                }
            }
        }
        method.modifierList.setModifierProperty(PsiModifier.PUBLIC, true)
    }
}