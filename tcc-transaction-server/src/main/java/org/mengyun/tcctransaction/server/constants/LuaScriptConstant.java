package org.mengyun.tcctransaction.server.constants;

public class LuaScriptConstant {

    public static String HSET_KEY2_IF_KKEY1_EXISTS="if redis.call(\"exists\",KEYS[1])==1 then\n" +
            "\treturn redis.call(\"hset\",KEYS[2],KEYS[3],ARGV[1])\n" +
            "else\n" +
            "\treturn 0\n" +
            "end";

}
