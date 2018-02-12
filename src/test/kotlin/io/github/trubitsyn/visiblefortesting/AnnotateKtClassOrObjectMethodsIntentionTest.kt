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

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import io.github.trubitsyn.visiblefortesting.intention.AnnotateKtClassOrObjectMethodsIntention
import org.junit.Test
import java.io.File

class AnnotateKtClassOrObjectMethodsIntentionTest : LightCodeInsightFixtureTestCase() {

    override fun getTestDataPath() = "src/test/resources/AnnotateKtClassOrObjectMethodsIntention"

    @Test
    fun testAndroid() {
        runTest("src/test/resources/classes/AndroidVisibleForTesting.java",
                "android/before.template.kt",
                "android/before.template.after.kt")
    }

    @Test
    fun testGuava() {
        runTest("src/test/resources/classes/GuavaVisibleForTesting.java",
                "guava/before.template.kt",
                "guava/before.template.after.kt")
    }

    @Test
    fun testObjectGuava() {
        runTest("src/test/resources/classes/GuavaVisibleForTesting.java",
                "guava/before.object.template.kt",
                "guava/before.object.template.after.kt")
    }

    fun testObjectAndroid() {
        runTest("src/test/resources/classes/AndroidVisibleForTesting.java",
                "android/before.object.template.kt",
                "android/before.object.template.after.kt")
    }

    private fun runTest(targetClass: String, before: String, after: String) {
        val text = File(targetClass).readText()
        myFixture.addClass(text)
        myFixture.configureByFile(before)
        val action = AnnotateKtClassOrObjectMethodsIntention()
        assertNotNull(action)
        myFixture.launchAction(action)
        myFixture.checkResultByFile(after)
    }
}