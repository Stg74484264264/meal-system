package com.meal.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JSON工具类，用于处理JSON文件的读写操作
 * 封装了JSON文件的读取、写入、新增、修改、删除等通用方法
 */
public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 读取JSON文件并转换为对象列表
     * 优先尝试使用类加载器读取JAR内的资源，如果失败则回退到文件系统读取
     *
     * @param filePath 文件路径
     * @param clazz    对象类型
     * @param <T>      泛型
     * @return 对象列表
     */
    public static <T> List<T> readJsonFile(String filePath, Class<T> clazz) {
        String resourcePath = filePath;
        if (resourcePath.startsWith("classpath:")) {
            resourcePath = resourcePath.substring("classpath:".length());
        } else if (resourcePath.startsWith("src/main/resources/")) {
            resourcePath = resourcePath.substring("src/main/resources/".length());
        }
        
        InputStream is = JsonUtil.class.getClassLoader().getResourceAsStream(resourcePath);
        if (is != null) {
            try {
                return objectMapper.readValue(is, new TypeReference<List<T>>() {});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        try {
            File file = getFile(filePath);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 写入对象列表到JSON文件
     *
     * @param filePath 文件路径
     * @param list     对象列表
     * @param <T>      泛型
     */
    public static <T> void writeJsonFile(String filePath, List<T> list) {
        try {
            File file = getFile(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据ID查询对象
     *
     * @param filePath 文件路径
     * @param id       对象ID
     * @param clazz    对象类型
     * @param <T>      泛型
     * @return 对象
     */
    public static <T> T getById(String filePath, String id, Class<T> clazz) {
        List<T> list = readJsonFile(filePath, clazz);
        return list.stream().filter(item -> {
            try {
                return objectMapper.writeValueAsString(item).contains("\"id\":\"" + id + "\"");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return false;
            }
        }).findFirst().orElse(null);
    }

    /**
     * 新增对象
     *
     * @param filePath 文件路径
     * @param obj      对象
     * @param <T>      泛型
     */
public static <T> void add(String filePath, T obj) {
        List<T> list = readJsonFile(filePath, (Class<T>) obj.getClass());
        list.add(obj);
        writeJsonFile(filePath, list);
    }

    /**
     * 更新对象
     *
     * @param filePath 文件路径
     * @param id       对象ID
     * @param obj      对象
     * @param <T>      泛型
     */
    public static <T> void update(String filePath, String id, T obj) {
        List<T> list = readJsonFile(filePath, (Class<T>) obj.getClass());
        for (int i = 0; i < list.size(); i++) {
            try {
                if (objectMapper.writeValueAsString(list.get(i)).contains("\"id\":\"" + id + "\"")) {
                    list.set(i, obj);
                    break;
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        writeJsonFile(filePath, list);
    }

    /**
     * 删除对象
     *
     * @param filePath 文件路径
     * @param id       对象ID
     * @param clazz    对象类型
     * @param <T>      泛型
     */
    public static <T> void delete(String filePath, String id, Class<T> clazz) {
        List<T> list = readJsonFile(filePath, clazz);
        list.removeIf(item -> {
            try {
                return objectMapper.writeValueAsString(item).contains("\"id\":\"" + id + "\"");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return false;
            }
        });
        writeJsonFile(filePath, list);
    }

    /**
     * 生成唯一ID
     *
     * @return 唯一ID
     */
    public static String generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取当前时间字符串
     *
     * @return 当前时间字符串
     */
    public static String getCurrentTime() {
        return LocalDateTime.now().format(formatter);
    }

    /**
     * 获取文件对象
     *
     * @param filePath 文件路径
     * @return 文件对象
     */
    private static File getFile(String filePath) throws IOException {
        if (filePath.startsWith("classpath:")) {
            return ResourceUtils.getFile(filePath);
        } else {
            return new File(filePath);
        }
    }

    /**
     * 读取JSON文件，转为对应实体类列表（符合要求的方法名）
     *
     * @param filePath 文件路径
     * @param clazz    实体类类型
     * @param <T>      泛型
     * @return 实体类列表
     */
    public static <T> List<T> readJson(String filePath, Class<T> clazz) {
        return readJsonFile(filePath, clazz);
    }

    /**
     * 将实体类列表写入指定JSON文件，覆盖原有内容（符合要求的方法名）
     *
     * @param filePath 文件路径
     * @param list     实体类列表
     * @param <T>      泛型
     */
    public static <T> void writeJson(String filePath, List<T> list) {
        writeJsonFile(filePath, list);
    }

    /**
     * 新增数据到JSON文件，自动生成唯一id
     *
     * @param filePath 文件路径
     * @param data     要新增的数据
     * @param clazz    数据类型
     * @param <T>      泛型
     * @return 是否新增成功
     */
    public static <T> boolean addData(String filePath, T data, Class<T> clazz) {
        try {
            // 读取现有数据
            List<T> list = readJson(filePath, clazz);
            
            // 为新数据设置唯一ID
            setIdField(data);
            
            // 添加新数据到列表
            list.add(data);
            
            // 写入文件
            writeJson(filePath, list);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据id修改JSON文件中的对应数据
     *
     * @param filePath 文件路径
     * @param data     要修改的数据（包含id）
     * @param clazz    数据类型
     * @param <T>      泛型
     * @return 是否修改成功
     */
    public static <T> boolean updateData(String filePath, T data, Class<T> clazz) {
        try {
            // 读取现有数据
            List<T> list = readJson(filePath, clazz);
            
            // 获取要修改数据的id
            String id = getIdField(data);
            if (id == null) {
                return false;
            }
            
            // 查找并替换数据
            boolean found = false;
            for (int i = 0; i < list.size(); i++) {
                T item = list.get(i);
                if (getIdField(item).equals(id)) {
                    list.set(i, data);
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                return false;
            }
            
            // 写入文件
            writeJson(filePath, list);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据id删除JSON文件中的对应数据
     *
     * @param filePath 文件路径
     * @param id       要删除的数据id
     * @param clazz    数据类型
     * @param <T>      泛型
     * @return 是否删除成功
     */
    public static <T> boolean deleteData(String filePath, Integer id, Class<T> clazz) {
        try {
            // 读取现有数据
            List<T> list = readJson(filePath, clazz);
            
            // 删除指定id的数据
            boolean removed = list.removeIf(item -> {
                try {
                    String itemId = getIdField(item);
                    return itemId != null && itemId.equals(id.toString());
                } catch (Exception e) {
                    return false;
                }
            });
            
            if (!removed) {
                return false;
            }
            
            // 写入文件
            writeJson(filePath, list);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置对象的id字段
     *
     * @param obj 要设置id的对象
     * @param <T> 泛型
     * @throws Exception 反射异常
     */
    private static <T> void setIdField(T obj) throws Exception {
        Field idField = obj.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        
        // 检查id字段类型并设置值
        if (idField.getType() == String.class) {
            idField.set(obj, generateId());
        } else if (idField.getType() == Integer.class || idField.getType() == int.class) {
            // 生成一个简单的数字ID
            idField.set(obj, generateNumericId());
        }
    }

    /**
     * 获取对象的id字段值
     *
     * @param obj 要获取id的对象
     * @param <T> 泛型
     * @return id字段值
     * @throws Exception 反射异常
     */
    private static <T> String getIdField(T obj) throws Exception {
        Field idField = obj.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        
        Object idValue = idField.get(obj);
        return idValue != null ? idValue.toString() : null;
    }

    /**
     * 生成数字类型的ID
     *
     * @return 数字ID
     */
    private static int generateNumericId() {
        return (int) (System.currentTimeMillis() % 10000000);
    }
}