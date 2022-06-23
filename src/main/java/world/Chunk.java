package world;

import engine.FastNoiseLite;
import engine.PerlinNoise;
import org.joml.Vector3i;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL32.*;

public class Chunk
{
    public Vector3i position;

    private static final int WIDTH = 32;

    private static final int TYPE_BITS = 0xFFFF;

    private int vaoID, geoBuffer, typeBuffer, vertexCount;

    private final int[][][] voxels = new int[WIDTH][WIDTH][WIDTH];

    public Chunk(Vector3i position)
    {
        this.position = position;
        generateChunkData();
        generateMesh();
    }

    public void generateChunkData()
    {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < WIDTH; y++) {
                for (int z = 0; z < WIDTH; z++) {
                    double xCoord = (double) (position.x + x) / WIDTH;
                    double yCoord = (double) (position.y + y) / WIDTH;
                    double zCoord = (double) (position.z + z) / WIDTH;

                    double noise = 0.0;
                    double amplitude = 0.5, scale = 1.0;

                    for (int i = 1; i < 2; i++) {
                        noise += (0.5 + 0.5 * PerlinNoise.noise(xCoord / scale, yCoord / scale, zCoord / scale)) * amplitude;
                        amplitude *= 0.4;
                        scale /= i;
                    }

                    voxels[x][y][z] = Block.AIR.id();

                    if (noise < 0.23) {
                        voxels[x][y][z] = Block.COBBLE.id();
                    }
                }
            }
        }

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < WIDTH; y++) {
                for (int z = 0; z < WIDTH; z++) {
                    if (voxels[x][y][z] == Block.COBBLE.id() && getBlock(x, y + 1, z) == Block.AIR.id()) {
                        voxels[x][y][z] = Block.DIRT.id();
                    }
                }
            }
        }
    }

    private void calculateAdjacency()
    {
        int adjacencyByte;
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < WIDTH; y++) {
                for (int z = 0; z < WIDTH; z++) {
                    if ((voxels[x][y][z] & TYPE_BITS) != 0) {
                        adjacencyByte  = getBlock(x - 1, y, z) == 0 ? 0b000001 : 0;
                        adjacencyByte |= getBlock(x + 1, y, z) == 0 ? 0b000010 : 0;
                        adjacencyByte |= getBlock(x, y - 1, z) == 0 ? 0b000100 : 0;
                        adjacencyByte |= getBlock(x, y + 1, z) == 0 ? 0b001000 : 0;
                        adjacencyByte |= getBlock(x, y, z - 1) == 0 ? 0b010000 : 0;
                        adjacencyByte |= getBlock(x, y, z + 1) == 0 ? 0b100000 : 0;
                        voxels[x][y][z] |= adjacencyByte << 16;
                    }
                }
            }
        }
    }

    /**
     * Greedy meshing algorithm optimises voxel mesh by combining similar, co-planar faces, ignoring hidden or occluded
     * faces, and reducing the final quad count.
     * @return list of quads
     */
    private List<Quad> generateQuads()
    {
        int[] pos = { 0, 0, 0 };
        int h, v;
        int h0, v0, h1, v1;
        int bit;
        int voxel, voxel0;

        List<Quad> quads = new ArrayList<>();

        for (int ax = 0; ax < 3; ++ax)
        {
            h = (ax + 1) % 3;
            v = (ax + 2) % 3;

            for (pos[ax] = 0; pos[ax] < WIDTH; ++pos[ax])
            {
                bit = 1 << (ax * 2 + 16);

                for (int i = 0; i < 2; ++i)
                {
                    for (pos[h] = 0; pos[h] < WIDTH; ++pos[h])
                    {
                        for (pos[v] = 0; pos[v] < WIDTH; ++pos[v])
                        {
                            voxel = getBlock(pos[0], pos[1], pos[2]);

                            // skip air blocks and hidden faces
                            if ((voxel & TYPE_BITS) == 0 || (voxel & bit) == 0)
                                continue;

                            h0=pos[h];
                            v0=pos[v];

                            while (pos[h] < WIDTH) {
                                voxel0 = getBlock(pos[0], pos[1], pos[2]);

                                if ((voxel0 & bit) != 0 && ((voxel0 & TYPE_BITS) == (voxel & TYPE_BITS))) {
                                    voxels[pos[0]][pos[1]][pos[2]] &= ~bit;
                                    ++pos[h];
                                } else {
                                    break;
                                }
                            }
                            h1 = --pos[h];

                            boolean rowMatch = true;
                            while (pos[v] < WIDTH && rowMatch) {
                                ++pos[v];

                                for (pos[h] = h0; pos[h] <= h1 && rowMatch; ++pos[h]) {
                                    voxel0 = getBlock(pos[0], pos[1], pos[2]);
                                    rowMatch = ((voxel0 & bit) == bit) && ((voxel0 & TYPE_BITS) == (voxel & TYPE_BITS));
                                }
                                for (pos[h] = h0; pos[h] <= h1 && rowMatch; ++pos[h]) {
                                    voxels[pos[0]][pos[1]][pos[2]] &= ~bit;
                                }
                            }
                            v1 = --pos[v];

                            quads.add(new Quad(voxel & TYPE_BITS, v0, v1, h0, h1, pos[ax], ax, i));
                            pos[v] = v0;
                        }
                    }
                    bit <<= 1;
                }
            }
        }

        return quads;
    }

    private void generateMesh()
    {
        calculateAdjacency();
        List<Quad> quads = generateQuads();

        vertexCount = quads.size() * 6;
        IntBuffer buffer = BufferUtils.createIntBuffer(vertexCount);
        IntBuffer blocks = BufferUtils.createIntBuffer(vertexCount);

        int i = 0, j;
        int constant;
        for (Quad q : quads) {
            j = 1 - q.orientation % 2;
            constant = ((q.height & 31) | (q.width & 31) << 5 | q.orientation << 10) << 15;

            // packs geometry of each quad vertex into a 32-bit integer
            buffer.put(      i        , q.vertices[0][0] | q.vertices[0][1] << 5 | q.vertices[0][2] << 10 | constant);
            buffer.put(i + 1 + j, q.vertices[1][0] | q.vertices[1][1] << 5 | q.vertices[1][2] << 10 | constant | 1 << 28);
            buffer.put(i + 2 - j, q.vertices[3][0] | q.vertices[3][1] << 5 | q.vertices[3][2] << 10 | constant | 3 << 28);
            buffer.put(i + 3    , q.vertices[3][0] | q.vertices[3][1] << 5 | q.vertices[3][2] << 10 | constant | 3 << 28);
            buffer.put(i + 4 + j, q.vertices[2][0] | q.vertices[2][1] << 5 | q.vertices[2][2] << 10 | constant | 2 << 28);
            buffer.put(i + 5 - j, q.vertices[0][0] | q.vertices[0][1] << 5 | q.vertices[0][2] << 10 | constant);

            for (int k = 0; k < 6; k++) {
                blocks.put(i + k, q.type - 1);
            }

            i += 6;
        }

        // Deletes old mesh
        if (vaoID > 0) {
            delete();
        }

        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Stores geometry data for all faces
        geoBuffer = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, geoBuffer);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribIPointer(0, 1, GL_UNSIGNED_INT, 0, 0);
        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Stores texture type for each block
        typeBuffer = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, typeBuffer);
        glBufferData(GL_ARRAY_BUFFER, blocks, GL_STATIC_DRAW);
        glVertexAttribIPointer(1, 1, GL_UNSIGNED_INT, 0, 0);
        glEnableVertexAttribArray(1);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindVertexArray(0);
    }

    /**
     * Returns the value of a voxel within the chunk, returns zero if invalid coordinate is specified.
     * @param x X coordinate in chunk
     * @param y Y coordinate in chunk
     * @param z Z coordinate in chunk
     * @return voxel
     */
    private int getBlock(int x, int y, int z)
    {
        if (0 <= x && x < WIDTH && 0 <= y && y < WIDTH && 0 <= z && z < WIDTH) {
            return voxels[x][y][z];
        } else {
            return 0;
        }
    }

    /**
     * Returns the vertex array object id.
     * @return vao id
     */
    public int getVaoID()
    {
        return vaoID;
    }

    /**
     * Returns the number of vertices in the chunk mesh.
     * @return vertex count
     */
    public int getVertexCount()
    {
        return vertexCount;
    }

    /**
     * Returns the world position of the chunk
     * @return position
     */
    public Vector3i getPosition()
    {
        return position;
    }

    /**
     * Deletes vertex buffers and vertex array object
     */
    public void delete()
    {
        glDeleteBuffers(typeBuffer);
        glDeleteBuffers(geoBuffer);
        glDeleteVertexArrays(vaoID);
    }

    /**
     * The quad class is used to represent greedy-mesh optimised faces to be rendered. These are used to generate
     * vertex buffers to be drawn to the screen.
     */
    private static class Quad
    {
        public int type;
        public int[][] vertices = new int[4][3];
        public int width, height, orientation;

        /**
         * Quad constructor.
         * @param type Block type specifies texture to be used from texture array
         * @param v0 First vertical offset on axis plane
         * @param v1 Second vertical offset on axis plane
         * @param h0 First horizontal offset on axis plane
         * @param h1 Second horizontal offset on axis plane
         * @param a Plane offset along specified axis
         * @param ax Axis along which face is aligned
         * @param neg 1 if face should be flipped on axis
         */
        Quad(int type, int v0, int v1, int h0, int h1, int a, int ax, int neg)
        {
            this.type = type;
            this.width = h1 - h0;
            this.height = v1 - v0;
            this.orientation = ax * 2 + neg;
            int h = (ax + 1) % 3;
            int v = (ax + 2) % 3;

            // First quad vertex
            vertices[0][ax] = a;
            vertices[0][h]  = h0;
            vertices[0][v]  = v0;

            // Second quad vertex
            vertices[1][ax] = a;
            vertices[1][h]  = h1;
            vertices[1][v]  = v0;

            // Third quad vertex
            vertices[2][ax] = a;
            vertices[2][h]  = h0;
            vertices[2][v]  = v1;

            // Fourth quad vertex
            vertices[3][ax] = a;
            vertices[3][h]  = h1;
            vertices[3][v]  = v1;
        }
    }
}
