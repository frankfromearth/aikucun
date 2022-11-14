package com.akucun.checkbill.biz;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.akucun.checkbill.Integration.PushAppNotify;
import com.akucun.checkbill.common.constant.CheckBillApplyStatusEnum;
import com.akucun.checkbill.common.constant.CheckBillConstant;
import com.akucun.checkbill.common.constant.ProductStatusEnum;
import com.akucun.checkbill.dao.detail.CheckBillActivityDetail;
import com.akucun.checkbill.dao.detail.CheckBillDeliveryDetail;
import com.akucun.checkbill.dao.detail.CheckBillMonthDetail;
import com.akucun.checkbill.dao.entity.CheckBillApplyEntity;
import com.akucun.checkbill.dao.entity.User;
import com.akucun.checkbill.service.CheckBillApplyService;
import com.akucun.checkbill.service.UploadFileService;

/**
 * Created by zhaojin on 3/31/2018.
 */
public class CheckBillUserByActivityTask implements Callable{
    public static final Logger LOG= LoggerFactory.getLogger(CheckBillUserByActivityTask.class);

    private User user;  
    private String checkBillApplyId;
    private String tmpDir;
    private String activityId;
    private String activityNo;
    private UploadFileService uploadFileService;
    private CheckBillApplyService checkBillApplyService;
    private PushAppNotify pushAppNotify;
    
    public void setPushAppNotify(PushAppNotify pushAppNotify) {
		this.pushAppNotify = pushAppNotify;
	}
  
	public void setUploadFileService(UploadFileService uploadFileService) {
        this.uploadFileService = uploadFileService;
    }

    public void setCheckBillApplyService(CheckBillApplyService checkBillApplyService) {
        this.checkBillApplyService = checkBillApplyService;
    }
    public void setTmpDir(String tmpDir) {
        this.tmpDir = tmpDir;
    }

    public void setUser(User user) {
        this.user = user;
    }
  
    public void setCheckBillApplyId(String checkBillApplyId) {
        this.checkBillApplyId = checkBillApplyId;
    } 

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public void setActivityNo(String activityNo) {
		this.activityNo = activityNo;
	}

	@Override
    public Object call() throws Exception
    {
        LOG.info("会员对账单申请 根据 活动 线程中 生成 用户{} 发货单{} activityId 对账 excel 文件 开始",user.getId(),activityId);
        try{

            String filename=user.getUserNo()+"_"+user.getName()+"_活动号"+activityNo+"对账单.xlsx";
            File file=new File(tmpDir+File.separator+filename);

            LOG.info ("会员对账单申请 根据 活动 线程中 userId={}, activityId={} 开始生成 excel文件{} ",user.getId(),activityId,file.getAbsolutePath());
            CheckBillApplyEntity res= genExcel(file);
            LOG.info ("会员对账单申请 根据 活动 线程中 userId={}, activityId={} 完成生成 excel文件{} ",user.getId(),activityId,file.getAbsolutePath());

            LOG.info ("会员对账单申请 根据 活动 线程中 userId={}, activityId={} 开始上传文件{} ",user.getId(),activityId,file.getAbsolutePath());
            String url=uploadFileService.uploadFile(filename,file);
            LOG.info ("会员对账单申请 根据 活动 线程中 userId={}, activityId={} 返回文件URL={} ",user.getId(),activityId,url);

            CheckBillApplyEntity entity=new CheckBillApplyEntity();
            entity.setId(checkBillApplyId);
            entity.setFileUrl(url);
            entity.setFinishTime(new Date());
            entity.setFileName(filename);
            entity.setStatus(CheckBillApplyStatusEnum.SUCESS.getStatus());
            entity.setCancelTotalAmount(res.getCancelTotalAmount());
            entity.setCancelTotalCount(res.getCancelTotalCount());
            entity.setPayTotalAmount(res.getPayTotalAmount());
            entity.setPayTotalCount(res.getPayTotalCount());

            int effectRows=checkBillApplyService.updateById(entity);
            LOG.info("会员对账单申请 根据 活动 更新checkBillApply :{}，effectRows={}",entity,effectRows);


            effectRows=checkBillApplyService.updateActivityUrlActivityId(user.getId(),activityId,url);
            LOG.info("会员对账单申请 根据 活动 更新Activity URL 为手机APP下载用 :userId={}，activityId={}，url={},effectRows={}",user.getId(),activityId,url,effectRows);


            LOG.info ("会员对账单申请 根据 活动 线程中 userId={}, activityId={} 删除临时文件{} ",user.getId(),activityId,file.getAbsolutePath());
            file.delete();

            //推送消息给用户
			StringBuilder msg=new StringBuilder();
			msg.append("您的活动对帐单系统已生成,可进入对帐功能中进行下载. 【爱库存】 ");
            pushAppNotify.notityMsg(user.getId(), msg.toString());
            
            LOG.info("会员对账单申请 根据 活动 线程中 生成 用户{} activityId={} 对账 excel 文件 成功 ",user.getId(),activityId);
        }catch (Exception e)
        {
            LOG.error("会员对账单申请 根据 活动 线程中 生成 用户{} activityId={} 对账 excel 文件 处理失败",user.getId(),activityId,e);

            CheckBillApplyEntity entity=new CheckBillApplyEntity();
            entity.setId(checkBillApplyId);
            entity.setStatus(CheckBillApplyStatusEnum.FAIL.getStatus());
            LOG.info("会员对账单申请 根据 活动 更新状态 为失败 checkBillApplyId={}",checkBillApplyId);
            checkBillApplyService.updateById(entity);
        }
        return null;
    }

