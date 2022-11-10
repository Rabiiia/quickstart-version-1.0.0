package dtos;

public class ChuckDTO {
    String value;
    String created_at;


    public ChuckDTO() {
    }

    @Override
    public String toString() {
        return  value + "\n" + created_at;
    }
}
