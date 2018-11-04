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
import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.ide.projectView.impl.ProjectRootsUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiJavaToken
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import org.trubitsyn.visiblefortesting.annotable.PsiAnnotableUtil
import org.trubitsyn.visiblefortesting.annotation.AnnotationTypes
import org.trubitsyn.visiblefortesting.annotation.base.AnnotationType
import org.trubitsyn.visiblefortesting.extension.areApplicableTo
import org.trubitsyn.visiblefortesting.ui.ChooseAnnotationTypePopup
import org.jetbrains.annotations.NonNls

class AnnotateMethodIntention : BaseElementAtCaretIntentionAction() {
    var availableAnnotationTypes: List<AnnotationType> = emptyList()

    @NonNls
    override fun getText() = "Annotate as @VisibleForTesting"

    @NonNls
    override fun getFamilyName() = CodeInsightBundle.message("intention.add.annotation.family")

    override fun isAvailable(project: Project, editor: Editor, psiElement: PsiElement): Boolean {
        if (ProjectRootsUtil.isInTestSource(psiElement.containingFile)) {
            return false
        }

        if (psiElement is PsiJavaToken && psiElement.parent is PsiMethod) {
            if (availableAnnotationTypes.isEmpty()) {
                availableAnnotationTypes = AnnotationTypes.available(project)
            }

            if (availableAnnotationTypes.isEmpty()) {
                return false
            }

            val method = psiElement.parent as PsiMethod
            return availableAnnotationTypes.areApplicableTo(method)
        }
        return false
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, psiElement: PsiElement) {
        val method = PsiTreeUtil.getParentOfType(psiElement, PsiMethod::class.java, false) ?: return

        ChooseAnnotationTypePopup(editor).show(availableAnnotationTypes, {
            PsiAnnotableUtil.addAnnotation(method, it)
        })
    }

    override fun startInWriteAction() = true
}
