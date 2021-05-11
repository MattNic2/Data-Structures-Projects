/**
 * Created by Matthew Niculae
 * 4/29/1999
 * 20 Questions is a guessing game in which the objective
 * is to ask yes/no questions to determine an object.
 */

import java.io.*;
import java.util.*;

public class QuestionsGame {
    // Your code here
    private QuestionNode root;

    /**
     * This question node class manages the data fields
     * of the question nodes.
     */
    private static class QuestionNode {
        // Your code here

        private String question;
        private QuestionNode left;
        private QuestionNode right;

        public QuestionNode(String question) {
            this.question = question;
        }

        public QuestionNode(String question, QuestionNode left, QuestionNode right) {
            this.question = question;
            this.left = left;
            this.right = right;
        }
    }

    /**
     * single node object creator.
     * @param object String that is translated into a node
     * Pre: Accepts a string
     * Post: String is translated to a question object
     */
    public QuestionsGame(String object) {
        QuestionNode obj = new QuestionNode(object);
        root = obj;
    }

    /**
     * Initializes a new game with a tree that is full of nodes that can be used
     *  for the game.
     * @param input a line of text that is translated into a new node on the BST
     */
    //nodes appear in pre-order
    public QuestionsGame(Scanner input) {
        //should call a recursive method to build the tree
        while(input.hasNextLine()) {
            root = makeNodes(input);
        }
    }

    //should write the questions to a file in the correct order

    /**
     * Saves questions to a new node if non existent
     * @param output enables new questions to be saved to the existing file
     */
    public void saveQuestions(PrintStream output) {
        if (output == null) throw new IllegalArgumentException("PrintStream is null");
        if (root == null) throw new IllegalArgumentException("No questions, the list is null.");
        saveQuestionsHelper(root, output);
    }

    /**
     * Helper to save questions
     * @param node the current question node that is being processed
     * @param output the output that is being saved to the file
     */
    private void saveQuestionsHelper(QuestionNode node, PrintStream output) {
        if (node.left != null && node.right != null) {
            output.println("Q:");
            output.println(node.question);
            saveQuestionsHelper(node.left, output);
            saveQuestionsHelper(node.right, output);
        } else {
            output.println("A:");
            output.println(node.question);
        }
    }

    /**
     * Helper to produce the nodes of the binary search tree
     * @param input question file
     * @return returns a node
     */
    private QuestionNode makeNodes(Scanner input) {
        String character = input.nextLine();
        String q = input.nextLine();
        QuestionNode node = new QuestionNode(q);
        if (character.equals("Q:")) {
            node.left = makeNodes(input);
            node.right = makeNodes(input);
        }
        return node;
    }


    /**
     * Main method to play the game, takes player input and navigates the tree
     * Pre: Starts at the top of the tree
     * Post: Ends at an answer node.
     * @param input this is the scanner that allows the program to process user input
     */
    public void play(Scanner input) {

        QuestionNode current = root;
        QuestionNode previous = current;
        boolean leaf = false;
        String userResponse;
        String answer = root.question;

        //while the branch can still be explored, explore
        while (current.left != null && current.right != null) {

            //print out the current question
            System.out.print(current.question + " (y/n)? ");

            //get an answer to the question, without extra spaces and to lowercase
            userResponse = input.nextLine().trim().toLowerCase();

            //if user input starts with y, go left
            //else, go right
            if ((current.right.right == null) || current.left.left == null) previous = current;
            if (userResponse.charAt(0) == 'y') {
                current = current.left;
                leaf = true;
            } else {
                current = current.right;
                leaf = false;
            }

            answer = current.question;
        }

        //guesses the answer, because it is the last node
        System.out.println("I guess that your object is " + answer + "!");
        System.out.print("Am I right? (y/n)? ");


        userResponse = input.nextLine().trim().toLowerCase();

        //if input yes, simply print "Awesome! I (the computer) win!"
        if (userResponse.charAt(0) == 'y') {
            //computer won message
            System.out.println("Awesome! I win!");
        } else {
            //get the new question info from the user
            System.out.println("Boo! I Lose.  Please help me get better!");
            System.out.print("What is your object? ");

            //newObj is the new object that the computer couldn't guess
            String newObj = input.nextLine();

            System.out.println("Please give me a yes/no question that distinguishes between "
                    + newObj  + " and " + answer + ".");
            System.out.print("Q: ");

            //generate a question to distinguish between the actual answer and your object
            String newQuestion = input.nextLine();
            System.out.print("Is the answer \"yes\" for " + newObj + "? (y/n)? ");

            //if the answer is yes to the question, put new object on left node,
            //otherwise put it on the right with the original answer on the left
            String newQuestionAns = input.nextLine().trim().toLowerCase();

            //captures the current data stored by the question node
            QuestionNode prevObj = current;
            QuestionNode objNode = new QuestionNode(newObj);

            if (previous == current) {
                if (newQuestionAns.charAt(0) == 'y') {
                    root = new QuestionNode(newQuestion, objNode, prevObj);
                } else {
                    root = new QuestionNode(newQuestion, prevObj, objNode);
                }
            } else {
                if (leaf) {
                    if (newQuestionAns.charAt(0) == 'y') {
                        previous.left = new QuestionNode(newQuestion, objNode, prevObj);
                    } else {
                        previous.left = new QuestionNode(newQuestion, prevObj, objNode);
                    }
                } else {
                    if (newQuestionAns.charAt(0) == 'y') {
                        previous.right = new QuestionNode(newQuestion, objNode, prevObj);
                    } else {
                        previous.right = new QuestionNode(newQuestion, prevObj, objNode);
                    }
                }
            }
        }
    }
}