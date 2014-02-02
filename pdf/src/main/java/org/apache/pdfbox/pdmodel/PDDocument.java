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

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.exceptions.InvalidPasswordException;
import org.apache.pdfbox.exceptions.SignatureException;
import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;

/**
 * This is the in-memory representation of the PDF document. You need to call close() on this object when you are done
 * using it!!
 * 
 * @author <a href="mailto:ben@benlitchfield.com">Ben Litchfield</a>
 * 
 */
public class PDDocument implements Closeable
{

    private COSDocument document;

    // cached values
    private PDDocumentInformation documentInformation;
    private PDDocumentCatalog documentCatalog;


    /**
     * This assocates object ids with a page number. It's used to determine the page number for bookmarks (or page
     * numbers for anything else for which you have an object id for that matter).
     */
    private Map<String, Integer> pageMap = null;

    /**
     * Keep tracking customized documentId for the trailer. If null, a new id will be generated for the document. This
     * ID doesn't represent the actual documentId from the trailer.
     */
    private Long documentId;

    /**
     * Constructor, creates a new PDF Document with no pages. You need to add at least one page for the document to be
     * valid.
     * 
     * @throws IOException If there is an error creating this document.
     */
    public PDDocument() throws IOException
    {
        document = new COSDocument();

        // First we need a trailer
        COSDictionary trailer = new COSDictionary();
        document.setTrailer(trailer);

        // Next we need the root dictionary.
        COSDictionary rootDictionary = new COSDictionary();
        trailer.setItem(COSName.ROOT, rootDictionary);
        rootDictionary.setItem(COSName.TYPE, COSName.CATALOG);
        rootDictionary.setItem(COSName.VERSION, COSName.getPDFName("1.4"));

        // next we need the pages tree structure
        COSDictionary pages = new COSDictionary();
        rootDictionary.setItem(COSName.PAGES, pages);
        pages.setItem(COSName.TYPE, COSName.PAGES);
        COSArray kidsArray = new COSArray();
        pages.setItem(COSName.KIDS, kidsArray);
        pages.setItem(COSName.COUNT, COSInteger.ZERO);
    }

    private void generatePageMap()
    {
        pageMap = new HashMap<String, Integer>();
        // these page nodes could be references to pages,
        // or references to arrays which have references to pages
        // or references to arrays which have references to arrays which have references to pages
        // or ... (I think you get the idea...)
        processListOfPageReferences(getDocumentCatalog().getPages().getKids());
    }

    private void processListOfPageReferences(List<Object> pageNodes)
    {
        int numberOfNodes = pageNodes.size();
        for (int i = 0; i < numberOfNodes; ++i)
        {
            Object pageOrArray = pageNodes.get(i);
            if (pageOrArray instanceof PDPage)
            {
                COSArray pageArray = ((COSArrayList) (((PDPage) pageOrArray).getParent()).getKids()).toList();
                parseCatalogObject((COSObject) pageArray.get(i));
            }
            else if (pageOrArray instanceof PDPageNode)
            {
                processListOfPageReferences(((PDPageNode) pageOrArray).getKids());
            }
        }
    }

    /**
     * This will either add the page passed in, or, if it's a pointer to an array of pages, it'll recursivly call itself
     * and process everything in the list.
     */
    private void parseCatalogObject(COSObject thePageOrArrayObject)
    {
        COSBase arrayCountBase = thePageOrArrayObject.getItem(COSName.COUNT);
        int arrayCount = -1;
        if (arrayCountBase instanceof COSInteger)
        {
            arrayCount = ((COSInteger) arrayCountBase).intValue();
        }

        COSBase kidsBase = thePageOrArrayObject.getItem(COSName.KIDS);
        int kidsCount = -1;
        if (kidsBase instanceof COSArray)
        {
            kidsCount = ((COSArray) kidsBase).size();
        }

        if (arrayCount == -1 || kidsCount == -1)
        {
            // these cases occur when we have a page, not an array of pages
            String objStr = String.valueOf(thePageOrArrayObject.getObjectNumber().intValue());
            String genStr = String.valueOf(thePageOrArrayObject.getGenerationNumber().intValue());
            getPageMap().put(objStr + "," + genStr, new Integer(getPageMap().size() + 1));
        }
        else
        {
            // we either have an array of page pointers, or an array of arrays
            if (arrayCount == kidsCount)
            {
                // process the kids... they're all references to pages
                COSArray kidsArray = ((COSArray) kidsBase);
                for (int i = 0; i < kidsArray.size(); ++i)
                {
                    COSObject thisObject = (COSObject) kidsArray.get(i);
                    String objStr = String.valueOf(thisObject.getObjectNumber().intValue());
                    String genStr = String.valueOf(thisObject.getGenerationNumber().intValue());
                    getPageMap().put(objStr + "," + genStr, new Integer(getPageMap().size() + 1));
                }
            }
            else
            {
                // this object is an array of references to other arrays
                COSArray list = null;
                if (kidsBase instanceof COSArray)
                {
                    list = ((COSArray) kidsBase);
                }
                if (list != null)
                {
                    for (int arrayCounter = 0; arrayCounter < list.size(); ++arrayCounter)
                    {
                        parseCatalogObject((COSObject) list.get(arrayCounter));
                    }
                }
            }
        }
    }

