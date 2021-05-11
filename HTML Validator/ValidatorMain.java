// This program tests your HTML validator object on any file or URL you want.
//
// When it prompts you for a file name, if you type a simple string such
// as "test1.html" (without the quotes) it will just look on your hard disk
// in the same directory as your code or Eclipse project.
//
// If you type a string such as "http://www.google.com/index.html", it will
// connect to that URL and download the HTML content from it.

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Scanner;
import java.util.Queue;

public class ValidatorMain {
    public static void main(String[] args) throws IOException {
        HtmlValidator validator = new HtmlValidator();
        String pageText = "";
        Scanner console = new Scanner(System.in);
        String choice = "s";
        
        while (true) {
            if (choice.startsWith("s")) {
                // prompt for page, then download it if it's a URL
                System.out.print("Page URL or file name (blank for empty): ");
                String url = console.nextLine().trim();
                if (url.length() > 0) {
                    if (isURL(url)) {
                        System.out.println("Downloading from " + url + " ...");
                    }
                    
                    try {
                        pageText = readCompleteFileOrURL(url);
                        Queue<HtmlTag> tags = HtmlTag.tokenize(pageText);

                        // create the HTML validator
                        validator = new HtmlValidator(tags);
                    } catch (MalformedURLException mfurle) {
                        System.out.println("Badly formatted URL: " + url);
                    } catch (FileNotFoundException fnfe) {
                        System.out.println("Web page or file not found: " + url);
                    } catch (IOException ioe) {
                        System.out.println("I/O error: " + ioe.getMessage());
                    }
                } else {
                    pageText = "No page text (starting from empty queue)";
                    validator = new HtmlValidator();
                }
            } else if (choice.startsWith("a")) {
                System.out.print("What tag (such as <table> or </p>)? ");
                String tagText = console.nextLine().trim();
                boolean isOpenTag = !tagText.contains("</");
                String element = tagText.replaceAll("[^a-zA-Z!-]+", "");
                if (element.contains("!--")) {
                    element = "!--";  // HTML comments
                }
                HtmlTag tag = new HtmlTag(element, isOpenTag);
                validator.addTag(tag);
            } else if (choice.startsWith("g")) {
                System.out.println(validator.getTags());
            } else if (choice.startsWith("p")) {
                System.out.println(pageText);
            } else if (choice.startsWith("r")) {
                System.out.print("Remove what element? ");
                String element = console.nextLine().trim();
                validator.removeAll(element);
            } else if (choice.startsWith("v")) {
                validator.validate();
                System.out.println();
            } else if (choice.startsWith("q")) {
                break;
            }

            System.out.println();
            System.out.print("(a)ddTag, (g)etTags, (r)emoveAll, (v)alidate, (s)et URL, (p)rint, (q)uit? ");
            choice = console.nextLine().trim().toLowerCase();
        }
    }
    
    /**
     * Returns an input stream to read from the given address.
     * Works with URLs or normal file names.
     */
    public static InputStream getInputStream(String address) throws IOException, MalformedURLException {
        if (isURL(address)) {
            return new URL(address).openStream();
        } else {
            // local file
            return new FileInputStream(address);
        }
    }

    /** Returns true if the given string represents a URL. */
    public static boolean isURL(String address) {
        return address.startsWith("http://") || address.startsWith("https://") ||
                address.startsWith("www.") ||
                address.endsWith("/") || 
                address.endsWith(".com") || address.contains(".com/") || 
                address.endsWith(".org") || address.contains(".org/") || 
                address.endsWith(".edu") || address.contains(".edu/") || 
                address.endsWith(".gov") || address.contains(".gov/");
    }

    /**
     * Opens the given address for reading input, and reads it until the end 
     * of the file, and returns the entire file contents as a big String.
     * 
     * If address starts with http[s]:// , assumes address is a URL and tries
     * to download the data from the web.  Otherwise, assumes the address
     * is a local file and tries to read it from the disk.
     */
    public static String readCompleteFileOrURL(String address) throws IOException {
        InputStream stream = getInputStream(address);   // open file

        // read each letter into a buffer
        StringBuffer buffer = new StringBuffer();
        while (true) {
            int ch = stream.read();
            if (ch < 0) {
                break;
            }

            buffer.append((char) ch);
        }

        return buffer.toString();
    }    
}