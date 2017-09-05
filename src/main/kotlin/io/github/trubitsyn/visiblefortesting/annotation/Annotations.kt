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

package io.github.trubitsyn.visiblefortesting.annotation

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifier
import io.github.trubitsyn.visiblefortesting.annotation.base.Annotation
import io.github.trubitsyn.visiblefortesting.annotation.impl.AndroidAnnotation
import io.github.trubitsyn.visiblefortesting.annotation.impl.GuavaAnnotation

object Annotations {
    private val annotations = setOf<Annotation>(
            AndroidAnnotation(),
            GuavaAnnotation()
    )

    fun available(project: Project) = annotations.filter { it.isAvailable(project) }

    fun areApplicableTo(method: PsiMethod, annotations: List<Annotation>): Boolean {
        return !method.hasModifierProperty(PsiModifier.PUBLIC) && annotations.none { it.isAppliedTo(method) }
    }
}