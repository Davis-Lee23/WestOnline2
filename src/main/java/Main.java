import com.fasterxml.jackson.core.JsonProcessingException;
import com.util.HttpClientUtil;

public class Main {
    public static void main(String[] args) throws JsonProcessingException {
        /**
         * 创建一个HttpClientUtil对象即可操作
         * 这是一个基于“两个晚上，一个奇迹”理念的产品，只实现了基础功能
         * 2022-01-16
         */

        HttpClientUtil httpClientUtil = new HttpClientUtil();
        /**
        城市需要的:
        增：根据名字
        查:根据名字
        删:根据id删除
         */
        httpClientUtil.addCity("福州");
        httpClientUtil.findCity("福州");
        httpClientUtil.findAllCity();
        httpClientUtil.delete(101230101);
        /**
         * 天气需要的参数都是id，可以写个用名字的正则模糊查询，但是我比较懒，别骂
         * 增、查、改
         * 增和改都放在add里边偷懒了
         * 删除功能比较懒不写了,题目说不用管(思路：以now()的年月日为值，删除小于它的值)
         */
        httpClientUtil.addWeather(101230101);
        httpClientUtil.addWeather(101230103);
        httpClientUtil.findCityWeather(101230101);
        httpClientUtil.findWeatherByDateAndId();
        httpClientUtil.close();
    }
}
