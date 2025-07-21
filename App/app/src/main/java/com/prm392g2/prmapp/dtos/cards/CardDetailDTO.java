package com.prm392g2.prmapp.dtos.cards;

public class CardDetailDTO {
    public int id;
    public String front;
    public String back;
    public int index;

    public CardDetailDTO(int id, String front, String back, int index) {
        this.id = id;
        this.front = front;
        this.back = back;
        this.index = index;
    }
}
