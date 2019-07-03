package com.eden.nginx.admin.common.util;

import com.eden.nginx.admin.exception.NginxException;
import com.github.odiszapc.nginxparser.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author chenqw
 * @version 1.0
 * @since 2019/6/29
 */
public class NgxUtil {

    /**
     * 配置到文本
     *
     * @param conf
     * @return
     */
    public static String toString(NgxConfig conf) {
        if (null == conf) {
            throw new NginxException("不能写入空配置");
        }
        NgxDumper dumper = new NgxDumper(conf);
        return dumper.dump();
    }

    public static List<NgxBlock> findBlock(NgxBlock targetBlock, String blockName) {
        List<NgxBlock> ngxEntries = new ArrayList<>();
        for (NgxEntry entry : targetBlock.getEntries()) {
            if (entry instanceof NgxBlock) {
                NgxBlock ngxBlock = (NgxBlock) entry;
                Iterator<NgxToken> iterator = ngxBlock.getTokens().iterator();
                if (iterator.next().getToken().equals(blockName)) {
                    ngxEntries.add(ngxBlock);
                }
            }
        }
        return ngxEntries;
    }

    public static <T> List<T> findEntryList(NgxBlock ngxBlock, Class entryType) {
        List<T> result = new ArrayList<>();
        Iterator<NgxEntry> iterator = ngxBlock.getEntries().iterator();
        while (iterator.hasNext()) {
            T entry = (T) iterator.next();
            if (entryType.equals(entry.getClass())) {
                result.add(entry);
            }
        }
        return result;
    }

}
