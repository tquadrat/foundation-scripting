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

package org.tquadrat.foundation.scripting.java;

import static org.apiguardian.api.API.Status.STABLE;

import javax.script.Compilable;
import javax.script.ScriptEngine;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;

/**
 *  This is the script engine for the Java programming language.
 *
 *  @extauthor  Thomas Thrien - thomas.thrien@tquadrat.org
 *  @thanks A. Sundararajan
 *  @version $Id: JavaEngine.java 878 2021-02-20 19:56:13Z tquadrat $
 *  @since 0.1.0
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: JavaEngine.java 878 2021-02-20 19:56:13Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public sealed interface JavaEngine extends ScriptEngine, Compilable permits org.tquadrat.foundation.scripting.internal.JavaEngineImpl
{
        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The name for the variable that holds the classpath: {@value}.
     */
    public static final String CLASSPATH = "classpath";

    /**
     *  The name for the variable that holds the name of the main class:
     *  {@value}.
     */
    public static final String MAINCLASS = "mainClass";

    /**
     *  The name for the variable that holds the class of the parent class
     *  loader: {@value}.
     */
    public static final String PARENTLOADER = "parentLoader";

    /**
     *  The name for the variable that holds the source path (the location for
     *  additional source files): {@value}.
     */
    public static final String SOURCEPATH = "sourcepath";

    /**
     *  For certain variables, we look for System properties. This is the
     *  prefix used for such System properties: {@value}.
     *
     *  @see System#getProperties()
     */
    public static final String SYSPROP_PREFIX = "com.sun.script.java.";

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
}
//  class JavaEngine

/*
 *  End of File
 */