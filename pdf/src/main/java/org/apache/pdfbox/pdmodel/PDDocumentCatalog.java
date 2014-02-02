/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pdfbox.pdmodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDDestinationOrAction;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDPageLabels;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentProperties;

/**
 * This class represents the acroform of a PDF document.
 *
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * @version $Revision: 1.21 $
 */
public class PDDocumentCatalog implements COSObjectable
{
    private COSDictionary root;
    private PDDocument document;

    /**
     * Page mode where neither the outline nor the thumbnails
     * are displayed.
     */
    public static final String PAGE_MODE_USE_NONE = "UseNone";
    /**
     * Show bookmarks when pdf is opened.
     */
    public static final String PAGE_MODE_USE_OUTLINES = "UseOutlines";
    /**
     * Show thumbnails when pdf is opened.
     */
    public static final String PAGE_MODE_USE_THUMBS = "UseThumbs";
    /**
     * Full screen mode with no menu bar, window controls.
     */
    public static final String PAGE_MODE_FULL_SCREEN = "FullScreen";
    /**
     * Optional content group panel is visible when opened.
     */
    public static final String PAGE_MODE_USE_OPTIONAL_CONTENT = "UseOC";
    /**
     * Attachments panel is visible.
     */
    public static final String PAGE_MODE_USE_ATTACHMENTS = "UseAttachments";

    /**
     * Display one page at a time.
     */
    public static final String PAGE_LAYOUT_SINGLE_PAGE = "SinglePage";
    /**
     * Display the pages in one column.
     */
    public static final String PAGE_LAYOUT_ONE_COLUMN = "OneColumn";
    /**
     * Display the pages in two columns, with odd numbered pagse on the left.
     */
    public static final String PAGE_LAYOUT_TWO_COLUMN_LEFT = "TwoColumnLeft";
    /**
     * Display the pages in two columns, with odd numbered pagse on the right.
     */
    public static final String PAGE_LAYOUT_TWO_COLUMN_RIGHT ="TwoColumnRight";
    /**
     * Display the pages two at a time, with odd-numbered pages on the left.
     * @since PDF Version 1.5
     */
    public static final String PAGE_LAYOUT_TWO_PAGE_LEFT = "TwoPageLeft";
    /**
     * Display the pages two at a time, with odd-numbered pages on the right.
     * @since PDF Version 1.5
     */
    public static final String PAGE_LAYOUT_TWO_PAGE_RIGHT = "TwoPageRight";



    /**
     * Constructor.
     *
     * @param doc The document that this catalog is part of.
     */
    public PDDocumentCatalog( PDDocument doc )
    {
        document = doc;
        root = new COSDictionary();
        root.setItem( COSName.TYPE, COSName.CATALOG );
        document.getDocument().getTrailer().setItem( COSName.ROOT, root );
    }

