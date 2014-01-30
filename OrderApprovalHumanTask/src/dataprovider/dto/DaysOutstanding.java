package dataprovider.dto;

public class DaysOutstanding {
    private Integer groupDay;
    private String _groupBy;
   
    public DaysOutstanding() {
       super();
    }

    public void setGroupBy(String _groupBy) {
        this._groupBy = _groupBy;
    }

    public String getGroupBy() {
        return _groupBy;
    }

    public void setGroupDay(Integer groupDay) {
        this.groupDay = groupDay;
    }

    public Integer getGroupDay() {
        return groupDay;
    }
}