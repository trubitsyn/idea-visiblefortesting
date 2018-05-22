/*
 * Copyright 2018 Nikola Trubitsyn
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
import com.intellij.ide.projectView.impl.ProjectRootsUtil
import com.intellij.openapi.editor.Editor
import io.github.trubitsyn.visiblefortesting.annotable.KtAnnotableUtil
import io.github.trubitsyn.visiblefortesting.annotation.AnnotationTypes
import io.github.trubitsyn.visiblefortesting.annotation.base.AnnotationType
import io.github.trubitsyn.visiblefortesting.extension.areApplicableTo
import io.github.trubitsyn.visiblefortesting.ui.ChooseAnnotationTypePopup
import org.jetbrains.kotlin.idea.intentions.SelfTargetingIntention
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.containingClassOrObject

class AnnotateKtFunctionFromTestIntention : SelfTargetingIntention<KtReferenceExpression>(
        KtReferenceExpression::class.java,
        "Annotate as @VisibleForTesting",
        CodeInsightBundle.message("intention.add.annotation.family")
) {
    var availableAnnotationTypes: List<AnnotationType> = emptyList()

    override fun isApplicableTo(element: KtReferenceExpression, caretOffset: Int): Boolean {
        if (!ProjectRootsUtil.isInTestSource(element.containingFile)) {
            return false
        }

        if (element.parent !is KtCallExpression) {
            return false
        }

        val resolvedMethod = element.mainReference.resolve() ?: return false
        val function = resolvedMethod as? KtFunction ?: return false

        if (availableAnnotationTypes.isEmpty()) {
            availableAnnotationTypes = AnnotationTypes.available(element.project)
        }

        if (availableAnnotationTypes.isEmpty()) {
            return false
        }

        if (availableAnnotationTypes.areApplicableTo(function)) {
            if (function.containingClassOrObject != null) {
                text = "Annotate '${function.containingClassOrObject?.name}.${function.name}' as @VisibleForTesting"
            } else {
                text = "Annotate '${function.name}' as @VisibleForTesting"
            }
            return true
        }
        return false
    }

    override fun applyTo(element: KtReferenceExpression, editor: Editor?) {
        val function = element.mainReference.resolve() as KtFunction

        ChooseAnnotationTypePopup(editor!!).show(availableAnnotationTypes) {
            KtAnnotableUtil.addAnnotation(function, it)
        }
    }
}