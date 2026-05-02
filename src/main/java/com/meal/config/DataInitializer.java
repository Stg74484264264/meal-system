package com.meal.config;

import com.meal.entity.Food;
import com.meal.entity.Notice;
import com.meal.entity.User;
import com.meal.repository.FoodRepository;
import com.meal.repository.NoticeRepository;
import com.meal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final NoticeRepository noticeRepository;

    public DataInitializer(UserRepository userRepository, FoodRepository foodRepository, NoticeRepository noticeRepository) {
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
        this.noticeRepository = noticeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 初始化用户数据
        if (userRepository.count() == 0) {
            createUser("admin", "123456", "系统管理员", "13800138000", "系统管理中心", "admin");
            createUser("canteen", "123456", "食堂管理员", "13800138001", "食堂管理中心", "canteen");
            createUser("zhanglao", "123456", "张三爷", "13800138002", "幸福小区1号楼", "elder");
            createUser("elder2", "123456", "王大娘", "13800138003", "幸福小区2号楼", "elder");
            createUser("wangjingshu", "123456", "王静姝", "15225554404", "和谐社区3号楼", "elder");
        }

        // 初始化菜品数据
        if (foodRepository.count() == 0) {
            createFood("红烧肉", "精选五花肉，肥而不腻", 18.00, "肉类", "lunch", "hongshaorou.jpg", 50);
            createFood("清蒸鱼", "新鲜鲈鱼，清蒸入味", 22.00, "海鲜", "lunch", "qingzheng.jpg", 30);
            createFood("炒时蔬", "当季新鲜蔬菜", 10.00, "蔬菜", "lunch", "chaoshi.jpg", 100);
            createFood("小米粥", "营养小米粥", 5.00, "主食", "breakfast", "xiaomi.jpg", 200);
            createFood("包子", "猪肉大葱馅包子", 3.00, "主食", "breakfast", "baozi.jpg", 150);
            createFood("油条", "香脆油条", 2.00, "主食", "breakfast", "youtiao.jpg", 100);
            createFood("蛋炒饭", "鸡蛋火腿炒饭", 12.00, "主食", "dinner", "danchaofan.jpg", 80);
            createFood("西红柿鸡蛋", "经典家常菜", 12.00, "蔬菜", "lunch", "xihongshi.jpg", 60);
            createFood("排骨汤", "滋补排骨汤", 20.00, "汤类", "dinner", "paigu.jpg", 40);
            createFood("豆腐脑", "鲜香豆腐脑", 6.00, "主食", "breakfast", "doufu.jpg", 100);
        }

        // 初始化公告数据
        if (noticeRepository.count() == 0) {
            createNotice("系统升级通知", "尊敬的用户，系统将于今晚22:00-24:00进行升级维护，期间将暂停服务，请您提前做好安排。");
            createNotice("端午节放假通知", "根据国家法定节假日安排，端午节期间（6月22日-24日）食堂暂停营业，6月25日恢复正常服务。");
            createNotice("新菜品上线", "本周新增菜品：红烧肉、清蒸鱼、排骨汤，欢迎品尝！");
        }
    }

    private void createUser(String username, String password, String name, String phone, String address, String role) {
        User user = new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setPhone(phone);
        user.setAddress(address);
        user.setRole(role);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
    }

    private void createFood(String name, String description, double price, String category, String mealType, String image, int stock) {
        Food food = new Food();
        food.setId(UUID.randomUUID().toString());
        food.setName(name);
        food.setDescription(description);
        food.setPrice(price);
        food.setCategory(category);
        food.setMealType(mealType);
        food.setImage(image);
        food.setStock(stock);
        food.setStatus("available");
        food.setCreateTime(LocalDateTime.now());
        food.setUpdateTime(LocalDateTime.now());
        foodRepository.save(food);
    }

    private void createNotice(String title, String content) {
        Notice notice = new Notice();
        notice.setId(UUID.randomUUID().toString());
        notice.setTitle(title);
        notice.setContent(content);
        notice.setCreateTime(LocalDateTime.now());
        notice.setUpdateTime(LocalDateTime.now());
        noticeRepository.save(notice);
    }
}
