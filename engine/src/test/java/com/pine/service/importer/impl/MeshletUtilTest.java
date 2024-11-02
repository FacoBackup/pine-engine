package com.pine.service.importer.impl;

import com.pine.service.meshlet.MeshletUtil;
import com.pine.service.meshlet.TerrainGenerationUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MeshletUtilTest {
    @Test
    public void generateMeshlets(){
        int size = 200;
        var data = TerrainGenerationUtil.computeMesh(size);

        var result = MeshletUtil.genMeshlets(data);
        Assertions.assertEquals(839, result.meshletInfos.size());
    }
}