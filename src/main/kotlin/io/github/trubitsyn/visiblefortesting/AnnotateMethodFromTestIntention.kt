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
import com.intellij.psi.*
import com.intellij.psi.util.PsiUtil
import com.intellij.util.IncorrectOperationException
import org.jetbrains.annotations.NonNls

class AnnotateMethodFromTestIntention : BaseElementAtCaretIntentionAction() {

    @NonNls
    override fun getFamilyName() = CodeInsightBundle.message("intention.add.annotation.family")

    override fun isAvailable(project: Project, editor: Editor, element: PsiElement): Boolean {
        val availableAnnotations = Annotations.getAvailable(project)

        if (availableAnnotations.isEmpty()) {
            return false
        }

        if (!ProjectRootsUtil.isInTestSource(element.containingFile)) {
            return false
        }

        if (element is PsiIdentifier && element.parent is PsiReferenceExpression) {
            if (element.parent.parent is PsiMethodCallExpression) {
                val call = element.parent.parent as PsiMethodCallExpression
                val method = call.resolveMethod() ?: return false
                val javaFile = element.containingFile as PsiJavaFile
                val currentPackage = JavaPsiFacade.getInstance(project).findPackage(javaFile.packageName) ?: return false

                if (!PsiUtil.isAccessibleFromPackage(method, currentPackage)) {
                    text = "Annotate '${method.containingClass?.name}.${method.name}' as @VisibleForTesting"
                    return true
                }
            }
        }
        return false
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, element: PsiElement) {
        val call = element.parent.parent as PsiMethodCallExpression
        val method = call.resolveMethod() ?: return

        AnnotationChooser.choose(project, editor, {
            AnnotationApplier.addAnnotation(method, it)
        })
    }

    override fun startInWriteAction() = true
}
