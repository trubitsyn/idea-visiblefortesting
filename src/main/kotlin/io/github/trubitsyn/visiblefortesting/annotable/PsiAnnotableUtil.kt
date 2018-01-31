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

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifier
import io.github.trubitsyn.visiblefortesting.annotation.base.Annotation
import io.github.trubitsyn.visiblefortesting.annotation.impl.AndroidAnnotation
import io.github.trubitsyn.visiblefortesting.extension.smartImportClass
import io.github.trubitsyn.visiblefortesting.visibility.PsiMethodVisibility

object PsiAnnotableUtil {

    fun canAddAnnotation(method: PsiMethod, annotation: Annotation): Boolean {
        return !method.hasModifierProperty(PsiModifier.PUBLIC) && !hasAnnotation(method, annotation)
    }

    fun hasAnnotation(method: PsiMethod, annotation: Annotation): Boolean {
        return method.modifierList.annotations
                .asSequence()
                .map { it.qualifiedName }
                .any { annotation.name == it || annotation.qualifiedName == it }
    }

    fun addAnnotation(method: PsiMethod, annotation: Annotation) {
        val javaFile = method.containingFile as PsiJavaFile
        val name = javaFile.smartImportClass(annotation.qualifiedName, annotation.resolveClass(method.project))

        method.modifierList.addAnnotation(annotation.qualifiedName).let { psi ->
            if (annotation is AndroidAnnotation) {
                val visibility = PsiMethodVisibility(method)
                annotation.innerText(visibility, name, method)?.let {
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