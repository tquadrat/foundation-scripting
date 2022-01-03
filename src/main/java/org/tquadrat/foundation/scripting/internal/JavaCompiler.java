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

package org.tquadrat.foundation.scripting.internal;

import static java.lang.System.err;
import static javax.tools.ToolProvider.getSystemJavaCompiler;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.tquadrat.foundation.lang.Objects.isNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.scripting.internal.MemoryJavaFileManager.makeStringSource;
import static org.tquadrat.foundation.util.StringUtils.isNotEmptyOrBlank;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.ImpossibleExceptionError;

/**
 *  Simple interface to the Java compiler using the JSR199 Compiler API.
 *
 *  @author A. Sundararajan
 *  @modified    Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: JavaCompiler.java 878 2021-02-20 19:56:13Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: JavaCompiler.java 878 2021-02-20 19:56:13Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.5" )
public final class JavaCompiler
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The compiler instance that is used.
     */
    private final javax.tools.JavaCompiler m_Compiler;

    /**
     *  The Java file manager that is used by the compiler.
     */
    private final StandardJavaFileManager m_FileManager;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code JavaCompiler} instance.
     */
    @SuppressWarnings( "ProhibitedExceptionThrown" )
    public JavaCompiler()
    {
        m_Compiler = getSystemJavaCompiler();
        if( isNull( m_Compiler ) )
        {
            throw new Error( "Unable to load the System Java Compiler" );
        }
        m_FileManager = m_Compiler.getStandardFileManager( null, null, null );
    }   //  JavaCompiler

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  Compiles the given source and returns the resulting byte code. Any
     *  error messages will be written to
     *  {@link System#err}.
     *
     *  @param  source  The source to compile.
     *  @param  fileName    The file name that is used for the source origin.
     *  @return The resulting byte code.
     */
    public final Map<String,byte []> compile( final String source, final String fileName )
    {
        final var errorOut = new OutputStreamWriter( err );
        final var retValue = compile( source, fileName, errorOut, null, null );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  compile()

    /**
     *  Compiles the given source and returns the resulting byte code. Any
     *  error messages will be written to the given
     *  {@link Writer}.
     *
     *  @param  fileName    The file name that is used for the source origin.
     *  @param  source  The source to compile.
     *  @param  errorOut    The destination for any error messages.
     *  @return The resulting byte code.
     */
    public final Map<String,byte []> compile( final String fileName, final String source, final Writer errorOut )
    {
        return compile( fileName, source, errorOut, null, null );
    }   //  compile()

    /**
     *  Compiles the given source and returns the resulting byte code. Any
     *  error messages will be written to the given
     *  {@link Writer}.
     *
     *  @param  fileName    The file name that is used for the source origin.
     *  @param  source  The source to compile.
     *  @param  errorOut    The destination for any error messages.
     *  @param  sourcePath  The locations of additional {@code *.java} source
     *      files; multiple folder names have to be separated with colons
     *      (':').
     *  @return The resulting byte code.
     */
    public final Map<String,byte []> compile( final String fileName, final String source, final Writer errorOut, final String sourcePath )
    {
        final var retValue = compile( fileName, source, errorOut, requireNonNullArgument( sourcePath, "sourcePath" ), null );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  compile()

    /**
     *  Compiles the given source and returns the resulting byte code. Any
     *  error messages will be written to the given
     *  {@link Writer}.
     *
     *  @param  fileName    The file name that is used for the source origin.
     *  @param  source  The source to compile.
     *  @param  errorOut    The destination for any error messages.
     *  @param  sourcePath  The location of additional {@code *.java} source
     *      files; multiple folder names have to be separated with colons
     *      (':'). May be {@code null}.
     *  @param  classPath   The location of additional {@code *.class} files;
     *      multiple folder names have to be separated with colons. May be
     *      {@code null}.
     *  @return The resulting byte code.
     */
    public final Map<String,byte []> compile( final String fileName, final String source, final Writer errorOut, final String sourcePath, final String classPath )
    {
        //---* Define the default javac options *------------------------------
        final var options = List.of( "-Xlint:all", "-g:none", "-deprecation" );

        final var retValue = compile( fileName, source, errorOut, sourcePath, classPath, options );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  compile()

    /**
     *  Compiles the given source and returns the resulting byte code. Any
     *  error messages will be written to the given
     *  {@link Writer}.
     *
     *  @param  fileName    The file name that is used for the source origin.
     *  @param  source  The source to compile.
     *  @param  errorOut    The destination for any error messages.
     *  @param  sourcePath  The location of additional {@code *.java} source
     *      files; multiple folder names have to be separated with colons
     *      (':'). May be {@code null}.
     *  @param  classPath   The location of additional {@code *.class} files;
     *      multiple folder names have to be separated with colons
     *      (':'). May be {@code null}.
     *  @param  options  The options for the invocation of {@code javac}.
     *  @return The resulting byte code.
     */
    @SuppressWarnings( {"resource", "NestedTryStatement"} )
    private Map<String,byte []> compile( final String fileName, final String source, final Writer errorOut, final String sourcePath, final String classPath, final List<String> options )
    {
        requireNonNullArgument( fileName, "fileName" );
        requireNonNullArgument( source, "source" );
        requireNonNullArgument( errorOut, "errorOut" );
        requireNonNullArgument( options, "options" );

        /*
         * The diagnostics collector that collects all the warnings and errors
         * that may show up on compiling the source.
         */
        final var diagnostics = new DiagnosticCollector<JavaFileObject>();

        Map<String,byte []> retValue = null;

        //---* Create a new memory JavaFileManager that takes the result *-----
        try( final var fileManager = new MemoryJavaFileManager( m_FileManager ) )
        {
            //---* Prepare the compilation unit *------------------------------
            final Collection<JavaFileObject> compilationUnits = new ArrayList<>( 1 );
            compilationUnits.add( makeStringSource( fileName, source ) );

            //---* Prepare the javac options *---------------------------------
            final Collection<String> effectiveOptions = new ArrayList<>( options );

            if( isNotEmptyOrBlank( sourcePath ) )
            {
                effectiveOptions.add( "-sourcepath" );
                effectiveOptions.add( sourcePath );
            }

            if( isNotEmptyOrBlank( classPath ) )
            {
                effectiveOptions.add( "-classpath" );
                effectiveOptions.add( classPath );
            }

            //---* Create a compilation task *---------------------------------
            final var task = m_Compiler.getTask( errorOut, fileManager, diagnostics, effectiveOptions, null, compilationUnits );

            if( !task.call() )
            {
                try( final var errorPrinter = new PrintWriter( errorOut ) )
                {
                    for( final var diagnostic : diagnostics.getDiagnostics() )
                    {
                        errorPrinter.println( diagnostic.getMessage( null ) );
                    }
                    errorPrinter.flush();
                }
            }
            else
            {
                retValue = fileManager.getClassBytes();
            }
        }
        catch( final IOException e )
        {
            throw new ImpossibleExceptionError( "MemoryJavaFileManager should not throw an exception on close()", e );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  compile()
}
//  class JavaCompiler

/*
 *  End of File
 */