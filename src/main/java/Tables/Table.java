package Tables;

import Order.Order;
import com.example.dinninghallapi.DinningHallApiApplication;

public class Table {

    private static int count = 0;

    private final int id = count++;

    private TableState state = TableState.Free;

    private Order order;

    public Table() {
    }

    public void generateOrder() {
        order = new Order();

        System.out.println("Table "+id+ " Order "+order.getId()+" was generated!");
    }

    public /*synchronized*/ Order makeOrder() throws InterruptedException {
        DinningHallApiApplication.timeUnit.sleep(7);

        state = TableState.WaitingOrder;

        order.setPickupTime();

        return order;
    }

    public synchronized void receiveOrder() {

        state = TableState.Free;

    }

    public int getId() {
        return id;
    }

    public synchronized Order getOrder() {
        return order;
    }

    public /*synchronized*/ void switchState(TableState state) {
        this.state=state;
    }

    public /*synchronized*/ TableState getState() {
        return state;
    }
}
