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

package io.github.trubitsyn.visiblefortesting.intention

import com.intellij.codeInsight.CodeInsightBundle
import com.intellij.codeInsight.intention.LowPriorityAction
import com.intellij.ide.projectView.impl.ProjectRootsUtil
import com.intellij.openapi.editor.Editor
import io.github.trubitsyn.visiblefortesting.annotable.KtAnnotableUtil
import io.github.trubitsyn.visiblefortesting.annotation.AnnotationTypes
import io.github.trubitsyn.visiblefortesting.annotation.base.AnnotationType
import io.github.trubitsyn.visiblefortesting.ui.ChooseAnnotationTypePopup
import org.jetbrains.kotlin.idea.intentions.SelfTargetingIntention
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFunction

class AnnotateKtClassMethodsIntention : SelfTargetingIntention<KtClassOrObject>(
        KtClassOrObject::class.java,
        "Annotate methods as @VisibleForTesting",
        CodeInsightBundle.message("intention.add.annotation.family")
), LowPriorityAction {
    var availableAnnotationTypes: List<AnnotationType> = emptyList()

    override fun isApplicableTo(element: KtClassOrObject, caretOffset: Int): Boolean {
        if (ProjectRootsUtil.isInTestSource(element.containingFile)) {
            return false
        }

        if (availableAnnotationTypes.isEmpty()) {
            availableAnnotationTypes = AnnotationTypes.available(element.project)
        }

        if (availableAnnotationTypes.isEmpty()) {
            return false
        }

        val functions = element.declarations.filterIsInstance(KtFunction::class.java)
        return functions
                .asSequence()
                .any { func -> AnnotationTypes.areApplicableTo(func, availableAnnotationTypes) }
    }

    override fun applyTo(element: KtClassOrObject, editor: Editor?) {
        val functions = element.declarations.filterIsInstance(KtFunction::class.java)

        val applicableAnnotations = functions
                .flatMap { func -> availableAnnotationTypes.filter { KtAnnotableUtil.canAddAnnotation(func, it) } }
                .toSet()
                .sortedBy { it.qualifiedName }
                .toList()

        ChooseAnnotationTypePopup(editor!!).show(availableAnnotationTypes, { annotation ->
            functions
                    .asSequence()
                    .filter { AnnotationTypes.areApplicableTo(it, applicableAnnotations) }
                    .forEach { KtAnnotableUtil.addAnnotation(it, annotation) }
        })
    }

    override fun startInWriteAction() = true
}