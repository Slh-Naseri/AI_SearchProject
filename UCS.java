
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class UCS {

    //Visualizer visualizer = new Visualizer();
    public void search(Node intialNode) {
        LinkedList<Node> frontier = new LinkedList<Node>();
        Hashtable<String, Boolean> inFrontier = new Hashtable<>();
        Hashtable<String, Boolean> explored = new Hashtable<>();
        if (isGoal(intialNode)) {
            result(intialNode);
            return;
        }
        frontier.add(intialNode);
        inFrontier.put(intialNode.hash(), true);
        while (!frontier.isEmpty()) {
            Node temp = frontier.poll();
            inFrontier.remove(temp.hash());
            explored.put(temp.hash(), true);
            ArrayList<Node> children = temp.successor(false);
            for (int i = 0; i < children.size(); i++) {
                if (!(inFrontier.containsKey(children.get(i).hash())) && !(explored.containsKey(children.get(i).hash()))) {
                    if (isGoal(children.get(i))) {
                        result(children.get(i));
                        return;
                    }
                    int j = 0;
                    for (; j < frontier.size(); j++) {
                        if (frontier.get(j).cost > children.get(i).cost) {
                            frontier.add(j, children.get(i));
                            inFrontier.put(children.get(i).hash(), true);
                            break;
                        }
                    }
                    if (j == frontier.size()) {
                        frontier.add(children.get(i));
                        inFrontier.put(children.get(i).hash(), true);
                    }
                }
            }
        }
    }

    public boolean isGoal(Node node) {
        if (node.map.at(node.player.i, node.player.j).name == 'C') {
            return true;
        } else {
            return false;
        }
    }

    public void result(Node node) {
        Stack<Node> nodes = new Stack<Node>();
        while (true) {
            nodes.push(node);
            if (node.parentNode == null) {
                break;
            } else {
                node = node.parentNode;
            }
        }
        nodes.pop();
        try {
            FileWriter myWriter = new FileWriter("result.txt");
            while (!nodes.empty()) {
                Node tempNode = nodes.pop();
                String action = tempNode.priviousAction;
                System.out.println(action + " " + tempNode.player.money + " " + tempNode.player.food);
                myWriter.write(action + "\n");
                //print visualized map for every movement
                //visualizer.printMap(tempNode.map, tempNode.player);
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
