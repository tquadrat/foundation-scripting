/*
 * ============================================================================
 * Copyright © 2002-2021 by Thomas Thrien.
 * All Rights Reserved.
 * ============================================================================
 *
 * Licensed to the public under the agreements of the GNU Lesser General Public
 * License, version 3.0 (the "License"). You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

/**
 *  Foundation <i>Scripting</i> provides scripting capabilities (JSR223) to the
 *  Foundation library set.
 *
 *  @provides javax.script.ScriptEngineFactory  The Script Engine Factory for
 *      the Java programming language.
 *
 *  @todo task.list
 */
module org.tquadrat.foundation.scripting
{
    requires java.base;
    requires transitive java.compiler;
    requires transitive java.scripting;

    //---* The foundation modules *--------------------------------------------
    requires org.tquadrat.foundation.base;
    requires org.tquadrat.foundation.util;

    //---* The exports *-------------------------------------------------------
    exports org.tquadrat.foundation.scripting.java;

    //---* The services *------------------------------------------------------
    provides javax.script.ScriptEngineFactory
        with org.tquadrat.foundation.scripting.factory.JavaEngineFactory;
}

/*
 *  End of File
 */