/**
 * The AssassinManager class is called by AssassinMain to carry out
 * tasks involved in administering the "Assassin" game.
 *
 * @author Matthew Niculae
 */

import java.util.List;

public class AssassinManager {

    /** A reference to the front node of the kill ring */
    public AssassinNode frontKillRing;

    /** A reference to the front node of the graveyard (null if empty) */
    public AssassinNode frontGraveYard;

    /**  Initializes a new assassin manager over the given list of
    people. A new list is created and populated by the for loop. The order of the
    nodes is the same as "names".
     Pre: if list is empty or null throw new IllegalArgument exception.
     Post: The killRing is initialized with the correct order of names */
    public AssassinManager(List<String> names){
        if (names.isEmpty() || names == null) throw new IllegalArgumentException("Empty List");
        frontKillRing = new AssassinNode(names.get(0));
        AssassinNode current = frontKillRing;
        for (int i = 1; i < names.size(); i++) {
            current.next = new AssassinNode(names.get(i));
            current = current.next;
        }
    }

    /**
     * This method prints the names of people in the kill ring, one per line,
     * indented by 4 spaces. If the game is over, something else will be printed to the screen.
     */
    public void printKillRing() {
        AssassinNode current = frontKillRing;
        while (current != null) {
            if(current.next == null) {
                System.out.println("    " + current.name + " is stalking " + frontKillRing.name);
            }
            if(current.next != null) {
                System.out.println("    " + current.name + " is stalking " + current.next.name);
            }
            current = current.next;
        }
        if (isGameOver() == true) {
            System.out.println(winner() + " won the game!");
        }
    }

    /**
     * This method prints the names of the people in the graveyard, one per line,
     * indented by 4 spaces. Names are printed from most recently killed onward.
     * If the graveyard is empty, this returns empty
     */
    public void printGraveyard(){
        AssassinNode current = frontGraveYard;
        while (current != null) {
            if (current != null) {
                System.out.println("    " + current.name + " was killed by " + current.killer);
            }
            current = current.next;
        }

    }

    /**
     * This method returns true if the given name is in  the current graveyard and otherwise
     * false. It also ignores case when comparing strings.
     * @param "name" is the name that the user inputted.
     * @return return true if the killRing contains the inputted name,
     * otherwise it returns false.
     */
    public boolean killRingContains(String name) {
        AssassinNode current = frontKillRing;
        while (current != null) {
            if (current.name.equalsIgnoreCase(name)){
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * This method should return true if the given name parameter is in the current
     * graveyard and false otherwise. This method also ignores case.
     * @param name is the user inputted name String
     * @return returns true if the graveyard contains the name, false otherwise
     */
    public boolean graveyardContains(String name) {
        AssassinNode current = frontGraveYard;
        while (current != null) {
            if(current.name.equalsIgnoreCase(name)){
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * This method returns true if the game is over. The game is over when
     * there is only one person left in the killRing
     * @return returns true if current.next points to null, false otherwise
     */
    public boolean isGameOver () {
        AssassinNode current = frontKillRing;
        if (current.next == null) {
            return true;
        }
        return false;
    }

    /**
     * This method returns the name of the winner of the game, or null
     * if "isGameOver()" returns false.
     * @return null if "isGameOver()" returns false. Otherwise returns name
     * of the front of the killRing.
     */
    public String winner() {
        if (!isGameOver()) {
            return null;
        }
        return frontKillRing.name;
    }

    /**
     * Records the assassination of the person with the given name, transferring the person
     * fro the kill ring to the front of the graveyard.
     * Pre: Throws exceptions if the game is over, or if the name is not in the killring.
     * Post: provided name is removed from the killRing list and added to the front of the
     * graveyard list. The order is unchanged in the killRing.
     * @param name is the user inputted name that is crossreferenced with the grave yard
     *              and kill ring.
     */
    public void kill(String name) {

        AssassinNode current = frontKillRing;
        AssassinNode currentCopy = frontKillRing;
        String str = "";

        if (isGameOver()) throw new IllegalStateException("The game is over!");
        if (!killRingContains(name)) throw new IllegalArgumentException("This name is not in the ring!");

        //This while-loop is used to find last name in
        //the list. This tells us who will "kill"
        //the first node
        while (currentCopy != null) {
            if (currentCopy.next == null) {
                str = currentCopy.name;
            }
            currentCopy = currentCopy.next;
        }

        //if the name is at the front of the list, it is taken out,
        // added to the front of the graveyard and frontKillRing is
        // reassigned to a new node.
        if (frontKillRing.name.equalsIgnoreCase(name)) {
            AssassinNode temp = frontKillRing;
            frontKillRing = frontKillRing.next;
            temp.next = frontGraveYard;
            frontGraveYard = temp;
            frontGraveYard.killer = str;
        }

        //This while loop handles the cases in which the person who is killed
        //is not the first node.
        while (current.next != null) {
            if(current.next.name.equalsIgnoreCase(name)) {
                AssassinNode temp = current.next;
                current.next = current.next.next;
                temp.next = frontGraveYard;
                frontGraveYard = temp;
                frontGraveYard.killer = current.name;
            } else {
                current = current.next;
            }
        }

    }








    //////// DO NOT MODIFY AssassinNode.  You will lose points if you do. ////////
    /**
     * Each AssassinNode object represents a single node in a linked list
     * for a game of Assassin.
     */
    private static class AssassinNode {
        public final String name;  // this person's name
        public String killer;      // name of who killed this person (null if alive)
        public AssassinNode next;  // next node in the list (null if none)
        
        /**
         * Constructs a new node to store the given name and no next node.
         */
        public AssassinNode(String name) {
            this(name, null);
        }

        /**
         * Constructs a new node to store the given name and a reference
         * to the given next node.
         */
        public AssassinNode(String name, AssassinNode next) {
            this.name = name;
            this.killer = null;
            this.next = next;
        }
    }
}
