/*
 * ============================================================================
 *  Copyright © 2002-2023 by Thomas Thrien.
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

package org.tquadrat.foundation.scripting.factory;

import static java.lang.String.format;
import static java.lang.System.getProperty;
import static javax.script.ScriptEngine.ENGINE;
import static javax.script.ScriptEngine.ENGINE_VERSION;
import static javax.script.ScriptEngine.LANGUAGE;
import static javax.script.ScriptEngine.LANGUAGE_VERSION;
import static javax.script.ScriptEngine.NAME;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.CommonConstants.NULL_STRING;
import static org.tquadrat.foundation.lang.CommonConstants.PROPERTY_JAVA_VERSION;
import static org.tquadrat.foundation.lang.CommonConstants.PROPERTY_JVM_VERSION;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.util.StringUtils.isNotEmpty;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicLong;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.scripting.internal.JavaEngineImpl;

/**
 *  This is script engine factory for the Foundation &quot;Java&quot; script
 *  engine.
 *
 *  @author A. Sundararajan
 *  @modified    Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: JavaEngineFactory.java 1070 2023-09-29 17:09:34Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: JavaEngineFactory.java 1070 2023-09-29 17:09:34Z tquadrat $" )
@API( status = STABLE, since = "0.0.5" )
public class JavaEngineFactory implements ScriptEngineFactory
{
        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  The name of the script engine for the Java language: {@value}.
     */
    public static final String ENGINE_NAME = "Foundation Java";

    /**
     *  The extension for source files for this script engine: {@value}.
     */
    public static final String EXTENSION = "java";

    /**
     *  The name of the language that is supported by this script engine:
     *  {@value}.
     */
    public static final String LANGUAGE_NAME = "Java";

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The internal class counter.
     *
     *  @see #getClassName()
     *  @see #getNextClassNumber()
     */
    private static final AtomicLong m_NextClassNumber = new AtomicLong( 0L );

        /*------------------------*\
    ====** Static Initialisations **===========================================
        \*------------------------*/
    /**
     *  The supported extensions of the script files for this engine.
     */
    @SuppressWarnings( "StaticCollection" )
    private static final List<String> m_Extensions;

    /**
     *  The mime types of script file that are recognised by this engine.
     */
    @SuppressWarnings( "StaticCollection" )
    private static final List<String> m_MimeTypes;

    /**
     *  The names of the supported languages.
     */
    @SuppressWarnings( "StaticCollection" )
    private static final List<String> m_Names;

    static
    {
        m_Names = List.of( ENGINE_NAME, LANGUAGE_NAME );
        m_Extensions = List.of( EXTENSION );
        m_MimeTypes = List.of( "text/x-java-source", "text/java", "text/x-java", "application/ms-java" );
    }

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new instance of {@code JavaEngineFactory}.
     */
    public JavaEngineFactory() {}

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Generates a unique class name; used by
     *  {@link #getProgram(String...)}.
     *
     *  @return The new class name.
     */
    private static String getClassName()
    {
        final var retValue = "org_tquadrat_foundation_scripting_java_Main$%1$d".formatted( getNextClassNumber() );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getClassName()

    /**
     *  {@inheritDoc}
     *
     *  @return Always {@value #ENGINE_NAME}.
     *
     *  @see #ENGINE_NAME
     */
    @Override
    public final String getEngineName() { return ENGINE_NAME; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String getEngineVersion() { return getProperty( PROPERTY_JVM_VERSION ); }

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "AssignmentOrReturnOfFieldWithMutableType" )
    @Override
    public final List<String> getExtensions() { return m_Extensions; }

    /**
     *  {@inheritDoc}
     *
     *  @return Always {@value #LANGUAGE_NAME};
     */
    @Override
    public final String getLanguageName() { return LANGUAGE_NAME; }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String getLanguageVersion() { return getProperty( PROPERTY_JAVA_VERSION ); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String getMethodCallSyntax( final String objectName, final String methodName, final String... args )
    {
        final var retValue = new StringBuilder( requireNonNullArgument( objectName, "objectName" ) )
            .append( "." )
            .append( requireNonNullArgument( methodName, "methodName" ) )
            .append( "(" );
        if( nonNull( args ) && (args.length > 0) )
        {
            final var joiner = new StringJoiner( ", ", " ", " " );
            for( final var arg : args ) joiner.add( arg );
            retValue.append( joiner.toString() );
        }
        retValue.append( ")" );

        //---* Done *----------------------------------------------------------
        return retValue.toString();
    }   //  getMethodCallSyntax()

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "AssignmentOrReturnOfFieldWithMutableType" )
    @Override
    public final List<String> getMimeTypes() { return m_MimeTypes; }

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "AssignmentOrReturnOfFieldWithMutableType" )
    @Override
    public final List<String> getNames() { return m_Names; }

    /**
     *  Determines the next class number.
     *
     *  @return The next class number.
     */
    private static long getNextClassNumber() { return m_NextClassNumber.getAndIncrement(); }

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String getOutputStatement( final String toDisplay )
    {
        final var argument = format( "\"%s\"", nonNull( toDisplay ) ? toDisplay : NULL_STRING );
        final var retValue = getMethodCallSyntax( "System.out", "print", argument );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getOutputStatement()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String getParameter( final String key )
    {
        final var retValue = switch( key )
        {
            case ENGINE -> getEngineName();
            case ENGINE_VERSION -> getEngineVersion();
            case NAME, LANGUAGE -> getLanguageName();
            case LANGUAGE_VERSION -> getLanguageVersion();
            case "THREADING" -> "MULTITHREADED";
            default -> null;
        };

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getParameter()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final String getProgram( final String... statements )
    {
        /*
         * We generate a Main class with main method that contains all the
         * given statements.
         */
        final var retValue = new StringBuilder();
        retValue.append( "class " )
            .append( getClassName() )
            .append(
                """

                {
                    public static void main( String... args )
                    {
                """ );
        if( nonNull( statements ) )
        {
            for( final var statement : statements )
            {
                if( isNotEmpty( statement ) )
                {
                    retValue.append( "        " ).append( statement );
                    if( !statement.endsWith( ";" ) )
                    {
                        retValue.append( ";");
                    }
                }
                retValue.append( "\n" );
            }
        }
        retValue.append(
            """
                }
            }""" );

        //---* Done *----------------------------------------------------------
        return retValue.toString();
    }   //  getProgram()

    /**
     *  {@inheritDoc}
     */
    @Override
    public final ScriptEngine getScriptEngine()
    {
        final var retValue = new JavaEngineImpl( this );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getScriptEngine()
}
//  class JavaEngineFactory

/*
 *  End of File
 */