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

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.ide.projectView.impl.ProjectRootsUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiJavaToken
import com.intellij.util.IncorrectOperationException
import io.github.trubitsyn.visiblefortesting.annotation.Annotations
import io.github.trubitsyn.visiblefortesting.ui.ChooseAnnotationPopup
import org.jetbrains.annotations.NonNls

class AnnotateClassMethodsIntention : PsiElementBaseIntentionAction() {

    @NonNls
    override fun getText() = "Annotate methods as @VisibleForTesting"

    @NonNls
    override fun getFamilyName() = text

    override fun isAvailable(project: Project, editor: Editor, psiElement: PsiElement): Boolean {
        if (ProjectRootsUtil.isInTestSource(psiElement.containingFile)) {
            return false
        }

        if (psiElement is PsiJavaToken && psiElement.parent is PsiClass) {
            val availableAnnotations = Annotations.available(project)

            if (availableAnnotations.isEmpty()) {
                return false
            }

            val psiClass = psiElement.parent as PsiClass
            return psiClass.methods
                    .asSequence()
                    .any { method -> Annotations.areApplicableTo(method, availableAnnotations) }
        }
        return false
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, psiElement: PsiElement) {
        val psiClass = psiElement.parent as PsiClass

        val availableAnnotations = Annotations.available(project)

        val applicableAnnotations = psiClass.methods
                .flatMap { method -> availableAnnotations.filter { it.isApplicableTo(method) } }
                .toSet()
                .sortedBy { it.qualifiedName }
                .toList()

        ChooseAnnotationPopup(editor).show(applicableAnnotations, { annotation ->
            psiClass.methods
                    .asSequence()
                    .filter { Annotations.areApplicableTo(it, applicableAnnotations) }
                    .forEach { annotation.applyTo(it) }
        })
    }

    override fun startInWriteAction() = true
}
