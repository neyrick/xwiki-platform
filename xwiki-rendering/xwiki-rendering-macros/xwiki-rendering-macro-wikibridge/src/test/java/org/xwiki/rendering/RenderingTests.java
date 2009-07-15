/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xwiki.rendering;

import junit.framework.Test;
import junit.framework.TestCase;

import org.xwiki.rendering.scaffolding.RenderingTestSuite;
import org.xwiki.test.ComponentManagerTestSetup;

/**
 * Rendering tests for wiki macros.
 *
 * @version $Id$
 * @since 2.0M2
 */
public class RenderingTests extends TestCase
{
    /**
     * Builds and returns a new {@link ComponentManagerTestSetup}.
     * 
     * @return a {@link ComponentManagerTestSetup}.
     * @throws Exception if an error occurs while building the test setup.
     */
    public static Test suite() throws Exception
    {
        RenderingTestSuite suite = new RenderingTestSuite("Test Wiki Macro Bridge");
        
        suite.addTestsFromResource("wikimacro1", true);
        
        return new ComponentManagerTestSetup(suite);
    }
}
