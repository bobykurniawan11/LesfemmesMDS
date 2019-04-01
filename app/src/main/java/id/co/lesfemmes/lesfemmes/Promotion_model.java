package id.co.lesfemmes.lesfemmes;

public class Promotion_model {
    String PromotionCode,PromotionDescription,DiscountPromotion;

    public Promotion_model(String PromotionCode,String PromotionDescription,String DiscountPromotion){
        this.PromotionCode = PromotionCode;
        this.PromotionDescription = PromotionDescription;
        this.DiscountPromotion = DiscountPromotion;
    }
    public String getPromotionCode() {
        return PromotionCode;
    }
    public String getPromotionDescription() {
        return PromotionDescription;
    }

    public String getDiscountPromotion() {
        return DiscountPromotion;
    }

    @Override
    public String toString() {
        return PromotionCode;
    }
}
