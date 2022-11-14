package com.akucun.checkbill.dao.detail;

import java.io.Serializable;
import java.math.BigDecimal;

public abstract class AbstractDetail  implements Serializable
{
	//---sc_akucun_deliver_table 级
	protected String  deliveryId; //为了去重复使用
	  
   /** 收货人	 	*/
   protected String receivePerson;

   /** 收货 联系电话 */
   protected String receiveTel;

   /**  收货地址 */
   protected String receiveAddr; 

   //---order detail级
   protected  String cancleBuy;
   
   /**  商品编号   */
   protected  String productNo;
   
   /**   名称   */
   protected  String productName;
   
   /**  尺码  */
   protected  String size;
   /**  商品条码	  */
   protected  String barcode;
   
   /**  颜色	  */
   protected  String color;
   
   /**  建议销售价	  */
   protected BigDecimal sugestSalePrice;

   /**   平台销售价	  */
   protected  BigDecimal platSalePrice;
   

   /**  数量   */
   protected  int pieceNum;
   
    
   /**  备注	  */
   protected  String memo;


	public String getProductName() {
		return productName;
	}


	public void setProductName(String productName) {
		this.productName = productName;
	}


	public String getDeliveryId() {
		return deliveryId;
	}


	public void setDeliveryId(String deliveryId) {
		this.deliveryId = deliveryId;
	}


	public String getReceivePerson() {
		return receivePerson;
	}


	public void setReceivePerson(String receivePerson) {
		this.receivePerson = receivePerson;
	}


	public String getReceiveTel() {
		return receiveTel;
	}


	public void setReceiveTel(String receiveTel) {
		this.receiveTel = receiveTel;
	}


	public String getReceiveAddr() {
		return receiveAddr;
	}


	public void setReceiveAddr(String receiveAddr) {
		this.receiveAddr = receiveAddr;
	}


	public String getCancleBuy() {
		return cancleBuy;
	}


	public void setCancleBuy(String cancleBuy) {
		this.cancleBuy = cancleBuy;
	}


	public String getProductNo() {
		return productNo;
	}


	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}


	public String getSize() {
		return size;
	}


	public void setSize(String size) {
		this.size = size;
	}


	public String getBarcode() {
		return barcode;
	}


	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}


	public String getColor() {
		return color;
	}


	public void setColor(String color) {
		this.color = color;
	}


	public BigDecimal getSugestSalePrice() {
		return sugestSalePrice;
	}


	public void setSugestSalePrice(BigDecimal sugestSalePrice) {
		this.sugestSalePrice = sugestSalePrice;
	}


	public BigDecimal getPlatSalePrice() {
		return platSalePrice;
	}


	public void setPlatSalePrice(BigDecimal platSalePrice) {
		this.platSalePrice = platSalePrice;
	}


	public int getPieceNum() {
		return pieceNum;
	}


	public void setPieceNum(int pieceNum) {
		this.pieceNum = pieceNum;
	}


	public String getMemo() {
		return memo;
	}


	public void setMemo(String memo) {
		this.memo = memo;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AbstractDetail [deliveryId=").append(deliveryId).append(", receivePerson=")
				.append(receivePerson).append(", receiveTel=").append(receiveTel).append(", receiveAddr=")
				.append(receiveAddr).append(", cancleBuy=").append(cancleBuy).append(", productNo=").append(productNo)
				.append(", productName=").append(productName).append(", size=").append(size).append(", barcode=")
				.append(barcode).append(", color=").append(color).append(", sugestSalePrice=").append(sugestSalePrice)
				.append(", platSalePrice=").append(platSalePrice).append(", pieceNum=").append(pieceNum)
				.append(", memo=").append(memo).append("]");
		return builder.toString();
	}
	

}
