package com.doraemon.monitor.test;

import com.alibaba.fastjson.JSONObject;
import com.sun.deploy.config.MacOSXPlatform;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zbs on 2017/7/14.
 */
public class Aaa {

    String a = "一种制备磷酸二氢铵副产甘蔗专用肥的生产方法，采用磷酸与碳酰胺按照摩尔比为(1.3‑1.6)：1混合，并以升温速度为5℃/min从95℃升温至160℃，搅拌反应4‑5h，分离得到中间体；再采用该中间体与氨水按照摩尔比为1：(1.1‑1.3)混合，反应结束后，获得含氮、磷、钾的料浆，并将含氮、磷、钾的料浆置于冷却结晶器中降温到30‑35℃，进行结晶过滤处理，使滤液在冷却结晶器中循环结晶处理，获得的滤饼为磷酸二氢铵产品，检测滤液中的成分含量；在滤液加氢氧化钾、氢氧化钙溶液和磷酸溶液，当溶液的pH为3.5‑4.0，氮成分含量为20‑25％、磷成分含量为4‑10％、钾成分含量为10‑15％、钙成分含量为4‑8％时，停止加入氢氧化钾、氢氧化钙溶液和磷酸溶液；向滤液中加入甲醛溶液，并进行加压和升温处理，得到悬浮乳液A，然后再向悬浮乳液A中添加微量元素，得到悬浮乳液B，然后再结晶、造粒和烘干制成甘蔗专用肥。";
    Map<String,String> bb = new HashMap<>();

    JSONObject j = JSONObject.parseObject(a);

}
