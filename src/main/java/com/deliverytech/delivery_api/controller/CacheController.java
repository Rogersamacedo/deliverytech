package com.deliverytech.delivery_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/cache")
@Tag(name = "Cache", description = "Gerenciamento manual do cache da aplicação")
public class CacheController {

    private final CacheManager cacheManager;

    public CacheController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @GetMapping
    @Operation(summary = "Lista os caches existentes")
    public ResponseEntity<Map<String, Object>> listar() {
        return ResponseEntity.ok(Map.of("caches", cacheManager.getCacheNames()));
    }

    @DeleteMapping
    @Operation(summary = "Limpa todos os caches")
    public ResponseEntity<Map<String, String>> limparTodos() {
        cacheManager.getCacheNames().forEach(nome -> {
            Cache cache = cacheManager.getCache(nome);
            if (cache != null) {
                cache.clear();
            }
        });
        return ResponseEntity.ok(Map.of("mensagem", "Todos os caches foram limpos."));
    }

    @DeleteMapping("/{nome}")
    @Operation(summary = "Limpa um cache específico pelo nome")
    public ResponseEntity<Map<String, String>> limparPorNome(@PathVariable String nome) {
        Cache cache = cacheManager.getCache(nome);
        if (cache == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("mensagem", "Cache não encontrado: " + nome));
        }
        cache.clear();
        return ResponseEntity.ok(Map.of("mensagem", "Cache '" + nome + "' limpo."));
    }
}
