#!/bin/bash

# 设置环境变量
export MEAL_DATA_PATH=./src/main/resources/data/
export MEAL_UPLOAD_PATH=./src/main/resources/static/images/

# 确保目录存在
mkdir -p $MEAL_DATA_PATH
mkdir -p $MEAL_UPLOAD_PATH/food

# 检查是否有jar包
if [ ! -f "./target/meal-0.0.1-SNAPSHOT.jar" ]; then
    echo "正在编译项目..."
    mvn clean package -DskipTests
fi

# 启动应用
echo "正在启动社区老年助餐预约系统..."
java -jar ./target/meal-0.0.1-SNAPSHOT.jar --spring.config.location=./src/main/resources/application.properties