    /**
     * This will return the Map containing the mapping from object-ids to pagenumbers.
     * 
     * @return the pageMap
     */
    public final Map<String, Integer> getPageMap()
    {
        if (pageMap == null)
        {
            generatePageMap();
        }
        return pageMap;
    }

    /**
     * This will add a page to the document. This is a convenience method, that will add the page to the root of the
     * hierarchy and set the parent of the page to the root.
     * 
     * @param page The page to add to the document.
     */
    public void addPage(PDPage page)
    {
        PDPageNode rootPages = getDocumentCatalog().getPages();
        rootPages.getKids().add(page);
        page.setParent(rootPages);
        rootPages.updateCount();
    }

    /**
     * Remove the page from the document.
     * 
     * @param page The page to remove from the document.
     * 
     * @return true if the page was found false otherwise.
     */
    public boolean removePage(PDPage page)
    {
        PDPageNode parent = page.getParent();
        boolean retval = parent.getKids().remove(page);
        if (retval)
        {
            // do a recursive updateCount starting at the root of the document
            getDocumentCatalog().getPages().updateCount();
        }
        return retval;
    }

    /**
     * Remove the page from the document.
     * 
     * @param pageNumber 0 based index to page number.
     * @return true if the page was found false otherwise.
     */
    public boolean removePage(int pageNumber)
    {
        boolean removed = false;
        List allPages = getDocumentCatalog().getAllPages();
        if (allPages.size() > pageNumber)
        {
            PDPage page = (PDPage) allPages.get(pageNumber);
            removed = removePage(page);
        }
        return removed;
    }

    /**
     * This will import and copy the contents from another location. Currently the content stream is stored in a scratch
     * file. The scratch file is associated with the document. If you are adding a page to this document from another
     * document and want to copy the contents to this document's scratch file then use this method otherwise just use
     * the addPage method.
     * 
     * @param page The page to import.
     * @return The page that was imported.
     * 
     * @throws IOException If there is an error copying the page.
     */
    public PDPage importPage(PDPage page) throws IOException
    {
        PDPage importedPage = new PDPage(new COSDictionary(page.getCOSDictionary()));
        InputStream is = null;
        OutputStream os = null;
        try
        {
            PDStream src = page.getContents();
            if (src != null)
            {
                PDStream dest = new PDStream(document.createCOSStream());
                importedPage.setContents(dest);
                os = dest.createOutputStream();

                byte[] buf = new byte[10240];
                int amountRead = 0;
                is = src.createInputStream();
                while ((amountRead = is.read(buf, 0, 10240)) > -1)
                {
                    os.write(buf, 0, amountRead);
                }
            }
            addPage(importedPage);
        }
        finally
        {
            if (is != null)
            {
                is.close();
            }
            if (os != null)
            {
                os.close();
            }
        }
        return importedPage;

    }

    /**
     * This will get the low level document.
     * 
     * @return The document that this layer sits on top of.
     */
    public COSDocument getDocument()
    {
        return document;
    }

    /**
     * This will get the document info dictionary. This is guaranteed to not return null.
     * 
     * @return The documents /Info dictionary
     */
    public PDDocumentInformation getDocumentInformation()
    {
        if (documentInformation == null)
        {
            COSDictionary trailer = document.getTrailer();
            COSDictionary infoDic = (COSDictionary) trailer.getDictionaryObject(COSName.INFO);
            if (infoDic == null)
            {
                infoDic = new COSDictionary();
                trailer.setItem(COSName.INFO, infoDic);
            }
            documentInformation = new PDDocumentInformation(infoDic);
        }
        return documentInformation;
    }