    /**
     * Constructor.
     *
     * @param doc The document that this catalog is part of.
     * @param rootDictionary The root dictionary that this object wraps.
     */
    public PDDocumentCatalog( PDDocument doc, COSDictionary rootDictionary )
    {
        document = doc;
        root = rootDictionary;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSBase getCOSObject()
    {
        return root;
    }

    /**
     * Convert this standard java object to a COS object.
     *
     * @return The cos object that matches this Java object.
     */
    public COSDictionary getCOSDictionary()
    {
        return root;
    }



    /**
     * This will get the root node for the pages.
     *
     * @return The parent page node.
     */
    public PDPageNode getPages()
    {
        return new PDPageNode( (COSDictionary)root.getDictionaryObject( COSName.PAGES ) );
    }

    /**
     * The PDF document contains a hierarchical structure of PDPageNode and PDPages, which
     * is mostly just a way to store this information.  This method will return a flat list
     * of all PDPage objects in this document.
     *
     * @return A list of PDPage objects.
     */
    public List getAllPages()
    {
        List retval = new ArrayList();
        PDPageNode rootNode = getPages();
        //old (slower):
        //getPageObjects( rootNode, retval );
        rootNode.getAllKids(retval);
        return retval;
    }


    /**
     * Set the list of threads for this pdf document.
     *
     * @param threads The list of threads, or null to clear it.
     */
    public void setThreads( List threads )
    {
        root.setItem( COSName.THREADS, COSArrayList.converterToCOSArray( threads ) );
    }

    /**
     * Get the metadata that is part of the document catalog.  This will
     * return null if there is no meta data for this object.
     *
     * @return The metadata for this object.
     */
    public PDMetadata getMetadata()
    {
        PDMetadata retval = null;
        COSStream stream = (COSStream)root.getDictionaryObject( COSName.METADATA );
        if( stream != null )
        {
            retval = new PDMetadata( stream );
        }
        return retval;
    }

    /**
     * Set the metadata for this object.  This can be null.
     *
     * @param meta The meta data for this object.
     */
    public void setMetadata( PDMetadata meta )
    {
        root.setItem( COSName.METADATA, meta );
    }

    /**
     * Set the Document Open Action for this object.
     *
     * @param action The action you want to perform.
     */
    public void setOpenAction( PDDestinationOrAction action )
    {
        root.setItem( COSName.OPEN_ACTION, action );
    }



    /**
     * Get the list of OutputIntents defined in the document.
     * 
     * @return The list of PDOoutputIntent
     */
    public List<PDOutputIntent> getOutputIntent () {
        List<PDOutputIntent> retval = new ArrayList<PDOutputIntent>();
        COSArray array = (COSArray)root.getItem(COSName.OUTPUT_INTENTS);
        if (array!=null) {
            for (COSBase cosBase : array)
            {
                PDOutputIntent oi = new PDOutputIntent((COSStream)cosBase);
                retval.add(oi);
            }
        }
        return retval;
    }

    /**
     * Add an OutputIntent to the list.
     * 
     * If there is not OutputIntent, the list is created and the first
     * element added.
     * 
     * @param outputIntent the OutputIntent to add.
     */
    public void addOutputIntent (PDOutputIntent outputIntent) {
        COSArray array = (COSArray)root.getItem(COSName.OUTPUT_INTENTS);
        if (array==null) {
            array = new COSArray();
            root.setItem(COSName.OUTPUT_INTENTS, array);
        }
        array.add(outputIntent.getCOSObject());
    }

    /**
     * Replace the list of OutputIntents of the document.
     * 
     * @param outputIntents the list of OutputIntents, if the list is empty all
     * OutputIntents are removed.
     */
    public void setOutputIntents (List<PDOutputIntent> outputIntents) {
        COSArray array = new COSArray();
        for (PDOutputIntent intent : outputIntents)
        {
            array.add(intent.getCOSObject());
        }
        root.setItem(COSName.OUTPUT_INTENTS, array);
    }
    
    /**
     * Set the page display mode, see the PAGE_MODE_XXX constants.
     * @return A string representing the page mode.
     */
    public String getPageMode()
    {
        return root.getNameAsString( COSName.PAGE_MODE, PAGE_MODE_USE_NONE );
    }

    /**
     * Set the page mode.  See the PAGE_MODE_XXX constants for valid values.
     * @param mode The new page mode.
     */
    public void setPageMode( String mode )
    {
        root.setName( COSName.PAGE_MODE, mode );
    }

    /**
     * Set the page layout, see the PAGE_LAYOUT_XXX constants.
     * @return A string representing the page layout.
     */
    public String getPageLayout()
    {
        return root.getNameAsString( COSName.PAGE_LAYOUT, PAGE_LAYOUT_SINGLE_PAGE );
    }

    /**
     * Set the page layout.  See the PAGE_LAYOUT_XXX constants for valid values.
     * @param layout The new page layout.
     */
    public void setPageLayout( String layout )
    {
        root.setName( COSName.PAGE_LAYOUT, layout );
    }

    /**
     * The language for the document.
     *
     * @return The language for the document.
     */
    public String getLanguage()
    {
        return root.getString( COSName.LANG );
    }

    /**
     * Set the Language for the document.
     *
     * @param language The new document language.
     */
    public void setLanguage( String language )
    {
        root.setString( COSName.LANG, language );
    }

    /**
     * Returns the PDF specification version this document conforms to.
     *
     * @return The PDF version.
     */
    public String getVersion()
    {
        return root.getNameAsString(COSName.VERSION);
    }

    /**
     * Sets the PDF specification version this document conforms to.
     *
     * @param version the PDF version (ex. "1.4")
     */
    public void setVersion(String version)
    {
        root.setName(COSName.VERSION, version);
    }

    /**
     * Returns the page labels descriptor of the document.
     *
     * @return the page labels descriptor of the document.
     *
     * @throws IOException If there is a problem retrieving the page labels.
     */
    public PDPageLabels getPageLabels() throws IOException
    {
        PDPageLabels labels = null;
        COSDictionary dict = (COSDictionary) root.getDictionaryObject(COSName.PAGE_LABELS);
        if (dict != null)
        {
            labels = new PDPageLabels(document, dict);
        }
        return labels;
    }

    /**
     * Set the page label descriptor for the document.
     *
     * @param labels the new page label descriptor to set.
     */
    public void setPageLabels(PDPageLabels labels)
    {
        root.setItem(COSName.PAGE_LABELS, labels);
    }

    /**
     * Get the optional content properties dictionary associated with this document.
     *
     * @return the optional properties dictionary or null if it is not present
     * @since PDF 1.5
     */
    public PDOptionalContentProperties getOCProperties()
    {
        PDOptionalContentProperties retval = null;
        COSDictionary dict = (COSDictionary)root.getDictionaryObject(COSName.OCPROPERTIES);
        if (dict != null)
        {
            retval = new PDOptionalContentProperties(dict);
        }

        return retval;
    }

    /**
     * Set the optional content properties dictionary.
     *
     * @param ocProperties the optional properties dictionary
     * @since PDF 1.5
     */
    public void setOCProperties(PDOptionalContentProperties ocProperties)
    {
        //TODO Check for PDF 1.5 or higher
        root.setItem(COSName.OCPROPERTIES, ocProperties);
    }

}
