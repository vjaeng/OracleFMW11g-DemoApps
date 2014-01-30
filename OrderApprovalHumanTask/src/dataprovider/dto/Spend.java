package dataprovider.dto;

public class Spend {
    public Spend() {
        super();
    }
    private String _period;
    private Double _customer;
    private Double _average;


    public void setPeriod(String _period) {
        this._period = _period;
    }

    public String getPeriod() {
        return _period;
    }

    public void setCustomer(Double _customer) {
        this._customer = _customer;
    }

    public Double getCustomer() {
        return _customer;
    }

    public void setAverage(Double _average) {
        this._average = _average;
    }

    public Double getAverage() {
        return _average;
    }
}
