/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package org.thuthu.seamlyhem

import kotlin.test.Test
import kotlin.test.assertNotNull

class AppTest {
    @Test fun appHasAGreeting() {
        val classUnderTest = App()
        assertNotNull(classUnderTest.fileManager, "app should have a fileManager")
        assertNotNull(classUnderTest.iGenerator, "app should have a imageGenerator")
    }
}
