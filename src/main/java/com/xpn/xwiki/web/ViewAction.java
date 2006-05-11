/*
 * Copyright 2006, XpertNet SARL, and individual contributors as indicated
 * by the contributors.txt.
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
 *
 * @author namphunghai
 * @author torcq
 * @author sdumitriu
 */
package com.xpn.xwiki.web;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;

import java.io.IOException;

public class ViewAction extends XWikiAction {
    public boolean action(XWikiContext context) throws XWikiException {
        XWikiRequest request = context.getRequest();
        String rev = request.getParameter("rev");
        if (rev!=null) {
            String url = context.getDoc().getURL("viewrev", request.getQueryString(), context);
            try {
                context.getResponse().sendRedirect(url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    public String render(XWikiContext context) throws XWikiException {
        handleRevision(context);
        XWikiDocument doc = (XWikiDocument) context.get("doc");
        String defaultTemplate = doc.getDefaultTemplate();
        if ((defaultTemplate !=null) && (!defaultTemplate.equals(""))) {
        	return defaultTemplate;
        }
        else
        	return "view";
    }
}
