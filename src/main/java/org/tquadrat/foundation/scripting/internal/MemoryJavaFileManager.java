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

import static java.nio.CharBuffer.wrap;
import static java.util.Collections.unmodifiableMap;
import static javax.tools.JavaFileObject.Kind.CLASS;
import static javax.tools.JavaFileObject.Kind.SOURCE;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.tquadrat.foundation.lang.Objects.requireNonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;

/**
 *  An implementation of
 *  {@link JavaFileManager}
 *  that keeps compiled {@code .class} bytes in memory.
 *
 *  @author A. Sundararajan
 *  @modified    Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: MemoryJavaFileManager.java 1070 2023-09-29 17:09:34Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: MemoryJavaFileManager.java 1070 2023-09-29 17:09:34Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.5" )
public final class MemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager>
{
        /*---------------*\
    ====** Inner Classes **====================================================
        \*---------------*/
    /**
     *  A file object that stores Java byte code into the classBytes map.
     *
     *  @author A. Sundararajan
     *  @modified    Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: MemoryJavaFileManager.java 1070 2023-09-29 17:09:34Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: MemoryJavaFileManager.java 1070 2023-09-29 17:09:34Z tquadrat $" )
    @API( status = INTERNAL, since = "0.0.5" )
    private class ClassOutputBuffer extends SimpleJavaFileObject
    {
            /*------------*\
        ====** Attributes **===================================================
            \*------------*/
        /**
         *  The name of the &quot;file&quot;.
         */
        private final String m_Name;

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new object for ClassOutputBuffer.
         *
         *  @param  name    The &quot;name&quot; of the buffer file.
         */
        public ClassOutputBuffer( final String name )
        {
            super( toURI( name ), CLASS );
            m_Name = name;
        }   //  ClassOutputBuffer()

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  {@inheritDoc}
         */
        @SuppressWarnings( "InnerClassTooDeeplyNested" )
        @Override
        public OutputStream openOutputStream()
        {
            @SuppressWarnings( "AnonymousInnerClass" )
            final OutputStream retValue = new FilterOutputStream( new ByteArrayOutputStream() )
            {
                /**
                 *  {@inheritDoc}
                 *
                 *  @see java.io.FilterOutputStream#close()
                 */
                @Override
                public void close() throws IOException
                {
                    out.close();
                    final var bos = (ByteArrayOutputStream) out;
                    m_ClassBytes.put( m_Name, bos.toByteArray() );
                }
            };

            //---* Done *------------------------------------------------------
            return retValue;
        }   //  openOutputStream()
    }
    //  class ClassOutputBuffer

    /**
     *  A file object used to represent Java source coming from a string.
     *
     *  @author A. Sundararajan
     *  @modified    Thomas Thrien - thomas.thrien@tquadrat.org
     *  @version $Id: MemoryJavaFileManager.java 1070 2023-09-29 17:09:34Z tquadrat $
     *  @since 0.0.5
     *
     *  @UMLGraph.link
     */
    @ClassVersion( sourceVersion = "$Id: MemoryJavaFileManager.java 1070 2023-09-29 17:09:34Z tquadrat $" )
    @API( status = INTERNAL, since = "0.0.5" )
    private static class StringInputBuffer extends SimpleJavaFileObject
    {
            /*------------*\
        ====** Attributes **===================================================
            \*------------*/
        /**
         *  The contents.
         */
        private final String m_Code;

            /*--------------*\
        ====** Constructors **=================================================
            \*--------------*/
        /**
         *  Creates a new {@code StringInputBuffer} instance.
         *
         *  @param  name    The name of the &quot;file&quot;.
         *  @param  code    The source code as the contents.
         */
        StringInputBuffer( final String name, final String code )
        {
            super( toURI( name ), SOURCE );
            m_Code = requireNonNullArgument( code, "code" );
        }   //  StringInputBuffer()

            /*---------*\
        ====** Methods **======================================================
            \*---------*/
        /**
         *  {@inheritDoc}
         */
        @Override
        public CharBuffer getCharContent( final boolean ignoreEncodingErrors ) { return wrap( m_Code ); }
    }
    //  class StringInputBuffer

        /*-----------*\
    ====** Constants **========================================================
        \*-----------*/
    /**
     *  Java source file extension: {@value}.
     */
    private static final String EXT = ".java";

        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
    /**
     *  The byte code that is stored by this file manager instance. The name of
     *  the class is the key to the map, the value is the byte code of that
     *  class.
     */
    private Map<String,byte []> m_ClassBytes = new HashMap<>();

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code MemoryJavaFileManager} instance.
     *
     *  @param  parent  The parent file manager.
     */
    public MemoryJavaFileManager( final JavaFileManager parent )
    {
        super( requireNonNull( parent, "parent" ) );
    }   //  MemoryJavaFileManager()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     *
     *  @see javax.tools.ForwardingJavaFileManager#close()
     */
    @Override
    public final void close() throws IOException { m_ClassBytes = new HashMap<>(); }

    /**
     *  {@inheritDoc}<br>
     *  <br>This implementation does nothing.
     *
     *  @see javax.tools.ForwardingJavaFileManager#flush()
     */
    @Override
    public final void flush() throws IOException { /* Does nothing */ }

    /**
     *  Returns the classes that are hold in memory.
     *
     *  @return The classes.
     */
    public final Map<String,byte []> getClassBytes() { return unmodifiableMap( m_ClassBytes ); }

    /**
     *  {@inheritDoc}
     */
    @SuppressWarnings( "SwitchStatementWithTooFewBranches" )
    @Override
    public final JavaFileObject getJavaFileForOutput( final JavaFileManager.Location location, final String className, final Kind kind, final FileObject sibling ) throws IOException
    {
        final var retValue = switch( kind )
        {
            case CLASS -> new ClassOutputBuffer( className );

            //$CASES-OMITTED$
            default -> super.getJavaFileForOutput( location, className, kind, sibling );
        };

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  getJavaFileForOutput()

    /**
     *  Creates a
     *  {@link JavaFileObject}
     *  from the given arguments that will use as the input to {@code javac}.
     *
     *  @param  name    The file name of the source file.
     *  @param  code    The source code itself.
     *  @return The result source file.
     */
    @SuppressWarnings( "StaticMethodOnlyUsedInOneClass" )
    public static final JavaFileObject makeStringSource( final String name, final String code )
    {
        final JavaFileObject retValue = new StringInputBuffer( name, code );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  makeStringSource()

    /**
     *  Translates the given name to an
     *  {@link URI}.<br>
     *  <br>If an error occurs, the default
     *  &quot;{@code mfm:///org/tquadrat/script/java/java_source}&quot;
     *  will be returned.
     *
     *  @param  name    The name to translate.
     *  @return The newly generated URI.
     */
    static URI toURI( final String name )
    {
        URI retValue = null;
        final var file = new File( requireNonNullArgument( name, "name" ) );
        if( file.exists() )
        {
            retValue = file.toURI();
        }
        else
        {
            try
            {
                final var newUri = new StringBuilder( "mfm:///" )
                    .append( name.replace( '.', '/' ) );
                if( name.endsWith( EXT ) )
                {
                    newUri.replace( newUri.length() - EXT.length(), newUri.length(), EXT );
                }
                retValue = URI.create( newUri.toString() );
            }
            catch( final RuntimeException ignored )
            {
                retValue = URI.create( "mfm:///org/tquadrat/foundation/scripting/java/java_source" );
            }
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  toURI()
}
//  class MemoryJavaFileManager

/*
 *  End of File
 */