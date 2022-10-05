
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class BDS {

    //Visualizer visualizer = new Visualizer();
    public void search(Node intialNode) {
        Queue<Node> frontier1 = new LinkedList<Node>();
        Queue<Node> frontier2 = new LinkedList<Node>();
        Hashtable<String, Boolean> inFrontier1 = new Hashtable<>();
        Hashtable<String, Boolean> inFrontier2 = new Hashtable<>();
        Hashtable<String, Boolean> explored1 = new Hashtable<>();
        Hashtable<String, Boolean> explored2 = new Hashtable<>();
        boolean back = false;
        BFS bfs = new BFS();
        Node goal = bfs.search(intialNode);
        frontier1.add(intialNode);
        frontier2.add(goal);
        inFrontier1.put(intialNode.hash(), true);
        inFrontier2.put(goal.hash(), true);
        int level = goal.level;
        while (true) {
            if (!frontier1.isEmpty()) {
                Node temp = frontier1.poll();
                inFrontier1.remove(temp.hash());
                explored1.put(temp.hash(), true);
                ArrayList<Node> children = temp.successor(back);
                for (int i = 0; i < children.size(); i++) {
                    if (!(inFrontier1.containsKey(children.get(i).hash())) && !(explored1.containsKey(children.get(i).hash()))) {
                        if (isGoal(children.get(i), explored2)) {
                            result(children.get(i));
                            System.out.println("h of tree : " + temp.level);
                            return;
                        }
                        frontier1.add(children.get(i));
                        inFrontier1.put(children.get(i).hash(), true);
                    }
                }
                back = true;
            }
            if (!frontier2.isEmpty()) {
                Node temp = frontier2.poll();
                inFrontier2.remove(temp.hash());
                explored2.put(temp.hash(), true);
                ArrayList<Node> children = temp.successor(back);
                for (int i = 0; i < children.size(); i++) {
                    if (!(inFrontier2.containsKey(children.get(i).hash())) && !(explored2.containsKey(children.get(i).hash()))) {
                        if (isGoal(children.get(i), explored1)) {
                            result(children.get(i));
                            System.out.println("h of back tree : " + (temp.level - level));
                            return;
                        }
                        frontier2.add(children.get(i));
                        inFrontier2.put(children.get(i).hash(), true);
                    }
                }
                back = false;
            }

        }
    }

    public boolean isGoal(Node node, Hashtable<String, Boolean> explored) {
        int size = explored.size();
        explored.put(node.hash(), true);
        if (explored.size() == size) {
            return true;
        } else {
            explored.remove(node.hash());
            return false;
        }
    }

    public void result(Node node) {
        Stack<Node> nodes = new Stack<Node>();
        while (true) {
            nodes.push(node);
            if (node.parentNode == null || node.parentNode.map.at(node.player.i, node.player.j).name == 'C') {
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