    /**
     *
     * @return 返回要相加的总数
     * @throws Exception
     */
    private CheckBillApplyEntity  genExcel(File file ) throws Exception
    {

   
        XSSFWorkbook workbook=new XSSFWorkbook();
        FileOutputStream out=new FileOutputStream(file,false);

        CheckBillApplyEntity res=new CheckBillApplyEntity();
        try
        {
        	
            //读数据
            
            CellStyle amtStyle = workbook.createCellStyle();
            DataFormat df = workbook.createDataFormat();
            amtStyle.setDataFormat(df.getFormat("#,#0.00")); //小数点后保留两位
            
            Sheet sheet1 = workbook.createSheet();
            workbook.setSheetName(0, "实发商品列表");
            writeRowHead(sheet1);//生成表头
            List<String> sendStatus=Arrays.asList(ProductStatusEnum.UNGET.getStatus(),
            		ProductStatusEnum.GET_PROCESS.getStatus(),
            		ProductStatusEnum.SEND.getStatus(),
            		ProductStatusEnum.ADD_SEND.getStatus() 
            		);
        	CheckBillApplyEntity send=genSheet(sheet1,amtStyle,sendStatus);//实发商品列表
        	
        	
        	Sheet sheet2 = workbook.createSheet();
            workbook.setSheetName(1, "缺发商品列表");
            writeRowHead(sheet2);//生成表头
            List<String> unsendStatus=Arrays.asList(ProductStatusEnum.LACK_UNSEND.getStatus(), 
            		ProductStatusEnum.USER_CACEL_UNSEND.getStatus(),
            		ProductStatusEnum.CACEL_REFUND.getStatus(),
            		ProductStatusEnum.REFUNDED.getStatus(),
            		ProductStatusEnum.COMPLAIN_LACK.getStatus(),
            		ProductStatusEnum.VERIFY_REJUECT.getStatus(),
            		ProductStatusEnum.LACK_REFUND.getStatus(),
            		ProductStatusEnum.SYS_CACEL_UNSEND.getStatus() 
            		);
        	CheckBillApplyEntity unSend=genSheet(sheet2,amtStyle,unsendStatus);//缺发商品列表
        	 
        	res.setCancelTotalAmount(unSend.getCancelTotalAmount());
        	res.setCancelTotalCount(unSend.getCancelTotalCount());
        	res.setPayTotalAmount(send.getPayTotalAmount());
        	res.setPayTotalCount(send.getPayTotalCount());
        	
        	
            workbook.write(out);
        }catch (Exception e)
        {
            LOG.error("生成Excel 出错",e);
            throw  e;
        }finally {
            workbook.close();
            out.close();
        }
       
        return res;
         
    }
         
