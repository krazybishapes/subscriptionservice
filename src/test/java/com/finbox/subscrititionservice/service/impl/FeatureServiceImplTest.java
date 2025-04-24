package com.finbox.subscrititionservice.service.impl;


import com.finbox.subscrititionservice.models.entities.Feature;
import com.finbox.subscrititionservice.models.request.FeatureRequest;
import com.finbox.subscrititionservice.repositories.FeatureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeatureServiceImplTest {

    @Mock  FeatureRepository featureRepository;
    @InjectMocks FeatureServiceImpl service;

    /* ---------- helpers ---------- */

    private static FeatureRequest req(String code,
                                      Long parentId,
                                      String name) {
        FeatureRequest r = mock(FeatureRequest.class);
        when(r.getCode()).thenReturn(code);
        when(r.getParentFeatureId()).thenReturn(parentId);
        when(r.createFeature()).thenAnswer(i -> {
            Feature f = new Feature();
            f.setCode(code);
            f.setParentFeatureId(parentId);
            f.setIsEnabled(false);
            return f;
        });
        return r;
    }

    private static Feature feature(Long id,
                                   String code,
                                   Long parentId,
                                   Boolean enabled) {
        Feature f = new Feature();
        f.setId(id);
        f.setCode(code);
        f.setParentFeatureId(parentId);
        f.setIsEnabled(enabled);
        return f;
    }

    /* ---------- 1. createFeature ---------- */

    @Nested class CreateFeature {

        @Test @DisplayName("1.1 saves new feature without parent")
        void create_withoutParent() {
            FeatureRequest r = req("CODE_A", null, "name");

            when(featureRepository.findByCode("CODE_A"))
                    .thenReturn(Optional.empty());

            Feature persisted = feature(7L, "CODE_A", null, false);
            when(featureRepository.save(any(Feature.class)))
                    .thenReturn(persisted);

            Feature result = service.createFeature(r);

            assertEquals("CODE_A", result.getCode());
            verify(featureRepository)
                    .save(any(Feature.class));
        }

        @Test @DisplayName("1.2 saves with valid parent")
        void create_withParent() {
            Feature parent = feature(3L, "PARENT", null, true);
            FeatureRequest r = req("CODE_B", 3L, "child");

            when(featureRepository.findByCode("CODE_B"))
                    .thenReturn(Optional.empty());
            when(featureRepository.findById(3L))
                    .thenReturn(Optional.of(parent));

            when(featureRepository.save(any(Feature.class)))
                    .thenAnswer(i -> i.getArgument(0));

            Feature child = service.createFeature(r);

            assertEquals(3L, child.getParentFeatureId());
        }

        @Test @DisplayName("1.3 null code → IllegalArgumentException")
        void null_code() {
            FeatureRequest r = req(null, null, "x");
            IllegalArgumentException ex =
                    assertThrows(IllegalArgumentException.class,
                            () -> service.createFeature(r));
            assertTrue(ex.getMessage().contains("must not be null"));
            verifyNoInteractions(featureRepository);
        }

        @Test @DisplayName("1.4 duplicate code → RuntimeException")
        void duplicate_code() {
            FeatureRequest r = req("DUP", null, "x");
            when(featureRepository.findByCode("DUP"))
                    .thenReturn(Optional.of(feature(1L, "DUP", null, false)));

            RuntimeException ex =
                    assertThrows(RuntimeException.class,
                            () -> service.createFeature(r));
            assertTrue(ex.getMessage().contains("already exists"));
        }

        @Test @DisplayName("1.5 missing parent → RuntimeException")
        void missing_parent() {
            FeatureRequest r = req("CHILD", 42L, "x");
            when(featureRepository.findByCode("CHILD"))
                    .thenReturn(Optional.empty());
            when(featureRepository.findById(42L))
                    .thenReturn(Optional.empty());

            RuntimeException ex =
                    assertThrows(RuntimeException.class,
                            () -> service.createFeature(r));
            assertTrue(ex.getMessage().contains("Parent feature not found"));
        }
    }

    /* ---------- 2. isFeatureEnabled ---------- */

    @Nested class IsFeatureEnabled {

        @Test @DisplayName("2.1 enabled feature returns true")
        void enabled() {
            when(featureRepository.findByCode("ON"))
                    .thenReturn(Optional.of(feature(1L,"ON",null,true)));
            assertTrue(service.isFeatureEnabled("ON"));
        }

        @Test @DisplayName("2.2 disabled or null flag returns false")
        void disabled_orNull() {
            when(featureRepository.findByCode("OFF"))
                    .thenReturn(Optional.of(feature(2L,"OFF",null,false)));
            assertFalse(service.isFeatureEnabled("OFF"));

            when(featureRepository.findByCode("NULL"))
                    .thenReturn(Optional.of(feature(3L,"NULL",null,null)));
            assertFalse(service.isFeatureEnabled("NULL"));
        }

        @Test @DisplayName("2.3 absent feature throws")
        void absent() {
            when(featureRepository.findByCode("X"))
                    .thenReturn(Optional.empty());
            assertThrows(RuntimeException.class,
                    () -> service.isFeatureEnabled("X"));
        }
    }

    /* ---------- 3. toggleFeature ---------- */

    @Nested class ToggleFeatureParent {

        @Test @DisplayName("3.1 parent ON propagates to children")
        void parent_on() {
            Feature parent = feature(10L,"PAR",null,false);
            Feature c1 = feature(11L,"C1",10L,false);
            Feature c2 = feature(12L,"C2",10L,false);

            when(featureRepository.findByCode("PAR"))
                    .thenReturn(Optional.of(parent));
            when(featureRepository.findByParentFeatureId(10L))
                    .thenReturn(Optional.of(List.of(c1,c2)));
            when(featureRepository.save(any(Feature.class)))
                    .thenAnswer(i -> i.getArgument(0));

            Feature res = service.toggleFeature("PAR",true);

            assertTrue(res.getIsEnabled());
            // capture child saves
            ArgumentCaptor<Feature> cap = ArgumentCaptor.forClass(Feature.class);
            verify(featureRepository, times(3)).save(cap.capture());
            cap.getAllValues().forEach(f -> assertTrue(f.getIsEnabled()));
        }

        @Test @DisplayName("3.2 parent OFF propagates to children")
        void parent_off() {
            Feature parent = feature(20L,"PA",null,true);
            Feature child = feature(21L,"CH",20L,true);

            when(featureRepository.findByCode("PA"))
                    .thenReturn(Optional.of(parent));
            when(featureRepository.findByParentFeatureId(20L))
                    .thenReturn(Optional.of(List.of(child)));
            when(featureRepository.save(any(Feature.class)))
                    .thenAnswer(i -> i.getArgument(0));

            Feature res = service.toggleFeature("PA",false);
            assertFalse(res.getIsEnabled());
        }

        @Test @DisplayName("3.3 absent parent throws")
        void parent_absent() {
            when(featureRepository.findByCode("NONE"))
                    .thenReturn(Optional.empty());
            assertThrows(RuntimeException.class,
                    () -> service.toggleFeature("NONE",true));
        }
    }

    @Nested class ToggleFeatureChild {

        @Test @DisplayName("3.4 parent ON → toggle child ON")
        void child_on_parent_on() {
            Feature child = feature(30L,"CHILD",40L,false);
            Feature parent = feature(40L,"PAR",null,true);

            when(featureRepository.findByCode("CHILD"))
                    .thenReturn(Optional.of(child));
            when(featureRepository.findById(40L))
                    .thenReturn(Optional.of(parent));
            when(featureRepository.save(any(Feature.class)))
                    .thenAnswer(i -> i.getArgument(0));

            Feature res = service.toggleFeature("CHILD",true);
            assertTrue(res.getIsEnabled());
        }

        @Test @DisplayName("3.5 parent ON → toggle child OFF")
        void child_off_parent_on() {
            Feature child = feature(31L,"CHILD2",41L,true);
            Feature parent = feature(41L,"PAR2",null,true);

            when(featureRepository.findByCode("CHILD2"))
                    .thenReturn(Optional.of(child));
            when(featureRepository.findById(41L))
                    .thenReturn(Optional.of(parent));
            when(featureRepository.save(any(Feature.class)))
                    .thenAnswer(i -> i.getArgument(0));

            Feature res = service.toggleFeature("CHILD2",false);
            assertFalse(res.getIsEnabled());
        }

        @Test @DisplayName("3.6 parent OFF → throws")
        void child_parent_off() {
            Feature child = feature(32L,"CHILD3",42L,false);
            Feature parent = feature(42L,"PAR3",null,false);

            when(featureRepository.findByCode("CHILD3"))
                    .thenReturn(Optional.of(child));
            when(featureRepository.findById(42L))
                    .thenReturn(Optional.of(parent));

            RuntimeException ex =
                    assertThrows(RuntimeException.class,
                            () -> service.toggleFeature("CHILD3",true));
            assertTrue(ex.getMessage().contains("Parent feature is not enabled"));
        }

        @Test @DisplayName("3.7 parent record missing → throws")
        void child_parent_missing() {
            Feature child = feature(33L,"CHILD4",43L,false);
            when(featureRepository.findByCode("CHILD4"))
                    .thenReturn(Optional.of(child));
            when(featureRepository.findById(43L))
                    .thenReturn(Optional.empty());

            assertThrows(RuntimeException.class,
                    () -> service.toggleFeature("CHILD4",true));
        }
    }

    /* ---------- 4. getAllFeatureFlag ---------- */

    @Nested class GetAllFeatureFlag {

        @Test @DisplayName("4.1 returns only enabled")
        void returns_enabled_only() {
            Feature a = feature(50L,"A",null,true);
            Feature b = feature(51L,"B",null,false);
            Feature c = feature(52L,"C",null,true);

            when(featureRepository.findAll())
                    .thenReturn(List.of(a,b,c));

            List<Feature> list = service.getAllFeatureFlag();
            assertEquals(2, list.size());
            assertTrue(list.stream().allMatch(Feature::getIsEnabled));
        }

        @Test @DisplayName("4.2 all disabled → empty list")
        void all_disabled() {
            Feature b = feature(53L,"B",null,false);
            when(featureRepository.findAll())
                    .thenReturn(List.of(b));

            List<Feature> list = service.getAllFeatureFlag();
            assertTrue(list.isEmpty());
        }

        @Test @DisplayName("4.3 repository empty → throws")
        void repo_empty() {
            when(featureRepository.findAll())
                    .thenReturn(List.of());

            assertThrows(RuntimeException.class,
                    () -> service.getAllFeatureFlag());
        }
    }
}

