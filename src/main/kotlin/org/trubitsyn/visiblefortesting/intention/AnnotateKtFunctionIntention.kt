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

package org.trubitsyn.visiblefortesting.intention

import com.intellij.codeInsight.CodeInsightBundle
import com.intellij.codeInsight.intention.LowPriorityAction
import com.intellij.ide.projectView.impl.ProjectRootsUtil
import com.intellij.openapi.editor.Editor
import org.trubitsyn.visiblefortesting.annotable.KtAnnotableUtil
import org.trubitsyn.visiblefortesting.annotation.AnnotationTypes
import org.trubitsyn.visiblefortesting.annotation.base.AnnotationType
import org.trubitsyn.visiblefortesting.extension.areApplicableTo
import org.trubitsyn.visiblefortesting.ui.ChooseAnnotationTypePopup
import org.jetbrains.kotlin.idea.intentions.SelfTargetingIntention
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType

class AnnotateKtFunctionIntention : SelfTargetingIntention<KtModifierListOwner>(
        KtModifierListOwner::class.java,
        "Annotate as @VisibleForTesting",
        CodeInsightBundle.message("intention.add.annotation.family")
), LowPriorityAction {
    var availableAnnotationTypes: List<AnnotationType> = emptyList()

    override fun isApplicableTo(element: KtModifierListOwner, caretOffset: Int): Boolean {
        if (ProjectRootsUtil.isInTestSource(element.containingFile)) {
            return false
        }

        val function = element.getParentOfType<KtFunction>(false) ?: return false

        if (availableAnnotationTypes.isEmpty()) {
            availableAnnotationTypes = AnnotationTypes.available(element.project)
        }

        if (availableAnnotationTypes.isEmpty()) {
            return false
        }
        return availableAnnotationTypes.areApplicableTo(function)
    }

    override fun applyTo(element: KtModifierListOwner, editor: Editor?) {
        val function = element.getParentOfType<KtFunction>(false) ?: return

        ChooseAnnotationTypePopup(editor!!).show(availableAnnotationTypes, {
            KtAnnotableUtil.addAnnotation(function, it)
        })
    }
}