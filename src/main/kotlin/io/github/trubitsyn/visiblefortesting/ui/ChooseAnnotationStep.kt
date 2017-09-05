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

package io.github.trubitsyn.visiblefortesting.ui

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.PopupStep
import com.intellij.openapi.ui.popup.util.BaseListPopupStep
import com.intellij.psi.PsiClass

class ChooseAnnotationStep(psiClasses: List<PsiClass?>, private val project: Project, private val onSelected: (psiClass: PsiClass) -> Unit) : BaseListPopupStep<PsiClass>("Choose class", psiClasses) {

    override fun isAutoSelectionEnabled() = false

    override fun isSpeedSearchEnabled() = true

    override fun onChosen(selectedValue: PsiClass?, finalChoice: Boolean): PopupStep<*>? {
        return when {
            selectedValue == null -> PopupStep.FINAL_CHOICE

            finalChoice -> doFinalStep {
                WriteCommandAction.runWriteCommandAction(project, {
                    onSelected(selectedValue)
                })
            }

            else -> super.onChosen(selectedValue, finalChoice)
        }
    }

    override fun hasSubstep(selectedValue: PsiClass?) = false

    override fun getTextFor(value: PsiClass?) = value!!.qualifiedName!!

    override fun getIconFor(value: PsiClass?) = value!!.getIcon(0)
}