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

package io.github.trubitsyn.visiblefortesting.extension

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.impl.source.codeStyle.ImportHelper
import org.jetbrains.kotlin.idea.caches.resolve.resolveImportReference
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile

fun PsiJavaFile.smartImportClass(qualifiedName: String, clazz: PsiClass?): String {
    val name = qualifiedName.substringAfterLast('.')
    val imports = importList
            ?.importStatements
            ?.filter { (it.qualifiedName?.endsWith(name) == true) && it.qualifiedName != qualifiedName }

    val useQualifiedName = imports != null && !imports.isEmpty()

    if (!useQualifiedName) {
        if (!ImportHelper.isAlreadyImported(this, qualifiedName)) {
            importClass(clazz)
        }
    }

    return if (useQualifiedName) qualifiedName else name
}

fun KtFile.smartImportClass(qualifiedName: String): String {
    val name = qualifiedName.substringAfterLast('.')

    val imports = importList?.imports
            ?.filter { (it.importedFqName?.asString()?.endsWith(name) == true) && it.importedFqName?.asString() != qualifiedName }

    val useQualifiedName = imports != null && !imports.isEmpty()

    if (!useQualifiedName && findImportByAlias(qualifiedName) == null) {
        this.resolveImportReference(FqName(qualifiedName))
    }

    return if (useQualifiedName) qualifiedName else name
}