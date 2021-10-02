package Tables;

import Order.Order;
import com.example.dinninghallapi.DinningHallApiApplication;

public class Table {

    private static int count = 0;

    private int id;

    private TableState state;

    private Order order;

    public Table() {
        id = count++;
        this.state = TableState.Free;
    }

    public synchronized void generateOrder() {
        System.out.println("Table "+id+ " Generating order...");

        order = new Order();

        System.out.println("Table "+id+ " Order is Ready!");
    }

    public synchronized Order makeOrder() throws InterruptedException {
        DinningHallApiApplication.timeUnit.sleep(7);
        order.setPickupTime();
        return order;
    }

    public synchronized Order getOrder() {
        return order;
    }

    public synchronized void switchState(TableState state) {
        this.state=state;
    }

    public synchronized TableState getState() {
        return state;
    }
}
