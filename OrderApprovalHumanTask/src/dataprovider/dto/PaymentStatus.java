package dataprovider.dto;

public class PaymentStatus {
    public PaymentStatus() {
        super();
    }
    static public final String STATUS_PAID = "Paid";
    static public final String STATUS_DISPUTED = "Disputed";
    static public final String STATUS_OPEN = "Open";
    
    private String _status;
    private Integer _numInstances;

    public void setStatus(String _status) {
        this._status = _status;
    }

    public String getStatus() {
        return _status;
    }

    public void setNumInstances(Integer _numInstances) {
        this._numInstances = _numInstances;
    }

    public Integer getNumInstances() {
        return _numInstances;
    }
}
