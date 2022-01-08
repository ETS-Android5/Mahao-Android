package ke.co.tonyoa.mahao.app.enums;

public enum SortBy {
    TIME("time"),
    NEG_TIME("-time"),
    PRICE("price"),
    NEG_PRICE("-price"),
    DISTANCE("distance"),
    NEG_DISTANCE("-distance");

    private String mApiValue;

    SortBy(String apiValue){
        mApiValue = apiValue;
    }

    public String getApiValue() {
        return mApiValue;
    }

}
