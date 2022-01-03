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

package org.tquadrat.foundation.scripting.spi;

import static java.util.stream.Collectors.joining;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.tquadrat.foundation.lang.Objects.requireNonNull;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;

/**
 *  This class is meant as a base class for a concrete implementation of a
 *  script engine. Based on
 *  {@link AbstractScriptEngine}
 *  it will provide some additional methods for convenience.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: ScriptEngineBase.java 878 2021-02-20 19:56:13Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: ScriptEngineBase.java 878 2021-02-20 19:56:13Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.5" )
public abstract class ScriptEngineBase extends AbstractScriptEngine
{
        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The name for the variable that holds the command line arguments:
     *  {@value}.
     */
    public static final String ARGUMENTS = "arguments";

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The factory that created this engine; may be {@code null}
     */
    private volatile ScriptEngineFactory m_EngineFactory;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code ScriptEngineBase} instance.
     */
    protected ScriptEngineBase() { this( null ); }

    /**
     *  Creates a new {@code ScriptEngineBase} instance.
     *
     *  @param  factory The reference to the engine factory; may be
     *      {@code null}.
     */
    protected ScriptEngineBase( final ScriptEngineFactory factory )
    {
        m_EngineFactory = factory;
    }   //  ScriptEngineBase()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     *
     *  @see javax.script.ScriptEngine#createBindings()
     */
    @Override
    public Bindings createBindings() { return new SimpleBindings(); }

    /**
     *  {@inheritDoc}
     *
     *  @see javax.script.ScriptEngine#getFactory()
     */
    @Override
    public synchronized ScriptEngineFactory getFactory() { return m_EngineFactory; }

    /**
     *  Reads the complete contents from a
     *  {@link Reader}
     *  into a String.
     *
     *  @param  reader  The reader
     *  @return The resulting String.
     *  @throws ScriptException Problems with reading from the reader.
     */
    protected static final String readToString( final Reader reader ) throws ScriptException
    {
        final String retValue;
        try( final var r = new BufferedReader( requireNonNull( reader, "reader" ) ) )
        {
            retValue = r.lines()
                .collect( joining( "\n" ) );
        }
        catch( final IOException e )
        {
            throw new ScriptException( e );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  readToString()

    /**
     *  Sets a new script factory.
     *
     *  @param  factory The new factory; can be {@code null}.
     */
    public final synchronized void setFactory( final ScriptEngineFactory factory ) { m_EngineFactory = factory; }
}
//  class ScriptEngineBase

/*
 *  End of File
 */