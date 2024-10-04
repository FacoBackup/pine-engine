package com.pine.service.loader.impl;

import com.pine.component.Entity;
import com.pine.component.MeshComponent;
import com.pine.component.Transformation;
import com.pine.injection.PBean;
import com.pine.injection.PInject;
import com.pine.repository.WorldRepository;
import com.pine.repository.streaming.MeshStreamableResource;
import com.pine.repository.streaming.StreamableResourceType;
import com.pine.service.loader.AbstractLoaderService;
import com.pine.service.loader.impl.info.AbstractLoaderExtraInfo;
import com.pine.service.loader.impl.info.LoadRequest;
import com.pine.service.loader.impl.info.MeshLoaderExtraInfo;
import com.pine.service.loader.impl.response.AbstractLoaderResponse;
import com.pine.service.loader.impl.response.MeshLoaderResponse;
import com.pine.service.rendering.RequestProcessingService;
import com.pine.service.request.AddComponentRequest;
import com.pine.service.request.AddEntityRequest;
import com.pine.service.request.HierarchyRequest;
import com.pine.service.resource.ResourceService;
import com.pine.service.streaming.mesh.MeshStreamData;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import javax.annotation.Nullable;
import java.util.*;

@PBean
public class MeshLoaderService extends AbstractLoaderService {
    private static final int FLAGS = Assimp.aiProcess_Triangulate | Assimp.aiProcess_FlipUVs | Assimp.aiProcess_GlobalScale | Assimp.aiProcess_GenNormals;

    private static class MeshInstance {
        public final List<MeshInstance> children = new ArrayList<>();
        public final MeshInstance parent;
        public Integer primitive;
        public Matrix4f transformation;
        public String name;

        private MeshInstance(MeshInstance parent, Matrix4f transformation) {
            this.parent = parent;
            this.transformation = transformation;
        }
    }

    @PInject
    public ResourceService resources;

    @PInject
    public RequestProcessingService requestProcessingService;

    @PInject
    public WorldRepository world;

    @Nullable
    private AIScene loadScene(LoadRequest resource) {
        return Assimp.aiImportFile(resource.path(), FLAGS);
    }

    @Override
    public AbstractLoaderResponse<?> load(LoadRequest request, @Nullable AbstractLoaderExtraInfo extraInfo) {
        var extra = (MeshLoaderExtraInfo) extraInfo;
        try {
            Map<Integer, List<MeshInstance>> byPrimitive = new HashMap<>();
            List<MeshStreamableResource> meshes = new ArrayList<>();
            AIScene scene = loadScene(request);

            if (scene == null) {
                getLogger().error("Failed to load mesh at {}", request.path());
                return new MeshLoaderResponse(false, Collections.emptyList());
            }

            if (extra == null || !extra.isInstantiateHierarchy()) {
                PointerBuffer meshList = scene.mMeshes();
                if (meshList != null) {
                    for (int i = 0; i < scene.mNumMeshes(); i++) {
                        AIMesh mesh = AIMesh.create(meshList.get(i));
                        meshes.add(processPrimitive(mesh, i, extra));
                    }
                }
            } else {
                MeshInstance root = new MeshInstance(null, getTransformationMatrix(scene.mRootNode()));
                root.name = scene.mName().dataString();
                traverseNode(scene.mRootNode(), root, byPrimitive);
                Map<Integer, MeshStreamableResource> processed = new HashMap<>();
                traverseTree(root, world.rootEntity, processed, scene, 0);
                meshes.addAll(processed.values());
            }
            Assimp.aiReleaseImport(scene);
            return new MeshLoaderResponse(true, meshes);
        } catch (Exception e) {
            getLogger().error(e.getMessage(), e);
            return new MeshLoaderResponse(false, Collections.emptyList());
        }
    }

    private void traverseTree(MeshInstance root, Entity parent, Map<Integer, MeshStreamableResource> processed, AIScene scene, int depth) {
        MeshStreamableResource primitive = processed.get(root.primitive);
        if (primitive == null && root.primitive != null) {
            primitive = processPrimitive(AIMesh.create(scene.mMeshes().get(root.primitive)), depth, null);
            processed.put(root.primitive, primitive);
        }
        AddEntityRequest entityRequest = new AddEntityRequest(List.of());
        requestProcessingService.addRequest(entityRequest);
        Entity entity = (Entity) entityRequest.getResponse();
        requestProcessingService.addRequest(new HierarchyRequest(parent, entity));
        entity.name = root.name;
        if (primitive != null) {
            requestProcessingService.addRequest(new AddComponentRequest(MeshComponent.class, entity));
            var primitiveComponent = (MeshComponent) entity.components.get(MeshComponent.class.getSimpleName());
            primitiveComponent.lod0 = primitive;
        }

        createPrimitiveTransformation(entity.transformation, root);

        for (var child : root.children) {
            traverseTree(child, entity, processed, scene, depth + 1);
        }
    }

