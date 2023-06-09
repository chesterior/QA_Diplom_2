package site.nomoreparties.stellarburgers.api.model;

public class CreateOrder {
    private String[] ingredients;

    public CreateOrder(String[] ingredients) {
        this.ingredients = ingredients;
    }
}
