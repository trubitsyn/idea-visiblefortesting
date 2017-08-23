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

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiExpression
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifier

class AndroidAnnotation : Annotation {
    override val name = "VisibleForTesting"
    override val qualifiedName = "android.support.annotation.VisibleForTesting"

    override fun buildAttributes(method: PsiMethod, onAttributeBuilt: (attribute: String, value: PsiExpression) -> Unit) {

        if (!method.hasModifierProperty(PsiModifier.PRIVATE)) {
            val value = JavaPsiFacade.getElementFactory(method.project)
                    .createExpressionFromText("VisibleForTesting." + findModifier(method), method)

            onAttributeBuilt("otherwise", value)
        }
    }

    private fun findModifier(method: PsiMethod): String {
        return when {
            method.hasModifierProperty(PsiModifier.PRIVATE) -> "PRIVATE"
            method.hasModifierProperty(PsiModifier.PACKAGE_LOCAL) -> "PACKAGE_PRIVATE"
            method.hasModifierProperty(PsiModifier.PROTECTED) -> "PROTECTED"
            else -> "PUBLIC"
        }
    }
}