    private void createPrimitiveTransformation(Transformation t, MeshInstance instance) {
        Matrix4f transformation = instance.transformation;
        transformation.getTranslation(t.translation);
        transformation.getUnnormalizedRotation(t.rotation);
        transformation.getScale(t.scale);
    }

    private void traverseNode(AINode node, MeshInstance parent, Map<Integer, List<MeshInstance>> byPrimitive) {
        Matrix4f transformationMatrix = getTransformationMatrix(node);
        MeshInstance newInstance = new MeshInstance(parent, transformationMatrix);
        newInstance.name = node.mName().dataString();

        parent.children.add(newInstance);
        if (node.mNumMeshes() > 0) {
            for (int i = 0; i < node.mNumMeshes(); i++) {
                newInstance.primitive = node.mMeshes().get(i);
                byPrimitive.putIfAbsent(newInstance.primitive, new ArrayList<>());
                byPrimitive.get(newInstance.primitive).add(newInstance);
            }
        }

        for (int i = 0; i < node.mNumChildren(); i++) {
            AINode child = AINode.create(node.mChildren().get(i));
            traverseNode(child, newInstance, byPrimitive);
        }
    }

    private Matrix4f getTransformationMatrix(AINode node) {
        AIMatrix4x4 aiMatrix = node.mTransformation();
        Matrix4f jomlMatrix = new Matrix4f();

        jomlMatrix.m00(aiMatrix.a1());
        jomlMatrix.m01(aiMatrix.a2());
        jomlMatrix.m02(aiMatrix.a3());
        jomlMatrix.m03(aiMatrix.a4());

        jomlMatrix.m10(aiMatrix.b1());
        jomlMatrix.m11(aiMatrix.b2());
        jomlMatrix.m12(aiMatrix.b3());
        jomlMatrix.m13(aiMatrix.b4());

        jomlMatrix.m20(aiMatrix.c1());
        jomlMatrix.m21(aiMatrix.c2());
        jomlMatrix.m22(aiMatrix.c3());
        jomlMatrix.m23(aiMatrix.c4());

        jomlMatrix.m30(aiMatrix.d1());
        jomlMatrix.m31(aiMatrix.d2());
        jomlMatrix.m32(aiMatrix.d3());
        jomlMatrix.m33(aiMatrix.d4());

        jomlMatrix.transpose();
        return jomlMatrix;
    }

    private MeshStreamableResource processPrimitive(AIMesh mesh, int index, @Nullable MeshLoaderExtraInfo extra) {
        if (extra == null || extra.getMeshIndex() == null || extra.getMeshIndex() == index) {
            float[] vertices = new float[mesh.mNumVertices() * 3];
            for (int i = 0; i < mesh.mNumVertices(); i++) {
                AIVector3D vert = mesh.mVertices().get(i);
                vertices[i * 3] = vert.x();
                vertices[i * 3 + 1] = vert.y();
                vertices[i * 3 + 2] = vert.z();
            }

            int[] indices = new int[mesh.mNumFaces() * 3];
            for (int i = 0; i < mesh.mNumFaces(); i++) {
                AIFace face = mesh.mFaces().get(i);
                indices[i * 3] = face.mIndices().get(0);
                indices[i * 3 + 1] = face.mIndices().get(1);
                indices[i * 3 + 2] = face.mIndices().get(2);
            }

            float[] normals = null;
            AIVector3D.Buffer mNormals = mesh.mNormals();
            if (mNormals != null) {
                normals = new float[mesh.mNumVertices() * 3];
                for (int i = 0; i < mesh.mNumVertices(); i++) {
                    AIVector3D normal = mNormals.get(i);
                    normals[i * 3] = normal.x();
                    normals[i * 3 + 1] = normal.y();
                    normals[i * 3 + 2] = normal.z();
                }
            }

            float[] uvs = null;
            AIVector3D.Buffer nUV = mesh.mTextureCoords(0);
            if (nUV != null) {
                uvs = new float[mesh.mNumVertices() * 2];
                for (int i = 0; i < mesh.mNumVertices(); i++) {
                    uvs[i * 2] = nUV.get(i).x();
                    uvs[i * 2 + 1] = nUV.get(i).y();
                }
            }

            MeshStreamableResource instance = streamingService.addNew(MeshStreamableResource.class, mesh.mName().dataString());
            MeshStreamData data = new MeshStreamData(vertices, indices, normals, uvs);
            persist(instance, data);
            return instance;
        }
        return null;
    }

    @Override
    public StreamableResourceType getResourceType() {
        return StreamableResourceType.MESH;
    }
}
