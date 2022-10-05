
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GBFS {

    //Visualizer visualizer = new Visualizer();
    public void search(Node intialNode) {
        LinkedList<Node> frontier = new LinkedList<Node>();
        Hashtable<String, Boolean> inFrontier = new Hashtable<>();
        Hashtable<String, Boolean> explored = new Hashtable<>();
        int a[] = {10, 10}, Ci = 0, Cj = 0, Ki = 0, Kj = 0;
        if (isGoal(intialNode)) {
            result(intialNode);
            return;
        }
        a = findC(intialNode);
        Ci = a[0];
        Cj = a[1];
        a = findK(intialNode);
        Ki = a[0];
        Kj = a[1];
        frontier.add(intialNode);
        intialNode.f = fCalculator(intialNode, Ci, Cj, Ki, Kj);
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
                    children.get(i).f = fCalculator(children.get(i), Ci, Cj, Ki, Kj);
                    int j = 0;
                    for (; j < frontier.size(); j++) {
                        if (frontier.get(j).f >= children.get(i).f) {
                            frontier.add(j,children.get(i));
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

    public int fCalculator(Node node, int Ci, int Cj, int Ki, int Kj) {
        int h = 0;
        if (!node.player.haskey) {
            h = Math.abs(node.player.i - Ki) + Math.abs(node.player.j - Kj) + Math.abs(Ci - Ki) + Math.abs(Cj - Kj);
        } else {
            h = Math.abs(node.player.i - Ci) + Math.abs(node.player.j - Cj);
        }
        return h;
    }
     public int[] findC(Node node) {
        int c[] = {0, 0}, i, j;
        for (j = 0; j < node.map.cols; j++) {
            for (i = 0; i < node.map.rows; i++) {
                if (node.map.game.get((i * node.map.cols) + j).name == 'C') {
                    c[0] = i;
                    c[1] = j;
                    break;
                }
            }
            if (i != node.map.rows) {
                break;
            }
        }
        return c;
    }

    public int[] findK(Node node) {
        int k[] = {0, 0}, i, j;
        for (j = 0; j < node.map.cols; j++) {
            for (i = 0; i < node.map.rows; i++) {
                if (node.map.game.get((i * node.map.cols) + j).name == 'K') {
                    k[0] = i;
                    k[1] = j;
                }
            }
            if (i != node.map.rows) {
                break;
            }
        }
        return k;

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
