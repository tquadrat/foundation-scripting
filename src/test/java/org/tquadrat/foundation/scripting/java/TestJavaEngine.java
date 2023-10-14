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

import static java.io.OutputStream.nullOutputStream;
import static java.io.Reader.nullReader;
import static java.io.Writer.nullWriter;
import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.lang.System.setErr;
import static javax.script.ScriptContext.ENGINE_SCOPE;
import static javax.script.ScriptEngine.ENGINE;
import static javax.script.ScriptEngine.ENGINE_VERSION;
import static javax.script.ScriptEngine.FILENAME;
import static javax.script.ScriptEngine.LANGUAGE;
import static javax.script.ScriptEngine.LANGUAGE_VERSION;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_STRING;
import static org.tquadrat.foundation.lang.CommonConstants.PROPERTY_JAVA_VERSION;
import static org.tquadrat.foundation.lang.CommonConstants.PROPERTY_JVM_VERSION;
import static org.tquadrat.foundation.scripting.java.JavaEngine.CLASSPATH;
import static org.tquadrat.foundation.scripting.java.JavaEngine.MAINCLASS;
import static org.tquadrat.foundation.scripting.java.JavaEngine.PARENTLOADER;
import static org.tquadrat.foundation.scripting.java.JavaEngine.SOURCEPATH;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Collection;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.NullArgumentException;
import org.tquadrat.foundation.exception.ValidationException;
import org.tquadrat.foundation.scripting.factory.JavaEngineFactory;
import org.tquadrat.foundation.testutil.TestBaseClass;

/**
 *  The JUnit tests for
 *  {@link JavaEngine}
 *  and
 *  {@link JavaEngineFactory}.
 *
 *  @extauthor Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: TestJavaEngine.java 1076 2023-10-03 18:36:07Z tquadrat $
 */
@ClassVersion( sourceVersion = "$Id: TestJavaEngine.java 1076 2023-10-03 18:36:07Z tquadrat $" )
public class TestJavaEngine extends TestBaseClass
{
        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Prepares a single test, the call to one of the test methods.
     *
     *  @throws Exception   Failing the preparation.
     */
    @SuppressWarnings( "static-method" )
    @BeforeEach
    public final void setUp() throws Exception
    {
        //---* Swallow the error output *--------------------------------------
        /*
         * The change of the default error stream will be reverted
         * automatically through the base class.
         */
        //noinspection ImplicitDefaultCharsetUsage
        setErr( new PrintStream( nullOutputStream() ) );
    }   //  setUp()

    /**
     *  Some tests for the class
     *  {@link JavaEngineFactory};
     *  usually is this class used to load a
     *  {@link JavaEngine}.
     *
     *  @throws Exception   Something unexpected went wrong.
     */
    @Test
    public final void testJavaEngineFactory() throws Exception
    {
        skipThreadTest();

        String actual, expected;

        final var factory = new JavaEngineFactory();
        assertNotNull( factory );

        assertEquals( JavaEngineFactory.ENGINE_NAME, factory.getEngineName() );
        assertEquals( getProperty( PROPERTY_JVM_VERSION ), factory.getEngineVersion() );
        assertEquals( JavaEngineFactory.LANGUAGE_NAME, factory.getLanguageName() );
        assertEquals( getProperty( PROPERTY_JAVA_VERSION ), factory.getLanguageVersion() );

        final Collection<String> extensions = new HashSet<>( factory.getExtensions() );
        assertTrue( extensions.remove( "java" ) );
        assertTrue( extensions.isEmpty(), "Unexpected Extension" );

        final Collection<String> mimeTypes = new HashSet<>( factory.getMimeTypes() );
        assertTrue( mimeTypes.remove( "text/x-java-source" ) );
        assertTrue( mimeTypes.remove( "text/java" ) );
        assertTrue( mimeTypes.remove( "text/x-java" ) );
        assertTrue( mimeTypes.remove( "application/ms-java" ) );
        assertTrue( mimeTypes.isEmpty(), "Unexpected Mime Type" );

        final Collection<String> names = new HashSet<>( factory.getNames() );
        assertTrue( names.remove( JavaEngineFactory.LANGUAGE_NAME ) );
        assertTrue( names.remove( JavaEngineFactory.ENGINE_NAME ) );
        assertTrue( names.isEmpty(), "Unexpected Name" );

        assertEquals( factory.getEngineName(), factory.getParameter( ENGINE ) );
        assertEquals( factory.getEngineVersion(), factory.getParameter( ENGINE_VERSION ) );
        assertEquals( factory.getLanguageName(), factory.getParameter( LANGUAGE ) );
        assertEquals( factory.getLanguageVersion(), factory.getParameter( LANGUAGE_VERSION ) );
        assertEquals( "MULTITHREADED", factory.getParameter( "THREADING" ) );
        assertNull( factory.getParameter( "NotExistingParameter" ) );

        final var engine = factory.getScriptEngine();
        assertNotNull( engine );
        assertTrue( engine instanceof JavaEngine );

        //---* getMethodCallSyntax() *-----------------------------------------
        final Class<? extends Throwable> expectedException = NullArgumentException.class;
        try
        {
            actual = factory.getMethodCallSyntax( null, "method" );
            assertNotNull( actual );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }
        try
        {
            actual = factory.getMethodCallSyntax( "object", null );
            assertNotNull( actual );
            fail( () -> format( MSG_ExceptionNotThrown, expectedException.getName() ) );
        }
        catch( final AssertionError e ) { throw e; }
        catch( final Throwable t )
        {
            final var isExpectedException = expectedException.isInstance( t );
            assertTrue( isExpectedException, () -> format( MSG_WrongExceptionThrown, expectedException.getName(), t.getClass().getName() ) );
        }

        expected = "object.method()";
        actual = factory.getMethodCallSyntax( "object", "method" );
        assertNotNull( actual );
        assertEquals( expected, actual );

        expected = "object.method( arg1 )";
        actual = factory.getMethodCallSyntax( "object", "method", "arg1" );
        assertNotNull( actual );
        assertEquals( expected, actual );

        expected = "object.method( arg1, arg2 )";
        actual = factory.getMethodCallSyntax( "object", "method", "arg1", "arg2" );
        assertNotNull( actual );
        assertEquals( expected, actual );

        //---* getOutputStatement() *------------------------------------------
        expected = "System.out.print( \"null\" )";
        actual = factory.getOutputStatement( null );
        assertNotNull( actual );
        assertEquals( expected, actual );

        expected = "System.out.print( \"Text\" )";
        actual = factory.getOutputStatement( "Text" );
        assertNotNull( actual );
        assertEquals( expected, actual );

        /*
         * getProgram() will be tested in testJavaEngine().
         */
    }   //  testJavaEngineFactory()

