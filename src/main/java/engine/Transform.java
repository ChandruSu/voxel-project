package engine;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform
{
    private final Vector3f position;
    private final Quaternionf rotation;
    private final Vector3f scale;

    public Transform()
    {
        this.position = new Vector3f(0.0f);
        this.rotation = new Quaternionf().identity();
        this.scale = new Vector3f(1.0f);
    }

    public Transform(float x, float y, float z)
    {
        this(new Vector3f(x, y, z));
    }

    public Transform(Vector3f position)
    {
        this(position, new Quaternionf().identity(), new Vector3f(1.0f));
    }

    public Transform(Vector3f position, Quaternionf rotation, Vector3f scale)
    {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public void translate(float dx, float dy, float dz)
    {
        this.position.add(dx, dy, dz);
    }

    public void translate(Vector3f dv) {
        this.position.add(dv);
    }

    public void rotate(float ax, float ay, float az)
    {
        rotation.rotateAxis((float)Math.toRadians(ax), 1, 0, 0);
        rotation.rotateAxis((float)Math.toRadians(ay), 0, 1, 0);
        rotation.rotateAxis((float)Math.toRadians(az), 0, 0, 1);
    }

    public void rotate(Quaternionf q)
    {
        this.rotation.mul(q);
    }

    public void scale(float sx, float sy, float sz)
    {
        this.scale.set(scale.x * sx, scale.y * sy, scale.z * sz);
    }

    public Matrix4f getTransformation()
    {
        Matrix4f matrix = new Matrix4f().identity();
        matrix.translate(position);
        matrix.rotate(rotation);
        matrix.scale(scale);
        return matrix;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Quaternionf getRotation() {
        return rotation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public Vector3f forward() {
        return rotation.transformInverse(new Vector3f(0, 0, 1));
    }

    public Vector3f forwardXZ() {
        return forward().mul(1, 0, 1).normalize();
    }

    public Vector3f right() {
        return rotation.transformInverse(new Vector3f(1, 0, 0));
    }

    public Vector3f rightXZ() {
        return right().mul(1, 0, 1).normalize();
    }
}
