package pwr.android_app.dataStructures;

public class StatsResponse {

    /* ========================================== DATA ========================================== */

    private int unspecified;
    private int down;
    private int up;

    /* ====================================== CONSTRUCTORS ====================================== */

    public StatsResponse(int unspecified, int down, int up) {
        this.unspecified = unspecified;
        this.down = down;
        this.up = up;
    }

    /* ========================================= GETTERS ======================================== */

    public int getUnspecified() {
        return unspecified;
    }

    public int getDown() {
        return down;
    }

    public int getUp() {
        return up;
    }

    /* ========================================= SETTERS ======================================== */

    public void setUnspecified(int unspecified) {
        this.unspecified = unspecified;
    }

    public void setDown(int down) {
        this.down = down;
    }

    public void setUp(int up) {
        this.up = up;
    }

    /* ========================================================================================== */
}