    /**
     * This will set the document information for this document.
     * 
     * @param info The updated document information.
     */
    public void setDocumentInformation(PDDocumentInformation info)
    {
        documentInformation = info;
        document.getTrailer().setItem(COSName.INFO, info.getDictionary());
    }

    /**
     * This will get the document CATALOG. This is guaranteed to not return null.
     * 
     * @return The documents /Root dictionary
     */
    public PDDocumentCatalog getDocumentCatalog()
    {
        if (documentCatalog == null)
        {
            COSDictionary trailer = document.getTrailer();
            COSBase dictionary = trailer.getDictionaryObject(COSName.ROOT);
            if (dictionary instanceof COSDictionary)
            {
                documentCatalog = new PDDocumentCatalog(this, (COSDictionary) dictionary);
            }
            else
            {
                documentCatalog = new PDDocumentCatalog(this);
            }
        }
        return documentCatalog;
    }


    /**
     * Save the document to a file.
     * 
     * @param fileName The file to save as.
     * 
     * @throws IOException If there is an error saving the document.
     * @throws COSVisitorException If an error occurs while generating the data.
     */
    public void save(String fileName) throws IOException, COSVisitorException
    {
        save(new File(fileName));
    }

    /**
     * Save the document to a file.
     * 
     * @param file The file to save as.
     * 
     * @throws IOException If there is an error saving the document.
     * @throws COSVisitorException If an error occurs while generating the data.
     */
    public void save(File file) throws IOException, COSVisitorException
    {
        save(new FileOutputStream(file));
    }

    /**
     * This will save the document to an output stream.
     * 
     * @param output The stream to write to.
     * 
     * @throws IOException If there is an error writing the document.
     * @throws COSVisitorException If an error occurs while generating the data.
     */
    public void save(OutputStream output) throws IOException, COSVisitorException
    {
        // update the count in case any pages have been added behind the scenes.
        getDocumentCatalog().getPages().updateCount();
        COSWriter writer = null;
        try
        {
            writer = new COSWriter(output);
            writer.write(this);
            writer.close();
        }
        finally
        {
            if (writer != null)
            {
                writer.close();
            }
        }
    }

    /**
     * Save the pdf as incremental.
     * 
     * @param fileName the filename to be used
     * @throws IOException if something went wrong
     * @throws COSVisitorException if something went wrong
     */
    public void saveIncremental(String fileName) throws IOException, COSVisitorException
    {
        saveIncremental(new FileInputStream(fileName), new FileOutputStream(fileName, true));
    }

    /**
     * Save the pdf as incremental.
     * 
     * @param input
     * @param output
     * @throws IOException if something went wrong
     * @throws COSVisitorException if something went wrong
     */
    public void saveIncremental(FileInputStream input, OutputStream output) throws IOException, COSVisitorException
    {
        // update the count in case any pages have been added behind the scenes.
        getDocumentCatalog().getPages().updateCount();
        COSWriter writer = null;
        try
        {
            // Sometimes the original file will be missing a newline at the end
            // In order to avoid having %%EOF the first object on the same line
            // as the %%EOF, we put a newline here. If there's already one at
            // the end of the file, an extra one won't hurt. PDFBOX-1051
            output.write("\r\n".getBytes());
            writer = new COSWriter(output, input);
            writer.write(this);
            writer.close();
        }
        finally
        {
            if (writer != null)
            {
                writer.close();
            }
        }
    }

    /**
     * This will return the total page count of the PDF document.
     * 
     * @return The total number of pages in the PDF document.
     */
    public int getNumberOfPages()
    {
        PDDocumentCatalog cat = getDocumentCatalog();
        return (int) cat.getPages().getCount();
    }

    /**
     * This will close the underlying COSDocument object.
     * 
     * @throws IOException If there is an error releasing resources.
     */
    public void close() throws IOException
    {
    	documentCatalog = null;
    	documentInformation = null;
    	if (pageMap != null)
    	{
    		pageMap.clear();
    		pageMap = null;
    	}
    	if (document != null)
    	{
	        document.close();
	        document = null;
    	}
    }

    public Long getDocumentId()
    {
        return documentId;
    }

    public void setDocumentId(Long docId)
    {
        documentId = docId;
    }
}
