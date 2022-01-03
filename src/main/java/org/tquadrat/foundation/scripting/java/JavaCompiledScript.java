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
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import javax.script.CompiledScript;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;

/**
 *  The base class for an implementation of
 *  {@link CompiledScript}
 *  for the Java language.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: JavaCompiledScript.java 878 2021-02-20 19:56:13Z tquadrat $
 *  @UMLGraph.link
 *  @since 0.1.0
 */
@ClassVersion( sourceVersion = "$Id: JavaCompiledScript.java 878 2021-02-20 19:56:13Z tquadrat $" )
@API( status = STABLE, since = "0.1.0" )
public abstract sealed class JavaCompiledScript extends CompiledScript
    permits org.tquadrat.foundation.scripting.internal.JavaEngineImpl.JavaCompiledScriptImpl
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The class that is represented by this object.
     */
    private final Class<?> m_ScriptClass;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code JavaCompiledScript} instance.
     *
     *  @param  scriptClass The class that is represented by this compiled
     *      script.
     */
    protected JavaCompiledScript( final Class<?> scriptClass )
    {
        m_ScriptClass = requireNonNullArgument( scriptClass, "scriptClass" );
    }   //  JavaCompiledScript()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Returns a reference to the Java class that is represented by this
     *  script.
     *
     *  @return The reference to the class object.
     */
    protected final Class<?> getScriptClass() { return m_ScriptClass; }
}
//  class JavaCompiledScript

/*
 *  End of File
 */