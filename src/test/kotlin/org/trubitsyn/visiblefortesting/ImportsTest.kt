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

package org.trubitsyn.visiblefortesting

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import org.trubitsyn.visiblefortesting.annotation.impl.AndroidAnnotationType
import org.trubitsyn.visiblefortesting.annotation.impl.GuavaAnnotationType
import org.trubitsyn.visiblefortesting.intention.AnnotateKtFunctionIntention
import org.trubitsyn.visiblefortesting.intention.AnnotateMethodIntention
import org.junit.Test
import java.io.File

class ImportsTest : LightCodeInsightFixtureTestCase() {

    override fun getTestDataPath() = "src/test/resources/imports"

    @Test
    fun testAndroidJava() {
        myFixture.addClass(
                File("src/test/resources/classes/AndroidVisibleForTesting.java").readText()
        )
        myFixture.addClass(
                File("src/test/resources/classes/GuavaVisibleForTesting.java").readText()
        )
        myFixture.configureByFile("java/imported_guava.before.java")
        val action = AnnotateMethodIntention()
        LightCodeInsightFixtureTestCase.assertNotNull(action)
        action.availableAnnotationTypes = listOf(AndroidAnnotationType())
        myFixture.launchAction(action)
        myFixture.checkResultByFile("java/imported_guava.after.java")
    }

    @Test
    fun testGuavaJava() {
        myFixture.addClass(
                File("src/test/resources/classes/GuavaVisibleForTesting.java").readText()
        )
        myFixture.addClass(
                File("src/test/resources/classes/AndroidVisibleForTesting.java").readText()
        )
        myFixture.configureByFile("java/imported_android.before.java")
        val action = AnnotateMethodIntention()
        LightCodeInsightFixtureTestCase.assertNotNull(action)
        action.availableAnnotationTypes = listOf(GuavaAnnotationType())
        myFixture.launchAction(action)
        myFixture.checkResultByFile("java/imported_android.after.java")
    }

    @Test
    fun testAndroidKt() {
        myFixture.addClass(
                File("src/test/resources/classes/AndroidVisibleForTesting.java").readText()
        )
        myFixture.addClass(
                File("src/test/resources/classes/GuavaVisibleForTesting.java").readText()
        )
        myFixture.configureByFile("kotlin/imported_guava.before.kt")
        val action = AnnotateKtFunctionIntention()
        LightCodeInsightFixtureTestCase.assertNotNull(action)
        action.availableAnnotationTypes = listOf(AndroidAnnotationType())
        myFixture.launchAction(action)
        myFixture.checkResultByFile("kotlin/imported_guava.after.kt")
    }

    @Test
    fun testGuavaKt() {
        myFixture.addClass(
                File("src/test/resources/classes/GuavaVisibleForTesting.java").readText()
        )
        myFixture.addClass(
                File("src/test/resources/classes/AndroidVisibleForTesting.java").readText()
        )
        myFixture.configureByFile("kotlin/imported_android.before.kt")
        val action = AnnotateKtFunctionIntention()
        LightCodeInsightFixtureTestCase.assertNotNull(action)
        action.availableAnnotationTypes = listOf(GuavaAnnotationType())
        myFixture.launchAction(action)
        myFixture.checkResultByFile("kotlin/imported_android.after.kt")
    }
}
