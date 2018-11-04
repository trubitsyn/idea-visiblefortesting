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

package org.trubitsyn.visiblefortesting

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import java.io.File

abstract class AbstractIntentionTest(val action: IntentionAction) : LightCodeInsightFixtureTestCase() {

    override fun getTestDataPath(): String {
        return "src/test/resources/" + action::class.simpleName
    }

    protected fun runTest(targetClass: String, before: String, after: String) {
        val text = File("src/test/resources/classes/$targetClass").readText()
        myFixture.addClass(text)
        myFixture.configureByFile(before)
        assertNotNull(action)
        myFixture.launchAction(action)
        myFixture.checkResultByFile(after)
    }
}