@echo off
chcp 65001 >nul
echo ==============================
echo   社区老年助餐预约系统启动脚本
echo ==============================
echo.

rem 设置环境变量
set MEAL_DATA_PATH=.\src\main\resources\data\
set MEAL_UPLOAD_PATH=.\src\main\resources\static\images\

rem 确保目录存在
if not exist "%MEAL_DATA_PATH%" mkdir "%MEAL_DATA_PATH%"
if not exist "%MEAL_UPLOAD_PATH%\food" mkdir "%MEAL_UPLOAD_PATH%\food"

rem 检查是否有jar包
if not exist ".\target\meal-system-1.0-SNAPSHOT.jar" (
    echo 正在编译项目...
    echo 请等待，首次编译可能需要几分钟...
    echo.
    mvn clean package -DskipTests
    echo.
    if not exist ".\target\meal-system-1.0-SNAPSHOT.jar" (
        echo 编译失败！请检查Maven环境是否配置正确
        pause
        exit /b 1
    )
)

rem 启动应用
echo 正在启动社区老年助餐预约系统...
echo.
echo ⚙️ 服务端口: 8081
echo 🌐 访问地址: http://localhost:8081
echo.
echo 测试账号：
echo   - 系统管理员: admin / 123456
echo   - 食堂管理员: canteen / 123456  
echo   - 老人用户: elder1 / 123456
echo.
echo 按 Ctrl+C 停止服务
echo ==============================
echo.

java -jar .\target\meal-system-1.0-SNAPSHOT.jar

pause