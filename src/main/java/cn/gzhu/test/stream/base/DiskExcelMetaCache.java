package cn.gzhu.test.stream.base;

import cn.gzhu.test.stream.ExcelMetaCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import static cn.gzhu.test.constant.CommonConstants.ONE_INT;
import static cn.gzhu.test.constant.CommonConstants.ZERO_INT;

/**
 * 应用模块名称<p>
 * 代码描述<p>
 * Copyright: Copyright (C) 2019 Bytedance，Inc. All rights reserved. <p>
 * Company: 北京字节跳动科技有限公司 Enterprise Application <p>
 *
 * @author zhaoxuedui
 * @since 2022/4/6 10:54
 */
@SuppressWarnings("unchecked")
@Slf4j
public class DiskExcelMetaCache implements ExcelMetaCache {

    private static final int DEFAULT_MAX_ACTIVE_SIZE = 5;

    private static final int BATCH_COUNT = 1000;

    private static final int DEBUG_WRITE_SIZE = 100 * 10000;

    private static final int DEBUG_CACHE_MISS_SIZE = 1000;

    private static final long DEFAULT_MAX_OBJECT_GRAPH = 1000 * 1000L;

    private int index = ZERO_INT;

    private HashMap<Integer, String> dataMap = new HashMap<>(BATCH_COUNT * 4 / 3 + 1);

    private static final CacheManager FILE_CACHE_MANAGER;

    private static final CacheConfiguration<Integer, HashMap> FILE_CACHE_CONFIGURATION;

    private static final CacheManager ACTIVE_CACHE_MANAGER;

    private final CacheConfiguration<Integer, HashMap> activeCacheConfiguration;

    private org.ehcache.Cache<Integer, HashMap> fileCache;

    private org.ehcache.Cache<Integer, HashMap> activeCache;

    private String cacheAlias;

    private int cacheMiss = ZERO_INT;

    public DiskExcelMetaCache() {
        activeCacheConfiguration = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(Integer.class, HashMap.class,
                        ResourcePoolsBuilder.newResourcePoolsBuilder().heap(DEFAULT_MAX_ACTIVE_SIZE, MemoryUnit.MB))
                .withSizeOfMaxObjectGraph(DEFAULT_MAX_OBJECT_GRAPH)
                .withSizeOfMaxObjectSize(DEFAULT_MAX_ACTIVE_SIZE, MemoryUnit.MB)
                .build();
    }

    static {
        File cacheFile = FileUtils.createCacheTmpFile();
        FILE_CACHE_MANAGER =
                CacheManagerBuilder.newCacheManagerBuilder().with(CacheManagerBuilder.persistence(cacheFile))
                        .build(true);
        ACTIVE_CACHE_MANAGER = CacheManagerBuilder.newCacheManagerBuilder().build(true);
        FILE_CACHE_CONFIGURATION = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(Integer.class, HashMap.class,
                        ResourcePoolsBuilder.newResourcePoolsBuilder().disk(10, MemoryUnit.GB))
                .withSizeOfMaxObjectGraph(DEFAULT_MAX_OBJECT_GRAPH).withSizeOfMaxObjectSize(10, MemoryUnit.GB).build();
    }

    @Override
    public void init() {
        cacheAlias = UUID.randomUUID().toString();
        fileCache = FILE_CACHE_MANAGER.createCache(cacheAlias, FILE_CACHE_CONFIGURATION);
        activeCache = ACTIVE_CACHE_MANAGER.createCache(cacheAlias, activeCacheConfiguration);
    }

    @Override
    public void add(String value) {
        if (fileCache == null) {
            init();
        }
        dataMap.put(index, value);
        if ((index + ONE_INT) % BATCH_COUNT == ZERO_INT) {
            fileCache.put(index / BATCH_COUNT, dataMap);
            dataMap = new HashMap<>(BATCH_COUNT * 4 / 3 + ONE_INT);
        }
        index++;
        if (log.isDebugEnabled()) {
            if (index % DEBUG_WRITE_SIZE == ZERO_INT) {
                log.debug("Already put :{}", index);
            }
        }
    }

    @Override
    public String get(Integer key) {
        if (key == null || key < ZERO_INT) {
            return null;
        }
        int route = key / BATCH_COUNT;
        HashMap<Integer, String> map = activeCache.get(route);
        if (map == null) {
            map = fileCache.get(route);
            activeCache.put(route, map);
            if (log.isDebugEnabled()) {
                if (cacheMiss++ % DEBUG_CACHE_MISS_SIZE == ZERO_INT) {
                    log.info("Cache misses count:{}", cacheMiss);
                }
            }
        }
        return map.get(key);
    }

    @Override
    public void putFinished() {
        if (MapUtils.isEmpty(dataMap)) {
            return;
        }
        fileCache.put(index / BATCH_COUNT, dataMap);
    }

    @Override
    public void clear() {
        FILE_CACHE_MANAGER.removeCache(cacheAlias);
        ACTIVE_CACHE_MANAGER.removeCache(cacheAlias);
    }
}
