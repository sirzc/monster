package com.yunli.monster.generator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * mybatis-plus代码生成器(用于生成entity)<br>
 * 注意:<br>
 * 因为没用mybatis-plus的Service和Controller所以生成完之后要删掉TTT目录
 *
 * @author stylefeng
 * @Date 2017/5/21 12:38
 */
public class Test {

    public static void main(String[] args) {

        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        //这里写你自己的java目录
        gc.setOutputDir("F:\\新建文件夹");
        //是否覆盖
        gc.setFileOverride(true);
        gc.setActiveRecord(true);
        // XML 二级缓存
        gc.setEnableCache(true);
        // XML ResultMap
        gc.setBaseResultMap(true);
        // XML columList
        gc.setBaseColumnList(false);
        gc.setAuthor("sirzc");
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
        dsc.setDriverName("com.mysql.jdbc.Driver");
        dsc.setUsername("test");
        dsc.setPassword("test");
        dsc.setUrl("jdbc:mysql://10.1.5.61:3306/unitop-job?characterEncoding=utf8");
        mpg.setDataSource(dsc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();

        // 指定需要生成的表名
        strategy.setInclude(new String[]{"UNITOP_JOB_QRTZ_OPERATION_LOG"});
        // 此处可以修改为您的表前缀,在生成实体类的时候会自动匹配到这个表并去掉前缀
        strategy.setTablePrefix(new String[] {"UNITOP_JOB_QRTZ_"});
        // 表名生成策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        mpg.setStrategy(strategy);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent(null);
        pc.setEntity("model");
        pc.setMapper("dao");
        pc.setXml("mapping");
        pc.setService("Service");
        pc.setServiceImpl("Impl");
        pc.setController("Controller");
        mpg.setPackageInfo(pc);

        // 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<>();
                map.put("abc", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
                this.setMap(map);
            }
        };

        mpg.setCfg(cfg);

        // 执行生成
        mpg.execute();

        // 打印注入设置
        System.err.println(mpg.getCfg().getMap().get("abc"));
    }

}
