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