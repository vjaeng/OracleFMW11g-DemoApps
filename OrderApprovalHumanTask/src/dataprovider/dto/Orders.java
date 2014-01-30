package dataprovider.dto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Orders 
{
    public Orders() {
        super();
        generateDemoData();
    }
    
    private void generateDemoData () 
    {

        String[] periods =
        { "Q1 '08", "Q2 '08", "Q3 '08", "Q4 '08", "Q1 '09" };
        double[][] spend =
        { { 3000, 4500, 5500, 6700, 4000 }, { 2000, 3500, 6500, 5700, 4500 } };
        String[] series = { "This Customer", "Average Customer" };

        Object[] elem;

        _spendHistory = new Spend[5];
        for (int j = 0; j < 5; j++) {
            _spendHistory[j] = new Spend();
            _spendHistory[j].setPeriod(periods[j]);
            _spendHistory[j].setCustomer(spend[0][j]);
            _spendHistory[j].setAverage(spend[1][j]);

        }

        String[] status =
        { PaymentStatus.STATUS_DISPUTED, PaymentStatus.STATUS_PAID,
          PaymentStatus.STATUS_OPEN };
        Integer[] numInstances = { 10, 100, 60 };

        _payments = new PaymentStatus[3];
        for (int j = 0; j < 3; j++) {
            _payments[j] = new PaymentStatus();
            _payments[j].setStatus(status[j]);
            _payments[j].setNumInstances(numInstances[j]);
            // _payments[j].setDaysToCollect(daysOutstanding[j]);
        }

        String[] product =
        { "IPod 32GB", "Amazon Kindle", "Plantronics DS55" };
        Integer[] quantity = { 5, 4, 10 };
        Double[] price = { 499.99, 199.96, 87.55 };


        _item = new Item[3];
        for (int j = 0; j < 3; j++) {
            _item[j] = new Item();
            _item[j].setProduct(product[j]);
            _item[j].setUnitPrice(price[j]);
        }
        _orderHistory = new OrderHistory[6];
        Double[] totals =
        { 400.00, 800.00, 2000.00, 1200.00, 1650.00, 1920.00 };
        for (int j = 0; j < 6; j++) {
            int daysOff = 120 - j * 15;
            double total = totals[j];

            _orderHistory[j] = new OrderHistory();

            Calendar cal = Calendar.getInstance();
            cal.roll(Calendar.DATE, daysOff);
            _orderHistory[j].setDate(cal.getTime());

            _orderHistory[j].setTotal(total);
        }

        Integer[] _days = { 94, 30, 21, 43 };
        String[] _groups = { "0-30", "30-60", "60-90", "90+" };

        _daysOutstanding = new DaysOutstanding[4];
        for (int x = 0; x < 4; x++) 
        {
            _daysOutstanding[x] = new DaysOutstanding();
            _daysOutstanding[x].setGroupDay(_days[x]);
            _daysOutstanding[x].setGroupBy(_groups[x]);
        }
    }
                                         
    
    Spend [] _spendHistory;
    PaymentStatus [] _payments;
    DaysOutstanding []_daysOutstanding;
    Item [] _item;
    OrderHistory [] _orderHistory;
    
    public void setSpendHistory(Spend[] _spendHistory) {
        this._spendHistory = _spendHistory;
    }

    public Spend[] getSpendHistory() {
        return _spendHistory;
    }

    public void setPayments(PaymentStatus[] _payments) {
        this._payments = _payments;
    }

    public PaymentStatus[] getPayments() {
        return _payments;
    }

    public void setDaysOutstanding(DaysOutstanding[] _daysOutstanding) {
        this._daysOutstanding = _daysOutstanding;
    }

    public DaysOutstanding[] getDaysOutstanding() {
        return _daysOutstanding;
    }

    public void setItem(Item[] _item) {
        this._item = _item;
    }

    public Item[] getItem() {
        return _item;
    }

    public void setOrderHistory(OrderHistory[] _orderHistory) {
        this._orderHistory = _orderHistory;
    }

    public OrderHistory[] getOrderHistory() {
        return _orderHistory;
    }
}
