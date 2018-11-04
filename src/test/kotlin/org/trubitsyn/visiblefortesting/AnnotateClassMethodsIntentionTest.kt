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

import org.trubitsyn.visiblefortesting.intention.AnnotateClassMethodsIntention
import org.junit.Test


class AnnotateClassMethodsIntentionTest : AbstractIntentionTest(AnnotateClassMethodsIntention()) {

    @Test
    fun testAndroid() {
        runTest("AndroidVisibleForTesting.java",
                "android/before.template.java",
                "android/before.template.after.java")
    }

    @Test
    fun testGuava() {
        runTest("GuavaVisibleForTesting.java",
                "guava/before.template.java",
                "guava/before.template.after.java")
    }
}
