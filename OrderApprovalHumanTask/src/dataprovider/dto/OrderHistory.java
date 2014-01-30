package dataprovider.dto;

import java.util.Date;

public class OrderHistory {
    public OrderHistory() {
        super();
    }
    private Date _date;
    private Double _total;

    public void setDate(Date _date) {
        this._date = _date;
    }

    public Date getDate() {
        return _date;
    }

    public void setTotal(Double _total) {
        this._total = _total;
    }

    public Double getTotal() {
        return _total;
    }
}
