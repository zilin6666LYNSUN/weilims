package com.wei;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 * @author wei
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = {"org.jeecg", "com.wei"})
public class ERPApplication {
    public static void main(String[] args) {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(ERPApplication.class, args);
        System.out.println(".:*~*:._.:*~*:._.:*~*:._.:*~*:._.:*~*:._.:*~*:._.:*~*:._.:*~*:._.:*~*:.._.:*~*:.._.:*~*:.\n" +
                "            .-\"-.                                                     .----.\"\"\".         \n" +
                "           /   6_6                                                   /____/ (O )\\        \n" +
                "           \\_  (__\\                                                    `--\\_    /        \n" +
                "           //   \\\\                                                        //   \\\\        \n" +
                "          ((     ))                                                      {{     }}       \n" +
                "    =======\"\"===\"\"========================================================\"\"===\"\"======= \n" +
                "             |||                                                            |||          \n" +
                "             |||                                                            '|'          \n" +
                "             '|'                                                                         \n" +
                ".:*~*:._.:*~*:._.:*~*:._.:*~*:._._.:.ERP服务 启动成功.:._.:*~*:.._.:*~*:.._.:*~*:.._.:*~*:.");
    }
}
