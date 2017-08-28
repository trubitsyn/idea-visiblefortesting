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

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.ui.popup.list.ListPopupImpl

object AnnotationChooser {

    fun choose(project: Project, editor: Editor, onChosen: (annotation: Annotation) -> Unit) {
        val annotations = Annotations.getAvailable(project)

        if (annotations.size == 1) {
            onChosen(annotations[0])
        } else {
            val facade = JavaPsiFacade.getInstance(project)
            val scope = GlobalSearchScope.allScope(project)
            val psiClasses = annotations.map { facade.findClass(it.qualifiedName, scope) }

            val importDialog = ChooseClassDialog(psiClasses, project, { psiClass ->
                val matchingAnnotation = annotations.first { it.qualifiedName == psiClass.qualifiedName}
                onChosen(matchingAnnotation)
            })

            ListPopupImpl(importDialog).showInBestPositionFor(editor)
        }
    }
}