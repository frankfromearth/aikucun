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
import com.akucun.checkbill.dao.detail.CheckBillDeliveryDetail;
import com.akucun.checkbill.dao.detail.CheckBillMonthDetail;
import com.akucun.checkbill.dao.entity.CheckBillApplyEntity;
import com.akucun.checkbill.dao.entity.User;
import com.akucun.checkbill.service.CheckBillApplyService;
import com.akucun.checkbill.service.UploadFileService;

/**
 * Created by zhaojin on 3/31/2018.
 */
public class CheckBillUserByDeliverOrderTask implements Callable{
    public static final Logger LOG= LoggerFactory.getLogger(CheckBillUserByDeliverOrderTask.class);

    private User user;  
    private String checkBillApplyId;
    private String tmpDir;
    private String deliveryOrderId;
    private String deliveryOrderNo;
    private UploadFileService uploadFileService;
    private CheckBillApplyService checkBillApplyService;
    private PushAppNotify pushAppNotify;
    
    public void setPushAppNotify(PushAppNotify pushAppNotify) {
		this.pushAppNotify = pushAppNotify;
	}

	public void setDeliveryOrderNo(String deliveryOrderNo) {
		this.deliveryOrderNo = deliveryOrderNo;
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

    public void setDeliveryOrderId(String deliveryOrderId) {
		this.deliveryOrderId = deliveryOrderId;
	}

	@Override
    public Object call() throws Exception
    {
        LOG.info("????????????????????? ?????? ????????? ????????? ?????? ??????{} ?????????{} deliveryOrderId ?????? excel ?????? ??????",user.getId(),deliveryOrderId);
        try{

            String filename=user.getUserNo()+"_"+user.getName()+"_?????????"+deliveryOrderNo+"?????????.xlsx";
            File file=new File(tmpDir+File.separator+filename);

            LOG.info ("????????????????????? ?????? ????????? ????????? userId={}, deliveryOrderId={} ???????????? excel??????{} ",user.getId(),deliveryOrderId,file.getAbsolutePath());
            CheckBillApplyEntity res= genExcel(file);
            LOG.info ("????????????????????? ?????? ????????? ????????? userId={}, deliveryOrderId={} ???????????? excel??????{} ",user.getId(),deliveryOrderId,file.getAbsolutePath());

            LOG.info ("????????????????????? ?????? ????????? ????????? userId={}, deliveryOrderId={} ??????????????????{} ",user.getId(),deliveryOrderId,file.getAbsolutePath());
            String url=uploadFileService.uploadFile(filename,file);
            LOG.info ("????????????????????? ?????? ????????? ????????? userId={}, deliveryOrderId={} ????????????URL={} ",user.getId(),deliveryOrderId,url);

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
            int effectRows= checkBillApplyService.updateById(entity);
            LOG.info("????????????????????? ?????? ?????????  ??????checkBillApply :{}???effectRows={}",entity,effectRows);


            effectRows=checkBillApplyService.updateDeliveryUrlByDeliveryOrderNo(user.getId(),deliveryOrderNo,url);
            LOG.info("????????????????????? ?????? ?????? ??????Delivery URL ?????????APP????????? :userId={}???deliveryOrderNo={}???url={},effectRows={}",user.getId(),deliveryOrderNo,url,effectRows);


            LOG.info ("????????????????????? ?????? ????????? ????????? userId={}, deliveryOrderId={} ??????????????????{} ",user.getId(),deliveryOrderId,file.getAbsolutePath());
            file.delete();

            //?????????????????????
			StringBuilder msg=new StringBuilder();
			msg.append("????????????????????????????????????????????????,?????????????????????????????? ").append(deliveryOrderNo);
			msg.append(" ????????????????????????????????????????????????");
            pushAppNotify.notityMsg(user.getId(), msg.toString());
            
            LOG.info("????????????????????? ?????? ????????? ????????? ?????? ??????{} deliveryOrderId={} ?????? excel ?????? ?????? ",user.getId(),deliveryOrderId);
        }catch (Exception e)
        {
            LOG.error("????????????????????? ?????? ????????? ????????? ?????? ??????{} deliveryOrderId={} ?????? excel ?????? ????????????",user.getId(),deliveryOrderId,e);

            CheckBillApplyEntity entity=new CheckBillApplyEntity();
            entity.setId(checkBillApplyId);
            entity.setStatus(CheckBillApplyStatusEnum.FAIL.getStatus());
            LOG.info("????????????????????? ?????? ????????? ???????????? ????????? checkBillApplyId={}",checkBillApplyId);
            checkBillApplyService.updateById(entity);
        }
        return null;
    }

    /**
     *
     * @return ????????????????????????
     * @throws Exception
     */
    private CheckBillApplyEntity  genExcel(File file ) throws Exception
    {

   
        XSSFWorkbook workbook=new XSSFWorkbook();
        FileOutputStream out=new FileOutputStream(file,false);

        CheckBillApplyEntity res=new CheckBillApplyEntity();
        try
        {
        	
            //?????????
            
            CellStyle amtStyle = workbook.createCellStyle();
            DataFormat df = workbook.createDataFormat();
            amtStyle.setDataFormat(df.getFormat("#,#0.00")); //????????????????????????
            
            Sheet sheet1 = workbook.createSheet();
            workbook.setSheetName(0, "??????????????????");
            writeRowHead(sheet1);//????????????
            List<String> sendStatus=Arrays.asList(ProductStatusEnum.UNGET.getStatus(),
            		ProductStatusEnum.GET_PROCESS.getStatus(),
            		ProductStatusEnum.SEND.getStatus(),
            		ProductStatusEnum.ADD_SEND.getStatus() 
            		);
        	CheckBillApplyEntity send=genSheet(sheet1,amtStyle,sendStatus);//??????????????????
        	
        	
        	Sheet sheet2 = workbook.createSheet();
            workbook.setSheetName(1, "??????????????????");
            writeRowHead(sheet2);//????????????
            List<String> unsendStatus=Arrays.asList(ProductStatusEnum.LACK_UNSEND.getStatus(), 
            		ProductStatusEnum.USER_CACEL_UNSEND.getStatus(),
            		ProductStatusEnum.CACEL_REFUND.getStatus(),
            		ProductStatusEnum.REFUNDED.getStatus(),
            		ProductStatusEnum.COMPLAIN_LACK.getStatus(),
            		ProductStatusEnum.VERIFY_REJUECT.getStatus(),
            		ProductStatusEnum.LACK_REFUND.getStatus(),
            		ProductStatusEnum.SYS_CACEL_UNSEND.getStatus() 
            		);
        	CheckBillApplyEntity unSend=genSheet(sheet2,amtStyle,unsendStatus);//??????????????????
        	 
        	res.setCancelTotalAmount(unSend.getCancelTotalAmount());
        	res.setCancelTotalCount(unSend.getCancelTotalCount());
        	res.setPayTotalAmount(send.getPayTotalAmount());
        	res.setPayTotalCount(send.getPayTotalCount());
        	
        	
            workbook.write(out);
        }catch (Exception e)
        {
            LOG.error("??????Excel ??????",e);
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
        List<CheckBillDeliveryDetail> queryData=null;
        do
        {
           
        	LOG.info ("????????????????????? ?????? ????????? ????????? userId={}, deliveryOrderId={} ?????????{}???  ",user.getId(),deliveryOrderId,pageNo);
            queryData=checkBillApplyService.queryExcelDataByDeliveryPage(user.getId(),deliveryOrderId,status,pageNo);
            
            CheckBillApplyEntity itemSum= writeRowData(sheet,amtStyle,queryData, rowNum);//????????????
            cancelTotalCount+=itemSum.getCancelTotalCount();
            payTotalCount+=itemSum.getPayTotalCount();
            cancelTotalAmount=cancelTotalAmount.add(itemSum.getCancelTotalAmount());
            payTotalAmount=payTotalAmount.add(itemSum.getPayTotalAmount());

            pageNo++;
            rowNum+=queryData.size();
            
        }while(queryData.size()== CheckBillConstant.PAGE_SIZE);
        	
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
		   //"????????????",
		   "????????????",
		   "????????????",
		   "??????",
		   "??????",
		   "??????",
		   "???????????????",
		   "???????????????",
		   "??????",
		   "??????",
		   "?????????",
		   "????????????",
		   "????????????"
		   };
        Row row = sheet.createRow(0);
        for(int i=0;i<title.length;i++)
        	row.createCell(i).setCellValue(title[i]);
         
        sheet.setColumnWidth(1, default_width*2);// ????????????
        sheet.setColumnWidth(2, default_width*3);//??????
        sheet.setColumnWidth(8, default_width*2);//??????
        
        sheet.setColumnWidth(title.length-2, default_width*2);// "????????????",
        sheet.setColumnWidth(title.length-1, default_width*5);//"????????????"
    }
 
    private CheckBillApplyEntity writeRowData(Sheet sheet,CellStyle amtStyle,List<CheckBillDeliveryDetail> dataList,int rowNum)
    {

        String processOrderId="firstRow";

        BigDecimal  cancelTotalAmount=BigDecimal.ZERO;
        BigDecimal  payTotalAmount=BigDecimal.ZERO;
        int  payTotalCount=0;
        int  cancelTotalCount=0;

        for(CheckBillDeliveryDetail  detail:dataList)
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
            
            if("????????????".equals(detail.getCancleBuy()))
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
                .append("deliveryOrderId", deliveryOrderId)
                .append("checkBillApplyId", checkBillApplyId)
                .append("tmpDir", tmpDir)
                .toString();
    }
}

