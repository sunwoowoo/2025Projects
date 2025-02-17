package org.zerock.projects.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.zerock.projects.domain.Material;
import org.zerock.projects.dto.MaterialDTO;
import org.zerock.projects.repository.MaterialRepository;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class MaterialServiceTest {

    @Mock
    private MaterialRepository materialRepository;

    @InjectMocks
    private MaterialService materialService;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pageable = PageRequest.of(0, 10);
    }


    @Test
    void testGetMaterialsByMid() {
        Material material = new Material("M001", "Material 1", "Category 1", 100, 10.5, "A1", "InStock");
        Page<Material> materialPage = new PageImpl<>(Arrays.asList(material));
        when(materialRepository.findByMidContaining("M001", pageable)).thenReturn(materialPage);

        Page<MaterialDTO> materialDTOs = materialService.getMaterialsByMid("M001", pageable);

        assertThat(materialDTOs.getContent()).hasSize(1);
        assertThat(materialDTOs.getContent().get(0).getMid()).isEqualTo("M001");
    }

    @Test
    void testGetMaterialsByMname() {
        Material material = new Material("M001", "Material 1", "Category 1", 100, 10.5, "A1", "InStock");
        Page<Material> materialPage = new PageImpl<>(Arrays.asList(material));
        when(materialRepository.findByMnameContaining("Material 1", pageable)).thenReturn(materialPage);

        Page<MaterialDTO> materialDTOs = materialService.getMaterialsByMname("Material 1", pageable);

        assertThat(materialDTOs.getContent()).hasSize(1);
        assertThat(materialDTOs.getContent().get(0).getMname()).isEqualTo("Material 1");
    }

    @Test
    void testGetMaterialsByMcategory() {
        Material material = new Material("M001", "Material 1", "Category 1", 100, 10.5, "A1", "InStock");
        Page<Material> materialPage = new PageImpl<>(Arrays.asList(material));
        when(materialRepository.findByMcategoryContaining("Category 1", pageable)).thenReturn(materialPage);

        Page<MaterialDTO> materialDTOs = materialService.getMaterialsByMcategory("Category 1", pageable);

        assertThat(materialDTOs.getContent()).hasSize(1);
        assertThat(materialDTOs.getContent().get(0).getMcategory()).isEqualTo("Category 1");
    }


}
