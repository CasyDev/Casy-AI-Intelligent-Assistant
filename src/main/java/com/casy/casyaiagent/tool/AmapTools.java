package com.casy.casyaiagent.tool;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 高德地图工具类
 * 提供地理编码、逆地理编码、POI搜索、天气查询、路线规划等功能
 * 替代 amap-maps-mcp-server，无需 Node.js 环境
 *
 * @author linlin
 */
@Slf4j
@Component
public class AmapTools {

    /**
     * 高德地图 API Key
     * 需要在 application.yml 中配置: amap.api.key=你的key
     */
    @Value("${amap.api.key:}")
    private String amapApiKey;

    // 高德地图 Web服务 API 地址
    private static final String GEOCODE_URL = "https://restapi.amap.com/v3/geocode/geo";
    private static final String REGEO_URL = "https://restapi.amap.com/v3/geocode/regeo";
    private static final String POI_SEARCH_URL = "https://restapi.amap.com/v3/place/text";
    private static final String POI_AROUND_URL = "https://restapi.amap.com/v3/place/around";
    private static final String WEATHER_URL = "https://restapi.amap.com/v3/weather/weatherInfo";
    private static final String DIRECTION_DRIVING_URL = "https://restapi.amap.com/v3/direction/driving";
    private static final String DIRECTION_WALKING_URL = "https://restapi.amap.com/v3/direction/walking";
    private static final String DIRECTION_TRANSIT_URL = "https://restapi.amap.com/v3/direction/transit/integrated";
    private static final String IP_LOCATION_URL = "https://restapi.amap.com/v3/ip";
    private static final String DISTANCE_URL = "https://restapi.amap.com/v3/distance";

    /**
     * 检查 API Key 是否配置
     */
    private void checkApiKey() {
        if (amapApiKey == null || amapApiKey.isEmpty()) {
            throw new RuntimeException("高德地图 API Key 未配置，请在 application.yml 中设置 amap.api.key");
        }
    }

