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

import com.intellij.ide.projectView.impl.ProjectRootsUtil
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiPackage
import com.intellij.psi.search.GlobalSearchScope
import io.github.trubitsyn.visiblefortesting.annotable.KtAnnotableUtil
import io.github.trubitsyn.visiblefortesting.annotation.Annotations
import io.github.trubitsyn.visiblefortesting.annotation.base.Annotation
import io.github.trubitsyn.visiblefortesting.ui.ChooseAnnotationPopup
import org.jetbrains.kotlin.idea.intentions.SelfTargetingIntention
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.idea.references.mainReference
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtReferenceExpression
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import org.jetbrains.kotlin.resolve.jvm.KotlinJavaPsiFacade

class AnnotateKtFunctionFromTestIntention : SelfTargetingIntention<KtReferenceExpression>(
        KtReferenceExpression::class.java,
        "Annotate as @VisibleForTesting"
) {
    var availableAnnotations: List<Annotation> = emptyList()

    override fun isApplicableTo(element: KtReferenceExpression, caretOffset: Int): Boolean {
        if (!ProjectRootsUtil.isInTestSource(element.containingFile)) {
            return false
        }

        if (element.parent !is KtCallExpression) {
            return false
        }

        val resolvedMethod = element.mainReference.resolve() ?: return false
        val function = resolvedMethod as KtFunction

        if (isAccessibleFromTestPackage(element, function)) {
            return false
        }

        if (availableAnnotations.isEmpty()) {
            availableAnnotations = Annotations.available(element.project)
        }

        if (availableAnnotations.isEmpty()) {
            return false
        }

        return Annotations.areApplicableTo(function, availableAnnotations)
    }

    private fun isAccessibleFromTestPackage(element: KtElement, function: KtFunction): Boolean {
        if (function.hasModifier(KtTokens.PUBLIC_KEYWORD)) return true

        val currentPackageName = element.containingKtFile.packageFqName.asString()
        val currentPackage = KotlinJavaPsiFacade
                .getInstance(element.project)
                .findPackage(currentPackageName, GlobalSearchScope.projectScope(element.project)) ?: return false

        if (function.hasModifier(KtTokens.PROTECTED_KEYWORD) &&
                (function.containingClass()?.hasModifier(KtTokens.OPEN_KEYWORD) == true) &&
                isInPackage(function, currentPackage)){
            return true
        }

        if (!function.hasModifier(KtTokens.PRIVATE_KEYWORD) && isInPackage(function, currentPackage)) {
            return true
        }
        return false
    }

    private fun isInPackage(function: KtFunction, pkg: PsiPackage): Boolean {
        val pkgName = pkg.getKotlinFqName()!!.asString()
        val funPkgName = function.containingKtFile.packageFqName.asString()

        if (pkgName == funPkgName) {
            return true
        }

        if (pkg.subPackages.any { it.getKotlinFqName()!!.asString() == funPkgName }) {
            return true
        }

        return false
    }

    override fun applyTo(element: KtReferenceExpression, editor: Editor?) {
        val function = element.mainReference.resolve() as KtFunction

        ChooseAnnotationPopup(editor!!).show(availableAnnotations) {
            KtAnnotableUtil.addAnnotation(function, it)
        }
    }
}