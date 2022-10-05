
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class RBFS {

    //Visualizer visualizer = new Visualizer();
    public void search(Node intialNode) {
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
        intialNode.f = fCalculator(intialNode, Ci, Cj, Ki, Kj);
        Rbfs(intialNode, 10000, inFrontier, explored, Ci, Cj, Ki, Kj);
        
    }

    public int[] Rbfs(Node node, int f_limit, Hashtable<String, Boolean> inFrontier, Hashtable<String, Boolean> explored,
            int Ci, int Cj, int Ki, int Kj) {
        int result[] = {0, 0}; 
        Node best;
        LinkedList<Node> frontier = new LinkedList<Node>();
        inFrontier.remove(node.hash());
        explored.put(node.hash(), true);
        ArrayList<Node> children = node.successor(false);
        for (int i = 0; i < children.size(); i++) {
            if (!(inFrontier.containsKey(children.get(i).hash())) && !(explored.containsKey(children.get(i).hash()))) {
                if (isGoal(children.get(i))) {
                    result(children.get(i));
                    result[0] = 1;
                    return result;
                }
                children.get(i).g = children.get(i).parentNode.g + 1;
                children.get(i).f = Math.max(fCalculator(children.get(i), Ci, Cj, Ki, Kj), node.f);
                frontier.add(children.get(i));
                inFrontier.put(children.get(i).hash(), true);
            }
        }
        if(frontier.size()==0){
                result[0]=0;
                result[1]=10000;
                return result;
            }
        while (true) {
            int alternative=10000;
            best = frontier.get(0);
            for (int j = 0; j < frontier.size(); j++) {
                    if (frontier.get(j).f < best.f) {
                        best=frontier.get(j);
                    }
                }
            
            if(best.f>f_limit){
                result[0]=0;
                result[1]=best.f;
                for(int j = 0; j < children.size(); j++){
                    explored.remove(children.get(j).hash());
                    inFrontier.remove(children.get(j).hash());
                }
                return result;
            }
            for (int j = 0; j < frontier.size(); j++) {
                if(frontier.get(j)!=best){
                    if (frontier.get(j).f <= alternative) {
                        alternative=frontier.get(j).f;
                    }
                }
                }
            result=Rbfs(best, Math.min(alternative, f_limit), inFrontier, explored, Ci, Cj, Ki, Kj);
            best.f=result[1];
            if(result[0]!=0){
                for(int j = 0; j < children.size(); j++){
                    explored.remove(children.get(j).hash());
                    inFrontier.remove(children.get(j).hash());
                }
            return result;
            }
        }
        
    }

    public int fCalculator(Node node, int Ci, int Cj, int Ki, int Kj) {
        int f = 0, h = 0;
        if (!node.player.haskey) {
            h = Math.abs(node.player.i - Ki) + Math.abs(node.player.j - Kj) + Math.abs(Ci - Ki) + Math.abs(Cj - Kj);
        } else {
            h = Math.abs(node.player.i - Ci) + Math.abs(node.player.j - Cj);
        }
        f = node.g + h;
        return f;
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
