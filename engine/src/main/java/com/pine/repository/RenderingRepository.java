package com.pine.repository;

import com.pine.PBean;
import com.pine.repository.rendering.RuntimeDrawDTO;

import java.util.ArrayList;
import java.util.List;

@PBean
public class RenderingRepository {
    public List<RuntimeDrawDTO> requests = new ArrayList<>();
}
