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

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.ide.projectView.impl.ProjectRootsUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiJavaToken
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.ui.popup.list.ListPopupImpl
import com.intellij.util.IncorrectOperationException
import org.jetbrains.annotations.NonNls

class AnnotateClassMethodsIntention : PsiElementBaseIntentionAction() {

    @NonNls
    override fun getText() = "Annotate methods as @VisibleForTesting"

    @NonNls
    override fun getFamilyName() = text

    override fun isAvailable(project: Project, editor: Editor, psiElement: PsiElement): Boolean {
        val availableAnnotations = Annotations.getAvailable(project)

        if (availableAnnotations.isEmpty()) {
            return false;
        }

        if (ProjectRootsUtil.isInTestSource(psiElement.containingFile)) {
            return false
        }

        if (psiElement is PsiJavaToken && psiElement.parent is PsiClass) {
            val psiClass = psiElement.parent as PsiClass

            return psiClass.methods.asSequence().any { method ->
                availableAnnotations.any { annotation ->
                    AnnotationApplier.canAnnotate(method, annotation)
                }
            }
        }
        return false
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, psiElement: PsiElement) {
        val psiClass = psiElement.parent as PsiClass

        val annotations = Annotations.getAvailable(project)

        if (annotations.size == 1) {
            psiClass.methods
                    .asSequence()
                    .filter { AnnotationApplier.canAnnotate(it, annotations[0]) }
                    .forEach { AnnotationApplier.addAnnotation(it, annotations[0]) }
        } else {
            val facade = JavaPsiFacade.getInstance(project)
            val scope = GlobalSearchScope.allScope(project)
            val psiClasses = annotations.map { facade.findClass(it.qualifiedName, scope) }

            val importDialog = ChooseClassDialog(psiClasses, project, { selectedClass ->
                val annotation = annotations.first { it.qualifiedName == selectedClass.qualifiedName }
                psiClass.methods
                        .asSequence()
                        .filter { AnnotationApplier.canAnnotate(it, annotation) }
                        .forEach { AnnotationApplier.addAnnotation(it, annotation) }
            })

            ListPopupImpl(importDialog).showInBestPositionFor(editor)
        }
    }

    override fun startInWriteAction() = true
}
