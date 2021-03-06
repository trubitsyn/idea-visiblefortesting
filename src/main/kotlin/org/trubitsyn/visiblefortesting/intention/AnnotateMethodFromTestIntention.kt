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
import com.intellij.psi.*
import com.intellij.psi.util.PsiUtil
import com.intellij.util.IncorrectOperationException
import org.trubitsyn.visiblefortesting.annotable.PsiAnnotableUtil
import org.trubitsyn.visiblefortesting.annotation.AnnotationTypes
import org.trubitsyn.visiblefortesting.annotation.base.AnnotationType
import org.trubitsyn.visiblefortesting.extension.areApplicableTo
import org.trubitsyn.visiblefortesting.ui.ChooseAnnotationTypePopup
import org.jetbrains.annotations.NonNls

class AnnotateMethodFromTestIntention : BaseElementAtCaretIntentionAction() {
    var availableAnnotationTypes: List<AnnotationType> = emptyList()

    @NonNls
    override fun getFamilyName() = CodeInsightBundle.message("intention.add.annotation.family")

    override fun isAvailable(project: Project, editor: Editor, element: PsiElement): Boolean {
        if (!ProjectRootsUtil.isInTestSource(element.containingFile)) {
            return false
        }

        if (isMethodCallExpression(element)) {
            val call = element.parent.parent as PsiMethodCallExpression
            val method = call.resolveMethod() ?: return false
            val javaFile = element.containingFile as PsiJavaFile
            val currentPackage = JavaPsiFacade
                    .getInstance(project)
                    .findPackage(javaFile.packageName) ?: return false

            if (PsiUtil.isAccessibleFromPackage(method, currentPackage)) {
                return false
            }

            if (availableAnnotationTypes.isEmpty()) {
                availableAnnotationTypes = AnnotationTypes.available(project)
            }

            if (availableAnnotationTypes.isEmpty()) {
                return false
            }

            if (availableAnnotationTypes.areApplicableTo(method)) {
                text = "Annotate '${method.containingClass?.name}.${method.name}' as @VisibleForTesting"
                return true
            }
        }
        return false
    }

    private fun isMethodCallExpression(element: PsiElement): Boolean {
        return (element is PsiIdentifier &&
                element.parent is PsiReferenceExpression &&
                element.parent.parent is PsiMethodCallExpression)
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, element: PsiElement) {
        val call = element.parent.parent as PsiMethodCallExpression
        val method = call.resolveMethod() ?: return

        ChooseAnnotationTypePopup(editor).show(availableAnnotationTypes, {
            PsiAnnotableUtil.addAnnotation(method, it)
        })
    }

    override fun startInWriteAction() = true
}
