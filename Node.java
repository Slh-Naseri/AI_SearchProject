
import java.util.ArrayList;

public class Node {

    public Map map;
    public Player player;
    public Node parentNode;
    public String priviousAction;
    public int cost = 0, level = 0, g = 0, f = 0;

    public Node(Player player, Map map, Node parentNode, String priviousAction) {
        this.map = map.copy();
        this.player = new Player(player.i, player.j, player.money, player.food, player.haskey);
        this.parentNode = parentNode;
        this.priviousAction = priviousAction;
        if (this.parentNode != null) {
            this.level = this.parentNode.level + 1;
        }
        this.g = 0;
        this.f = 0;

    }

    public String hash() {
        int key = player.haskey ? 1 : 0;
        String result = player.i + "," + player.j + "," + player.money + "," + player.food + "," + key;
        int size = map.game.size();
        for (int i = 0; i < size; i++) {
            if (map.game.get(i) instanceof Bridge) {
                key = ((Bridge) map.game.get(i)).traveresd ? 1 : 0;
                result += key;
            } else if (map.game.get(i) instanceof Loot) {
                key = ((Loot) map.game.get(i)).used ? 1 : 0;
                result += key;
            }
        }
        return result;
    }

    public ArrayList<Node> successor(boolean back) {
        ArrayList<Node> result = new ArrayList<Node>();
        if (this.player.j < this.map.cols - 1) {//player can move right
            BaseEntity entity = this.map.at(this.player.i, this.player.j + 1);
            if (entity.name != 'S') {
                if (entity.name == 'G') {
                    Node temp = new Node(this.player, this.map, this, "right");
                    temp.player.j++;
                    temp.cost = temp.parentNode.cost + 3;
                    result.add(temp);
                } else if (entity.name == 'P') {
                    if (!back) {
                        if (!((Bridge) entity).traveresd) {
                            Node temp = new Node(this.player, this.map, this, "right");
                            temp.player.j++;
                            ((Bridge) temp.map.at(temp.player.i, temp.player.j)).traveresd = true;
                            temp.cost = temp.parentNode.cost + 3;
                            result.add(temp);
                        }
                    } else {
                        if (((Bridge) entity).traveresd) {
                            Node temp = new Node(this.player, this.map, this, "right");
                            temp.player.j++;
                            ((Bridge) temp.map.at(temp.player.i, temp.player.j)).traveresd = false;
                            result.add(temp);
                        }
                    }
                } else if (entity.name == 'C') {
                    if (player.haskey && !back) {
                        Node temp = new Node(this.player, this.map, this, "right");
                        temp.player.j++;
                        temp.cost = temp.parentNode.cost + 3;
                        result.add(temp);
                    }
                } else if (entity.name == 'K') {
                    if (!back) {
                        Node temp = new Node(this.player, this.map, this, "right");
                        temp.player.j++;
                        temp.player.haskey = true;
                        temp.cost = temp.parentNode.cost + 3;
                        result.add(temp);
                    } else {
                        Node temp = new Node(this.player, this.map, this, "right");
                        temp.player.j++;
                        temp.player.haskey = false;
                        result.add(temp);
                    }
                } else if (entity.name == 'B') {
                    Bandit bandit = (Bandit) entity;
                    if (!back) {
                        if (this.player.money > bandit.power) {
                            Node temp = new Node(this.player, this.map, this, "right");
                            temp.player.j++;
                            bandit.takeMoney(temp.player);
                            temp.cost = temp.parentNode.cost + 3;
                            result.add(temp);
                        }
                    } else {
                        Node temp = new Node(this.player, this.map, this, "right");
                        temp.player.j++;
                        temp.player.changeMoney(bandit.power);
                        result.add(temp);
                    }
                } else if (entity.name == 'W') {
                    WildAnimall wildAnimall = (WildAnimall) entity;
                    if (!back) {
                        if (this.player.food > wildAnimall.power) {
                            Node temp = new Node(this.player, this.map, this, "right");
                            temp.player.j++;
                            wildAnimall.takeFood(temp.player);
                            temp.cost = temp.parentNode.cost + 3;
                            result.add(temp);
                        }
                    } else {
                        Node temp = new Node(this.player, this.map, this, "right");
                        temp.player.j++;
                        temp.player.changeFood(wildAnimall.power);
                        result.add(temp);
                    }
                } else if (entity.name == 'L') {
                    Loot loot = (Loot) entity;
                    if (!back) {
                        if (loot.used) {
                            Node temp = new Node(this.player, this.map, this, "right");
                            temp.player.j++;
                            temp.cost = +temp.parentNode.cost + 3;
                            result.add(temp);
                        } else {
                            Node temp1 = new Node(this.player, this.map, this, "right, use money");
                            temp1.player.j++;
                            ((Loot) temp1.map.at(temp1.player.i, temp1.player.j)).useMoney(temp1.player);
                            temp1.cost = +temp1.parentNode.cost + 3;
                            result.add(temp1);
                            Node temp2 = new Node(this.player, this.map, this, "right, use food");
                            temp2.cost = +temp2.parentNode.cost + 3;
                            temp2.player.j++;
                            ((Loot) temp2.map.at(temp2.player.i, temp2.player.j)).useFood(temp2.player);
                            result.add(temp2);
                        }
                    } else {
                        if (loot.used) {
                            Node temp1 = new Node(this.player, this.map, this, "right, use money");
                            temp1.player.j++;
                            temp1.player.changeMoney(-((Loot) temp1.map.at(temp1.player.i, temp1.player.j)).money);
                            result.add(temp1);
                            Node temp2 = new Node(this.player, this.map, this, "right, use food");
                            temp2.player.j++;
                            temp2.player.changeFood(-((Loot) temp2.map.at(temp2.player.i, temp2.player.j)).food);
                            result.add(temp2);
                            ((Loot) temp1.map.at(temp1.player.i, temp1.player.j)).used=false;
                            ((Loot) temp2.map.at(temp2.player.i, temp2.player.j)).used=false;
                        }
                        else{
                            Node temp = new Node(this.player, this.map, this, "right");
                            temp.player.j++;
                            result.add(temp);
                        }
                    }
                }
            }
        }
        if (this.player.j > 0) {//player can move left
            BaseEntity entity = this.map.at(this.player.i, this.player.j - 1);
            if (entity.name != 'S') {
                if (entity.name == 'G') {
                    Node temp = new Node(this.player, this.map, this, "left");
                    temp.player.j--;
                    temp.cost = temp.parentNode.cost + 1;
                    result.add(temp);
                } else if (entity.name == 'P') {
                    if (!back) {
                        if (!((Bridge) entity).traveresd) {
                            Node temp = new Node(this.player, this.map, this, "left");
                            temp.player.j--;
                            ((Bridge) temp.map.at(temp.player.i, temp.player.j)).traveresd = true;
                            temp.cost = temp.parentNode.cost + 1;
                            result.add(temp);
                        }
                    } else {
                        if (((Bridge) entity).traveresd) {
                            Node temp = new Node(this.player, this.map, this, "left");
                            temp.player.j--;
                            ((Bridge) temp.map.at(temp.player.i, temp.player.j)).traveresd = false;
                            result.add(temp);
                        }
                    }
                } else if (entity.name == 'C') {
                    if (player.haskey && !back) {
                        Node temp = new Node(this.player, this.map, this, "left");
                        temp.player.j--;
                        temp.cost = temp.parentNode.cost + 1;
                        result.add(temp);
                    }
                } else if (entity.name == 'K') {
                    if (!back) {
                        Node temp = new Node(this.player, this.map, this, "left");
                        temp.player.j--;
                        temp.player.haskey = true;
                        temp.cost = temp.parentNode.cost + 1;
                        result.add(temp);
                    } else {
                        Node temp = new Node(this.player, this.map, this, "left");
                        temp.player.j--;
                        temp.player.haskey = false;
                        result.add(temp);
                    }
                } else if (entity.name == 'B') {
                    Bandit bandit = (Bandit) entity;
                    if (!back) {
                        if (this.player.money > bandit.power) {
                            Node temp = new Node(this.player, this.map, this, "left");
                            temp.player.j--;
                            bandit.takeMoney(temp.player);
                            temp.cost = temp.parentNode.cost + 1;
                            result.add(temp);
                        }
                    } else {
                        Node temp = new Node(this.player, this.map, this, "left");
                        temp.player.j--;
                        temp.player.changeMoney(bandit.power);
                        result.add(temp);
                    }
                } else if (entity.name == 'W') {
                    WildAnimall wildAnimall = (WildAnimall) entity;
                    if (!back) {
                        if (this.player.food > wildAnimall.power) {
                            Node temp = new Node(this.player, this.map, this, "left");
                            temp.player.j--;
                            wildAnimall.takeFood(temp.player);
                            temp.cost = temp.parentNode.cost + 1;
                            result.add(temp);
                        }
                    } else {
                        Node temp = new Node(this.player, this.map, this, "left");
                        temp.player.j--;
                        temp.player.changeFood(wildAnimall.power);
                        result.add(temp);
                    }
                } else if (entity.name == 'L') {
                    Loot loot = (Loot) entity;
                    if (!back) {
                        if (loot.used) {
                            Node temp = new Node(this.player, this.map, this, "left");
                            temp.player.j--;
                            temp.cost = temp.parentNode.cost + 1;
                            result.add(temp);
                        } else {
                            Node temp1 = new Node(this.player, this.map, this, "left, use money");
                            temp1.player.j--;
                            ((Loot) temp1.map.at(temp1.player.i, temp1.player.j)).useMoney(temp1.player);
                            temp1.cost = temp1.parentNode.cost + 1;
                            result.add(temp1);
                            Node temp2 = new Node(this.player, this.map, this, "left, use food");
                            temp2.player.j--;
                            ((Loot) temp2.map.at(temp2.player.i, temp2.player.j)).useFood(temp2.player);
                            temp2.cost = temp2.parentNode.cost + 1;
                            result.add(temp2);
                        }
                    } else {
                        if (loot.used) {
                            Node temp1 = new Node(this.player, this.map, this, "left, use money");
                            temp1.player.j--;
                            temp1.player.changeMoney(-((Loot) temp1.map.at(temp1.player.i, temp1.player.j)).money);
                            result.add(temp1);
                            Node temp2 = new Node(this.player, this.map, this, "left, use food");
                            temp2.player.j--;
                            temp2.player.changeFood(-((Loot) temp2.map.at(temp2.player.i, temp2.player.j)).food);
                            result.add(temp2);
                            ((Loot) temp1.map.at(temp1.player.i, temp1.player.j)).used=false;
                            ((Loot) temp2.map.at(temp2.player.i, temp2.player.j)).used=false;
                        }
                        else{
                            Node temp = new Node(this.player, this.map, this, "left");
                            temp.player.j--;
                            result.add(temp);
                        }
                    }
                }
            }
        }
        if (this.player.i > 0) {//player can move up
            BaseEntity entity = this.map.at(this.player.i - 1, this.player.j);
            if (entity.name != 'S') {
                if (entity.name == 'G') {
                    Node temp = new Node(this.player, this.map, this, "up");
                    temp.player.i--;
                    temp.cost = temp.parentNode.cost + 1;
                    result.add(temp);
                } else if (entity.name == 'P') {
                    if (!back) {
                        if (!((Bridge) entity).traveresd) {
                            Node temp = new Node(this.player, this.map, this, "up");
                            temp.player.i--;
                            ((Bridge) temp.map.at(temp.player.i, temp.player.j)).traveresd = true;
                            temp.cost = temp.parentNode.cost + 1;
                            result.add(temp);
                        }
                    } else {
                        if (((Bridge) entity).traveresd) {
                            Node temp = new Node(this.player, this.map, this, "up");
                            temp.player.i--;
                            ((Bridge) temp.map.at(temp.player.i, temp.player.j)).traveresd = false;
                            result.add(temp);
                        }
                    }
                } else if (entity.name == 'C') {
                    if (player.haskey && !back) {
                        Node temp = new Node(this.player, this.map, this, "up");
                        temp.player.i--;
                        temp.cost = temp.parentNode.cost + 1;
                        result.add(temp);
                    }
                } else if (entity.name == 'K') {
                    if (!back) {
                        Node temp = new Node(this.player, this.map, this, "up");
                        temp.player.i--;
                        temp.player.haskey = true;
                        temp.cost = temp.parentNode.cost + 1;
                        result.add(temp);
                    } else {
                        Node temp = new Node(this.player, this.map, this, "up");
                        temp.player.i--;
                        temp.player.haskey = false;
                        result.add(temp);
                    }
                } else if (entity.name == 'B') {
                    Bandit bandit = (Bandit) entity;
                    if (!back) {
                        if (this.player.money > bandit.power) {
                            Node temp = new Node(this.player, this.map, this, "up");
                            temp.player.i--;
                            bandit.takeMoney(temp.player);
                            temp.cost = temp.parentNode.cost + 1;
                            result.add(temp);
                        }
                    } else {
                        Node temp = new Node(this.player, this.map, this, "up");
                        temp.player.i--;
                        temp.player.changeMoney(bandit.power);
                        result.add(temp);
                    }
                } else if (entity.name == 'W') {
                    WildAnimall wildAnimall = (WildAnimall) entity;
                    if (!back) {
                        if (this.player.food > wildAnimall.power) {
                            Node temp = new Node(this.player, this.map, this, "up");
                            temp.player.i--;
                            wildAnimall.takeFood(temp.player);
                            temp.cost = temp.parentNode.cost + 1;
                            result.add(temp);
                        }
                    } else {
                        Node temp = new Node(this.player, this.map, this, "up");
                        temp.player.i--;
                        temp.player.changeFood(wildAnimall.power);
                        result.add(temp);
                    }
                } else if (entity.name == 'L') {
                    Loot loot = (Loot) entity;
                    if (!back) {
                        if (loot.used) {
                            Node temp = new Node(this.player, this.map, this, "up");
                            temp.player.i--;
                            temp.cost = temp.parentNode.cost + 1;
                            result.add(temp);
                        } else {
                            Node temp1 = new Node(this.player, this.map, this, "up, use money");
                            temp1.player.i--;
                            ((Loot) temp1.map.at(temp1.player.i, temp1.player.j)).useMoney(temp1.player);
                            temp1.cost = temp1.parentNode.cost + 1;
                            result.add(temp1);
                            Node temp2 = new Node(this.player, this.map, this, "up, use food");
                            temp2.player.i--;
                            ((Loot) temp2.map.at(temp2.player.i, temp2.player.j)).useFood(temp2.player);
                            temp2.cost = temp2.parentNode.cost + 1;
                            result.add(temp2);
                        }
                    } else {
                        if (loot.used) {
                            Node temp1 = new Node(this.player, this.map, this, "up, use money");
                            temp1.player.i--;
                            temp1.player.changeMoney(-((Loot) temp1.map.at(temp1.player.i, temp1.player.j)).money);
                            result.add(temp1);
                            Node temp2 = new Node(this.player, this.map, this, "up, use food");
                            temp2.player.i--;
                            temp2.player.changeFood(-((Loot) temp2.map.at(temp2.player.i, temp2.player.j)).food);
                            result.add(temp2);
                            ((Loot) temp1.map.at(temp1.player.i, temp1.player.j)).used=false;
                            ((Loot) temp2.map.at(temp2.player.i, temp2.player.j)).used=false;
                        }
                        else{
                            Node temp = new Node(this.player, this.map, this, "up");
                            temp.player.i--;
                            result.add(temp);
                        }
                    }
                }
            }
        }
        if (this.player.i < this.map.rows - 1) {//player can move down
            BaseEntity entity = this.map.at(this.player.i + 1, this.player.j);
            if (entity.name != 'S') {
                if (entity.name == 'G') {
                    Node temp = new Node(this.player, this.map, this, "down");
                    temp.player.i++;
                    temp.cost = temp.parentNode.cost + 1;
                    result.add(temp);
                } else if (entity.name == 'P') {
                    if (!back) {
                        if (!((Bridge) entity).traveresd) {
                            Node temp = new Node(this.player, this.map, this, "down");
                            temp.player.i++;
                            ((Bridge) temp.map.at(temp.player.i, temp.player.j)).traveresd = true;
                            temp.cost = temp.parentNode.cost + 1;
                            result.add(temp);
                        }
                    } else {
                        if (((Bridge) entity).traveresd) {
                            Node temp = new Node(this.player, this.map, this, "down");
                            temp.player.i++;
                            ((Bridge) temp.map.at(temp.player.i, temp.player.j)).traveresd = false;
                            result.add(temp);
                        }
                    }
                } else if (entity.name == 'C') {
                    if (player.haskey && !back) {
                        Node temp = new Node(this.player, this.map, this, "down");
                        temp.player.i++;
                        temp.cost = temp.parentNode.cost + 1;
                        result.add(temp);
                    }
                } else if (entity.name == 'K') {
                    if (!back) {
                        Node temp = new Node(this.player, this.map, this, "down");
                        temp.player.i++;
                        temp.player.haskey = true;
                        temp.cost = temp.parentNode.cost + 1;
                        result.add(temp);
                    } else {
                        Node temp = new Node(this.player, this.map, this, "down");
                        temp.player.i++;
                        temp.player.haskey = false;
                        result.add(temp);
                    }
                } else if (entity.name == 'B') {
                    Bandit bandit = (Bandit) entity;
                    if (!back) {
                        if (this.player.money > bandit.power) {
                            Node temp = new Node(this.player, this.map, this, "down");
                            temp.player.i++;
                            bandit.takeMoney(temp.player);
                            temp.cost = temp.parentNode.cost + 1;
                            result.add(temp);
                        }
                    } else {
                        Node temp = new Node(this.player, this.map, this, "down");
                        temp.player.i++;
                        temp.player.changeMoney(bandit.power);
                        result.add(temp);
                    }
                } else if (entity.name == 'W') {
                    WildAnimall wildAnimall = (WildAnimall) entity;
                    if (!back) {
                        if (this.player.food > wildAnimall.power) {
                            Node temp = new Node(this.player, this.map, this, "down");
                            temp.player.i++;
                            wildAnimall.takeFood(temp.player);
                            temp.cost = temp.parentNode.cost + 1;
                            result.add(temp);
                        }
                    } else {
                        Node temp = new Node(this.player, this.map, this, "down");
                        temp.player.i++;
                        temp.player.changeFood(wildAnimall.power);
                        result.add(temp);
                    }
                } else if (entity.name == 'L') {
                    Loot loot = (Loot) entity;
                    if (!back) {
                        if (loot.used) {
                            Node temp = new Node(this.player, this.map, this, "down");
                            temp.player.i++;
                            temp.cost = temp.parentNode.cost + 1;
                            result.add(temp);
                        } else {
                            Node temp1 = new Node(this.player, this.map, this, "down, use money");
                            temp1.player.i++;
                            ((Loot) temp1.map.at(temp1.player.i, temp1.player.j)).useMoney(temp1.player);
                            temp1.cost = temp1.parentNode.cost + 1;
                            result.add(temp1);
                            Node temp2 = new Node(this.player, this.map, this, "down, use food");
                            temp2.player.i++;
                            ((Loot) temp2.map.at(temp2.player.i, temp2.player.j)).useFood(temp2.player);
                            temp2.cost = temp2.parentNode.cost + 1;
                            result.add(temp2);
                        }
                    } else {
                        if (loot.used) {
                            Node temp1 = new Node(this.player, this.map, this, "down, use money");
                            temp1.player.i++;
                            temp1.player.changeMoney(-((Loot) temp1.map.at(temp1.player.i, temp1.player.j)).money);
                            result.add(temp1);
                            Node temp2 = new Node(this.player, this.map, this, "down, use food");
                            temp2.player.i++;
                            temp2.player.changeFood(-((Loot) temp2.map.at(temp2.player.i, temp2.player.j)).food);
                            result.add(temp2);
                            ((Loot) temp1.map.at(temp1.player.i, temp1.player.j)).used=false;
                            ((Loot) temp2.map.at(temp2.player.i, temp2.player.j)).used=false;
                        }
                        else{
                            Node temp = new Node(this.player, this.map, this, "down");
                            temp.player.i++;
                            result.add(temp);
                        }
                    }
                }
            }
        }
        return result;
    }
}
