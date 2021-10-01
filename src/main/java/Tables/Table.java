package Tables;

import Order.Order;

import java.util.concurrent.TimeUnit;

public class Table {

    private static int count = 0;

    private int id;

    private TableState state;

    private Order order;

    public Table() {
        id = count++;
        this.state = TableState.Free;
    }

    public void generateOrder() throws InterruptedException {
        System.out.println("Generating order...");
        TimeUnit.SECONDS.sleep((int)(Math.random()*4+2));
        order = new Order();
    }

    public Order getOrder() {
        return order;
    }

    public void switchState(TableState state) {
        this.state=state;
    }

}

enum TableState {
    Free,
    WaitingMakingOrder,
    WaitingOrder
}
