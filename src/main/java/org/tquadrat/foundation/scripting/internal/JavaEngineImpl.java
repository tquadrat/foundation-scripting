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

package org.tquadrat.foundation.scripting.internal;

import static java.lang.System.getProperty;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;
import static javax.script.ScriptContext.ENGINE_SCOPE;
import static org.apiguardian.api.API.Status.STABLE;
import static org.tquadrat.foundation.lang.CommonConstants.EMPTY_String_ARRAY;
import static org.tquadrat.foundation.lang.CommonConstants.PROPERTY_CLASSPATH;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.scripting.factory.JavaEngineFactory;
import org.tquadrat.foundation.scripting.java.JavaCompiledScript;
import org.tquadrat.foundation.scripting.java.JavaEngine;
import org.tquadrat.foundation.scripting.spi.ScriptEngineBase;

/**
 *  This is the script engine for the Java programming language.
 *
 *  @author A. Sundararajan
 *  @modified    Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: JavaEngineImpl.java 1070 2023-09-29 17:09:34Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: JavaEngineImpl.java 1070 2023-09-29 17:09:34Z tquadrat $" )
@API( status = STABLE, since = "0.0.5" )
public final class JavaEngineImpl extends ScriptEngineBase implements JavaEngine
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  An implementation of
     *  {@link CompiledScript}
     *  for the Java engine.
     *
     *  @author A. Sundararajan
     *  @modified    Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: JavaEngineImpl.java 1070 2023-09-29 17:09:34Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: JavaEngineImpl.java 1070 2023-09-29 17:09:34Z tquadrat $" )
    public final class JavaCompiledScriptImpl extends JavaCompiledScript
    {
            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new {@code JavaCompiledScriptImpl} instance.
         *
         *  @param  scriptClass The class that is represented by this compiled
         *      script.
         */
        public JavaCompiledScriptImpl( final Class<?> scriptClass )
        {
            super( requireNonNullArgument( scriptClass, "scriptClass" ) );
        }   //  JavaCompiledScriptImpl()

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  {@inheritDoc}
         *
         *  @see CompiledScript#eval(ScriptContext)
         */
        @Override
        public Object eval( final ScriptContext scriptContext ) throws ScriptException
        {
            final var retValue = evalClass( getScriptClass(), requireNonNullArgument( scriptContext, "scriptContext" ) );

            //---* Done *------------------------------------------------------
            return retValue;
        }   //  eval()

        /**
         *  {@inheritDoc}
         *
         *  @see CompiledScript#getEngine()
         */
        @Override
        public ScriptEngine getEngine() { return JavaEngineImpl.this; }
    }
    //  class JavaCompileScriptImpl

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The Java compiler that is used by this engine.
     */
    @SuppressWarnings( "UseOfConcreteClass" )
    private final JavaCompiler m_Compiler;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code JavaEngineImpl} instance.
     *
     *  @param factory  The reference to the script engine factory that was
     *      used to create this engine.
     */
    public JavaEngineImpl( final ScriptEngineFactory factory )
    {
        setFactory( factory );
        m_Compiler = new JavaCompiler();
    }   //  JavaEngineImpl()

    /**
     *  Creates a new {@code JavaEngineImpl} instance.
     */
    public JavaEngineImpl()
    {
        this( null );
    }   //  JavaEngineImpl()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     *
     *  @see Compilable#compile(Reader)
     */
    @Override
    public final CompiledScript compile( final Reader reader ) throws ScriptException
    {
        final var retValue = compile( readToString( requireNonNull( reader, "reader" ) ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  compile()

    /**
     *  {@inheritDoc}
     *
     *  @see Compilable#compile(String)
     */
    @Override
    public final CompiledScript compile( final String script ) throws ScriptException
    {
        final var scriptClass = parse( requireNonNull( script, "script" ), context );
        if( isNull( scriptClass ) )
        {
            throw new ScriptException( "A main class could not be determined for the provided script" );
        }
        final CompiledScript retValue = new JavaCompiledScriptImpl( scriptClass );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  compile()

    /**
     *  {@inheritDoc}
     *
     *  @see ScriptEngine#eval(Reader, ScriptContext)
     */
    @Override
    public final Object eval( final Reader reader, final ScriptContext scriptContext ) throws ScriptException
    {
        final var retValue = eval( readToString( requireNonNull( reader, "reader" ) ), requireNonNull( scriptContext, "scriptContext" ) );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  eval()

    /**
     *  {@inheritDoc}
     *
     *  @see ScriptEngine#eval(String, ScriptContext)
     */
    @Override
    public final Object eval( final String script, final ScriptContext scriptContext ) throws ScriptException
    {
        final var scriptClass = parse( requireNonNull( script, "script" ), requireNonNull( scriptContext, "scriptContext" ) );
        final var retValue = evalClass( scriptClass, scriptContext );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  eval()

    /**
     *  Executes the code defined in the Java class that makes up this script.
     *
     *  @param  scriptClass The script class; may be {@code null}.
     *  @param  context The script context.
     *  @return The script class; if the {@code scriptClass} is
     *      {@code null}, the return value is {@code null}, too.
     *  @throws ScriptException The script throws an exception.
     */
    private static final Object evalClass( final Class<?> scriptClass, final ScriptContext context ) throws ScriptException
    {
        //---* As required by JSR-223 *----------------------------------------
        context.setAttribute( "context", requireNonNull( context, "context" ), ENGINE_SCOPE );

        @SuppressWarnings( "UnnecessaryLocalVariable" )
        final var retValue = scriptClass;
        if( nonNull( retValue ) )
        {
            try
            {
                final var isPublicClass = isPublic( retValue.getModifiers() );

                //---* Find the setScriptContext() method *--------------------
                final var setContextMethod = findSetScriptContextMethod( retValue );

                //---* Call setScriptContext() and pass current context *------
                if( nonNull( setContextMethod ) )
                {
                    if( !isPublicClass )
                    {
                        //---* Try to relax access *---------------------------
                        setContextMethod.setAccessible( true );
                    }
                    setContextMethod.invoke( null, context );
                }

                //---* Find the main() method *--------------------------------
                final var mainMethod = findMainMethod( retValue );
                if( nonNull( mainMethod ) )
                {
                    if( !isPublicClass )
                    {
                        //---* Try to relax access *---------------------------
                        mainMethod.setAccessible( true );
                    }

                    //---* Get the command line arguments for main *-----------
                    final var args = getArguments( context );

                    //---* Call the main method *------------------------------
                    mainMethod.invoke( null, new Object [] {args} );
                }
            }
            catch( final IllegalAccessException | InvocationTargetException e )
            {
                throw new ScriptException( e );
            }
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  evalClass()

    /**
     *  Looks up the main class from the given list of classes. The main class
     *  is that one that has a method {@code main(String[])}.
     *
     *  @param  classes The candidates.
     *  @return The main class, or {@code null} if none could be found.
     */
    private static Class<?> findMainClass( final Iterable<Class<?>> classes )
    {
        Class<?> retValue = null;

        //---* Find a public class with main() *-------------------------------
        Method mainMethod;
        SearchPublicLoop: for( final var clazz : requireNonNullArgument( classes, "classes" ) )
        {
            if( isPublic( clazz.getModifiers() ) )
            {
                mainMethod = findMainMethod( clazz );
                if( nonNull( mainMethod ) )
                {
                    retValue = clazz;
                    break SearchPublicLoop;
                }
            }
        }   //  SearchPublicLoop:

        if( nonNull( retValue ) )
        {
            //---* Find a package private class with main() *------------------
            SearchPackageLoop: for( final var clazz : classes )
            {
                mainMethod = findMainMethod( clazz );
                if( nonNull( mainMethod ) )
                {
                    retValue = clazz;
                    break SearchPackageLoop;
                }
            }
        }   //  SearchPackageLoop:

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  findMainClass()

    /**
     *  Looks up the method {@code main(String[])} from the script class.
     *
     *  @param  clazz   The class to search.
     *  @return The {@code main()} method, or {@code null} if the class
     *      does not contain such a method.
     */
    private static Method findMainMethod( final Class<?> clazz )
    {
        Method retValue = null;
        try
        {
            final var method = requireNonNullArgument( clazz, "clazz" )
                .getMethod( "main", String [].class );
            final var modifiers = method.getModifiers();
            if( isPublic( modifiers ) && isStatic( modifiers ) )
            {
                retValue = method;
            }
        }
        catch( final NoSuchMethodException ignored ) { /* The exception will be ignored deliberately */ }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  findMainMethod()

    /**
     *  Looks up the method {@code setScriptContext(ScriptContext)} from the
     *  script call.
     *
     *  @param  clazz   The class to search.
     *  @return The {@code setScriptContext()} method, or {@code null} if
     *      the class does not contain such a method.
     */
    private static Method findSetScriptContextMethod( final Class<?> clazz )
    {
        Method retValue = null;
        try
        {
            final var method = requireNonNullArgument( clazz, "clazz" )
                .getMethod( "setScriptContext", ScriptContext.class );
            final var modifiers = method.getModifiers();
            if( isPublic( modifiers ) && isStatic( modifiers ) )
            {
                retValue = method;
            }
        }
        catch( final NoSuchMethodException ignored ) { /* The exception will be ignored deliberately */ }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  findSetScriptContextMethod()

    /**
     *  Retrieves the script arguments from the context.
     *
     *  @param  context The script context
     *  @return The command line arguments; if no arguments were defined in the
     *      context, an empty array will be returned.
     */
    private static String [] getArguments( final ScriptContext context )
    {
        final var scope = requireNonNull( context, "context" ).getAttributesScope( ARGUMENTS );
        var retValue = EMPTY_String_ARRAY;
        if( scope != -1 )
        {
            final var obj = context.getAttribute( ARGUMENTS, scope );
            if( obj instanceof final String [] args ) retValue = args;
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getArguments()

    /**
     *  Retrieves the classpath. First the method will look into the provided
     *  context, and, if no classpath is given there, it will look at the
     *  system property with the name {@code com.sun.script.java.classpath}.
     *
     *  @param  context The script context.
     *  @return The classpath, or {@code null} if no classpath could be
     *      retrieved.
     *
     *  @see #CLASSPATH
     *  @see #SYSPROP_PREFIX
     */
    private static String getClassPath( final ScriptContext context )
    {
        final var scope = requireNonNull( context, "context" ).getAttributesScope( CLASSPATH );
        String retValue;
        if( scope != -1 )
        {
            retValue = context.getAttribute( CLASSPATH, scope ).toString();
        }
        else
        {
            //noinspection ConstantExpression
            retValue = getProperty( SYSPROP_PREFIX + CLASSPATH );
            if( isNull( retValue ) ) retValue = getProperty( PROPERTY_CLASSPATH );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getClassPath()

    /**
     *  {@inheritDoc}
     *
     *  @see ScriptEngine#getFactory()
     */
    @Override
    public final synchronized ScriptEngineFactory getFactory()
    {
        var retValue = super.getFactory();
        if( isNull( retValue ) )
        {
            retValue = new JavaEngineFactory();
            setFactory( retValue );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getFactory()

    /**
     *  Retrieves the filename of the script from the context; if none is given
     *  there, the method will return {@code $unnamed.java}.
     *
     *  @param  context The script context.
     *  @return The filename of the script.
     */
    private static String getFileName( final ScriptContext context )
    {
        final var scope = requireNonNull( context, "context" ).getAttributesScope( FILENAME );
        var retValue = "$unnamed.java";
        if( scope != -1 )
        {
            retValue = context.getAttribute( FILENAME, scope ).toString();
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getFileName()

    /**
     *  Retrieves the name of the main class, either from the provided context
     *  or from the system properties.
     *
     *  @param  context The script context.
     *  @return The name of the main class, or {@code null} if it could
     *      not be found.
     *
     *  @see #MAINCLASS
     *  @see #SYSPROP_PREFIX
     */
    private static String getMainClassName( final ScriptContext context )
    {
        final var scope = requireNonNull( context, "context" ).getAttributesScope( MAINCLASS );
        @SuppressWarnings( {"ConditionalExpressionWithNegatedCondition", "ConstantExpression"} )
        final var retValue = scope != -1
            ? context.getAttribute( MAINCLASS, scope ).toString()
            : getProperty( SYSPROP_PREFIX + MAINCLASS );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getMainClassName()

    /**
     *  Retrieves the parent
     *  {@link ClassLoader}
     *  from the provided context.
     *
     *  @param  context The script context.
     *  @return The parent classloader for the script, or {@code null} if
     *      none was defined in the context.
     */
    private static ClassLoader getParentLoader( final ScriptContext context )
    {
        final var scope = requireNonNull( context, "context" ).getAttributesScope( PARENTLOADER );
        ClassLoader retValue = null;
        if( scope != -1 )
        {
            final var loader = context.getAttribute( PARENTLOADER, scope );
            if( loader instanceof ClassLoader )
            {
                retValue = (ClassLoader) loader;
            }
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getParentLoader()

    /**
     *  Retrieves the sourcepath either from the context or from the system
     *  properties.
     *
     *  @param  context The script context.
     *  @return The sourcepath or {@code null} if none is defined.
     *
     *  @see #SOURCEPATH
     *  @see #SYSPROP_PREFIX
     */
    private static String getSourcePath( final ScriptContext context )
    {
        final var scope = requireNonNull( context, "context" ).getAttributesScope( SOURCEPATH );
        @SuppressWarnings( {"ConditionalExpressionWithNegatedCondition", "ConstantExpression"} )
        final var retValue = scope != -1
            ? context.getAttribute( SOURCEPATH, scope ).toString()
            : getProperty( SYSPROP_PREFIX + SOURCEPATH );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getSourcePath()

    /**
     *  Parses and translates the provided script (a Java source in this case)
     *  and returns the resulting class.
     *
     *  @param  script  The script source.
     *  @param  scriptContext   The script context.
     *  @return The class that is used to start the script, or
     *      {@code null} if that could not be found.
     *  @throws ScriptException The script could not be successfully parsed.
     */
    private Class<?> parse( final String script, final ScriptContext scriptContext ) throws ScriptException
    {
        final var fileName = getFileName( requireNonNull( scriptContext, "scriptContext" ) );
        final var sourcePath = getSourcePath( scriptContext );
        final var classPath = getClassPath( scriptContext );

        final var classBytes = m_Compiler.compile( fileName, requireNonNull( script, "script" ), scriptContext.getErrorWriter(), sourcePath, classPath );

        if( isNull( classBytes ) || classBytes.isEmpty() )
        {
            throw new ScriptException( "The compilation of '%1$s' has failed".formatted( fileName ) );
        }

        /*
         * Create a ClassLoader to load classes from MemoryJavaFileManager.
         */
        Class<?> retValue;
        try( final var loader = new MemoryClassLoader( classBytes, classPath, getParentLoader( scriptContext ) ) )
        {
            //---* Determine the main class *----------------------------------
            final var mainClassName = getMainClassName( scriptContext );
            if( nonNull( mainClassName ) )
            {
                retValue = loader.loadClass( mainClassName );
                final var mainMethod = findMainMethod( retValue );
                if( isNull( mainMethod ) )
                {
                    throw new ScriptException( "The class '%1$s' does not define the method 'main()'".formatted( mainClassName ) );
                }
            }
            else
            {
                /*
                 * No explicit main class was configured, so we have to load
                 * all compiled classes and search for the main class.
                 */
                final var classes = loader.loadAll();

                //---* Search for class with main method *---------------------
                retValue = findMainClass( classes );
                if( isNull( retValue ) )
                {
                    /*
                     * If there is no class with main method, then return the
                     * first class.
                     */
                    final var iterator = classes.iterator();
                    if( iterator.hasNext() )
                    {
                        retValue = iterator.next();
                    }
                }
            }
        }
        catch( final ClassNotFoundException | IOException e )
        {
            throw new ScriptException( e );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  parse()
}
//  class JavaEngine

/*
 *  End of File
 */