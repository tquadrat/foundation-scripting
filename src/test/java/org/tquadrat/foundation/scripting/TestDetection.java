/*
 * ============================================================================
 *  Copyright © 2002-2021 by Thomas Thrien.
 *  All Rights Reserved.
 * ============================================================================
 *  Licensed to the public under the agreements of the GNU Lesser General Public
 *  License, version 3.0 (the "License"). You may obtain a copy of the License at
 *
 *       http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 */

package org.tquadrat.foundation.scripting;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.scripting.factory.JavaEngineFactory;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  Test the detection of the defined script engines.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 */
@ClassVersion( sourceVersion = "$Id: TestDetection.java 878 2021-02-20 19:56:13Z tquadrat $" )
public class TestDetection extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Test whether the script engine for Java can be found.
     *
     *  @throws Exception   Something went unexpectedly wrong.
     */
    @Test
    final void testDetectionJava() throws Exception
    {
        skipThreadTest();

        final var engineManager = new ScriptEngineManager();

        ScriptEngine engine;

        engine = engineManager.getEngineByExtension( JavaEngineFactory.EXTENSION );
        assertNotNull( engine );

        engine = engineManager.getEngineByName( JavaEngineFactory.ENGINE_NAME );
        assertNotNull( engine );

        engine = engineManager.getEngineByName( "Java" );
        assertNotNull( engine );

        for( final var mimeType : List.of( "text/x-java-source", "text/java", "text/x-java", "application/ms-java" ) )
        {
            engine = engineManager.getEngineByMimeType( mimeType );
            assertNotNull( engine );
        }
    }   //  testDetectionJava()
}
//  class TestDetection

/*
 *  End of File
 */