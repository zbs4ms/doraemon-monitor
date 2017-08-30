package com.doraemon.monitor.dao.mapper;

import com.doraemon.monitor.dao.models.Client;
import com.us.base.mybatis.base.MyMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface ClientMapper extends MyMapper<Client> {

    @Select({"select region from client GROUP BY region"})
    List<String> queryAllRegion();

    @Select({" <script> " +
            "   select * from client where 1=1 ",
            "     <if test='ip != null'> " ,
            "         AND ip = #{ip} ",
            "     </if> ",
            "     <if test='regions != null'> ",
            "         AND region IN ",
            "         <foreach item='item' collection='regions' open='(' separator=',' close=')'> ",
            "                  #{item} ",
            "         </foreach>",
            "     </if> ",
            "  </script> "})
    List<Client> selectByIdAndRegion(Map<String,Object> map);

}