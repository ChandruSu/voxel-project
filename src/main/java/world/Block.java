package world;

public enum Block
{
    AIR(0), COBBLE(1), DIRT(2);

    private final int id;

    private Block(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }
}