    /**
     * 地理编码 - 将地址转换为经纬度坐标
     *
     * @param address 详细地址（如：北京市朝阳区望京街道）
     * @param city    可选，指定城市（如：北京）
     * @return 经纬度坐标信息
     */
    @Tool(name = "maps_geo", description = """
            地理编码服务，将详细的结构化地址转换为经纬度坐标。
            使用场景：
            - 用户提供了地址，需要获取该位置的经纬度坐标
            - 需要进行位置计算或地图展示
            
            参数说明：
            - address: 必填，详细地址（如：北京市朝阳区望京街道）
            - city: 可选，指定城市名称（如：北京），可提高解析准确度
            """)
    public String geocode(
            @ToolParam(description = "详细地址，如：北京市朝阳区望京街道") String address,
            @ToolParam(description = "可选，指定城市名称，如：北京", required = false) String city) {
        checkApiKey();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("key", amapApiKey);
            params.put("address", address);
            if (city != null && !city.isEmpty()) {
                params.put("city", city);
            }
            params.put("output", "JSON");

            String response = HttpUtil.get(GEOCODE_URL, params);
            JSONObject json = JSONUtil.parseObj(response);

            if ("1".equals(json.getStr("status"))) {
                JSONArray geocodes = json.getJSONArray("geocodes");
                if (geocodes != null && !geocodes.isEmpty()) {
                    JSONObject location = geocodes.getJSONObject(0);
                    String result = String.format("地址：%s\n经纬度：%s\n省市区：%s-%s-%s\n街道：%s",
                            location.getStr("formatted_address"),
                            location.getStr("location"),
                            location.getStr("province"),
                            location.getStr("city"),
                            location.getStr("district"),
                            location.getStr("street"));
                    return result;
                }
                return "未找到该地址的坐标信息";
            } else {
                return "地理编码失败：" + json.getStr("info");
            }
        } catch (Exception e) {
            log.error("地理编码失败", e);
            return "地理编码失败：" + e.getMessage();
        }
    }

    /**
     * 逆地理编码 - 将经纬度转换为地址
     *
     * @param location 经纬度坐标（格式：经度,纬度，如：116.481488,39.990464）
     * @return 地址信息
     */
    @Tool(name = "maps_regeocode", description = """
            逆地理编码服务，将经纬度坐标转换为详细地址。
            使用场景：
            - 用户提供了经纬度，需要获取具体地址信息
            - 需要根据坐标查询周边信息
            
            参数说明：
            - location: 必填，经纬度坐标（格式：经度,纬度，如：116.481488,39.990464）
            """)
    public String regeocode(
            @ToolParam(description = "经纬度坐标，格式：经度,纬度，如：116.481488,39.990464") String location) {
        checkApiKey();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("key", amapApiKey);
            params.put("location", location);
            params.put("output", "JSON");
            params.put("extensions", "all"); // 返回详细地址信息

            String response = HttpUtil.get(REGEO_URL, params);
            JSONObject json = JSONUtil.parseObj(response);

            if ("1".equals(json.getStr("status"))) {
                JSONObject regeocode = json.getJSONObject("regeocode");
                JSONObject addressComponent = regeocode.getJSONObject("addressComponent");

                StringBuilder result = new StringBuilder();
                result.append("详细地址：").append(regeocode.getStr("formatted_address")).append("\n");
                result.append("国家：").append(addressComponent.getStr("country")).append("\n");
                result.append("省份：").append(addressComponent.getStr("province")).append("\n");
                result.append("城市：").append(addressComponent.getStr("city")).append("\n");
                result.append("区县：").append(addressComponent.getStr("district")).append("\n");
                result.append("街道：").append(addressComponent.getStr("township")).append("\n");
                result.append("街道门牌号：").append(addressComponent.getStr("street")).append(" ")
                        .append(addressComponent.getStr("streetNumber")).append("\n");

                // 周边POI
                JSONArray pois = regeocode.getJSONArray("pois");
                if (pois != null && !pois.isEmpty()) {
                    result.append("\n周边地标：\n");
                    for (int i = 0; i < Math.min(5, pois.size()); i++) {
                        JSONObject poi = pois.getJSONObject(i);
                        result.append(String.format("- %s（%s，距离：%s米）\n",
                                poi.getStr("name"),
                                poi.getStr("type"),
                                poi.getStr("distance")));
                    }
                }

                return result.toString();
            } else {
                return "逆地理编码失败：" + json.getStr("info");
            }
        } catch (Exception e) {
            log.error("逆地理编码失败", e);
            return "逆地理编码失败：" + e.getMessage();
        }
    }

    /**
     * POI 关键词搜索
     *
     * @param keywords 搜索关键词（如：餐厅、酒店、加油站）
     * @param city     可选，搜索城市
     * @return POI 列表
     */
    @Tool(name = "maps_text_search", description = """
            POI关键词搜索，根据关键词搜索地点信息。
            使用场景：
            - 搜索特定类型的地点（如：餐厅、酒店、银行）
            - 在指定城市搜索某个地点名称
            
            参数说明：
            - keywords: 必填，搜索关键词（如：麦当劳、加油站）
            - city: 可选，指定搜索城市（如：北京）
            """)
    public String searchPoi(
            @ToolParam(description = "搜索关键词，如：麦当劳、加油站") String keywords,
            @ToolParam(description = "可选，指定搜索城市，如：北京", required = false) String city) {
        checkApiKey();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("key", amapApiKey);
            params.put("keywords", keywords);
            if (city != null && !city.isEmpty()) {
                params.put("city", city);
            }
            params.put("output", "JSON");
            params.put("offset", 10); // 每页10条

            String response = HttpUtil.get(POI_SEARCH_URL, params);
            JSONObject json = JSONUtil.parseObj(response);

            if ("1".equals(json.getStr("status"))) {
                JSONArray pois = json.getJSONArray("pois");
                if (pois != null && !pois.isEmpty()) {
                    StringBuilder result = new StringBuilder("找到 " + pois.size() + " 个结果：\n\n");
                    for (int i = 0; i < pois.size(); i++) {
                        JSONObject poi = pois.getJSONObject(i);
                        result.append(String.format("%d. %s\n", i + 1, poi.getStr("name")));
                        result.append(String.format("   地址：%s\n", poi.getStr("address")));
                        result.append(String.format("   类型：%s\n", poi.getStr("type")));
                        result.append(String.format("   电话：%s\n", poi.getStr("tel")));
                        result.append(String.format("   经纬度：%s\n\n", poi.getStr("location")));
                    }
                    return result.toString();
                }
                return "未找到相关地点";
            } else {
                return "搜索失败：" + json.getStr("info");
            }
        } catch (Exception e) {
            log.error("POI搜索失败", e);
            return "POI搜索失败：" + e.getMessage();
        }
    }

    /**
     * 周边 POI 搜索
     *
     * @param location 中心点经纬度（格式：经度,纬度）
     * @param keywords 可选，搜索关键词
     * @param radius   搜索半径（单位：米，默认3000）
     * @return 周边POI列表
     */
    @Tool(name = "maps_around_search", description = """
            周边搜索，搜索指定坐标点周边的POI信息。
            使用场景：
            - 查找当前位置附近的餐厅、酒店等
            - 搜索某个地标周边的设施
            
            参数说明：
            - location: 必填，中心点经纬度（格式：经度,纬度，如：116.481488,39.990464）
            - keywords: 可选，搜索关键词（如：餐厅、酒店）
            - radius: 可选，搜索半径（单位：米，默认3000）
            """)
    public String searchAround(
            @ToolParam(description = "中心点经纬度，格式：经度,纬度，如：116.481488,39.990464") String location,
            @ToolParam(description = "可选，搜索关键词，如：餐厅", required = false) String keywords,
            @ToolParam(description = "可选，搜索半径（米），默认3000", required = false) Integer radius) {
        checkApiKey();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("key", amapApiKey);
            params.put("location", location);
            if (keywords != null && !keywords.isEmpty()) {
                params.put("keywords", keywords);
            }
            params.put("radius", radius != null ? radius : 3000);
            params.put("output", "JSON");
            params.put("offset", 10);

            String response = HttpUtil.get(POI_AROUND_URL, params);
            JSONObject json = JSONUtil.parseObj(response);

            if ("1".equals(json.getStr("status"))) {
                JSONArray pois = json.getJSONArray("pois");
                if (pois != null && !pois.isEmpty()) {
                    StringBuilder result = new StringBuilder("周边找到 " + pois.size() + " 个结果：\n\n");
                    for (int i = 0; i < pois.size(); i++) {
                        JSONObject poi = pois.getJSONObject(i);
                        result.append(String.format("%d. %s\n", i + 1, poi.getStr("name")));
                        result.append(String.format("   地址：%s\n", poi.getStr("address")));
                        result.append(String.format("   类型：%s\n", poi.getStr("type")));
                        result.append(String.format("   距离：%s米\n", poi.getStr("distance")));
                        result.append(String.format("   电话：%s\n\n", poi.getStr("tel")));
                    }
                    return result.toString();
                }
                return "周边未找到相关地点";
            } else {
                return "周边搜索失败：" + json.getStr("info");
            }
        } catch (Exception e) {
            log.error("周边搜索失败", e);
            return "周边搜索失败：" + e.getMessage();
        }
    }

    /**
     * 天气查询
     *
     * @param city 城市名称或adcode（如：北京 或 110000）
     * @return 天气信息
     */
    @Tool(name = "maps_weather", description = """
            天气查询服务，根据城市名称或adcode查询指定城市的天气。
            使用场景：
            - 查询某个城市的天气情况
            - 获取温度、湿度、风向等信息
            
            参数说明：
            - city: 必填，城市名称（如：北京、上海）或城市编码（adcode，如：110000）
            """)
    public String getWeather(
            @ToolParam(description = "城市名称，如：北京、上海") String city) {
        checkApiKey();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("key", amapApiKey);
            params.put("city", city);
            params.put("output", "JSON");
            params.put("extensions", "all"); // 获取详细天气信息

            String response = HttpUtil.get(WEATHER_URL, params);
            JSONObject json = JSONUtil.parseObj(response);

            if ("1".equals(json.getStr("status"))) {
                JSONArray forecasts = json.getJSONArray("forecasts");
                if (forecasts != null && !forecasts.isEmpty()) {
                    JSONObject forecast = forecasts.getJSONObject(0);
                    String cityName = forecast.getStr("city");
                    JSONArray casts = forecast.getJSONArray("casts");

                    StringBuilder result = new StringBuilder();
                    result.append(cityName).append("天气预报：\n\n");

                    for (int i = 0; i < casts.size(); i++) {
                        JSONObject cast = casts.getJSONObject(i);
                        String date = cast.getStr("date");
                        String week = cast.getStr("week");
                        String dayWeather = cast.getStr("dayweather");
                        String nightWeather = cast.getStr("nightweather");
                        String dayTemp = cast.getStr("daytemp");
                        String nightTemp = cast.getStr("nighttemp");
                        String dayWind = cast.getStr("daywind");
                        String dayPower = cast.getStr("daypower");

                        result.append(String.format("%s（星期%s）：\n", date, week));
                        result.append(String.format("  白天：%s，%s℃\n", dayWeather, dayTemp));
                        result.append(String.format("  夜间：%s，%s℃\n", nightWeather, nightTemp));
                        result.append(String.format("  风向：%s %s级\n\n", dayWind, dayPower));
                    }
                    return result.toString();
                }
                return "未找到该城市的天气信息";
            } else {
                return "天气查询失败：" + json.getStr("info");
            }
        } catch (Exception e) {
            log.error("天气查询失败", e);
            return "天气查询失败：" + e.getMessage();
        }
    }

    /**
     * 驾车路线规划
     *
     * @param origin      起点经纬度（格式：经度,纬度）
     * @param destination 终点经纬度（格式：经度,纬度）
     * @return 路线信息
     */
    @Tool(name = "maps_direction_driving", description = """
            驾车路线规划，根据起终点经纬度规划驾车路线。
            使用场景：
            - 规划从A地到B地的驾车路线
            - 获取行驶距离、时间、收费等信息
            
            参数说明：
            - origin: 必填，起点经纬度（格式：经度,纬度，如：116.481488,39.990464）
            - destination: 必填，终点经纬度（格式：经度,纬度，如：116.481488,39.990464）
            """)
    public String drivingDirection(
            @ToolParam(description = "起点经纬度，格式：经度,纬度，如：116.481488,39.990464") String origin,
            @ToolParam(description = "终点经纬度，格式：经度,纬度，如：116.434446,39.90816") String destination) {
        checkApiKey();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("key", amapApiKey);
            params.put("origin", origin);
            params.put("destination", destination);
            params.put("output", "JSON");
            params.put("extensions", "all");

            String response = HttpUtil.get(DIRECTION_DRIVING_URL, params);
            JSONObject json = JSONUtil.parseObj(response);

            if ("1".equals(json.getStr("status"))) {
                JSONObject route = json.getJSONObject("route");
                JSONArray paths = route.getJSONArray("paths");

                if (paths != null && !paths.isEmpty()) {
                    JSONObject path = paths.getJSONObject(0);
                    String distance = path.getStr("distance");
                    String duration = path.getStr("duration");
                    String tolls = path.getStr("tolls");
                    String strategy = path.getStr("strategy");

                    StringBuilder result = new StringBuilder();
                    result.append("驾车路线规划：\n\n");
                    result.append(String.format("策略：%s\n", strategy));
                    result.append(String.format("总距离：%.1f 公里\n", Integer.parseInt(distance) / 1000.0));
                    result.append(String.format("预计时间：%.0f 分钟\n", Integer.parseInt(duration) / 60.0));
                    result.append(String.format("过路费：%s 元\n\n", tolls));

                    // 导航步骤
                    JSONArray steps = path.getJSONArray("steps");
                    if (steps != null && !steps.isEmpty()) {
                        result.append("导航指引：\n");
                        for (int i = 0; i < steps.size(); i++) {
                            JSONObject step = steps.getJSONObject(i);
                            String instruction = step.getStr("instruction");
                            String stepDistance = step.getStr("distance");
                            String road = step.getStr("road");
                            result.append(String.format("%d. %s（%s米）%s\n",
                                    i + 1, instruction, stepDistance,
                                    road != null && !road.isEmpty() ? "- " + road : ""));
                        }
                    }
                    return result.toString();
                }
                return "未找到可行路线";
            } else {
                return "路线规划失败：" + json.getStr("info");
            }
        } catch (Exception e) {
            log.error("驾车路线规划失败", e);
            return "驾车路线规划失败：" + e.getMessage();
        }
    }

    /**
     * 步行路线规划
     *
     * @param origin      起点经纬度
     * @param destination 终点经纬度
     * @return 路线信息
     */
    @Tool(name = "maps_direction_walking", description = """
            步行路线规划，根据起终点经纬度规划步行路线。
            使用场景：
            - 规划短途步行路线
            - 获取步行距离和时间
            
            参数说明：
            - origin: 必填，起点经纬度（格式：经度,纬度）
            - destination: 必填，终点经纬度（格式：经度,纬度）
            """)
    public String walkingDirection(
            @ToolParam(description = "起点经纬度，格式：经度,纬度，如：116.481488,39.990464") String origin,
            @ToolParam(description = "终点经纬度，格式：经度,纬度，如：116.434446,39.90816") String destination) {
        checkApiKey();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("key", amapApiKey);
            params.put("origin", origin);
            params.put("destination", destination);
            params.put("output", "JSON");

            String response = HttpUtil.get(DIRECTION_WALKING_URL, params);
            JSONObject json = JSONUtil.parseObj(response);

            if ("1".equals(json.getStr("status"))) {
                JSONObject route = json.getJSONObject("route");
                JSONArray paths = route.getJSONArray("paths");

                if (paths != null && !paths.isEmpty()) {
                    JSONObject path = paths.getJSONObject(0);
                    String distance = path.getStr("distance");
                    String duration = path.getStr("duration");

                    StringBuilder result = new StringBuilder();
                    result.append("步行路线规划：\n\n");
                    result.append(String.format("总距离：%.1f 公里\n", Integer.parseInt(distance) / 1000.0));
                    result.append(String.format("预计时间：%.0f 分钟\n\n", Integer.parseInt(duration) / 60.0));

                    // 导航步骤
                    JSONArray steps = path.getJSONArray("steps");
                    if (steps != null && !steps.isEmpty()) {
                        result.append("步行指引：\n");
                        for (int i = 0; i < steps.size(); i++) {
                            JSONObject step = steps.getJSONObject(i);
                            String instruction = step.getStr("instruction");
                            String stepDistance = step.getStr("distance");
                            result.append(String.format("%d. %s（%s米）\n",
                                    i + 1, instruction, stepDistance));
                        }
                    }
                    return result.toString();
                }
                return "未找到可行路线";
            } else {
                return "路线规划失败：" + json.getStr("info");
            }
        } catch (Exception e) {
            log.error("步行路线规划失败", e);
            return "步行路线规划失败：" + e.getMessage();
        }
    }

    /**
     * 公交路线规划
     *
     * @param origin      起点经纬度
     * @param destination 终点经纬度
     * @param city        城市名或adcode
     * @param cityd       终点城市名或adcode（同城则与city相同）
     * @return 公交路线信息
     */
    @Tool(name = "maps_direction_transit_integrated", description = """
            公交路线规划，根据起终点经纬度规划公交/地铁路线。
            使用场景：
            - 规划公共交通出行路线
            - 获取地铁、公交换乘方案
            
            参数说明：
            - origin: 必填，起点经纬度（格式：经度,纬度）
            - destination: 必填，终点经纬度（格式：经度,纬度）
            - city: 必填，起点所在城市（如：北京）
            - cityd: 可选，终点所在城市（同城可不填）
            """)
    public String transitDirection(
            @ToolParam(description = "起点经纬度，格式：经度,纬度，如：116.481488,39.990464") String origin,
            @ToolParam(description = "终点经纬度，格式：经度,纬度，如：116.434446,39.90816") String destination,
            @ToolParam(description = "起点所在城市，如：北京") String city,
            @ToolParam(description = "终点所在城市（同城可不填）", required = false) String cityd) {
        checkApiKey();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("key", amapApiKey);
            params.put("origin", origin);
            params.put("destination", destination);
            params.put("city", city);
            if (cityd != null && !cityd.isEmpty()) {
                params.put("cityd", cityd);
            } else {
                params.put("cityd", city);
            }
            params.put("output", "JSON");

            String response = HttpUtil.get(DIRECTION_TRANSIT_URL, params);
            JSONObject json = JSONUtil.parseObj(response);

            if ("1".equals(json.getStr("status"))) {
                JSONObject route = json.getJSONObject("route");
                JSONArray transits = route.getJSONArray("transits");

                if (transits != null && !transits.isEmpty()) {
                    StringBuilder result = new StringBuilder();
                    result.append("公交路线规划：\n\n");

                    // 显示前3条方案
                    for (int i = 0; i < Math.min(3, transits.size()); i++) {
                        JSONObject transit = transits.getJSONObject(i);
                        String cost = transit.getStr("cost");
                        String duration = transit.getStr("duration");
                        String walkingDistance = transit.getStr("walking_distance");

                        result.append(String.format("方案 %d：\n", i + 1));
                        result.append(String.format("  费用：%s 元\n", cost));
                        result.append(String.format("  时间：%.0f 分钟\n", Integer.parseInt(duration) / 60.0));
                        result.append(String.format("  步行距离：%.0f 米\n", Integer.parseInt(walkingDistance)));

                        // 显示路径段
                        JSONArray segments = transit.getJSONArray("segments");
                        if (segments != null && !segments.isEmpty()) {
                            result.append("  路线详情：\n");
                            for (int j = 0; j < segments.size(); j++) {
                                JSONObject segment = segments.getJSONObject(j);

                                // 公交段
                                JSONObject bus = segment.getJSONObject("bus");
                                if (bus != null) {
                                    JSONArray buslines = bus.getJSONArray("buslines");
                                    if (buslines != null && !buslines.isEmpty()) {
                                        JSONObject busline = buslines.getJSONObject(0);
                                        String name = busline.getStr("name");
                                        String departureStop = busline.getStr("departure_stop");
                                        String arrivalStop = busline.getStr("arrival_stop");
                                        String viaNum = busline.getStr("via_num");
                                        result.append(String.format("    - 乘坐 %s，从 %s 上车，%s 下车（%s站）\n",
                                                name, departureStop, arrivalStop, viaNum));
                                    }
                                }

                                // 地铁段
                                JSONObject railway = segment.getJSONObject("railway");
                                if (railway != null) {
                                    String name = railway.getStr("name");
                                    String departure = railway.getJSONObject("departure_stop").getStr("name");
                                    String arrival = railway.getJSONObject("arrival_stop").getStr("name");
                                    result.append(String.format("    - 乘坐 %s，从 %s 上车，%s 下车\n",
                                            name, departure, arrival));
                                }

                                // 步行段
                                JSONObject walking = segment.getJSONObject("walking");
                                if (walking != null) {
                                    String dist = walking.getStr("distance");
                                    result.append(String.format("    - 步行 %s 米\n", dist));
                                }
                            }
                        }
                        result.append("\n");
                    }
                    return result.toString();
                }
                return "未找到公交路线";
            } else {
                return "公交路线规划失败：" + json.getStr("info");
            }
        } catch (Exception e) {
            log.error("公交路线规划失败", e);
            return "公交路线规划失败：" + e.getMessage();
        }
    }

    /**
     * IP 定位
     *
     * @param ip 可选，指定 IP 地址，不传则返回当前 IP
     * @return IP 位置信息
     */
    @Tool(name = "maps_ip_location", description = """
            IP定位服务，根据IP地址获取其所在城市及中心点经纬度。
            使用场景：
            - 获取用户当前大致位置
            - 根据IP反查地理位置
            
            参数说明：
            - ip: 可选，指定IP地址（不传则返回当前IP位置）
            """)
    public String ipLocation(
            @ToolParam(description = "可选，指定IP地址", required = false) String ip) {
        checkApiKey();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("key", amapApiKey);
            if (ip != null && !ip.isEmpty()) {
                params.put("ip", ip);
            }
            params.put("output", "JSON");

            String response = HttpUtil.get(IP_LOCATION_URL, params);
            JSONObject json = JSONUtil.parseObj(response);

            if ("1".equals(json.getStr("status"))) {
                String province = json.getStr("province");
                String city = json.getStr("city");
                String adcode = json.getStr("adcode");
                String rectangle = json.getStr("rectangle");

                return String.format("IP定位结果：\n省份：%s\n城市：%s\n城市编码：%s\n坐标范围：%s",
                        province, city, adcode, rectangle);
            } else {
                return "IP定位失败：" + json.getStr("info");
            }
        } catch (Exception e) {
            log.error("IP定位失败", e);
            return "IP定位失败：" + e.getMessage();
        }
    }

    /**
     * 距离测量
     *
     * @param origins     起点经纬度（多个用|分隔，如：116.481488,39.990464|116.123456,39.654321）
     * @param destination 终点经纬度
     * @param type        路径类型：1-驾车，3-步行
     * @return 距离信息
     */
    @Tool(name = "maps_distance", description = """
            距离测量服务，测量两个或多个坐标点之间的距离。
            使用场景：
            - 计算两点之间的直线距离或驾车/步行距离
            - 批量测量多个点到目标点的距离
            
            参数说明：
            - origins: 必填，起点经纬度（格式：经度,纬度，多个用|分隔）
            - destination: 必填，终点经纬度（格式：经度,纬度）
            - type: 可选，距离类型：1-驾车距离，3-步行距离，不传则为直线距离
            """)
    public String distance(
            @ToolParam(description = "起点经纬度，格式：经度,纬度，多个用|分隔，如：116.481488,39.990464|116.123456,39.654321") String origins,
            @ToolParam(description = "终点经纬度，格式：经度,纬度，如：116.434446,39.90816") String destination,
            @ToolParam(description = "可选，距离类型：1-驾车，3-步行", required = false) String type) {
        checkApiKey();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("key", amapApiKey);
            params.put("origins", origins);
            params.put("destination", destination);
            if (type != null && !type.isEmpty()) {
                params.put("type", type);
            }
            params.put("output", "JSON");

            String response = HttpUtil.get(DISTANCE_URL, params);
            JSONObject json = JSONUtil.parseObj(response);

            if ("1".equals(json.getStr("status"))) {
                JSONArray results = json.getJSONArray("results");
                StringBuilder sb = new StringBuilder();
                sb.append("距离测量结果：\n\n");

                for (int i = 0; i < results.size(); i++) {
                    JSONObject result = results.getJSONObject(i);
                    String originId = result.getStr("origin_id");
                    String distance = result.getStr("distance");
                    String duration = result.getStr("duration");

                    sb.append(String.format("起点 %s：\n", originId));
                    sb.append(String.format("  距离：%.1f 公里\n", Integer.parseInt(distance) / 1000.0));
                    if (duration != null && !duration.isEmpty()) {
                        sb.append(String.format("  耗时：%.0f 分钟\n", Integer.parseInt(duration) / 60.0));
                    }
                }
                return sb.toString();
            } else {
                return "距离测量失败：" + json.getStr("info");
            }
        } catch (Exception e) {
            log.error("距离测量失败", e);
            return "距离测量失败：" + e.getMessage();
        }
    }
}
