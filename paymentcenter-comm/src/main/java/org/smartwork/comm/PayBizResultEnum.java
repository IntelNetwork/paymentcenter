package org.smartwork.comm;
/***
 * BizResultEnum概要说明：业务系统错误代码
 * @author Huanghy
 */
public enum PayBizResultEnum {
	/***
	 * 006-消息中心系统
	 * 功能暂无-表示通用异常
	 * 001-为空判断
	 */
	MCH_TYPE_NOT_EXISTS("007001001","商户类型错误","%s商户类型不存在"),
	MCH_STATE_NOT_EXISTS("007001002","商户状态错误","%s商户状态不存在"),
	MCH_ID_EXISTS("007001003","商户ID已存在","%s商户ID已存在"),
	CHANNEL_NAME_NOT_EXISTS("007002001","支付渠道错误","%s支付渠道不存在"),
	CHANNEL_ID_NOT_EXISTS("007002002","支付渠道ID错误","%s支付渠道ID不存在"),
	CHANNEL_ID_EXISTS("007002003","商户支付渠道已存在","商户已存在%s支付渠道");


	/**错误编码业务系统代码+功能编码+错误代码**/
	private String bizCode;
	/**错误描述****/
	private String bizMessage;
	/**带格式错误描述****/
	private String bizFormateMessage;

	/***
	 * 构造函数:
	 * @param bizCode
	 * @param bizMessage
	 * @param bizFormateMessage
	 */
	PayBizResultEnum(String bizCode, String bizMessage, String bizFormateMessage){
		this.bizCode = bizCode;
		this.bizMessage = bizMessage;
		this.bizFormateMessage = bizFormateMessage;
	}

	/** 
	 * @return bizCode 
	 */
	public String getBizCode() {
		return bizCode;
	}

	/** 
	 * @param bizCode 要设置的 bizCode 
	 */
	public void setBizCode(String bizCode) {
		this.bizCode = bizCode;
	}

	/** 
	 * @return bizMessage 
	 */
	public String getBizMessage() {
		return bizMessage;
	}

	/** 
	 * @param bizMessage 要设置的 bizMessage 
	 */
	public void setBizMessage(String bizMessage) {
		this.bizMessage = bizMessage;
	}

	/** 
	 * @return bizFormateMessage 
	 */
	public String getBizFormateMessage() {
		return bizFormateMessage;
	}

	/** 
	 * @param bizFormateMessage 要设置的 bizFormateMessage 
	 */
	public void setBizFormateMessage(String bizFormateMessage) {
		this.bizFormateMessage = bizFormateMessage;
	}
}
