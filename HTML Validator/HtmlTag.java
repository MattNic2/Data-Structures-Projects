// An HtmlTag object represents an HTML tag, such as <b> or </table>.
// HtmlTags can be sorted first alphabetically by element then by whether
// they are opening or not (opening tags go first).  Java code such as TreeSet,
// Collections.sort, etc can take advantage of this because we implement
// the comparable interface.

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/** An HtmlTag object represents an HTML tag, such as <b> or </table>. */
public class HtmlTag implements Comparable<HtmlTag> {
	// fields
	private final String element; // p, table, div
	private final boolean isOpenTag; // true <p>; false </p>

	// Return < 0 if this tag comes before the given other tag
	// Return > 0 if this tag comes after the given other tag
	// Return 0 if this tag is the same as the given other tag
	// order by element, opening tags before closing tags
	public int compareTo(HtmlTag other) {
		if (element.compareTo(other.element) > 0) {
			return 1;
		} else if (element.compareTo(other.element) < 0) {
			return -1;
		} else {
			if (isOpenTag && !other.isOpenTag) { // I'm opening, the other is closing
				return -1; // I go before!
			} else if (!isOpenTag && other.isOpenTag) { // I'm closing, other is opening
				return 1; // I go after.
			} else {
				return 0;
			}
		}
	}

	/**
	 * Constructs an HTML "opening" tag with the given element (e.g. "table").
	 * Throws a NullPointerException if element is null.
	 */
	public HtmlTag(String element) {
		this(element, true);
	}

	/**
	 * Constructs an HTML tag with the given element (e.g. "table") and type.
	 * Self-closing tags like <br />
	 * are considered to be "opening" tags, and return true from the isOpenTag
	 * method. Throws a NullPointerException if element is null.
	 */
	public HtmlTag(String element, boolean isOpenTag) {
		this.element = element.toLowerCase();
		this.isOpenTag = isOpenTag;
	}

	/** Returns this HTML tag's element, such as "table" or "p". */
	public String getElement() {
		return element;
	}

	/**
	 * Returns true if this HTML tag is an "opening" (starting) tag and false if
	 * it is a closing tag. Self-closing tags like <br />
	 * are considered to be "opening" tags.
	 */
	public boolean isOpenTag() {
		return isOpenTag;
	}

	/**
	 * Returns true if the given other tag is non-null and matches this tag;
	 * that is, if they have the same element but opposite types, such as <body>
	 * and </body>.
	 */
	public boolean matches(HtmlTag other) {
		return other != null && element.equalsIgnoreCase(other.element)
				&& isOpenTag != other.isOpenTag;
	}

	/**
	 * Returns true if this tag does not requires a matching closing tag, which
	 * is the case for certain elements such as br and img.
	 */
	public boolean isSelfClosing() {
		return SELF_CLOSING_TAGS.contains(element);
	}

	/** Returns a string representation of this HTML tag, such as "</table>". */
	public String toString() {
		return "<" + (isOpenTag ? "" : "/")
				+ (element.equals("!--") ? "!-- --" : element) + ">";
	}

	/**
	 * Returns true if this tag has the same element and type as the given other
	 * tag.
	 */
	public boolean equals(Object o) {
		if (o instanceof HtmlTag) {
			HtmlTag other = (HtmlTag) o;
			return element.equals(other.element)
					&& isOpenTag == other.isOpenTag;
		} else {
			return false;
		}
	}

	// a set of tags that don't need to be matched (self-closing)
	private static final Set<String> SELF_CLOSING_TAGS = new HashSet<String>(
			Arrays.asList("!doctype", "!--", "?xml", "xml", "area", "base",
					"basefont", "br", "col", "frame", "hr", "img", "input",
					"link", "meta", "param"));

	// all whitespace characters; used in text parsing
	private static final String WHITESPACE = " \f\n\r\t";

	/**
	 * Reads a string such as "
	 * <table>
	 * " or "</p>" and converts it into an HtmlTag, which is returned. Throws a
	 * NullPointerException if tagText is null.
	 */
	public static HtmlTag parse(String tagText) {
		tagText = tagText.trim();
		boolean isOpenTag = !tagText.contains("</");
		String element = tagText.replaceAll("[^a-zA-Z!-?]+", "");
		if (element.contains("!--")) {
			element = "!--"; // HTML comments
		}
		return new HtmlTag(element, isOpenTag);
	}

	/**
	 * Reads the file or URL given, and tokenizes the text in that file, placing
	 * the tokens into the given Queue. You don't need to call this method in
	 * your homework code. Precondition: text != null
	 */
	public static LinkedList<HtmlTag> tokenize(String text) {
		StringBuffer buf = new StringBuffer(text);
		LinkedList<HtmlTag> queue = new LinkedList<HtmlTag>();

		while (true) {
			HtmlTag nextTag = nextTag(buf);
			if (nextTag == null) {
				break;
			} else {
				queue.add(nextTag);
			}
		}

		return queue;
	}

	// advances to next tag in input;
	// probably not a perfect HTML tag tokenizer, but it will do for this HW
	private static HtmlTag nextTag(StringBuffer buf) {
		int index1 = buf.indexOf("<");
		int index2 = buf.indexOf(">");

		if (index1 >= 0 && index2 > index1) {
			// check for HTML comments: <!-- -->
			if (index1 + 4 <= buf.length()
					&& buf.substring(index1 + 1, index1 + 4).equals("!--")) {
				// a comment; look for closing comment tag -->
				index2 = buf.indexOf("-->", index1 + 4);
				if (index2 < 0) {
					return null;
				} else {
					buf.insert(index1 + 4, " "); // fixes things like <!--hi-->
					index2 += 3; // advance to the closing >
				}
			}

			String element = buf.substring(index1 + 1, index2).trim();

			// remove attributes
			for (int i = 0; i < WHITESPACE.length(); i++) {
				int index3 = element.indexOf(WHITESPACE.charAt(i));
				if (index3 >= 0) {
					element = element.substring(0, index3);
				}
			}

			// determine whether opening or closing tag
			boolean isOpenTag = true;
			if (element.indexOf("/") == 0) {
				isOpenTag = false;
				element = element.substring(1);
			}
			element = element.replaceAll("[^a-zA-Z0-9!-]+", "");

			buf.delete(0, index2 + 1);
			return new HtmlTag(element, isOpenTag);
		} else {
			return null;
		}
	}
}