package com.smartauto.oa.system.controller;

import com.smartauto.oa.common.Constants;
import com.smartauto.oa.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码控制器
 */
@Tag(name = "验证码管理")
@RestController
@RequestMapping("/auth")
public class SysCaptchaController {

    private final RedisTemplate<String, Object> redisTemplate;

    /** 验证码字符集（排除容易混淆的字符） */
    private static final String CAPTCHA_CHARS = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";

    /** 验证码图片宽度 */
    private static final int WIDTH = 120;

    /** 验证码图片高度 */
    private static final int HEIGHT = 40;

    /** 验证码字符数 */
    private static final int CODE_LENGTH = 4;

    /** 干扰线数量 */
    private static final int LINE_COUNT = 6;

    public SysCaptchaController(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Operation(summary = "获取图片验证码")
    @GetMapping("/captcha")
    public Result<Map<String, String>> captcha() throws IOException {
        // 生成随机验证码
        String captchaCode = generateCaptchaCode();

        // 生成唯一key
        String captchaKey = java.util.UUID.randomUUID().toString().replace("-", "");

        // 生成验证码图片
        String captchaImage = generateCaptchaImage(captchaCode);

        // 存入Redis，5分钟过期
        String redisKey = Constants.REDIS_CAPTCHA_PREFIX + captchaKey;
        redisTemplate.opsForValue().set(redisKey, captchaCode.toLowerCase(), Constants.CAPTCHA_EXPIRE_SECONDS, TimeUnit.SECONDS);

        Map<String, String> result = new HashMap<>();
        result.put("captchaKey", captchaKey);
        result.put("captchaImage", "data:image/png;base64," + captchaImage);
        return Result.success(result);
    }

    /**
     * 生成随机验证码字符串
     */
    private String generateCaptchaCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CAPTCHA_CHARS.charAt(random.nextInt(CAPTCHA_CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * 生成验证码图片（Base64编码）
     */
    private String generateCaptchaImage(String code) throws IOException {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        Random random = new Random();

        // 设置背景色
        g.setColor(getRandomLightColor(random));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 设置字体
        Font font = new Font("Arial", Font.BOLD, 28);
        g.setFont(font);

        // 绘制干扰线
        for (int i = 0; i < LINE_COUNT; i++) {
            g.setColor(getRandomColor(random));
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // 绘制噪点
        for (int i = 0; i < 30; i++) {
            g.setColor(getRandomColor(random));
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            g.drawOval(x, y, 2, 2);
        }

        // 绘制验证码字符
        for (int i = 0; i < code.length(); i++) {
            g.setColor(getRandomDarkColor(random));
            // 随机旋转角度
            double theta = Math.toRadians(random.nextInt(30) - 15);
            int x = 22 + i * 24;
            int y = 28 + random.nextInt(6) - 3;
            g.rotate(theta, x, y);
            g.drawString(String.valueOf(code.charAt(i)), x, y);
            g.rotate(-theta, x, y);
        }

        g.dispose();

        // 转为Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /**
     * 获取随机浅色（背景用）
     */
    private Color getRandomLightColor(Random random) {
        int r = 200 + random.nextInt(56);
        int g = 200 + random.nextInt(56);
        int b = 200 + random.nextInt(56);
        return new Color(r, g, b);
    }

    /**
     * 获取随机颜色（干扰线用）
     */
    private Color getRandomColor(Random random) {
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    /**
     * 获取随机深色（文字用）
     */
    private Color getRandomDarkColor(Random random) {
        int r = random.nextInt(100);
        int g = random.nextInt(100);
        int b = random.nextInt(100);
        return new Color(r, g, b);
    }
}
