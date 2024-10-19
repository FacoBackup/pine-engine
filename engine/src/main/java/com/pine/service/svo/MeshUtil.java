package com.pine.service.svo;

import com.pine.service.streaming.mesh.MeshStreamData;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MeshUtil {
    public static BoundingBox computeBoundingBox(MeshStreamData mesh, Matrix4f transform) {
        var bounding = new BoundingBox();
        for (int i = 0; i < mesh.vertices().length; i += 3) {
            float x = mesh.vertices()[i];
            float y = mesh.vertices()[i + 1];
            float z = mesh.vertices()[i + 2];
            Vector4f vector = new Vector4f(x, y, z, 1).mul(transform);
            Vector3f vector3 = new Vector3f(vector.x, vector.y, vector.z);
            if (vector3.length() > bounding.max.length()) {
                bounding.max = vector3;
            }else if(vector3.length() < bounding.min.length()){
                bounding.min = vector3;
            }
        }
        return bounding;
    }
}
