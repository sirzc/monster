package com.yunli.mq.common;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.SystemPropertyUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 扫描包工具类
 *
 * @author zhouchao
 * @date 2019-01-18 14:49
 */
public class ScanPackageUtil {

    private static final Logger logger = LoggerFactory.getLogger(ScanPackageUtil.class);

    /**
     * 扫描scanPackages 下的文件的匹配符
     */
    protected static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    /**
     * 获取所有的类
     *
     * @param scanPackages 扫描包路径
     * @param annotation   注解类
     * @return
     */
    public static Set<String> findAnnotationClass(String scanPackages, Class<? extends Annotation> annotation) {
        // 找到指定包下所有的类
        Set<String> fullClazzSet = findPackageClass(scanPackages);
        // 存放包含注解的类的完整路径名称
        Set<String> clazzSet = Sets.newHashSet();
        for (String clazz : fullClazzSet) {
            try {
                if (findAnnotationClasses(clazz, annotation)) {
                    clazzSet.add(clazz);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return clazzSet;
    }

    /**
     * 结合spring的类扫描方式
     * 根据需要扫描的包路径及相应的注解，获取最终测method集合
     * 仅返回public方法，如果方法是非public类型的，不会被返回
     * 可以扫描工程下的class文件及jar中的class文件
     *
     * @param scanPackages
     * @param annotation
     * @return
     */
    public static Set<Method> findClassAnnotationMethods(String scanPackages, Class<? extends Annotation> annotation) {
        //获取所有的类
        Set<String> clazzSet = findPackageClass(scanPackages);
        Set<Method> methods = new HashSet<Method>();
        //遍历类，查询相应的annotation方法
        for (String clazz : clazzSet) {
            try {
                Set<Method> ms = findAnnotationMethods(clazz, annotation);
                if (ms != null) {
                    methods.addAll(ms);
                }
            } catch (ClassNotFoundException ignore) {
            }
        }
        return methods;
    }

    /**
     * 根据扫描包的,查询下面的所有类
     *
     * @param scanPackages 扫描的package路径
     * @return
     */
    public static Set<String> findPackageClass(String scanPackages) {
        // 检查包路径
        if (StringUtils.isEmpty(scanPackages)) {
            return Collections.EMPTY_SET;
        }
        // 验证及排重包路径,避免父子路径多次扫描
        Set<String> packages = checkPackage(scanPackages);
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
        Set<String> clazzSet = new HashSet<String>();
        for (String basePackage : packages) {
            if (StringUtils.isEmpty(basePackage)) {
                continue;
            }
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils
                    .convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage)) + "/"
                    + DEFAULT_RESOURCE_PATTERN;
            try {
                Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
                for (Resource resource : resources) {
                    // 检查resource，这里的resource都是class
                    String clazz = loadClassName(metadataReaderFactory, resource);
                    clazzSet.add(clazz);
                }
            } catch (Exception e) {
                logger.error("获取包下面的类信息失败,package:" + basePackage, e);
            }

        }
        return clazzSet;
    }

    /**
     * 排重、检测package父子关系，避免多次扫描
     *
     * @param scanPackages
     * @return 返回检查后有效的路径集合
     */
    private static Set<String> checkPackage(String scanPackages) {
        if (StringUtils.isEmpty(scanPackages)) {
            return Collections.EMPTY_SET;
        }
        Set<String> packages = new HashSet<String>();
        // 排重路径
        Collections.addAll(packages, scanPackages.split(","));
        for (String pInArr : packages.toArray(new String[packages.size()])) {
            if (StringUtils.isEmpty(pInArr) || pInArr.equals(".") || pInArr.startsWith(".")) {
                continue;
            }
            if (pInArr.endsWith(".")) {
                pInArr = pInArr.substring(0, pInArr.length() - 1);
            }
            Iterator<String> packageIte = packages.iterator();
            boolean needAdd = true;
            while (packageIte.hasNext()) {
                String pack = packageIte.next();
                if (pInArr.startsWith(pack + ".")) {
                    // 如果待加入的路径是已经加入的pack的子集，不加入
                    needAdd = false;
                } else if (pack.startsWith(pInArr + ".")) {
                    // 如果待加入的路径是已经加入的pack的父集，删除已加入的pack
                    packageIte.remove();
                }
            }
            if (needAdd) {
                packages.add(pInArr);
            }
        }
        return packages;
    }

    /**
     * 加载资源，根据resource获取className
     *
     * @param metadataReaderFactory spring中用来读取resource为class的工具
     * @param resource              这里的资源就是一个Class
     */
    private static String loadClassName(MetadataReaderFactory metadataReaderFactory, Resource resource) {
        try {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                if (metadataReader != null) {
                    return metadataReader.getClassMetadata().getClassName();
                }
            }
        } catch (Exception e) {
            logger.error("根据resource获取类名称失败", e);
        }
        return null;
    }

    /**
     * 把action下面的所有method遍历一次，标记他们是否需要进行敏感词验证
     * 如果需要，放入cache中
     *
     * @param fullClassName 完整的类名称
     * @param annotationClass 注解名称
     * @return
     * @throws ClassNotFoundException
     */
    public static Set<Method> findAnnotationMethods(String fullClassName, Class<? extends Annotation> annotationClass)
            throws ClassNotFoundException {
        Set<Method> methodSet = new HashSet<Method>();
        Class<?> clz = Class.forName(fullClassName);
        Method[] methods = clz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getModifiers() != Modifier.PUBLIC) {
                continue;
            }
            Annotation annotation = method.getAnnotation(annotationClass);
            if (annotation != null) {
                methodSet.add(method);
            }
        }
        return methodSet;
    }

    /**
     * 检查是否包含该注解
     *
     * @param fullClassName   完整的类名称
     * @param annotationClass 注解名称
     * @return
     * @throws ClassNotFoundException
     */
    public static boolean findAnnotationClasses(String fullClassName, Class<? extends Annotation> annotationClass)
            throws ClassNotFoundException {
        Class<?> clz = Class.forName(fullClassName);
        Annotation annotation = clz.getAnnotation(annotationClass);
        if (annotation != null) {
            return true;
        }
        return false;
    }
}
