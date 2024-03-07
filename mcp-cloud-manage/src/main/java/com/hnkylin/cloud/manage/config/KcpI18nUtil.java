package com.hnkylin.cloud.manage.config;

import com.hnkylin.cloud.core.enums.OperateLogAction;
import com.hnkylin.cloud.core.enums.OperateLogStatus;
import com.hnkylin.cloud.core.enums.OperateLogType;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class KcpI18nUtil {

    private static final String OPERATE_LOG_TYPE_PREFIX = "mcp.operateLog.type.";

    private static final String OPERATE_LOG_ACTION_PREFIX = "mcp.operateLog.action.";

    private static final String OPERATE_LOG_STATUS_PREFIX = "mcp.operateLog.status.";

    private static final String I18N_PREFIX = "mcp.";

    @Resource
    private MessageSource messageSource;

    public String getI18nMessage(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    public String getI18nMessageByParams(String key, Object[] params) {
        return messageSource.getMessage(key, params, LocaleContextHolder.getLocale());
    }

    /**
     * 操作日志-类型-国际化获取
     *
     * @param operateLogType
     * @return
     */
    public String operateLogTypeMessage(OperateLogType operateLogType) {
        String logType = operateLogType.name().toLowerCase();
        return messageSource.getMessage(OPERATE_LOG_TYPE_PREFIX + logType, null, LocaleContextHolder.getLocale());
    }

    /**
     * 操作日志-操作-国际化获取
     *
     * @param operateLogAction
     * @return
     */
    public String operateLogActionMessage(OperateLogAction operateLogAction) {
        String logAction = operateLogAction.name().toLowerCase();
        return messageSource.getMessage(OPERATE_LOG_ACTION_PREFIX + logAction, null, LocaleContextHolder.getLocale());
    }

    /**
     * 操作日志-状态-国际化获取
     *
     * @param operateLogStatus
     * @return
     */
    public String operateLogStatusMessage(OperateLogStatus operateLogStatus) {
        String operateStatus = operateLogStatus.name().toLowerCase();
        return messageSource.getMessage(OPERATE_LOG_STATUS_PREFIX + operateStatus, null,
                LocaleContextHolder.getLocale());
    }

    /**
     * 操作日志-状态-国际化获取
     *
     * @param i8nKey
     * @return
     */
    public String commonI18N(String i8nKey) {
        String key = i8nKey.toLowerCase();
        return messageSource.getMessage(I18N_PREFIX + key, null, LocaleContextHolder.getLocale());
    }
}
