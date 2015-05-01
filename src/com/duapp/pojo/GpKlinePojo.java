package com.duapp.pojo;

/**
 * 股票K线数据
 * 
 * @author shui.jiang
 * @date 2015-05-01
 */
public class GpKlinePojo {

	private int id;
	/** 股票代码 */
	private String gpdm;
	/** 日期 */
	private int klineDay;
	/** 开盘价 */
	private float kpPrice;
	/** 最高价 */
	private float zgPrice;
	/** 最低价 */
	private float zdPrice;
	/** 收盘价 */
	private float spPrice;
	/** 交易量(手) */
	private float cjl;
	/** 交易额(元) */
	private float cje;
	/** K线数据类型（1:日K，2:周K，3:月K） */
	private int klineType;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getKlineDay() {
		return klineDay;
	}

	public void setKlineDay(int klineDay) {
		this.klineDay = klineDay;
	}

	public float getKpPrice() {
		return kpPrice;
	}

	public void setKpPrice(float kpPrice) {
		this.kpPrice = kpPrice;
	}

	public float getZgPrice() {
		return zgPrice;
	}

	public void setZgPrice(float zgPrice) {
		this.zgPrice = zgPrice;
	}

	public float getZdPrice() {
		return zdPrice;
	}

	public void setZdPrice(float zdPrice) {
		this.zdPrice = zdPrice;
	}

	public float getSpPrice() {
		return spPrice;
	}

	public void setSpPrice(float spPrice) {
		this.spPrice = spPrice;
	}

	public float getCjl() {
		return cjl;
	}

	public void setCjl(float cjl) {
		this.cjl = cjl;
	}

	public float getCje() {
		return cje;
	}

	public void setCje(float cje) {
		this.cje = cje;
	}

	public int getKlineType() {
		return klineType;
	}

	public void setKlineType(int klineType) {
		this.klineType = klineType;
	}

	public String getGpdm() {
		return gpdm;
	}

	public void setGpdm(String gpdm) {
		this.gpdm = gpdm;
	}

}
