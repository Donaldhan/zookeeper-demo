package org.donald.constant;

/**
 * @ClassName: ConfigConstant
 * @Description:
 * @Author: Donaldhan
 * @Date: 2018-05-09 15:54
 */
public class ConfigConstant {
    public static final String IP = "127.0.0.1:2181";
    public static final int SESSION_TIMEOUT = 5000;
    public static final int CONNETING_TIMEOUT = 3000;
    public static final String CHAR_SET_NAME = "UTF-8";
    public static final int BASE_SLEEP_TIMES = 1000;
    public static final int MAX_RETRIES = 3;
    public static final String MASTER_SELECTOR_PATH = "/curator_recipes_master_path";
    public static final String  BARRIER_PATH = "/curator_recipes_barrier_path";
}