    private CheckBillApplyEntity genSheet( Sheet sheet, CellStyle amtStyle,List<String> status) throws Exception
    {

        BigDecimal  cancelTotalAmount=BigDecimal.ZERO;
        BigDecimal  payTotalAmount=BigDecimal.ZERO;
        int  payTotalCount=0;
        int  cancelTotalCount=0;

        int pageNo=1;
        int rowNum=1; 
        List<CheckBillActivityDetail> queryData=null;
       do
       {
        	LOG.info ("会员对账单申请 根据 活动  线程中 userId={}, activityId={} 查询第{}页  ",user.getId(),activityId,pageNo);
            queryData=checkBillApplyService.queryExcelDataByActivityPage(user.getId(),activityId,status,pageNo);

            CheckBillApplyEntity itemSum= writeRowData(sheet,amtStyle,queryData, rowNum);//生成数据
            cancelTotalCount+=itemSum.getCancelTotalCount();
            payTotalCount+=itemSum.getPayTotalCount();
            cancelTotalAmount=cancelTotalAmount.add(itemSum.getCancelTotalAmount());
            payTotalAmount=payTotalAmount.add(itemSum.getPayTotalAmount());

            pageNo++;
            rowNum+=queryData.size();
         
        }while(queryData.size()==CheckBillConstant.PAGE_SIZE);
       
        CheckBillApplyEntity res=new CheckBillApplyEntity();
        res.setPayTotalCount(payTotalCount);
        res.setPayTotalAmount(payTotalAmount);
        res.setCancelTotalCount(cancelTotalCount);
        res.setCancelTotalAmount(cancelTotalAmount);
        return res;
    }
    private void writeRowHead(Sheet sheet)
    {
        int default_width=sheet.getColumnWidth(1);//default_width=2048
 
        String [] title=new String[] {
		   //"播货序号",
		   "商品编号",
		   "商品条码",
		   "名称",
		   "尺码",
		   "颜色",
		   "建议销售价",
		   "平台销售价",
		   "数量",
		   "备注",
		   "收货人",
		   "联系电话",
		   "收货地址"
		   };
        Row row = sheet.createRow(0);
        for(int i=0;i<title.length;i++)
        	row.createCell(i).setCellValue(title[i]);
         
        sheet.setColumnWidth(1, default_width*2);// 商品条码
        sheet.setColumnWidth(2, default_width*3);//名称
        sheet.setColumnWidth(8, default_width*2);//备注
        
        sheet.setColumnWidth(title.length-2, default_width*2);// "联系电话",
        sheet.setColumnWidth(title.length-1, default_width*5);//"收货地址"
    }
 
    private CheckBillApplyEntity writeRowData(Sheet sheet,CellStyle amtStyle,List<CheckBillActivityDetail> dataList,int rowNum)
    {

        String processOrderId="firstRow";

        BigDecimal  cancelTotalAmount=BigDecimal.ZERO;
        BigDecimal  payTotalAmount=BigDecimal.ZERO;
        int  payTotalCount=0;
        int  cancelTotalCount=0;

        for(CheckBillActivityDetail  detail:dataList)
        {
            Row row = sheet.createRow(rowNum++);
            if(
                    !(processOrderId.equals(detail.getDeliveryId()))
             )
            {

                row.createCell(9).setCellValue(detail.getReceivePerson());
                row.createCell(10).setCellValue(detail.getReceiveTel());
                row.createCell(11).setCellValue(detail.getReceiveAddr());
               

                processOrderId=detail.getDeliveryId();
            }else
            {

                row.createCell(9).setCellValue("");
                row.createCell(10).setCellValue("");
                row.createCell(11).setCellValue("");
            }

            row.createCell(0).setCellValue(detail.getProductNo());
            row.createCell(1).setCellValue(detail.getBarcode());
            row.createCell(2).setCellValue(detail.getProductName());
            row.createCell(3).setCellValue(detail.getSize());
            row.createCell(4).setCellValue(detail.getColor());
            
            Cell cell05=row.createCell(5, CellType.NUMERIC);
            cell05.setCellStyle(amtStyle);
            cell05.setCellValue(detail.getSugestSalePrice().divide(BigDecimal.valueOf(100)).doubleValue());
             
            Cell cell06=row.createCell(6, CellType.NUMERIC);
            cell06.setCellStyle(amtStyle);
            cell06.setCellValue(detail.getPlatSalePrice().divide(BigDecimal.valueOf(100)).doubleValue());
            
            Cell cell07=row.createCell(7, CellType.NUMERIC); 
            cell07.setCellValue(detail.getPieceNum());
            row.createCell(8).setCellValue(detail.getMemo());
            
//          //DateUtil.formateDateISO((detail.getPayTime()))
            
            if("正常购买".equals(detail.getCancleBuy()))
            {
                payTotalAmount=payTotalAmount.add(detail.getPlatSalePrice());
                payTotalCount++;
            }else
            {
                cancelTotalAmount=cancelTotalAmount.add(detail.getPlatSalePrice());
                cancelTotalCount++;
            }
        }
        
        CheckBillApplyEntity res=new CheckBillApplyEntity();
        res.setCancelTotalAmount(cancelTotalAmount);
        res.setCancelTotalCount(cancelTotalCount);
        res.setPayTotalAmount(payTotalAmount);
        res.setPayTotalCount(payTotalCount);
        return res;
    }
 
    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("user", user) 
                .append("activityId", activityId)
                .append("checkBillApplyId", checkBillApplyId)
                .append("tmpDir", tmpDir)
                .toString();
    }
}

