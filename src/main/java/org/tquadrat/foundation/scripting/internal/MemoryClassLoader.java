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

import static java.io.File.pathSeparator;
import static org.apiguardian.api.API.Status.INTERNAL;
import static org.tquadrat.foundation.lang.Objects.nonNull;
import static org.tquadrat.foundation.lang.Objects.requireNonNullArgument;
import static org.tquadrat.foundation.lang.Objects.requireNotEmptyArgument;
import static org.tquadrat.foundation.util.StringUtils.isNotEmpty;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apiguardian.api.API;
import org.tquadrat.foundation.annotation.ClassVersion;
import org.tquadrat.foundation.exception.ImpossibleExceptionError;

/**
 *  An implementation of
 *  {@link ClassLoader}
 *  that loads {@code .class} bytes from memory.
 *
 *  @author A. Sundararajan
 *  @modified    Thomas Thrien - thomas.thrien@tquadrat.org
 *  @version $Id: MemoryClassLoader.java 1070 2023-09-29 17:09:34Z tquadrat $
 *  @since 0.0.5
 *
 *  @UMLGraph.link
 */
@ClassVersion( sourceVersion = "$Id: MemoryClassLoader.java 1070 2023-09-29 17:09:34Z tquadrat $" )
@API( status = INTERNAL, since = "0.0.5" )
public final class MemoryClassLoader extends URLClassLoader
{
        /*------------*\
    ====** Attributes **=======================================================
        \*------------*/
   /**
    *  The byte code that was loaded by this class loader instance. The name of
    *  the class is the key to the map, the value is the byte code of that
    *  class.<br>
    *  <br>This is a reference to a map instance maintained elsewhere!
    */
    private final Map<String,byte []> m_ClassBytes;

        /*--------------*\
    ====** Constructors **=====================================================
        \*--------------*/
    /**
     *  Creates a new {@code MemoryClassLoader} instance.
     *
     *  @param  classBytes  The reference for the buffer with the byte code.
     *  @param  classPath   The {@code CLASSPATH}.
     *  @param  parent  The parent class loader; can be {@code null}.
     */
    public MemoryClassLoader( final Map<String,byte []> classBytes, final String classPath, final ClassLoader parent )
    {
        super( toURLs( classPath ), parent );
        m_ClassBytes = new HashMap<>( requireNonNullArgument( classBytes, "classBytes" ) );
    }   //  MemoryClassLoader()

    /**
     *  Creates a new {@code MemoryClassLoader} instance.
     *
     *  @param  classBytes  The reference for the buffer with the byte code.
     *  @param  classPath   The classpath.
     */
    public MemoryClassLoader( final Map<String,byte []> classBytes, final String classPath )
    {
        this( classBytes, classPath, null );
    }   //  MemoryClassLoader()

        /*---------*\
    ====** Methods **==========================================================
        \*---------*/
    /**
     *  {@inheritDoc}
     */
    @Override
    protected final Class<?> findClass( final String className ) throws ClassNotFoundException
    {
        final var classBytes = m_ClassBytes.get( requireNotEmptyArgument( className, "className" ) );
        final Class<?> retValue;
        if( nonNull( classBytes ) )
        {
            //---* Clear the bytes in the map - we don't need it anymore *-----
            m_ClassBytes.put( className, null );
            retValue = defineClass( className, classBytes, 0, classBytes.length );
        }
        else
        {
            retValue = super.findClass( className );
        }

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  findClass()

    /**
     *  Loads all the classes that are loadable by this classloader.
     *
     *  @return All the classes.
     *  @throws ClassNotFoundException  A class could not be loaded.
     */
    public final Iterable<Class<?>> loadAll() throws ClassNotFoundException
    {
        final Collection<Class<?>> result = new ArrayList<>( m_ClassBytes.size() );
        for( final var name : m_ClassBytes.keySet() )
        {
            result.add( loadClass( name ) );
        }

        final var retValue = List.copyOf( result );

        //---* Done *----------------------------------------------------------
        return retValue;
    }   //  loadAll()

    /**
     *  Translates the {@code CLASSPATH} string into an array of
     *  {@link URL}s.
     *
     *  @param  classPath   The {@code CLASSPATH} string.
     *  @return The URLs.
     */
    private static URL [] toURLs( final String classPath )
    {
        final Collection<URL> retValue = new ArrayList<>();
        if( isNotEmpty( classPath ) )
        {
            String token;
            File file;
            @SuppressWarnings( "UseOfStringTokenizer" )
            final var tokenizer = new StringTokenizer( classPath, pathSeparator );
            while( tokenizer.hasMoreTokens() )
            {
                token = tokenizer.nextToken();
                file = new File( token );
                if( file.exists() )
                {
                    try
                    {
                        retValue.add( file.toURI().toURL() );
                    }
                    catch( final MalformedURLException e )
                    {
                        throw new ImpossibleExceptionError( "Generated URL should be correct", e );
                    }
                }
                else
                {
                    try
                    {
                        retValue.add( new URL( token ) );
                    }
                    catch( final MalformedURLException ignored )
                    {
                        /*
                         * Deliberately ignored; if no URL could be generated
                         * from the given token, nothing will be added to the
                         * return value.
                         */
                    }
                }
            }
        }

        //---* Done *----------------------------------------------------------
        return retValue.toArray( URL []::new );
    }   //  toURLs()
}
//  class MemoryClassLoader

/*
 *  End of File
 */