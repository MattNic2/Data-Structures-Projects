/**
 * Created by Matthew Niculae
 * 4/29/2021
 */
import java.io.PrintStream;
import java.util.*;
public class HuffmanCode {

    private HuffmanNode root;

    private static class HuffmanNode implements Comparable<HuffmanNode> {
        public final Character data;
        public final Integer frequency;
        public HuffmanNode left;
        public HuffmanNode right;

        public HuffmanNode() {
            this.data = null;
            this.frequency = null;

        }

        public HuffmanNode(char data, int frequency) {
            this.data = data;
            this.frequency = frequency;
        }

        public HuffmanNode(char data, int frequency, HuffmanNode left, HuffmanNode right) {
            this.data = data;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }

        @Override
        public int compareTo(HuffmanNode other) {
            if (this.frequency == null || other.frequency == null)
                throw new NullPointerException("Frequency is null");
            return this.frequency - other.frequency;
        }
    }

    /**
     *New huffman code object initialized using an array of frequencies
     * @param frequencies an array of ascii frequencies
     * Pre: no Huffman code objects have been built
     * Post: All Huffman Code objects have been built
     */
    public HuffmanCode(int[] frequencies) {
        //use a priority Queue to build the HuffmanCode
        HuffmanNode current = root;
        PriorityQueue<HuffmanNode> q = new PriorityQueue<>();
        for (int i = 0; i < frequencies.length; i++) {
            if (frequencies[i] != 0) {
                char currChar = (char) i;
                HuffmanNode temp = new HuffmanNode(currChar, frequencies[i]);
                q.add(temp);
            }
        }
        while (q.size() > 1) {
            //remove the two smallest nodes
            HuffmanNode node1 = q.remove();
            HuffmanNode node2 = q.remove();
            HuffmanNode newNode = new HuffmanNode((char) -1, node1.frequency + node2.frequency, node1, node2);
            q.add(newNode);
        }
        root = q.remove();
    }

    public HuffmanCode(Scanner input) {
        root = new HuffmanNode();
        while (input.hasNextLine()) {
            char data = (char) Integer.parseInt(input.nextLine());
            String path = input.nextLine();
            huffmanHelper(root, data, path);
        }
    }

    private void huffmanHelper(HuffmanNode current, char data, String path) {
        //base case: if path is empty
        if (path.length() == 1) {
            if (path.charAt(0) == '0') {
                current.left = new HuffmanNode(data, -1);
            } else {
                current.right = new HuffmanNode(data, -1);
            }
            return;
        }

        if (path.charAt(0) == '1') {
            //go right
            if (current.right == null) current.right = new HuffmanNode();
            huffmanHelper(current.right, data, path.substring(1));
        } else {
            //go left
            if (current.left == null) current.left = new HuffmanNode();
            huffmanHelper(current.left, data, path.substring(1));
        }
    }

    /**
     * Stores the current Huffman code objects to the given output stream
     * in the standard format
     * Pre: Output is empty
     * Post: Output has been populated with a Huffman Code object
     *
     * @param output where the Huffman Code is stored
     */
    public void save(PrintStream output) {
        saveHelper(output, "", root);
    }

    public void saveHelper(PrintStream output, String path, HuffmanNode current) {
        //base case
        if (current.left == null && current.right == null) {
            output.println((int) current.data);
            output.println(path);
        } else {
            //explore the left
            saveHelper(output, path + "0", current.left);

            //explore the right
            saveHelper(output, path + "1", current.right);
        }
    }


    public void translate(BitInputStream input, PrintStream output) {
        //stops reading when BitInputStream is empty
        HuffmanNode current = root;
        while (input.hasNextBit()) {
            current = (input.nextBit() == 0) ? current.left : current.right;
            if (current.left == null || current.right == null) {
                output.write((int) current.data);
                current = root;
            }
        }
    }
}