    /**
     *  Some tests for the class
     *  {@link JavaEngine}.
     *
     *  @throws Exception   Something unexpected went wrong.
     */
    @SuppressWarnings( "resource" )
    @Test
    public final void testJavaEngine() throws Exception
    {
        final var factory = new JavaEngineFactory();
        assertNotNull( factory );

        final var eng = factory.getScriptEngine();
        assertNotNull( eng );
        assertTrue( eng instanceof JavaEngine );
        final var engine = (JavaEngine) eng;

        final var bindings = engine.createBindings();
        assertNotNull( bindings );

        assertNotNull( engine.getFactory() );
        assertEquals( factory, engine.getFactory() );
        assertSame( factory, engine.getFactory() );

        final var reader = nullReader();
        final var string = EMPTY_STRING;

        assertThrows( ValidationException.class, () -> engine.compile( (Reader) null ) );
        assertThrows( ValidationException.class, () -> engine.compile( (String) null ) );
        assertThrows( ValidationException.class, () -> engine.eval( (Reader) null ) );
        assertThrows( ValidationException.class, () -> engine.eval( (String) null ) );

        assertThrows( ScriptException.class, () -> engine.compile( nullReader() ) );
        assertThrows( ScriptException.class, () -> engine.compile( EMPTY_STRING ) );

        final ScriptContext scriptContext = createMock( ScriptContext.class );
        replayAll();

        assertThrows( ValidationException.class, () -> engine.eval( (Reader) null, scriptContext ) );
        assertThrows( ValidationException.class, () -> engine.eval( (String) null, scriptContext ) );
        resetAll();

        assertThrows( ValidationException.class, () -> engine.eval( reader, (ScriptContext) null ) );
        assertThrows( ValidationException.class, () -> engine.eval( string, (ScriptContext) null ) );

        expect( scriptContext.getAttributesScope( CLASSPATH ) ).andReturn( -1 ).anyTimes();
        expect( scriptContext.getAttributesScope( FILENAME ) ).andReturn( -1 ).anyTimes();
        expect( scriptContext.getAttributesScope( MAINCLASS ) ).andReturn( -1 ).anyTimes();
        expect( scriptContext.getAttributesScope( PARENTLOADER ) ).andReturn( -1 ).anyTimes();
        expect( scriptContext.getAttributesScope( SOURCEPATH ) ).andReturn( -1 ).anyTimes();
        expect( scriptContext.getErrorWriter() ).andReturn( nullWriter() ).anyTimes();
        scriptContext.setAttribute( "context", scriptContext, ENGINE_SCOPE );
        expectLastCall().anyTimes();
        replayAll();

        assertThrows( ScriptException.class, () -> engine.eval( nullReader(), scriptContext ) );
        assertThrows( ScriptException.class, () -> engine.eval( string, scriptContext ) );

        //---* Testing JavaEngineFactory.getProgram() *------------------------
        String actual, expected;
        CompiledScript compiledScript;

        expected =
            """
            class org_tquadrat_foundation_scripting_java_Main$0
            {
                public static void main( String... args )
                {
                }
            }""";
        compiledScript = engine.compile( expected );
        assertNotNull( compiledScript );
        actual = factory.getProgram();
        assertNotNull( actual );
        assertEquals( expected, actual );
        compiledScript = engine.compile( actual );
        assertNotNull( compiledScript );

        expected = format(
            """
            class org_tquadrat_foundation_scripting_java_Main$1
            {
                public static void main( String... args )
                {
                    %s;
                }
            }""",
            factory.getMethodCallSyntax( "System.out", "print", "\"Text\"" ) );
        compiledScript = engine.compile( expected );
        assertNotNull( compiledScript );
        actual = factory.getProgram( factory.getMethodCallSyntax( "System.out", "print", "\"Text\"" ) );
        assertNotNull( actual );
        assertEquals( expected, actual );
        compiledScript = engine.compile( actual );
        assertNotNull( compiledScript );

        expected = format(
            """
            class org_tquadrat_foundation_scripting_java_Main$2
            {
                public static void main( String... args )
                {
                    %s;
                }
            }""",
            factory.getMethodCallSyntax( "System.out", "print", "\"Text\"" ) );
        compiledScript = engine.compile( expected );
        assertNotNull( compiledScript );
        actual = factory.getProgram( factory.getOutputStatement( "Text" ) );
        assertNotNull( actual );
        assertEquals( expected, actual );
        compiledScript = engine.compile( actual );
        assertNotNull( compiledScript );
    }   //  testJavaEngine()
}
//  class TestJavaEngine

/*
 *  End of File
 */