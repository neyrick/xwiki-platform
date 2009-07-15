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
package org.xwiki.rendering.internal.macro.rss;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.xwiki.bridge.SkinAccessBridge;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.rendering.block.Block;
import org.xwiki.rendering.block.GroupBlock;
import org.xwiki.rendering.block.ImageBlock;
import org.xwiki.rendering.block.LinkBlock;
import org.xwiki.rendering.block.MacroBlock;
import org.xwiki.rendering.block.ParagraphBlock;
import org.xwiki.rendering.listener.Link;
import org.xwiki.rendering.listener.LinkType;
import org.xwiki.rendering.listener.URLImage;
import org.xwiki.rendering.macro.AbstractMacro;
import org.xwiki.rendering.macro.Macro;
import org.xwiki.rendering.macro.MacroExecutionException;
import org.xwiki.rendering.macro.box.BoxMacroParameters;
import org.xwiki.rendering.macro.rss.RssMacroParameters;
import org.xwiki.rendering.transformation.MacroTransformationContext;
import org.xwiki.rendering.util.ParserUtils;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

/**
 * Macro that output latest feed entries from a RSS feed.
 * 
 * @version $Id$
 * @since 1.8RC1
 */
@Component("rss")
public class RssMacro extends AbstractMacro<RssMacroParameters>
{
    /**
     * The name of the CSS class attribute.
     */
    private static final String CLASS_ATTRIBUTE = "class";

    /**
     * The description of the macro.
     */
    private static final String DESCRIPTION = "Output latest feed entries from a RSS feed.";

    /**
     * The Box macro is used to draw boxes around RSS feed items and for the main around the RSS feed list.
     */
    @Requirement("box")
    protected Macro<BoxMacroParameters> boxMacro;
    
    /**
     * Used to get the RSS icon.
     */
    @Requirement
    private SkinAccessBridge skinAccessBridge;
    
    /** 
     * Needed to parse the ordinary text. 
     */
    private ParserUtils parserUtils = new ParserUtils();

    /**
     * Create a Feed object from a feed specified as a URL.
     */
    private RomeFeedFactory romeFeedFactory = new DefaultRomeFeedFactory();

    /**
     * Create and initialize the descriptor of the macro.
     */
    public RssMacro()
    {
        super(DESCRIPTION, RssMacroParameters.class);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.rendering.macro.Macro#supportsInlineMode()
     */
    public boolean supportsInlineMode()
    {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.xwiki.rendering.macro.Macro#execute(Object, String, MacroTransformationContext)
     */
    public List<Block> execute(RssMacroParameters parameters, String content, MacroTransformationContext context)
        throws MacroExecutionException
    {
        SyndFeed feed = this.romeFeedFactory.createFeed(parameters);
        
        BoxMacroParameters boxParameters = new BoxMacroParameters();
        boolean hasImage = parameters.isImage() && (feed.getImage() != null);
        boxParameters.setCssClass("rssfeed");
 
        if (!StringUtils.isEmpty(parameters.getWidth())) {
            boxParameters.setWidth(parameters.getWidth());
        }

        boxParameters.setBlockTitle(generateBoxTitle("rsschanneltitle", feed));

        if (hasImage) {
            boxParameters.setImage(feed.getImage().getUrl());
        } 

        List<Block> result = boxMacro.execute(boxParameters, content == null ? StringUtils.EMPTY : content, context);
        generaterEntries(result.get(0), feed, parameters, context);

        return result;
    }

    /**
     * Renders the RSS's title.
     * 
     * @param cssClass the CSS sheet
     * @param feed the RSS feed data
     * @return the list of blocks making the RSS Box title
     */
    private List< ? extends Block> generateBoxTitle(String cssClass, SyndFeed feed)
    {
        List<Block> titleBlocks;

        if (feed.getLink() == null) {
            titleBlocks = this.parserUtils.parsePlainText(feed.getTitle());
        } else {
            // Title link.
            Link titleLink = new Link();
            titleLink.setReference(feed.getLink());
            titleLink.setType(LinkType.URI);
            
            // Title text link.
            Block titleTextLinkBlock =
                    new LinkBlock(this.parserUtils.parsePlainText(feed.getTitle()), titleLink, true);
            
            // Rss icon.
            String imagePath = skinAccessBridge.getSkinFile("icons/black-rss.png");
            ImageBlock imageBlock = new ImageBlock(new URLImage(imagePath), false);
            
            // Title rss icon link.
            Block titleImageLinkBlock = new LinkBlock(Arrays.<Block>asList(imageBlock), titleLink, true);
            
            titleBlocks = Arrays.<Block>asList(titleTextLinkBlock, titleImageLinkBlock);            
        }
        ParagraphBlock titleBlock = new ParagraphBlock(titleBlocks);
        titleBlock.setParameter(CLASS_ATTRIBUTE, cssClass);

        return Collections.singletonList(titleBlock);
    }

    /**
     * Renders the given RSS's entries.
     *
     * @param parentBlock the parent Block to which the output is going to be added
     * @param feed the RSS Channel we retrieved via the Feed URL
     * @param parameters our parameter helper object
     * @param context the macro's transformation context
     * @throws MacroExecutionException if the content cannot be rendered
     */
    private void generaterEntries(Block parentBlock, SyndFeed feed, RssMacroParameters parameters,
        MacroTransformationContext context) throws MacroExecutionException
    {
        int maxElements = parameters.getCount();
        int count = 0;

        for (Object item : feed.getEntries()) {
            ++count;
            if (count > maxElements) {
                break;
            }
            SyndEntry entry = (SyndEntry) item;

            Link titleLink = new Link();
            titleLink.setType(LinkType.URI);
            titleLink.setReference(entry.getLink());
            Block titleBlock = new LinkBlock(this.parserUtils.parsePlainText(entry.getTitle()), titleLink, true);
            ParagraphBlock paragraphTitleBlock = new ParagraphBlock(Collections.singletonList(titleBlock));
            paragraphTitleBlock.setParameter(CLASS_ATTRIBUTE, "rssitemtitle");
            parentBlock.addChild(paragraphTitleBlock);

            if (parameters.isContent() && entry.getDescription() != null) {
                // We are wrapping the feed entry content in a HTML macro, not considering what the declared content
                // is, because some feed will declare text while they actually contain HTML.
                // See http://stuffthathappens.com/blog/2007/10/29/i-hate-rss/
                // A case where doing this might hurt is if a feed declares "text" and has any XML inside it does
                // not want to be interpreted as such, but displayed as is instead. But this certainly is too rare
                // compared to mis-formed feeds that say text while they want to say HTML.
                Block html =
                    new MacroBlock("html", Collections.singletonMap("wiki", "false"),
                        entry.getDescription().getValue(), context.isInline());

                parentBlock.addChild(new GroupBlock(Arrays.asList(html), Collections.singletonMap(CLASS_ATTRIBUTE,
                    "rssitemdescription")));
            }
        }
    }
    
    /**
     * @param romeFeedFactory a custom implementation to use instead of the default, useful for tests
     */
    protected void setFeedFactory(RomeFeedFactory romeFeedFactory)
    {
        this.romeFeedFactory = romeFeedFactory;
    }
}
