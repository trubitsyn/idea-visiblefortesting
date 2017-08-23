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

package io.github.trubitsyn.visiblefortesting

import com.intellij.codeInsight.CodeInsightBundle
import com.intellij.codeInsight.intention.BaseElementAtCaretIntentionAction
import com.intellij.ide.projectView.impl.ProjectRootsUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiJavaToken
import com.intellij.psi.PsiMethod
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.popup.list.ListPopupImpl
import com.intellij.util.IncorrectOperationException
import org.jetbrains.annotations.NonNls

class AnnotateMethodIntention : BaseElementAtCaretIntentionAction() {

    @NonNls
    override fun getText() = "Annotate as @VisibleForTesting"

    @NonNls
    override fun getFamilyName() = CodeInsightBundle.message("intention.add.annotation.family")

    override fun isAvailable(project: Project, editor: Editor, psiElement: PsiElement): Boolean {
        val availableAnnotations = Annotations.getAvailable(project)

        if (availableAnnotations.isEmpty()) {
            return false
        }

        if (ProjectRootsUtil.isInTestSource(psiElement.containingFile)) {
            return false
        }

        if (psiElement is PsiJavaToken && psiElement.parent is PsiMethod) {
            val method = psiElement.parent as PsiMethod
            return availableAnnotations.any { AnnotationApplier.canAnnotate(method, it) }
        }
        return false
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, psiElement: PsiElement) {
        val method = PsiTreeUtil.getParentOfType(psiElement, PsiMethod::class.java, false) ?: return

        val annotations = Annotations.getAvailable(project)

        if (annotations.size == 1) {
            AnnotationApplier.addAnnotation(method, annotations[0])
        } else {
            val facade = JavaPsiFacade.getInstance(project)
            val scope = GlobalSearchScope.allScope(project)
            val psiClasses = annotations.map { facade.findClass(it.qualifiedName, scope) }

            val importDialog = ChooseClassDialog(psiClasses, project, { psiClass ->
                AnnotationApplier.addAnnotation(method, annotations.first { it.qualifiedName == psiClass.qualifiedName})
            })

            ListPopupImpl(importDialog).showInBestPositionFor(editor)
        }
    }

    override fun startInWriteAction() = true
}
