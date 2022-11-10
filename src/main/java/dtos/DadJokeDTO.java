package dtos;

public class DadJokeDTO {
    String joke;
    String status;

    public DadJokeDTO() {
    }

    @Override
    public String toString() {
        return joke + '\n' + status ;
    }
}
