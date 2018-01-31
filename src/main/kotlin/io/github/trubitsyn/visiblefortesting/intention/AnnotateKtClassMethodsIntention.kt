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

package io.github.trubitsyn.visiblefortesting.intention

import com.intellij.codeInsight.intention.LowPriorityAction
import com.intellij.ide.projectView.impl.ProjectRootsUtil
import com.intellij.openapi.editor.Editor
import io.github.trubitsyn.visiblefortesting.annotable.KtAnnotableUtil
import io.github.trubitsyn.visiblefortesting.annotation.Annotations
import io.github.trubitsyn.visiblefortesting.annotation.base.Annotation
import io.github.trubitsyn.visiblefortesting.ui.ChooseAnnotationPopup
import org.jetbrains.kotlin.idea.intentions.SelfTargetingIntention
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtFunction

class AnnotateKtClassMethodsIntention : SelfTargetingIntention<KtClass>(
        KtClass::class.java,
        "Annotate methods as @VisibleForTesting"
), LowPriorityAction {
    var availableAnnotations: List<Annotation> = emptyList()

    override fun isApplicableTo(element: KtClass, caretOffset: Int): Boolean {
        if (ProjectRootsUtil.isInTestSource(element.containingFile)) {
            return false
        }

        if (availableAnnotations.isEmpty()) {
            availableAnnotations = Annotations.available(element.project)
        }

        if (availableAnnotations.isEmpty()) {
            return false
        }

        val functions = element.declarations.filterIsInstance(KtFunction::class.java)
        return functions
                .asSequence()
                .any { func -> Annotations.areApplicableTo(func, availableAnnotations) }
    }

    override fun applyTo(element: KtClass, editor: Editor?) {
        val functions = element.declarations.filterIsInstance(KtFunction::class.java)

        val applicableAnnotations = functions
                .flatMap { func -> availableAnnotations.filter { KtAnnotableUtil.canAddAnnotation(func, it) } }
                .toSet()
                .sortedBy { it.qualifiedName }
                .toList()

        ChooseAnnotationPopup(editor!!).show(availableAnnotations, { annotation ->
            functions
                    .asSequence()
                    .filter { Annotations.areApplicableTo(it, applicableAnnotations) }
                    .forEach { KtAnnotableUtil.addAnnotation(it, annotation) }
        })
    }

    override fun startInWriteAction() = true
}