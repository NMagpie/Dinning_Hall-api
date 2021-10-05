package Order;

import Tables.Table;
import Tables.TableState;

import static com.example.dinninghallapi.DinningHallApiApplication.timeUnit;

public class OrderGeneration implements Runnable {

    private Table[] tables;

    public OrderGeneration(Table[] tables) {
        this.tables = tables;
    }

    @Override
    public void run() {

        Thread.currentThread().setName("OrderGen");

        int tableId;

        while (true)
        {
            try { timeUnit.sleep(7); }
            catch (InterruptedException e) { e.printStackTrace(); }

            if (Math.random()>0.65)
            {
                do tableId = (int) (Math.random() * tables.length-1 + 0);
                while (tables[tableId].getState()!= TableState.Free);

                tables[tableId].generateOrder();
                tables[tableId].switchState(TableState.WaitingMakingOrder);
            }

        }

    }



}
