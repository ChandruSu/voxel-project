package engine;

public class MathUtils
{
    public static final float RAD90 = (float) Math.PI / 2;

    public static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
}
