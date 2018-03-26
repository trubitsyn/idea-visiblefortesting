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

package io.github.trubitsyn.visiblefortesting.annotation

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethod
import io.github.trubitsyn.visiblefortesting.annotable.KtAnnotableUtil
import io.github.trubitsyn.visiblefortesting.annotable.PsiAnnotableUtil
import io.github.trubitsyn.visiblefortesting.annotation.base.AnnotationType
import io.github.trubitsyn.visiblefortesting.annotation.impl.AndroidAnnotationType
import io.github.trubitsyn.visiblefortesting.annotation.impl.FlinkAnnotationType
import io.github.trubitsyn.visiblefortesting.annotation.impl.GuavaAnnotationType
import org.jetbrains.kotlin.psi.KtFunction

object AnnotationTypes {
    private val annotations = setOf(
            AndroidAnnotationType(),
            GuavaAnnotationType(),
            FlinkAnnotationType()
    )

    fun available(project: Project) = annotations.filter { it.isAvailable(project) }

    fun areApplicableTo(method: PsiMethod, annotationTypes: List<AnnotationType>): Boolean {
        return annotationTypes.any { PsiAnnotableUtil.canAddAnnotation(method, it) }
    }

    fun areApplicableTo(function: KtFunction, annotationTypes: List<AnnotationType>): Boolean {
        return annotationTypes.any { KtAnnotableUtil.canAddAnnotation(function, it) }
    }
}