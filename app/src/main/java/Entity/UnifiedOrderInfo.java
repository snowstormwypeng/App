package Entity;

public class UnifiedOrderInfo extends BaseEnity {
    private String cab_no;
    private String cell_no;
    private long giftinfo_id;
    private String buyer_card_no;
    private String buyer_card_name;
    private String buyer_card_type;
    private String buyer_regtime;
    private String body;
    private String attach;
    private Double trade_tickets;
    private Double trade_coins;
    private Integer trade_points;

    public String getCab_no() {
        return cab_no;
    }

    public void setCab_no(String cab_no) {
        this.cab_no = cab_no;
    }

    public String getCell_no() {
        return cell_no;
    }

    public void setCell_no(String cell_no) {
        this.cell_no = cell_no;
    }

    public long getGiftinfo_id() {
        return giftinfo_id;
    }

    public void setGiftinfo_id(long giftinfo_id) {
        this.giftinfo_id = giftinfo_id;
    }

    public String getBuyer_card_no() {
        return buyer_card_no;
    }

    public void setBuyer_card_no(String buyer_card_no) {
        this.buyer_card_no = buyer_card_no;
    }

    public String getBuyer_card_name() {
        return buyer_card_name;
    }

    public void setBuyer_card_name(String buyer_card_name) {
        this.buyer_card_name = buyer_card_name;
    }

    public String getBuyer_card_type() {
        return buyer_card_type;
    }

    public void setBuyer_card_type(String buyer_card_type) {
        this.buyer_card_type = buyer_card_type;
    }

    public String getBuyer_regtime() {
        return buyer_regtime;
    }

    public void setBuyer_regtime(String buyer_regtime) {
        this.buyer_regtime = buyer_regtime;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public Double getTrade_tickets() {
        return trade_tickets;
    }

    public void setTrade_tickets(Double trade_tickets) {
        this.trade_tickets = trade_tickets;
    }

    public Double getTrade_coins() {
        return trade_coins;
    }

    public void setTrade_coins(Double trade_coins) {
        this.trade_coins = trade_coins;
    }

    public Integer getTrade_points() {
        return trade_points;
    }

    public void setTrade_points(Integer trade_points) {
        this.trade_points = trade_points;
    }
}
