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
package org.xwiki.officeimporter.builder;

import org.xwiki.bridge.DocumentName;
import org.xwiki.component.annotation.ComponentRole;
import org.xwiki.officeimporter.OfficeImporterException;
import org.xwiki.officeimporter.document.XHTMLOfficeDocument;

/**
 * Component responsible for building {@link XHTMLOfficeDocument} objects from binary office files.
 * 
 * @version $Id$
 * @since 2.1M1
 */
@ComponentRole
public interface XHTMLOfficeDocumentBuilder
{
    /**
     * Builds a {@link XHTMLOfficeDocument} corresponding to the given office document.
     * 
     * @param officeFileData binary data of the office document.
     * @param reference reference document w.r.t which html cleaning is performed. If the office file contains images or
     *            other binary artifacts, html cleaning will be performed assuming that those artifacts are present as
     *            attachments to the reference document.
     * @param filterStyles whether to filter css styles present in the html content produced by openoffice server.
     * @return an {@link XHTMLOfficeDocument} corresponding to the office document.
     * @throws OfficeImporterException if an error occurs while performing the import operation.
     */
    XHTMLOfficeDocument build(byte[] officeFileData, DocumentName reference, boolean filterStyles)
        throws OfficeImporterException;
}
